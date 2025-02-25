package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.ws.rs.core.Response.Status;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.ThrowingFunction;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SampleDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.RestException;

public abstract class RelationFinder<M extends Identifiable> {
  protected static final Logger log = LoggerFactory.getLogger(RelationFinder.class);

  public abstract static class RelationAdapter<M extends Identifiable, P extends Identifiable, D> {

    public abstract D asDto(P model);

    public abstract Stream<P> find(M model, Consumer<String> emitError) throws IOException;

    private final String category;

    public final String category() {
      return category;
    }

    public RelationAdapter(String category) {
      this.category = category;
    }

    public final List<D> handle(Collection<M> items) throws IOException, JsonProcessingException {
      List<String> errors = new ArrayList<>();

      Stream<P> relationStream = Stream.empty();
      for (M item : items) {
        relationStream = Stream.concat(relationStream, find(item, errors::add));
      }
      List<D> relations = relationStream
          .filter(Objects::nonNull)//
          .collect(Collectors.groupingBy(Identifiable::getId)).values().stream()//
          .map(l -> l.get(0))//
          .map(this::asDto)//
          .collect(Collectors.toList());
      if (!errors.isEmpty() && relations.isEmpty()) {
        throw new RestException(errors.stream().collect(Collectors.joining("\n")), Status.BAD_REQUEST);
      }
      return relations;
    }
  }

  public static ParentSampleAdapter<Sample> parent(String category, Class<? extends DetailedSample> targetClass) {
    return new ParentSampleAdapter<>(category, true, targetClass, Stream::of);
  }

  public static final class ParentSampleAdapter<M extends Identifiable> extends RelationAdapter<M, Sample, SampleDto> {
    private final ThrowingFunction<M, Stream<Sample>, IOException> getSample;
    private final Class<? extends DetailedSample> targetClass;
    private final boolean strict;

    public ParentSampleAdapter(String category, Class<? extends DetailedSample> targetClass,
        ThrowingFunction<M, Stream<Sample>, IOException> getSample) {
      this(category, false, targetClass, getSample);
    }

    public ParentSampleAdapter(String category, boolean strict, Class<? extends DetailedSample> targetClass,
        ThrowingFunction<M, Stream<Sample>, IOException> getSample) {
      super(category);
      this.strict = strict;
      this.targetClass = targetClass;
      this.getSample = getSample;
    }

    @Override
    public SampleDto asDto(Sample model) {
      return Dtos.asDto(model, false);
    }

    @Override
    public Stream<Sample> find(M model, Consumer<String> emitError) throws IOException {
      return getSample.apply(model).flatMap(sample -> {
        if (sample == null) {
          return Stream.empty();
        }
        if (sample instanceof DetailedSample) {
          if (!strict && targetClass.isInstance(sample)) {
            return Stream.of(sample);
          }
          DetailedSample parent = LimsUtils.getParent(targetClass, (DetailedSample) sample);
          if (parent == null) {
            emitError.accept(String.format("%s (%s) has no %s.", sample.getName(), sample.getAlias(), category()));
            return Stream.empty();
          }
          return Stream.of(parent);
        } else {
          emitError.accept(String.format("%s (%s) has no parents of any kind.", sample.getName(), sample.getAlias()));
          return Stream.empty();
        }
      });
    }

  }

  private final Map<String, RelationAdapter<M, ?, ?>> adapters = new HashMap<>();

  public RelationFinder<M> add(RelationAdapter<M, ?, ?> adapter) {
    adapters.put(adapter.category(), adapter);
    return this;
  }

  protected abstract List<M> fetchByIds(List<Long> ids) throws IOException;

  public List<?> list(List<Long> ids, String category) throws IOException {
    RelationAdapter<M, ?, ?> adapter = adapters.get(category);
    if (adapter == null) {
      throw new RestException(String.format("No such category %s.", category), Status.NOT_FOUND);
    }
    List<M> items = fetchByIds(ids);
    return adapter.handle(items);
  }

  public static ChildrenSampleAdapter child(String category, Class<? extends DetailedSample> targetClass) {
    return new ChildrenSampleAdapter(category, targetClass);
  }

  public static class ChildrenSampleAdapter extends RelationAdapter<Sample, DetailedSample, SampleDto> {

    public ChildrenSampleAdapter(String category, Class<? extends DetailedSample> targetClass) {
      super(category);
      this.targetClass = targetClass;
    }

    private final Class<? extends DetailedSample> targetClass;

    @Override
    public SampleDto asDto(DetailedSample model) {
      return Dtos.asDto(model, false);
    }

    @Override
    public Stream<DetailedSample> find(Sample model, Consumer<String> emitError) {
      Set<DetailedSample> children = searchChildren(targetClass, (DetailedSample) model).collect(Collectors.toSet());
      if (children.isEmpty()) {
        emitError.accept(String.format("%s (%s) has no %s.", model.getName(), model.getAlias(), category()));
        return Stream.empty();
      }
      return children.stream();
    }

    private static Stream<DetailedSample> searchChildren(Class<? extends DetailedSample> targetChildClass,
        DetailedSample model) {
      return model.getChildren().stream()
          .flatMap(child -> Stream.concat(Stream.of(child).filter(targetChildClass::isInstance),
              searchChildren(targetChildClass, child)));
    }

    public static Stream<Library> searchChildrenLibraries(Sample model, LibraryService libraryService) {
      try {
        Stream<Library> libraries = libraryService.listBySampleId(model.getId()).stream();
        if (LimsUtils.isDetailedSample(model)) {
          libraries = Stream.concat(libraries,
              ((DetailedSample) model).getChildren().stream()
                  .flatMap(child -> searchChildrenLibraries(child, libraryService)));
        }
        return libraries;
      } catch (IOException e) {
        log.error("Failed to get child libraries.", e);
        throw new RestException("Error fetching library information", Status.INTERNAL_SERVER_ERROR);
      }
    }

  }

}

package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SampleDto;

public abstract class RelationFinder<M extends Identifiable> {
  protected static final Logger log = LoggerFactory.getLogger(RelationFinder.class);

  public abstract static class RelationAdapter<M extends Identifiable, P extends Identifiable, D> {

    public abstract D asDto(P model);

    public abstract Stream<P> find(M model, Consumer<String> emitError);

    private final String category;

    public final String category() {
      return category;
    }

    public RelationAdapter(String category) {
      this.category = category;
    }

    public final HttpEntity<byte[]> handle(ObjectMapper mapper, Stream<M> items) throws JsonProcessingException {
      List<String> errors = new ArrayList<>();
      List<D> relations = items.flatMap(item -> find(item, errors::add))//
          .filter(Objects::nonNull)//
          .collect(Collectors.groupingBy(Identifiable::getId)).values().stream()//
          .map(l -> l.get(0))//
          .map(this::asDto)//
          .collect(Collectors.toList());
      if (!errors.isEmpty() && relations.isEmpty()) {
        throw new RestException(errors.stream().collect(Collectors.joining("\n")), Status.BAD_REQUEST);
      }
      return new HttpEntity<>(mapper.writeValueAsBytes(relations));
    }
  }

  public static ParentSampleAdapter<Sample> parent(String category, Class<? extends DetailedSample> targetClass) {
    return new ParentSampleAdapter<>(category, true, targetClass, Stream::of);
  }
  public static final class ParentSampleAdapter<M extends Identifiable> extends RelationAdapter<M, Sample, SampleDto> {
    private final Function<M, Stream<Sample>> getSample;
    private final Class<? extends DetailedSample> targetClass;
    private final boolean strict;

    public ParentSampleAdapter(String category, Class<? extends DetailedSample> targetClass, Function<M, Stream<Sample>> getSample) {
      this(category, false, targetClass, getSample);
    }

    public ParentSampleAdapter(String category, boolean strict, Class<? extends DetailedSample> targetClass,
        Function<M, Stream<Sample>> getSample) {
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
    public Stream<Sample> find(M model, Consumer<String> emitError) {
      return getSample.apply(model).flatMap(sample -> {
        if (sample == null) return Stream.empty();
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
  private final ObjectMapper mapper = new ObjectMapper();

  public RelationFinder<M> add(RelationAdapter<M, ?, ?> adapter) {
    adapters.put(adapter.category(), adapter);
    return this;
  }

  protected abstract M fetch(long id) throws IOException;

  public HttpEntity<byte[]> list(List<Long> ids, String category) throws JsonProcessingException {
    RelationAdapter<M, ?, ?> adapter = adapters.get(category);
    if (adapter == null) {
      throw new RestException(String.format("No such category %s.", category), Status.NOT_FOUND);
    }
    return adapter.handle(mapper, ids.stream().map(WhineyFunction.rethrow(this::fetch)));
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

    private static Stream<DetailedSample> searchChildren(Class<? extends DetailedSample> targetChildClass, DetailedSample model) {
      return model.getChildren().stream()
          .flatMap(child -> Stream.concat(Stream.of(child).filter(targetChildClass::isInstance), searchChildren(targetChildClass, child)));
    }

    public static Stream<Library> searchChildrenLibraries(Sample model, LibraryService libraryService) {
      try {
        Stream<Library> libraries = libraryService.listBySampleId(model.getId()).stream();
        if (LimsUtils.isDetailedSample(model)) {
          libraries = Stream.concat(libraries,
              ((DetailedSample) model).getChildren().stream().flatMap(child -> searchChildrenLibraries(child, libraryService)));
        }
        return libraries;
      } catch (IOException e) {
        log.error("Failed to get child libraries.", e);
        throw new RestException("Error fetching library information", Status.INTERNAL_SERVER_ERROR);
      }
    }

  }

}

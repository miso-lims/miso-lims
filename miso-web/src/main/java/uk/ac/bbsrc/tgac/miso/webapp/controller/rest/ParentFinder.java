package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.core.Response.Status;

import org.springframework.http.HttpEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SampleDto;

public abstract class ParentFinder<M extends Identifiable> {
  public abstract static class ParentAdapter<M extends Identifiable, P extends Identifiable, D> {

    public abstract D asDto(P model);

    public abstract P find(M model, Consumer<String> emitError);

    private final String category;

    public final String category() {
      return category;
    }

    public ParentAdapter(String category) {
      this.category = category;
    }

    public final HttpEntity<byte[]> handle(ObjectMapper mapper, Stream<M> items) throws JsonProcessingException {
      List<String> errors = new ArrayList<>();
      List<D> parents = items.map(item -> find(item, errors::add))//
          .filter(Objects::nonNull)//
          .collect(Collectors.groupingBy(Identifiable::getId)).values().stream()//
          .map(l -> l.get(0))//
          .map(this::asDto)//
          .collect(Collectors.toList());
      if (!errors.isEmpty()) {
        throw new RestException(errors.stream().collect(Collectors.joining("\n")), Status.BAD_REQUEST);
      }
      return new HttpEntity<>(mapper.writeValueAsBytes(parents));
    }
  }

  public static SampleAdapter<Sample> parent(String category, Class<? extends DetailedSample> targetClass) {
    return new SampleAdapter<>(category, true, targetClass, Function.identity());
  }
  public static final class SampleAdapter<M extends Identifiable> extends ParentAdapter<M, Sample, SampleDto> {
    private final Function<M, Sample> getSample;
    private final Class<? extends DetailedSample> targetClass;
    private final boolean strict;

    public SampleAdapter(String category, Class<? extends DetailedSample> targetClass, Function<M, Sample> getSample) {
      this(category, false, targetClass, getSample);
    }
    public SampleAdapter(String category, boolean strict, Class<? extends DetailedSample> targetClass, Function<M, Sample> getSample) {
      super(category);
      this.strict = strict;
      this.targetClass = targetClass;
      this.getSample = getSample;
    }

    @Override
    public SampleDto asDto(Sample model) {
      return Dtos.asDto(model);
    }

    @Override
    public Sample find(M model, Consumer<String> emitError) {
      Sample sample = getSample.apply(model);
      if (sample == null) return null;
      if (sample instanceof DetailedSample) {
        if (!strict && targetClass.isInstance(sample)) {
          return sample;
        }
        DetailedSample parent = LimsUtils.getParent(targetClass, (DetailedSample) sample);
        if (parent == null) {
          emitError.accept(String.format("%s (%s) has no %s.", sample.getName(), sample.getAlias(), category()));
        }
        return parent;
      } else {
        emitError.accept(String.format("%s (%s) has no parents of any kind.", sample.getName(), sample.getAlias()));
        return null;
      }
    }

  }

  private final Map<String, ParentAdapter<M, ?, ?>> adapters = new HashMap<>();
  private final ObjectMapper mapper = new ObjectMapper();

  public ParentFinder<M> add(ParentAdapter<M, ?, ?> adapter) {
    adapters.put(adapter.category(), adapter);
    return this;
  }

  protected abstract M fetch(long id) throws IOException;

  public HttpEntity<byte[]> list(List<Long> ids, String category) throws JsonProcessingException {
    ParentAdapter<M, ?, ?> adapter = adapters.get(category);
    if (adapter == null) {
      throw new RestException(String.format("No such category %s.", category), Status.NOT_FOUND);
    }
    return adapter.handle(mapper, ids.stream().map(WhineyFunction.rethrow(this::fetch)));
  }

}

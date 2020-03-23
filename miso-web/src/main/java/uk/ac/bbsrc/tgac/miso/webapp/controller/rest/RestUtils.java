package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.ws.rs.core.Response.Status;

import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.service.DeleterService;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.core.service.SaveService;

public class RestUtils {

  private RestUtils() {
    throw new IllegalStateException("Util class not intended for instantiation");
  }

  public static <T, R extends Identifiable> T getObject(String type, long id, ProviderService<R> service, Function<R, T> toDto)
      throws IOException {
    R object = retrieve(type, id, service);
    return toDto.apply(object);
  }

  public static <T, R extends Identifiable, S> T createObject(String type, T dto, Function<T, R> toObject, SaveService<R> service,
      Function<R, T> toDto) throws IOException {
    validateDtoProvided(type, dto);
    R object = toObject.apply(dto);
    validateNewObject(type, object);
    long savedId = service.create(object);
    return toDto.apply(service.get(savedId));
  }

  public static <T> void validateDtoProvided(String type, T dto) {
    if (dto == null) {
      throw new RestException(type + " not provided", Status.BAD_REQUEST);
    }
  }

  public static <T extends Identifiable> void validateNewObject(String type, T object) {
    if (object.isSaved()) {
      throw new RestException(type + " is already saved", Status.BAD_REQUEST);
    }
  }

  public static <T, R extends Identifiable, S> T updateObject(String type, long targetId, T dto, Function<T, R> toObject,
      SaveService<R> service, Function<R, T> toDto) throws IOException {
    validateDtoProvided(type, dto);
    R object = toObject.apply(dto);
    if (object.getId() != targetId) {
      throw new RestException(type + " ID mismatch", Status.BAD_REQUEST);
    } else if (service.get(targetId) == null) {
      throw new RestException(type + " not found", Status.NOT_FOUND);
    }
    long savedId = service.update(object);
    return toDto.apply(service.get(savedId));
  }

  public static <T extends Deletable> void bulkDelete(String type, List<Long> ids, DeleterService<T> service) throws IOException {
    List<T> items = new ArrayList<>();
    for (Long id : ids) {
      if (id == null) {
        throw new RestException(type + " id cannot be null", Status.BAD_REQUEST);
      }
      T item = retrieve(type, id, service);
      items.add(item);
    }
    service.bulkDelete(items);
  }

  public static <T extends Deletable> void delete(String type, long targetId, DeleterService<T> service) throws IOException {
    T item = service.get(targetId);
    if (item == null) {
      throw new RestException(type + " not found", Status.NOT_FOUND);
    }
    service.delete(item);
  }

  public static <T extends Identifiable> T retrieve(String type, long id, ProviderService<T> service) throws IOException {
    if (id <= 0) {
      throw new RestException("Invalid id: " + id, Status.BAD_REQUEST);
    }
    T object = service.get(id);
    if (object == null) {
      throw new RestException(type + " " + id + " not found", Status.NOT_FOUND);
    }
    return object;
  }

}

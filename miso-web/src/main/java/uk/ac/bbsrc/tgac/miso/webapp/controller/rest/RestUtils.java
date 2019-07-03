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
    if (id <= 0) {
      throw new RestException("Invalid id", Status.BAD_REQUEST);
    }
    R object = service.get(id);
    if (object == null) {
      throw new RestException(type + " not found");
    }
    return toDto.apply(object);
  }

  public static <T, R extends Identifiable, S> T createObject(String type, T dto, Function<T, R> toObject, SaveService<R> service,
      Function<R, T> toDto) throws IOException {
    if (dto == null) {
      throw new RestException(type + " not provided", Status.BAD_REQUEST);
    }
    R object = toObject.apply(dto);
    if (object.isSaved()) {
      throw new RestException(type + " is already saved", Status.BAD_REQUEST);
    }
    long savedId = service.create(object);
    return toDto.apply(service.get(savedId));
  }

  public static <T, R extends Identifiable, S> T updateObject(String type, long targetId, T dto, Function<T, R> toObject,
      SaveService<R> service, Function<R, T> toDto) throws IOException {
    if (dto == null) {
      throw new RestException(type + " not provided", Status.BAD_REQUEST);
    }
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
      T item = service.get(id);
      if (item == null) {
        throw new RestException(type + " " + id + " not found", Status.BAD_REQUEST);
      }
      items.add(item);
    }
    service.bulkDelete(items);
  }

}

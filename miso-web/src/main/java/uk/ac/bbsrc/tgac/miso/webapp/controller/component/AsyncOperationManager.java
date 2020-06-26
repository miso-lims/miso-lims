package uk.ac.bbsrc.tgac.miso.webapp.controller.component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response.Status;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.BulkSaveOperation;
import uk.ac.bbsrc.tgac.miso.core.service.BulkSaveService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.BulkValidationException;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.RestException;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.RestUtils;

@Component
public class AsyncOperationManager {

  private final ConcurrentHashMap<String, BulkSaveOperation<?>> asyncOperations = new ConcurrentHashMap<>();

  public <T, R extends Identifiable> ObjectNode startAsyncBulkCreate(String type, List<T> dtos, Function<T, R> toObject,
      BulkSaveService<R> service) throws IOException {
    List<R> items = new ArrayList<>();
    for (T dto : dtos) {
      if (dto == null) {
        throw new RestException(String.format("Cannot save null %s", type), Status.BAD_REQUEST);
      }
      R item = toObject.apply(dto);
      if (item.isSaved()) {
        throw new RestException("One or more of these items are already saved", Status.BAD_REQUEST);
      }
      items.add(item);
    }
    BulkSaveOperation<R> operation = service.startBulkCreate(items);
    String uuid = addAsyncOperation(operation);
    return makeRunningProgress(uuid, operation);
  }

  public <T, R extends Identifiable> ObjectNode startAsyncBulkUpdate(String type, List<T> dtos, Function<T, R> toObject,
      BulkSaveService<R> service) throws IOException {
    List<R> items = new ArrayList<>();
    for (T dto : dtos) {
      if (dto == null) {
        throw new RestException(String.format("Cannot save null %s", type), Status.BAD_REQUEST);
      }
      R item = toObject.apply(dto);
      if (!item.isSaved()) {
        throw new RestException("Cannot update unsaved item", Status.BAD_REQUEST);
      } else if (service.get(item.getId()) == null) {
        throw new RestException(String.format("No %s found with ID: %d", type, item.getId()), Status.BAD_REQUEST);
      }
      items.add(item);
    }
    BulkSaveOperation<R> operation = service.startBulkUpdate(items);
    String uuid = addAsyncOperation(operation);
    return makeRunningProgress(uuid, operation);
  }

  public <T, R extends Identifiable> ObjectNode getAsyncProgress(String uuid, Class<R> itemClass, BulkSaveService<R> service,
      AuthorizationManager authorizationManager, Function<R, T> toDto) throws Exception {
    BulkSaveOperation<?> operation = asyncOperations.get(uuid);
    if (operation == null) {
      throw new RestException("No operation found with ID: " + uuid, Status.NOT_FOUND);
    }
    authorizationManager.throwIfNotOwner(operation.getOwner());
    if (!itemClass.isAssignableFrom(operation.getItemClass())) {
      throw new RestException("Invalid operation type", Status.BAD_REQUEST);
    }
    if (!operation.isComplete()) {
      return makeRunningProgress(uuid, operation);
    } else {
      if (operation.isSuccess()) {
        List<T> dtos = service.listByIdList(operation.getSavedIds()).stream()
            .map(toDto)
            .collect(Collectors.toList());
        return makeCompletedProgress(uuid, operation, dtos);
      } else {
        return makeFailedProgress(uuid, operation);
      }
    }
  }

  private static ObjectNode makeRunningProgress(String uuid, BulkSaveOperation<?> operation) {
    return makeBaseProgress(uuid, operation, "running");
  }

  private static <T> ObjectNode makeCompletedProgress(String uuid, BulkSaveOperation<?> operation, List<T> dtos) {
    ObjectNode json = makeBaseProgress(uuid, operation, "completed");
    json.putPOJO("data", dtos);
    return json;
  }

  private static ObjectNode makeFailedProgress(String uuid, BulkSaveOperation<?> operation) {
    ObjectNode json = makeBaseProgress(uuid, operation, "failed");
    if (operation.getException() instanceof BulkValidationException) {
      BulkValidationException bulkValidation = (BulkValidationException) operation.getException();
      json.put("detail", bulkValidation.getLocalizedMessage());
      RestUtils.addBulkValidationData(json, bulkValidation);
    } else {
      json.put("detail", "An unexpected error has occurred");
    }
    return json;
  }

  private static ObjectNode makeBaseProgress(String uuid, BulkSaveOperation<?> operation, String status) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode json = mapper.createObjectNode();
    json.put("operationId", uuid);
    json.put("status", status);
    json.put("completedUnits", operation.getProgress());
    json.put("totalUnits", operation.getTotalCount());
    return json;
  }

  private <R extends Identifiable> String addAsyncOperation(BulkSaveOperation<R> operation) {
    String uuid = null;
    while (uuid == null) {
      uuid = UUID.randomUUID().toString();
      if (asyncOperations.putIfAbsent(uuid, operation) != null) {
        uuid = null;
      }
    }
    return uuid;
  }

  @Scheduled(fixedDelay = 300000)
  private void cleanOperations() {
    LocalDateTime cutoffTime = LocalDateTime.now().minus(5L, ChronoUnit.MINUTES);
    asyncOperations.values().removeIf(operation -> {
      LocalDateTime completionTime = operation.getCompletionTime();
      return completionTime != null && completionTime.isBefore(cutoffTime);
    });
  }

}

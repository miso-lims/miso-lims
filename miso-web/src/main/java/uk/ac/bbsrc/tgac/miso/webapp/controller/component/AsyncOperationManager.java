package uk.ac.bbsrc.tgac.miso.webapp.controller.component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.BulkQcSaveOperation;
import uk.ac.bbsrc.tgac.miso.core.service.BulkSaveOperation;
import uk.ac.bbsrc.tgac.miso.core.service.BulkSaveService;
import uk.ac.bbsrc.tgac.miso.core.service.QualityControlService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.BulkValidationException;
import uk.ac.bbsrc.tgac.miso.core.util.ThrowingFunction;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.QcDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.RestException;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.RestUtils;

@Component
public class AsyncOperationManager {

  @Autowired
  private QualityControlService qualityControlService;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private ConstantsController constantsController;
  @Autowired
  private ObjectMapper mapper;

  private final ConcurrentHashMap<String, BulkSaveOperation<?>> asyncOperations = new ConcurrentHashMap<>();

  public synchronized <T, R extends Identifiable> ObjectNode startAsyncBulkCreate(String type, List<T> dtos,
      Function<T, R> toObject, BulkSaveService<R> service) throws IOException {
    return startAsyncBulkCreate(type, dtos, toObject, service, false);
  }

  public synchronized <T, R extends Identifiable> ObjectNode startAsyncBulkCreate(String type, List<T> dtos,
      Function<T, R> toObject, BulkSaveService<R> service, boolean updateConstants) throws IOException {
    List<R> items = validateBulkCreate(type, dtos, toObject);
    checkProjectLocks(items, service);
    Consumer<BulkSaveOperation<R>> callback = null;
    if (updateConstants) {
      callback = operation -> {
        if (operation.isSuccess()) {
          constantsController.refreshConstants();
        }
      };
    }
    BulkSaveOperation<R> operation = service.startBulkCreate(items, callback);
    String uuid = addAsyncOperation(operation);
    return makeRunningProgress(uuid, operation);
  }

  public ObjectNode startAsyncBulkQcCreate(List<QcDto> dtos) throws IOException {
    List<QC> items = validateBulkCreate("QC", dtos, Dtos::to);
    BulkSaveOperation<QC> operation = qualityControlService.startBulkCreate(items);
    String uuid = addAsyncOperation(operation);
    return makeRunningProgress(uuid, operation);
  }

  private <T, R extends Identifiable> List<R> validateBulkCreate(String type, List<T> dtos, Function<T, R> toObject)
      throws IOException {
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
    return items;
  }

  private <T extends Identifiable> void checkProjectLocks(List<T> items, BulkSaveService<T> service)
      throws IOException {
    Set<Long> lockProjectIds = new HashSet<>();
    for (T item : items) {
      Long lockProjectId = service.getLockProjectId(item);
      if (lockProjectId != null) {
        lockProjectIds.add(lockProjectId);
      }
    }
    for (BulkSaveOperation<?> operation : asyncOperations.values()) {
      if (!operation.isComplete()
          && operation.getLockProjectIds().stream().anyMatch(id -> lockProjectIds.contains(id))) {
        throw new RestException(
            "There is another ongoing save operation for the same project. Please wait and try again.",
            Status.BAD_REQUEST);
      }
    }
  }

  public synchronized <T extends Identifiable> ObjectNode startAsyncBulkUpdate(String type, List<T> items,
      BulkSaveService<T> service)
      throws IOException {
    BulkSaveOperation<T> operation = service.startBulkUpdate(items, null);
    String uuid = addAsyncOperation(operation);
    return makeRunningProgress(uuid, operation);
  }

  public synchronized <T, R extends Identifiable> ObjectNode startAsyncBulkUpdate(String type, List<T> dtos,
      Function<T, R> toObject, BulkSaveService<R> service) throws IOException {
    return startAsyncBulkUpdate(type, dtos, toObject, service, false);
  }

  public synchronized <T, R extends Identifiable> ObjectNode startAsyncBulkUpdate(String type, List<T> dtos,
      Function<T, R> toObject, BulkSaveService<R> service, boolean updateConstants) throws IOException {
    List<R> items = validateBulkUpdate(type, dtos, toObject, service::get);
    checkProjectLocks(items, service);
    Consumer<BulkSaveOperation<R>> callback = null;
    if (updateConstants) {
      callback = operation -> {
        if (operation.isSuccess()) {
          constantsController.refreshConstants();
        }
      };
    }
    BulkSaveOperation<R> operation = service.startBulkUpdate(items, callback);
    String uuid = addAsyncOperation(operation);
    return makeRunningProgress(uuid, operation);
  }

  public <T, R extends Identifiable> ObjectNode startAsyncBulkQcUpdate(List<QcDto> dtos) throws IOException {
    final QcTarget qcTarget = getQcTarget(dtos.get(0).getQcTarget());
    List<QC> items = validateBulkUpdate("QC", dtos, Dtos::to, id -> qualityControlService.get(qcTarget, id));
    BulkSaveOperation<QC> operation = qualityControlService.startBulkUpdate(items);
    String uuid = addAsyncOperation(operation);
    return makeRunningProgress(uuid, operation);
  }

  private QcTarget getQcTarget(String label) {
    try {
      return QcTarget.valueOf(label);
    } catch (IllegalArgumentException | NullPointerException e) {
      throw new RestException("Invalid QC Target: " + label, Status.BAD_REQUEST);
    }
  }

  private <T, R extends Identifiable> List<R> validateBulkUpdate(String type, List<T> dtos, Function<T, R> toObject,
      ThrowingFunction<Long, R, IOException> getItem) throws IOException {
    List<R> items = new ArrayList<>();
    for (T dto : dtos) {
      if (dto == null) {
        throw new RestException(String.format("Cannot save null %s", type), Status.BAD_REQUEST);
      }
      R item = toObject.apply(dto);
      if (!item.isSaved()) {
        throw new RestException("Cannot update unsaved item", Status.BAD_REQUEST);
      } else if (getItem.apply(item.getId()) == null) {
        throw new RestException(String.format("No %s found with ID: %d", type, item.getId()), Status.BAD_REQUEST);
      }
      items.add(item);
    }
    return items;
  }

  public <T, R extends Identifiable> ObjectNode getAsyncProgress(String uuid, Class<R> itemClass) throws Exception {
    return getAsyncProgress(uuid, itemClass, null);
  }

  public <T, R extends Identifiable> ObjectNode getAsyncProgress(String uuid, Class<R> itemClass,
      BulkSaveService<R> service,
      Function<R, T> toDto) throws Exception {
    return getAsyncProgress(uuid, itemClass, operation -> {
      return service.listByIdList(operation.getSavedIds()).stream()
          .map(toDto)
          .collect(Collectors.toList());
    });
  }

  public <T, R extends Identifiable> ObjectNode getAsyncQcProgress(String uuid) throws Exception {
    return getAsyncProgress(uuid, QC.class, operation -> {
      BulkQcSaveOperation qcOperation = (BulkQcSaveOperation) operation;
      return qualityControlService.listByIdList(qcOperation.getQcTarget(), qcOperation.getSavedIds()).stream()
          .map(Dtos::asDto)
          .collect(Collectors.toList());
    });
  }

  private <T, R extends Identifiable> ObjectNode getAsyncProgress(String uuid, Class<R> itemClass,
      ThrowingFunction<BulkSaveOperation<?>, List<T>, IOException> getDtos) throws Exception {
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
        List<T> dtos = getDtos == null ? null : getDtos.apply(operation);
        return makeCompletedProgress(uuid, operation, dtos);
      } else {
        return makeFailedProgress(uuid, operation);
      }
    }
  }

  private ObjectNode makeRunningProgress(String uuid, BulkSaveOperation<?> operation) {
    return makeBaseProgress(uuid, operation, "running");
  }

  private <T> ObjectNode makeCompletedProgress(String uuid, BulkSaveOperation<?> operation, List<T> dtos) {
    ObjectNode json = makeBaseProgress(uuid, operation, "completed");
    if (dtos != null) {
      json.putPOJO("data", dtos);
    }
    return json;
  }

  private ObjectNode makeFailedProgress(String uuid, BulkSaveOperation<?> operation) {
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

  private ObjectNode makeBaseProgress(String uuid, BulkSaveOperation<?> operation, String status) {
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
  public void cleanOperations() {
    LocalDateTime cutoffTime = LocalDateTime.now().minus(5L, ChronoUnit.MINUTES);
    asyncOperations.values().removeIf(operation -> {
      LocalDateTime completionTime = operation.getCompletionTime();
      return completionTime != null && completionTime.isBefore(cutoffTime);
    });
  }

}

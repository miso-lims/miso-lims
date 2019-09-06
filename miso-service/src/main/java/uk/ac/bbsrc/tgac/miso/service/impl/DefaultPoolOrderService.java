package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.OrderLibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrder;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.*;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.IndexChecker;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.persistence.PoolOrderDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;
import uk.ac.bbsrc.tgac.miso.service.PoolOrderService;
@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultPoolOrderService extends AbstractSaveService<PoolOrder> implements PoolOrderService {

  @Value("${miso.pools.strictIndexChecking:false}")
  private Boolean strictPools;

  @Autowired
  private PoolOrderDao poolOrderDao;

  @Autowired
  private DeletionStore deletionStore;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private SequencingParametersService sequencingParametersService;

  @Autowired
  private OrderPurposeService orderPurposeService;

  @Autowired
  private LibraryAliquotService libraryAliquotService;

  @Autowired
  private PoolService poolService;

  @Autowired
  private SequencingOrderService sequencingOrderService;

  @Autowired
  private IndexChecker indexChecker;

  @Autowired
  private ChangeLogService changeLogService;

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public SaveDao<PoolOrder> getDao() {
    return poolOrderDao;
  }

  public void setChangeLogService(ChangeLogService changeLogService) {
    this.changeLogService = changeLogService;
  }

  @Override
  protected void loadChildEntities(PoolOrder object) throws IOException {
    loadChildEntity(object.getParameters(), object::setParameters, sequencingParametersService);
    loadChildEntity(object.getPurpose(), object::setPurpose, orderPurposeService);
    for (OrderLibraryAliquot orderAliquot : object.getOrderLibraryAliquots()) {
      orderAliquot.setAliquot(libraryAliquotService.get(orderAliquot.getAliquot().getId()));
    }
    loadChildEntity(object.getPool(), object::setPool, poolService);
    loadChildEntity(object.getSequencingOrder(), object::setSequencingOrder, sequencingOrderService);
  }

  @Override
  protected void collectValidationErrors(PoolOrder object, PoolOrder beforeChange, List<ValidationError> errors) throws IOException {
    if (object.getPool() != null || object.getSequencingOrder() != null) {
      // order should be fulfilled
      if (object.isDraft()) {
        errors.add(new ValidationError("draft", "Fulfilled order cannot be a draft"));
      }
      if (object.getPool() == null) {
        errors.add(new ValidationError("poolId", "Pool must be provided if a sequencing order is linked"));
      } else {
        for (OrderLibraryAliquot orderAli : object.getOrderLibraryAliquots()) {
          if (object.getPool().getPoolContents().stream().map(PoolElement::getPoolableElementView).noneMatch(poolElement -> {
            for (LibraryAliquot parent = poolElement.getAliquot(); parent != null; parent = parent.getParentAliquot()) {
              if (parent.getId() == orderAli.getAliquot().getId()) {
                return true;
              }
            }
            return false;
          })) {
            errors.add(new ValidationError("Pool does not contain all of the required aliquots"));
            break;
          }
        }
        if (object.getSequencingOrder() != null) {
          if (object.getSequencingOrder().getPurpose().getId() != object.getPurpose().getId()
              || object.getSequencingOrder().getSequencingParameter().getId() != object.getParameters().getId()
              || !object.getSequencingOrder().getPartitions().equals(object.getPartitions())
              || object.getSequencingOrder().getPool().getId() != object.getPool().getId()) {
            errors.add(new ValidationError("Sequencing order does not match the pool order"));
          }
        }
      }
      preventFulfilledChange("partitions", PoolOrder::getPartitions, object, beforeChange, errors);
      preventFulfilledChange("parametersId", PoolOrder::getParameters, object, beforeChange, errors);
      preventFulfilledChange("description", PoolOrder::getDescription, object, beforeChange, errors);
      preventFulfilledChange("alias", PoolOrder::getAlias, object, beforeChange, errors);
      preventFulfilledChange("purposeId", PoolOrder::getPurpose, object, beforeChange, errors);
      if (!allMatch(object.getOrderLibraryAliquots(), beforeChange.getOrderLibraryAliquots())) {
        errors.add(new ValidationError("Aliquots cannot be changed after the order is fulfilled"));
      }
    }

    PlatformType orderPlatform = object.getParameters() == null ? null : object.getParameters().getInstrumentModel().getPlatformType();
    for (OrderLibraryAliquot orderAli : object.getOrderLibraryAliquots()) {
      PlatformType libPlatform = orderAli.getAliquot().getLibrary().getPlatformType();
      if (orderPlatform == null) {
        orderPlatform = libPlatform;
      } else if (!libPlatform.equals(orderPlatform)) {
        errors.add(new ValidationError("Platform for all aliquots and sequencing parameters (if specified) must match"));
        break;
      }
    }

    if (!object.isDraft() && object.getOrderLibraryAliquots().isEmpty()) {
      errors.add(new ValidationError("Non-draft order must include at least one library aliquot"));
    }

    if(strictPools) validateNoNewDuplicateIndices(object, beforeChange, errors);
  }

  private void validateNoNewDuplicateIndices(PoolOrder object, PoolOrder beforeChange, List<ValidationError> errors){
    // Work based on whether bad index count increases, rather than >0, in case Pool Orders already exist w >1
    if(indexChecker.getDuplicateIndicesSequences(beforeChange).size()
            < indexChecker.getDuplicateIndicesSequences(object).size()
            || indexChecker.getNearDuplicateIndicesSequences(beforeChange).size()
            < indexChecker.getNearDuplicateIndicesSequences(object).size()) {
      Set<String> indices = indexChecker.getDuplicateIndicesSequences(object);
      indices.addAll(indexChecker.getNearDuplicateIndicesSequences(object));
      Set<String> bcIndices = indexChecker.getDuplicateIndicesSequences(beforeChange);
      bcIndices.addAll(indexChecker.getNearDuplicateIndicesSequences(beforeChange));
      String errorMessage = String.format("Pools may not contain Library Aliquots with indices with %d or " +
                      "fewer positions of difference, please address the following conflicts: ",
              indexChecker.getWarningMismatches());
      indices.removeAll(bcIndices);

      for (String index : indices) {
        errorMessage += index + " ";
      }

      errors.add(new ValidationError(errorMessage));
    }
  }

  private static <R> void preventFulfilledChange(String property, Function<PoolOrder, R> getter, PoolOrder newItem, PoolOrder beforeChange,
      List<ValidationError> errors) {
    if (ValidationUtils.isSetAndChanged(getter, newItem, beforeChange)) {
      errors.add(new ValidationError(property, "Cannot be changed after order is fulfilled"));
    }
  }

  private static boolean allMatch(Collection<OrderLibraryAliquot> one, Collection<OrderLibraryAliquot> two) {
    if (one.size() != two.size()) {
      return false;
    }
    for (OrderLibraryAliquot lib : one) {
      if (two.stream().noneMatch(matcher(lib))) {
        return false;
      }
    }
    return true;
  }

  private static Predicate<OrderLibraryAliquot> matcher(OrderLibraryAliquot o1) {
    return o2 -> o2.getProportion() == o1.getProportion() && o2.getAliquot().getId() == o1.getAliquot().getId();
  }

  @Override
  protected void applyChanges(PoolOrder to, PoolOrder from) throws IOException {
    // Properties not changelogged here are caught by SQL triggers
    to.setPartitions(from.getPartitions());

    if (from.getParameters() == null && to.getParameters() != null) {
      changeLogService.create(
              to.createChangeLog("Sequencing Parameters deleted.",
                      "parametersId",
                      authorizationManager.getCurrentUser())
      );
    } else if(to.getParameters() == null && from.getParameters() != null){
      changeLogService.create(
              to.createChangeLog("New Sequencing Parameters:"
                      + from.getParameters().getInstrumentModel().getAlias()
                      + ", " + from.getParameters().getName(),
                      "parametersId",
                      authorizationManager.getCurrentUser())
      );
    } else if(to.getParameters() != null && from.getParameters() != null
            && !to.getParameters().equals(from.getParameters())) {
      changeLogService.create(
              to.createChangeLog("Changed Sequencing Parameters: "
                              + to.getParameters().getInstrumentModel().getAlias()
                              + ", " + to.getParameters().getName()+ " to "
                              + from.getParameters().getInstrumentModel().getAlias()
                              + ", " + from.getParameters().getName(),
                      "parametersId",
                      authorizationManager.getCurrentUser())
      );
    }
    to.setParameters(from.getParameters());
    to.setAlias(from.getAlias());

    if(!to.getPurpose().getAlias().equals(from.getPurpose().getAlias())) changeLogService.create(
            to.createChangeLog("Changed Purpose: "
                            + to.getPurpose().getAlias()
                            + " to " + from.getPurpose().getAlias(),
                    "purposeId",
                    authorizationManager.getCurrentUser())
    );
    to.setPurpose(from.getPurpose());
    to.setDescription(from.getDescription());
    to.setDraft(from.isDraft());

    // remove from TO if library isn't in FROM
    // add to TO if library isn't in TO
    // update proportion in TO if different in FROM
    Set<Long> fromIds = new HashSet<>();
    for (OrderLibraryAliquot fromOrderAli : from.getOrderLibraryAliquots()){
      fromIds.add(fromOrderAli.getAliquot().getId());
    }
    Set<OrderLibraryAliquot> removed = new HashSet<>();
    for (OrderLibraryAliquot toOrderAli : to.getOrderLibraryAliquots()){
      if(!fromIds.contains(toOrderAli.getAliquot().getId())){
        changeLogService.create(
                to.createChangeLog("Removed Library Aliquot: " + toOrderAli.getAliquot().getAlias(),
                        "aliquotId",
                        authorizationManager.getCurrentUser())
        );
        removed.add(toOrderAli);
      }
    }
    to.getOrderLibraryAliquots().removeAll(removed);

    for (OrderLibraryAliquot fromOrderAli : from.getOrderLibraryAliquots()) {
      OrderLibraryAliquot toOrderAli = to.getOrderLibraryAliquots().stream()
          .filter(lib -> lib.getAliquot().getId() == fromOrderAli.getAliquot().getId())
          .findFirst().orElse(null);
      if (toOrderAli == null) {
        changeLogService.create(
                to.createChangeLog("Added Library Aliquot: " + fromOrderAli.getAliquot().getAlias(),
                        "aliquotId",
                        authorizationManager.getCurrentUser())
        );
        to.getOrderLibraryAliquots().add(fromOrderAli);
      } else {
        if(toOrderAli.getProportion() != fromOrderAli.getProportion()) changeLogService.create(
                to.createChangeLog(fromOrderAli.getAliquot().getAlias() + " proportion changed: "
                                + toOrderAli.getProportion() + " to " + fromOrderAli.getProportion(),
                        "aliquotId",
                        authorizationManager.getCurrentUser())
        );
        toOrderAli.setProportion(fromOrderAli.getProportion());
      }
    }

    if(from.getPool() == null && to.getPool() != null){
      changeLogService.create(to.createChangeLog("Pool unlinked.",
              "poolId",
              authorizationManager.getCurrentUser()));
    }else if(to.getPool() == null && from.getPool() != null){
      changeLogService.create(
              to.createChangeLog("Associated pool: " + from.getPool().getAlias(),
                      "poolId",
                      authorizationManager.getCurrentUser()));
    }
    to.setPool(from.getPool());

    if(from.getSequencingOrder() == null && to.getSequencingOrder() != null){
      changeLogService.create(to.createChangeLog("Sequencing order unlinked.",
              "sequencingOrderId",
              authorizationManager.getCurrentUser()));
    }else if(to.getSequencingOrder() == null && from.getSequencingOrder() != null){
      changeLogService.create(
              to.createChangeLog("Associated sequencing order.",
                      "sequencingOrderId",
                      authorizationManager.getCurrentUser()));
    }
    to.setSequencingOrder(from.getSequencingOrder());

    //check if there's an associated Pool and if it still matches the Order
    if(to.getPool() != null) poolService.checkMismatchedWithOrder(to.getPool());
  }

  @Override
  protected void beforeSave(PoolOrder object) throws IOException {
    object.setChangeDetails(authorizationManager.getCurrentUser());
  }

  @Override
  public void authorizeDeletion(PoolOrder object) throws IOException {
    authorizationManager.throwIfNonAdminOrMatchingOwner(object.getCreator());
  }

  @Override
  public ValidationResult validateDeletion(PoolOrder object) throws IOException {
    ValidationResult result = new ValidationResult();
    if (object.getPool() != null || object.getSequencingOrder() != null) {
      result.addError(new ValidationError("Cannot delete a fulfilled order"));
    }
    return result;
  }

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return poolOrderDao.count(errorHandler, filter);
  }

  @Override
  public List<PoolOrder> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter) throws IOException {
    return poolOrderDao.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }

}

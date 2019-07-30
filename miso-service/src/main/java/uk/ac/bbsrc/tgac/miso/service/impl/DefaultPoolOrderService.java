package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.OrderLibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrder;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryAliquotService;
import uk.ac.bbsrc.tgac.miso.core.service.OrderPurposeService;
import uk.ac.bbsrc.tgac.miso.core.service.PoolService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingOrderService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingParametersService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.persistence.PoolOrderDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;
import uk.ac.bbsrc.tgac.miso.service.PoolOrderService;
@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultPoolOrderService extends AbstractSaveService<PoolOrder> implements PoolOrderService {

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
    to.setPartitions(from.getPartitions());
    to.setParameters(from.getParameters());
    to.setAlias(from.getAlias());
    to.setPurpose(from.getPurpose());
    to.setDescription(from.getDescription());
    to.setDraft(from.isDraft());

    // remove from TO if library isn't in FROM
    // add to TO if library isn't in TO
    // update proportion in TO if different in FROM
    to.getOrderLibraryAliquots().removeIf(toOrderAli -> from.getOrderLibraryAliquots().stream()
        .noneMatch(fromOrderAli -> toOrderAli.getAliquot().getId() == fromOrderAli.getAliquot().getId()));
    for (OrderLibraryAliquot fromOrderAli : from.getOrderLibraryAliquots()) {
      OrderLibraryAliquot toOrderAli = to.getOrderLibraryAliquots().stream()
          .filter(lib -> lib.getAliquot().getId() == fromOrderAli.getAliquot().getId())
          .findFirst().orElse(null);
      if (toOrderAli == null) {
        to.getOrderLibraryAliquots().add(fromOrderAli);
      } else {
        toOrderAli.setProportion(fromOrderAli.getProportion());
      }
    }

    to.setPool(from.getPool());
    to.setSequencingOrder(from.getSequencingOrder());
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

package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.OrderPurpose;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.OrderPurposeService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.OrderPurposeDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultOrderPurposeService extends AbstractSaveService<OrderPurpose> implements OrderPurposeService {

  @Autowired
  private OrderPurposeDao orderPurposeDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private DeletionStore deletionStore;

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public SaveDao<OrderPurpose> getDao() {
    return orderPurposeDao;
  }

  @Override
  public List<OrderPurpose> list() throws IOException {
    return orderPurposeDao.list();
  }

  @Override
  protected void collectValidationErrors(OrderPurpose object, OrderPurpose beforeChange, List<ValidationError> errors) throws IOException {
    if (ValidationUtils.isSetAndChanged(OrderPurpose::getAlias, object, beforeChange)
        && orderPurposeDao.getByAlias(object.getAlias()) != null) {
      errors.add(new ValidationError("alias", "There is already an order purpose with this alias"));
    }
  }

  @Override
  protected void applyChanges(OrderPurpose to, OrderPurpose from) throws IOException {
    to.setAlias(from.getAlias());
  }

  @Override
  public ValidationResult validateDeletion(OrderPurpose object) throws IOException {
    ValidationResult result = new ValidationResult();
    long poolUsage = orderPurposeDao.getUsageByPoolOrders(object);
    long seqUsage = orderPurposeDao.getUsageBySequencingOrders(object);
    if (poolUsage > 0L || seqUsage > 0L) {
      result.addError(new ValidationError(String.format("Order purpose %s is used by %d pool %s and %d sequencing %s", object.getAlias(),
          poolUsage, Pluralizer.orders(poolUsage), seqUsage, Pluralizer.orders(seqUsage))));
    }
    return result;
  }

}

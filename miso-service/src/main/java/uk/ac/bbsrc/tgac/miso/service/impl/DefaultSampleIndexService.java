package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIndex;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.SampleIndexFamilyService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleIndexService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.persistence.SampleIndexDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultSampleIndexService extends AbstractSaveService<SampleIndex> implements SampleIndexService {

  @Autowired
  private SampleIndexDao sampleIndexDao;
  @Autowired
  private SampleIndexFamilyService sampleIndexFamilyService;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private TransactionTemplate transactionTemplate;
  @Autowired
  private DeletionStore deletionStore;

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }

  @Override
  public List<SampleIndex> listByIdList(List<Long> ids) throws IOException {
    return sampleIndexDao.listByIdList(ids);
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public List<SampleIndex> list() throws IOException {
    return sampleIndexDao.list();
  }

  @Override
  public SaveDao<SampleIndex> getDao() {
    return sampleIndexDao;
  }

  @Override
  protected void authorizeUpdate(SampleIndex object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void loadChildEntities(SampleIndex object) throws IOException {
    ValidationUtils.loadChildEntity(object::setFamily, object.getFamily(), sampleIndexFamilyService, "family");
  }

  @Override
  protected void collectValidationErrors(SampleIndex object, SampleIndex beforeChange, List<ValidationError> errors)
      throws IOException {
    if (ValidationUtils.isChanged(SampleIndex::getName, object, beforeChange)
        && sampleIndexDao.getByFamilyAndName(object.getFamily(), object.getName()) != null) {
      errors.add(ValidationError.forDuplicate("sample index", "name"));
    }
  }

  @Override
  protected void applyChanges(SampleIndex to, SampleIndex from) throws IOException {
    to.setName(from.getName());
  }

}

package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.transaction.support.TransactionTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ReferenceGenomeService;
import uk.ac.bbsrc.tgac.miso.core.service.ScientificNameService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.ReferenceGenomeDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultReferenceGenomeService extends AbstractSaveService<ReferenceGenome>
    implements ReferenceGenomeService {

  @Autowired
  private ReferenceGenomeDao referenceGenomeDao;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private ScientificNameService scientificNameService;
  @Autowired
  private TransactionTemplate transactionTemplate;

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  @Override
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }

  @Override
  public SaveDao<ReferenceGenome> getDao() {
    return referenceGenomeDao;
  }

  public void setReferenceGenomeDao(ReferenceGenomeDao referenceGenomeDao) {
    this.referenceGenomeDao = referenceGenomeDao;
  }

  @Override
  public List<ReferenceGenome> list() throws IOException {
    return referenceGenomeDao.list();
  }

  @Override
  public List<ReferenceGenome> listByIdList(List<Long> ids) throws IOException {
    return referenceGenomeDao.listByIdList(ids);
  }

  @Override
  protected void authorizeUpdate(ReferenceGenome object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void loadChildEntities(ReferenceGenome reference) throws IOException {
    ValidationUtils.loadChildEntity(reference::setDefaultScientificName, reference.getDefaultScientificName(),
        scientificNameService,"defaultScientificNameId");
  }

  @Override
  protected void collectValidationErrors(ReferenceGenome reference, ReferenceGenome beforeChange, List<ValidationError> errors) throws IOException {
    if (ValidationUtils.isSetAndChanged(ReferenceGenome::getAlias, reference, beforeChange)
        && referenceGenomeDao.getByAlias(reference.getAlias()) != null) {
      errors.add(new ValidationError("alias", "There is already a reference genome with this alias"));
    }
  }

  @Override
  protected void applyChanges(ReferenceGenome to, ReferenceGenome from) {
    to.setAlias(from.getAlias());
    to.setDefaultScientificName(from.getDefaultScientificName());
  }

  @Override
  public ValidationResult validateDeletion(ReferenceGenome object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = referenceGenomeDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.projects(usage)));
    }
    return result;
  }

}

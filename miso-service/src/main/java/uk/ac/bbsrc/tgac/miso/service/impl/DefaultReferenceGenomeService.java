package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultReferenceGenomeService implements ReferenceGenomeService {

  @Autowired
  private ReferenceGenomeDao referenceGenomeDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private DeletionStore deletionStore;

  @Autowired
  private ScientificNameService scientificNameService;

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

  public void setReferenceGenomeDao(ReferenceGenomeDao referenceGenomeDao) {
    this.referenceGenomeDao = referenceGenomeDao;
  }

  @Override
  public List<ReferenceGenome> list() throws IOException {
    return referenceGenomeDao.list();
  }

  @Override
  public ReferenceGenome get(long id) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return referenceGenomeDao.get(id);
  }

  @Override
  public long create(ReferenceGenome reference) throws IOException {
    authorizationManager.throwIfNonAdmin();
    ValidationUtils.loadChildEntity(reference::setDefaultScientificName, reference.getDefaultScientificName(), scientificNameService,
        "defaultScientificNameId");
    validateChange(reference, null);
    return referenceGenomeDao.create(reference);
  }

  @Override
  public long update(ReferenceGenome reference) throws IOException {
    authorizationManager.throwIfNonAdmin();
    ValidationUtils.loadChildEntity(reference::setDefaultScientificName, reference.getDefaultScientificName(), scientificNameService,
        "defaultScientificNameId");
    ReferenceGenome managed = get(reference.getId());
    validateChange(reference, managed);
    applyChanges(managed, reference);
    return referenceGenomeDao.update(managed);
  }

  private void validateChange(ReferenceGenome reference, ReferenceGenome beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();

    if (ValidationUtils.isSetAndChanged(ReferenceGenome::getAlias, reference, beforeChange)
        && referenceGenomeDao.getByAlias(reference.getAlias()) != null) {
      errors.add(new ValidationError("alias", "There is already a reference genome with this alias"));
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void applyChanges(ReferenceGenome to, ReferenceGenome from) {
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

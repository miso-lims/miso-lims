package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.ScientificName;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ScientificNameService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.core.util.TaxonomyUtils;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.persistence.ScientificNameDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultScientificNameService extends AbstractSaveService<ScientificName> implements ScientificNameService {

  @Autowired
  private ScientificNameDao scientificNameDao;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;

  @Value("${miso.taxonLookup.enabled:false}")
  private boolean taxonLookupEnabled;

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public List<ScientificName> list() throws IOException {
    return scientificNameDao.list();
  }

  @Override
  public SaveDao<ScientificName> getDao() {
    return scientificNameDao;
  }

  @Override
  protected void authorizeSave(ScientificName object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void collectValidationErrors(ScientificName object, ScientificName beforeChange, List<ValidationError> errors)
      throws IOException {
    if (ValidationUtils.isSetAndChanged(ScientificName::getAlias, object, beforeChange)) {
      if (scientificNameDao.getByAlias(object.getAlias()) != null) {
        errors.add(ValidationError.forDuplicate("scientific name", "alias"));
      }

      if (taxonLookupEnabled && TaxonomyUtils.checkScientificNameAtNCBI(object.getAlias()) == null) {
        errors.add(new ValidationError("alias", "This scientific name is not of a known taxonomy"));
      }
    }
  }

  @Override
  protected void applyChanges(ScientificName to, ScientificName from) throws IOException {
    to.setAlias(from.getAlias());
  }

  @Override
  public ValidationResult validateDeletion(ScientificName object) throws IOException {
    ValidationResult result = new ValidationResult();
    long sampleUsage = scientificNameDao.getUsageBySamples(object);
    if (sampleUsage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, sampleUsage, Pluralizer.samples(sampleUsage)));
    }
    long referenceGenomeUsage = scientificNameDao.getUsageByReferenceGenomes(object);
    if (referenceGenomeUsage > 0L) {
      result.addError(
          ValidationError.forDeletionUsage(object, referenceGenomeUsage, "reference " + Pluralizer.genomes(referenceGenomeUsage)));
    }
    return result;
  }

}

package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop.SopCategory;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.SopService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.persistence.SopDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;
import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.isChanged;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultSopService extends AbstractSaveService<Sop> implements SopService {

  @Autowired
  private SopDao sopDao;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;
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

  @Override
  public SaveDao<Sop> getDao() {
    return sopDao;
  }

  @Override
  public List<Sop> listByCategory(SopCategory category) throws IOException {
    return sopDao.listByCategory(category);
  }

  @Override
  public List<Sop> list() throws IOException {
    return sopDao.list();
  }

  @Override
  protected void authorizeUpdate(Sop object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void collectValidationErrors(Sop sop, Sop beforeChange, List<ValidationError> errors) throws IOException {
    if ((isChanged(Sop::getAlias, sop, beforeChange) || isChanged(Sop::getVersion, sop, beforeChange))
        && sopDao.get(sop.getCategory(), sop.getAlias(), sop.getVersion()) != null) {
      errors.add(ValidationError.forDuplicate("SOP", null, "alias and version"));
    }
  }

  @Override
  protected void applyChanges(Sop to, Sop from) throws IOException {
    to.setAlias(from.getAlias());
    to.setUrl(from.getUrl());
    to.setArchived(from.isArchived());
  }
  //09-24 Working on SOP deletion error bug.
  @Override
  public ValidationResult validateDeletion(Sop object) throws IOException {
    ValidationResult result = new ValidationResult();
    long sampleUsage = sopDao.getUsageBySamples(object);
    if (sampleUsage > 0) {
      result.addError(ValidationError.forDeletionUsage(object, sampleUsage, Pluralizer.samples(sampleUsage)));
      //result.addError(new ValidationError("Error is this"));
    }
    long libUsage = sopDao.getUsageByLibraries(object);
    if (libUsage > 0) {
      result.addError(ValidationError.forDeletionUsage(object, libUsage, Pluralizer.libraries(libUsage)));
    }
    long runUsage = sopDao.getUsageByRuns(object);
    if (runUsage > 0) {
      result.addError(ValidationError.forDeletionUsage(object, runUsage, Pluralizer.runs(runUsage)));
    }
    return result;
  }


  @Override
  public List<Sop> listByIdList(List<Long> ids) throws IOException {
    return sopDao.listByIdList(ids);
  }

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return sopDao.count(errorHandler, filter);
  }

  @Override
  public List<Sop> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol, PaginationFilter... filter)
      throws IOException {
    return sopDao.list(offset, limit, sortDir, sortCol, filter);
  }

  @Override
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }

}

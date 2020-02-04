package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.type.TissuePieceType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.TissuePieceTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissuePieceTypeDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultTissuePieceTypeService extends AbstractSaveService<TissuePieceType> implements TissuePieceTypeService {

  @Autowired
  private TissuePieceTypeDao tissuePieceTypeDao;

  @Autowired
  private DeletionStore deletionStore;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public SaveDao<TissuePieceType> getDao() {
    return tissuePieceTypeDao;
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public List<TissuePieceType> list() throws IOException {
    return tissuePieceTypeDao.list();
  }

  @Override
  protected void authorizeSave(TissuePieceType object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void collectValidationErrors(TissuePieceType tissuePieceType, TissuePieceType beforeChange, List<ValidationError> errors)
      throws IOException {
    if (ValidationUtils.isSetAndChanged(TissuePieceType::getName, tissuePieceType, beforeChange)
        && tissuePieceTypeDao.getByName(tissuePieceType.getName()) != null) {
      errors.add(new ValidationError("name", "There is already a tissue piece type with this name"));
    }
  }

  @Override
  protected void applyChanges(TissuePieceType to, TissuePieceType from) {
    to.setName(from.getName());
    to.setAbbreviation(from.getAbbreviation());
    to.setV2NamingCode(from.getV2NamingCode());
    to.setArchived(from.getArchived());
  }

  @Override
  public ValidationResult validateDeletion(TissuePieceType object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = tissuePieceTypeDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.tissuePiece(usage)));
    }
    return result;
  }

}

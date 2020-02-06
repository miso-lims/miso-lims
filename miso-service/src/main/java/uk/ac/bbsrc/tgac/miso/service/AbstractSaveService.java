package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationException;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.core.service.SaveService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;

public abstract class AbstractSaveService<T extends Identifiable> implements SaveService<T> {

  public abstract SaveDao<T> getDao();

  @Override
  public T get(long id) throws IOException {
    return getDao().get(id);
  }

  @Override
  public long create(T object) throws IOException {
    authorizeSave(object);
    loadChildEntities(object);
    beforeValidate(object);
    validateChange(object, null);
    beforeSave(object);
    long savedId = getDao().create(object);
    afterSave(object);
    return savedId;
  }

  @Override
  public long update(T object) throws IOException {
    T managed = get(object.getId());
    authorizeSave(managed);
    loadChildEntities(object);
    beforeValidate(object);
    validateChange(object, managed);
    applyChanges(managed, object);
    beforeSave(managed);
    long savedId = getDao().update(managed);
    afterSave(managed);
    return savedId;
  }

  /**
   * Check authorization to save object. Should throw {@link AuthorizationException} if the user is not authorized to save the object.
   * Default implementation does nothing, which means the save is always authorized
   * 
   * @param object object being saved
   * @throws IOException
   */
  protected void authorizeSave(T object) throws IOException {
    // do nothing
  }

  /**
   * Make any changes necessary to the object before validating. Default implementation does nothing
   * 
   * @param object object being saved
   * @throws IOException
   */
  protected void beforeValidate(T object) throws IOException {
    // do nothing
  }

  /**
   * Make any other changes necessary to the object before saving. This happens *after* validation. Default implementation does nothing
   * 
   * @param object object being saved
   * @throws IOException
   */
  protected void beforeSave(T object) throws IOException {
    // do nothing
  }

  /**
   * Perform any other actions necessary after saving. Default implementation does nothing
   * 
   * @param object object that has been saved
   * @throws IOException
   */
  protected void afterSave(T object) throws IOException {
    // do nothing
  }

  /**
   * Load any of the object's member entities from other services. Default implementation does nothing
   * 
   * @param object object being saved
   * @throws IOException
   */
  protected void loadChildEntities(T object) throws IOException {
    // do nothing
  }

  protected static <U extends Identifiable> void loadChildEntity(U member, Consumer<U> setter, ProviderService<U> service)
      throws IOException {
    if (member != null) {
      setter.accept(service.get(member.getId()));
    }
  }

  protected void validateChange(T object, T beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();
    collectValidationErrors(object, beforeChange, errors);
    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  protected abstract void collectValidationErrors(T object, T beforeChange, List<ValidationError> errors) throws IOException;

  protected abstract void applyChanges(T to, T from) throws IOException;

}

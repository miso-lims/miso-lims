package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationException;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

public interface DeleterService<T extends Deletable> extends ProviderService<T> {

  /**
   * Check if the current user is authorized to delete the provided item. Should throw {@link AuthorizationException} if the user is not
   * authorized. Default implementation only authorizes deletion if the current user is an admin
   * 
   * @param object item the user is attempting to delete
   */
  public default void authorizeDeletion(T object) throws IOException {
    getAuthorizationManager().throwIfNonAdmin();
  }

  /**
   * Check whether it is valid to delete the provided item. Default implementation performs no checks, and thus declares all deletions valid
   * 
   * @param object item the user is attempting to delete
   * @return a ValidationResult describing whether the item can be deleted. If the deletion is invalid, reasons are included
   */
  public default ValidationResult validateDeletion(T object) {
    return new ValidationResult();
  }

  public DeletionStore getDeletionStore();

  public AuthorizationManager getAuthorizationManager();

  public default void delete(T object) throws IOException {
    T managed = get(object.getId());
    authorizeDeletion(managed);
    validateDeletion(managed).throwIfInvalid();
    beforeDelete(managed);
    getDeletionStore().delete(managed, getAuthorizationManager().getCurrentUser());
    afterDelete(managed);
  }

  /**
   * Perform any pre-delete actions, such as updating lastModifier and timestamps of related entities which would be used for
   * database-generated changelog entries. Default implementation does nothing
   * 
   * @param object item that is to be deleted
   */
  public default void beforeDelete(T object) throws IOException {
    // do nothing
  }

  /**
   * Perform any additional delete actions, such as deleting attachments, after the main entity has been deleted. Default implementation
   * does nothing
   * 
   * @param object item that has been deleted
   */
  public default void afterDelete(T object) throws IOException {
    // do nothing
  }

  public default void bulkDelete(Collection<T> objects) throws IOException {
    List<T> managedObjects = new ArrayList<>();
    for (T object : objects) {
      managedObjects.add(get(object.getId()));
    }

    for (T object : managedObjects) {
      authorizeDeletion(object);
    }
    ValidationResult result = new ValidationResult();
    for (T object : managedObjects) {
      result.merge(validateDeletion(object));
    }
    result.throwIfInvalid();
    for (T object : managedObjects) {
      beforeDelete(object);
      getDeletionStore().delete(object, getAuthorizationManager().getCurrentUser());
      afterDelete(object);
    }
  }

}

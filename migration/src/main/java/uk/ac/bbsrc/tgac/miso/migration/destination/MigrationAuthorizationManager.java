package uk.ac.bbsrc.tgac.miso.migration.destination;

import java.io.IOException;
import java.util.Collection;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationException;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;

/**
 * Custom AuthorizationManager that sets the user to attribute migration to and authorizes that user to do everything. This basically
 * disables authorization checks and should only be used for migration
 */
public class MigrationAuthorizationManager implements AuthorizationManager {

  User migrationUser;
  
  /**
   * Creates a MigrationAuthorizationManager to perform migration as migrationUser
   * 
   * @param migrationUser
   */
  public MigrationAuthorizationManager(User migrationUser) {
    this.migrationUser = migrationUser;
  }
  
  @Override
  public User getCurrentUser() throws IOException {
    return migrationUser;
  }

  @Override
  public String getCurrentUsername() {
    return migrationUser.getLoginName();
  }

  @Override
  public boolean isUserAuthenticated() throws IOException {
    // auth disabled in this manager; allow all access
    return true;
  }

  @Override
  public void throwIfUnauthenticated() throws IOException, AuthorizationException {
    // auth disabled in this manager; do nothing
  }

  @Override
  public boolean isAdminUser() throws IOException {
    // auth disabled in this manager; allow all access
    return true;
  }

  @Override
  public boolean isInternalUser() throws IOException {
    // auth disabled in this manager; allow all access
    return true;
  }

  @Override
  public void throwIfNonAdmin() throws IOException, AuthorizationException {
    // auth disabled in this manager; do nothing
  }

  @Override
  public void throwIfNonAdminOrMatchingOwner(User owner) throws IOException, AuthorizationException {
    // auth disabled in this manager; do nothing
  }

  @Override
  public void throwIfNotInternal() throws IOException, AuthorizationException {
    // auth disabled in this manager; do nothing
  }

  @Override
  public void throwIfNotOwner(User owner) throws IOException {
    // auth disabled in this manager; do nothing
  }

  @Override
  public boolean isGroupMember(Group group) throws IOException {
    // auth disabled in this manager; allow all access
    return true;
  }

  @Override
  public void throwIfNonAdminOrGroupMember(Collection<Group> groups) throws IOException {
    // auth disabled in this manager; do nothing
  }

}

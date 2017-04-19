package uk.ac.bbsrc.tgac.miso.migration.destination;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationException;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

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
  public void throwIfNonAdmin() throws IOException, AuthorizationException {
    // auth disabled in this manager; do nothing
  }

  @Override
  public boolean readCheck(SecurableByProfile resource) throws IOException {
    // auth disabled in this manager; allow all access
    return true;
  }

  @Override
  public void throwIfNotReadable(SecurableByProfile resource) throws IOException, AuthorizationException {
    // auth disabled in this manager; do nothing
  }

  @Override
  public boolean writeCheck(SecurableByProfile resource) throws IOException {
    // auth disabled in this manager; allow all access
    return true;
  }

  @Override
  public void throwIfNotWritable(SecurableByProfile resource) throws IOException, AuthorizationException {
    // auth disabled in this manager; do nothing
  }

  @Override
  public <T extends SecurableByProfile> List<T> filterUnreadable(Collection<T> unfiltered) throws IOException, AuthorizationException {
    // auth disabled in this manager; allow all access
    return new ArrayList<>(unfiltered);
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
  public <T, P extends SecurableByProfile> List<T> filterUnreadable(Collection<T> unfiltered, Function<T, P> getOwner)
      throws IOException, AuthorizationException {
    // auth disabled in this manager; allow all access
    return new ArrayList<>(unfiltered);
  }

}

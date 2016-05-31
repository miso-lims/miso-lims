package uk.ac.bbsrc.tgac.miso.migration.destination;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationException;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

/**
 * Custom AuthorizationManager that sets the user to attribute migration to and authorizes that user to do everything.
 * This basically disables authorization checks and should only be used for migration
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
    return true;
  }

  @Override
  public void throwIfUnauthenticated() throws IOException, AuthorizationException {
    
  }

  @Override
  public boolean isAdminUser() throws IOException {
    return true;
  }

  @Override
  public void throwIfNonAdmin() throws IOException, AuthorizationException {
    
  }

  @Override
  public boolean readCheck(SecurableByProfile resource) throws IOException {
    return true;
  }

  @Override
  public void throwIfNotReadable(SecurableByProfile resource) throws IOException, AuthorizationException {
    
  }

  @Override
  public boolean writeCheck(SecurableByProfile resource) throws IOException {
    return true;
  }

  @Override
  public void throwIfNotWritable(SecurableByProfile resource) throws IOException, AuthorizationException {
    
  }

  @Override
  public <T extends SecurableByProfile> Set<T> filterUnreadable(Collection<T> unfiltered) throws IOException, AuthorizationException {
    return new HashSet<>(unfiltered);
  }

}

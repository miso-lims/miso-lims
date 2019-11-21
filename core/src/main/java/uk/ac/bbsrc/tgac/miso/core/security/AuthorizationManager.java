package uk.ac.bbsrc.tgac.miso.core.security;

import java.io.IOException;
import java.util.Collection;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.User;

/**
 * An implementation of this interface should be used to check user authorization before any read/write 
 * operations at the service-level
 */
public interface AuthorizationManager {
  
  /**
   * @return The current user, or null if no user is authenticated
   * @throws IOException if there is an error while looking up the current user. This does not indicate whether 
   * the user exists or is authenticated, but represents other errors occurring while checking these conditions
   */
  public User getCurrentUser() throws IOException;
  
  /**
   * @return the current user's full name, or "Unknown" if the current user's name cannot be determined
   */
  public String getCurrentUsername();
  
  /**
   * @return true if the current user is authenticated; false otherwise
   * @throws IOException if there is an error while looking up the current user. This does not indicate whether 
   * the user exists or is authenticated, but represents other errors occurring while checking these conditions
   */
  public boolean isUserAuthenticated() throws IOException;
  
  /**
   * Verifies that the current user is authenticated, and throws an AuthorizationException if not
   * 
   * @throws AuthorizationException if the current user is not authenticated
   * @throws IOException if there is an error while looking up the current user. This does not indicate whether 
   * the user exists or is authenticated, but represents other errors occurring while checking these conditions
   */
  public void throwIfUnauthenticated() throws IOException, AuthorizationException;
  
  /**
   * @return true if the current user is an admin; false otherwise
   * @throws IOException if there is an error while looking up the current user. This does not indicate whether 
   * the user exists or is authenticated, but represents other errors occurring while checking these conditions
   */
  public boolean isAdminUser() throws IOException;

  public boolean isInternalUser() throws IOException;

  /**
   * Verifies that the current user is an admin and throws an AuthorizationException if not
   * 
   * @throws AuthorizationException if the current user is not an admin
   * @throws IOException if there is an error while looking up the current user. This does not indicate whether 
   * the user exists or is authenticated, but represents other errors occurring while checking these conditions
   */
  public void throwIfNonAdmin() throws IOException, AuthorizationException;
  
  /**
   * Verifies that the current user is either an admin, or the specified owner of a supposed resource
   * 
   * @param owner the User to accept as valid
   * @throws AuthorizationException if the current user is neither an admin nor the specified owner
   * @throws IOException if there is an error while looking up the current user. This does not indicate whether
   *           the user exists or is authenticated, but represents other errors occurring while checking these conditions
   */
  public void throwIfNonAdminOrMatchingOwner(User owner) throws IOException, AuthorizationException;

  /**
   * Verifies that the current user is an internal user
   * 
   * @throws AuthorizationException if the current user is not an internal user
   * @throws IOException if there is an error while looking up the current user. This does not indicate whether
   *           the user exists or is authenticated, but represents other errors occurring while checking these conditions
   */
  public void throwIfNotInternal() throws IOException, AuthorizationException;

  public void throwIfNotOwner(User owner) throws IOException;

  public boolean isGroupMember(Group group) throws IOException;

  public void throwIfNonAdminOrGroupMember(Collection<Group> groups) throws IOException;

}

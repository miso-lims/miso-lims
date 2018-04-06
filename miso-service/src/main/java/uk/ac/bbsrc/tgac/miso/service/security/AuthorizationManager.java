package uk.ac.bbsrc.tgac.miso.service.security;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

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
   * Checks whether the current user has read permission on a resource
   * 
   * @param resource the object to check permissions for
   * @return true if the user has permission to view this object, or if object is null; false otherwise
   * @throws IOException if there is an error while looking up the current user. This does not indicate whether 
   * the user exists or is authenticated, but represents other errors occurring while checking these conditions
   */
  public boolean readCheck(SecurableByProfile resource) throws IOException;
  
  /**
   * Checks whether the provided user has read permission on a resource
   * 
   * @param resource the object on which to check permission
   * @param user the User for whom to check permission
   * @return true if the user has permission to view this object, or if object is null; false otherwise
   */
  public boolean readCheck(SecurableByProfile resource, User user);

  /**
   * Verifies that the current user has permission to view a resource and throws an AuthorizationException if not
   * 
   * @param resource the object to check permissions for
   * @throws AuthorizationException if the current user does not have read permission to the resource
   */
  public void throwIfNotReadable(SecurableByProfile resource) throws IOException, AuthorizationException;

  /**
   * Verifies that the provided user has permission to view a resource and throws an AuthorizationException if not
   * 
   * @param resource the object on which to check permissions
   * @param user the User for whom to check permission
   * @throws AuthorizationException if the current user does not have read permission to the resource
   */
  public void throwIfNotReadable(SecurableByProfile resource, User user) throws AuthorizationException;
  
  /**
   * Checks whether the current user has write permission on a resource
   * 
   * @param resource the object to check permissions for
   * @return true if the user has permission to modify this object; false otherwise
   * @throws IOException if there is an error while looking up the current user. This does not indicate whether 
   * the user exists or is authenticated, but represents other errors occurring while checking these conditions
   * @throws NullPointerException if resource is null
   */
  public boolean writeCheck(SecurableByProfile resource) throws IOException;
  
  /**
   * Checks whether the provided user has write permission on a resource
   * 
   * @param resource the object on which to check permissions
   * @param user the User for whom to check permission
   * @return true if the user has permission to modify this object; false otherwise
   * @throws NullPointerException if resource is null
   */
  public boolean writeCheck(SecurableByProfile resource, User user);

  /**
   * Verifies that the current user has permission to modify a resource and throws an AuthorizationException if not
   * 
   * @param resource the object to check permissions for
   * @throws AuthorizationException if the current user does not have write permission to the resource
   * @throws IOException if there is an error while looking up the current user. This does not indicate whether
   *           the user exists or is authenticated, but represents other errors occurring while checking these conditions
   */
  public void throwIfNotWritable(SecurableByProfile resource) throws IOException, AuthorizationException;
  
  /**
   * Verifies that the provided user has permission to modify a resource and throws an AuthorizationException if not
   * 
   * @param resource the object on which to check permissions
   * @param user the User for whom to check permissions
   * @throws AuthorizationException if the current user does not have write permission to the resource
   */
  public void throwIfNotWritable(SecurableByProfile resource, User user) throws AuthorizationException;

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

  /**
   * Determines which items in an unfiltered collection are readable by the current user
   * 
   * @param unfiltered the items to check permissions for
   * @return a List containing only the items to which the current user has read permission. If unfiltered is null, or there are no readable
   * items, an empty List is returned
   * @throws IOException if there is an error while looking up the current user. This does not indicate whether the user exists or is
   * authenticated, but represents other errors occurring while checking these conditions
   * @throws AuthorizationException if the current user is not authenticated
   */
  public <T extends SecurableByProfile> List<T> filterUnreadable(Collection<T> unfiltered) throws IOException, AuthorizationException;

  public <T, P extends SecurableByProfile> List<T> filterUnreadable(Collection<T> unfiltered, Function<T, P> getOwner)
      throws IOException, AuthorizationException;

  public void throwIfNotOwner(User owner) throws IOException;
}

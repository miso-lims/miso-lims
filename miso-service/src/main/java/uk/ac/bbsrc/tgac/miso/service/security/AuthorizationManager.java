package uk.ac.bbsrc.tgac.miso.service.security;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

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
   * Verifies that the current user has permission to view a resource and throws an AuthorizationException if not
   * 
   * @param resource the object to check permissions for
   * @throws AuthorizationException if the current user does not have read permission to the resource
   * @throws IOException if there is an error while looking up the current user. This does not indicate whether 
   * the user exists or is authenticated, but represents other errors occurring while checking these conditions
   */
  public void throwIfNotReadable(SecurableByProfile resource) throws IOException, AuthorizationException;
  
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
   * Verifies that the current user has permission to modify a resource and throws an AuthorizationException if not
   * 
   * @param resource the object to check permissions for
   * @throws AuthorizationException if the current user does not have write permission to the resource
   * @throws IOException if there is an error while looking up the current user. This does not indicate whether 
   * the user exists or is authenticated, but represents other errors occurring while checking these conditions
   */
  public void throwIfNotWritable(SecurableByProfile resource) throws IOException, AuthorizationException;
  
  /**
   * Determines which items in an unfiltered collection are readable by the current user
   * 
   * @param unfiltered the items to check permissions for
   * @return a Set containing only the items to which the current user has read permission. If unfiltered is null, 
   * or there are no readable items, an empty Set is returned
   * @throws IOException if there is an error while looking up the current user. This does not indicate whether 
   * the user exists or is authenticated, but represents other errors occurring while checking these conditions
   * @throws AuthorizationException if the current user is not authenticated
   */
  public <T extends SecurableByProfile> Set<T> filterUnreadable(Collection<T> unfiltered) throws IOException, AuthorizationException;
  
}

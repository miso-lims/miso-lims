package uk.ac.bbsrc.tgac.miso.service.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public class DefaultAuthorizationManager implements AuthorizationManager {

  public static final String UNKNOWN_USER = "Unknown";

  @Autowired
  private SecurityManager securityManager;

  private final SecurityContextHolderStrategy securityContextHolderStrategy;

  public DefaultAuthorizationManager() {
    this.securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
  }

  @Override
  public User getCurrentUser() throws IOException {
    Authentication auth = securityContextHolderStrategy.getContext().getAuthentication();
    if (auth == null) {
      return null;
    }
    User user = securityManager.getUserByLoginName(auth.getName());
    if (user == null && auth.isAuthenticated()) {
      user = new UserImpl();
      user.setAdmin(true);
      user.setActive(true);
    }
    return user;
  }

  @Override
  public String getCurrentUsername() {
    User user = null;
    try {
      user = getCurrentUser();
    } catch (IOException e) {
      user = null;
    }
    if (user == null || LimsUtils.isStringEmptyOrNull(user.getFullName())) {
      return UNKNOWN_USER;
    } else {
      return user.getFullName();
    }
  }

  @Override
  public boolean isUserAuthenticated() throws IOException {
    return getCurrentUser() != null;
  }

  @Override
  public void throwIfUnauthenticated() throws IOException, AuthorizationException {
    if (!isUserAuthenticated()) {
      throw new AuthorizationException("Current user is not authenticated");
    }
  }

  @Override
  public boolean isAdminUser() throws IOException {
    return getCurrentUser().isAdmin();
  }

  @Override
  public boolean isInternalUser() throws IOException {
    return getCurrentUser().isInternal();
  }

  @Override
  public void throwIfNonAdmin() throws IOException, AuthorizationException {
    if (!isAdminUser()) {
      throw new AuthorizationException("Current user is not admin");
    }
  }

  @Override
  public boolean readCheck(SecurableByProfile resource) throws IOException {
    if (resource == null) {
      return true;
    } else {
      return resource.userCanRead(getCurrentUser());
    }
  }

  @Override
  public void throwIfNotReadable(SecurableByProfile resource) throws IOException, AuthorizationException {
    if (!readCheck(resource)) {
      throw new AuthorizationException("Current user does not have permission to view this resource");
    }
  }

  @Override
  public boolean writeCheck(SecurableByProfile resource) throws IOException {
    return resource.userCanWrite(getCurrentUser());
  }

  @Override
  public void throwIfNotWritable(SecurableByProfile resource) throws IOException, AuthorizationException {
    if (!writeCheck(resource)) {
      throw new AuthorizationException("Current user does not have permission to modify this resource");
    }
  }

  @Override
  public <T, R extends SecurableByProfile> List<T> filterUnreadable(Collection<T> unfiltered, Function<T, R> getOwner) throws IOException {
    User currentUser = getCurrentUser();
    List<T> filtered = new ArrayList<>();
    if (unfiltered != null) {
      for (T item : unfiltered) {
        if (getOwner.apply(item).userCanRead(currentUser)) {
          filtered.add(item);
        }
      }
    }
    return filtered;
  }

  @Override
  public void throwIfNotInternal() throws IOException, AuthorizationException {
    if (!getCurrentUser().isInternal()) throw new AuthorizationException("Current user is not an internal user");
  }

  @Override
  public void throwIfNonAdminOrMatchingOwner(User owner) throws IOException, AuthorizationException {
    if (owner == null) {
      throwIfNonAdmin();
    } else {
      User currentUser = getCurrentUser();
      if (!(currentUser.isAdmin() || currentUser.getUserId().equals(owner.getUserId()))) {
        throw new AuthorizationException("Current user is not admin or owner");
      }
    }
  }

  @Override
  public <T extends SecurableByProfile> List<T> filterUnreadable(Collection<T> unfiltered) throws IOException, AuthorizationException {
    return filterUnreadable(unfiltered, x -> x);
  }

}

package uk.ac.bbsrc.tgac.miso.service.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationException;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.UserService;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public class DefaultAuthorizationManager implements AuthorizationManager {

  public static final String UNKNOWN_USER = "Unknown";

  @Autowired
  private UserService userService;

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
    User user = userService.getByLoginName(auth.getName());
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
  public void throwIfNotInternal() throws IOException, AuthorizationException {
    if (!getCurrentUser().isInternal()) throw new AuthorizationException("Current user is not an internal user");
  }

  @Override
  public void throwIfNonAdminOrMatchingOwner(User owner) throws IOException, AuthorizationException {
    if (owner == null) {
      throwIfNonAdmin();
    } else {
      User currentUser = getCurrentUser();
      if (!(currentUser.isAdmin() || currentUser.getId() == owner.getId())) {
        throw new AuthorizationException("Current user is not admin or owner");
      }
    }
  }

  @Override
  public void throwIfNotOwner(User owner) throws IOException {
    if (getCurrentUser().getId() != owner.getId()) {
      throw new AuthorizationException("Current user is not owner");
    }
  }
}

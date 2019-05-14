package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.service.UserService;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityStore;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultUserService implements UserService {

  @Autowired
  private SecurityStore securityStore;
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private AuthorizationManager authorizationManager;

  public void setSecurityStore(SecurityStore securityStore) {
    this.securityStore = securityStore;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @Override
  public long create(User user) throws IOException {
    authorizationManager.throwIfNonAdmin();
    return securityStore.saveUser(user);
  }

  @Override
  public long update(User user) throws IOException {
    User original = get(user.getId());
    authorizationManager.throwIfNonAdminOrMatchingOwner(original);
    validateChange(user, original);

    original.setFullName(user.getFullName());
    original.setEmail(user.getEmail());
    original.setFavouriteWorkflows(user.getFavouriteWorkflows());
    User currentUser = authorizationManager.getCurrentUser();
    if (currentUser.isAdmin()) {
      original.setExternal(user.isExternal());
      original.setInternal(user.isInternal());
      original.setRoles(user.getRoles());
      original.setLoginName(user.getLoginName());
      if (currentUser.getId() != original.getId()) {
        original.setActive(user.isActive());
        original.setAdmin(user.isAdmin());
      }
    }
    if (securityManager.isPasswordMutable()) {
      if (user.getPassword() != null) {
        original.setPassword(user.getPassword());
      }
    } else {
      original.setPassword(null);
    }
    return securityStore.saveUser(original);
  }

  private void validateChange(User user, User beforeChange) throws IOException {
    if (ValidationUtils.isSetAndChanged(User::getLoginName, user, beforeChange) && getByLoginName(user.getLoginName()) != null) {
      throw new ValidationException(new ValidationError("loginName", "There is already a user with this login name"));
    }
  }

  @Override
  public User get(long id) throws IOException {
    return securityStore.getUserById(id);
  }

  @Override
  public User getByLoginName(String loginName) throws IOException {
    return securityStore.getUserByLoginName(loginName);
  }

  @Override
  public List<User> list() throws IOException {
    return securityStore.listAllUsers();
  }

}

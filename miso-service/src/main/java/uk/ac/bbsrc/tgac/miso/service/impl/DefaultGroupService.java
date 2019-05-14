package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Group;

import uk.ac.bbsrc.tgac.miso.core.service.GroupService;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityStore;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultGroupService implements GroupService {

  @Autowired
  private SecurityStore securityStore;
  @Autowired
  private AuthorizationManager authorizationManager;

  public void setSecurityStore(SecurityStore securityStore) {
    this.securityStore = securityStore;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  @Override
  public long create(Group group) throws IOException {
    authorizationManager.throwIfNonAdmin();
    validateChange(group, null);
    return securityStore.saveGroup(group);
  }

  @Override
  public long update(Group group) throws IOException {
    authorizationManager.throwIfNonAdmin();
    Group managed = get(group.getId());
    validateChange(group, managed);
    applyChanges(group, managed);
    return securityStore.saveGroup(managed);
  }

  private void applyChanges(Group from, Group to) {
    to.setName(from.getName());
    to.setDescription(from.getDescription());
  }

  private void validateChange(Group group, Group beforeChange) throws IOException {
    if (ValidationUtils.isSetAndChanged(Group::getName, group, beforeChange) && securityStore.getGroupByName(group.getName()) != null) {
      throw new ValidationException(new ValidationError("name", "There is already a group with this name"));
    }
  }

  @Override
  public Group get(long id) throws IOException {
    return securityStore.getGroupById(id);
  }

  @Override
  public List<Group> list() throws IOException {
    return securityStore.listAllGroups();
  }

  @Override
  public void updateMembers(Group group) throws IOException {
    authorizationManager.throwIfNonAdmin();
    Group managed = get(group.getId());
    managed.setUsers(group.getUsers());
    securityStore.saveGroup(managed);
  }

}

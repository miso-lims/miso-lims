package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Group;

import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.GroupService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.SecurityStore;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultGroupService implements GroupService {

  @Autowired
  private SecurityStore securityStore;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;

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
    if (ValidationUtils.isSetAndChanged(Group::getName, group, beforeChange)
        && securityStore.getGroupByName(group.getName()) != null) {
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

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public ValidationResult validateDeletion(Group object) throws IOException {
    ValidationResult result = new ValidationResult();
    if (object.isBuiltIn()) {
      result.addError(new ValidationError("This group is built-in and required for MISO functionality"));
    }
    long usage = securityStore.getUsageByTransfers(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.transfers(usage)));
    }
    return result;
  }

}

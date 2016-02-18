package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Identity;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.persistence.IdentityDao;
import uk.ac.bbsrc.tgac.miso.service.IdentityService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional
@Service
public class DefaultIdentityService implements IdentityService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultIdentityService.class);

  @Autowired
  private IdentityDao identityDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public Identity get(Long identityId) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return identityDao.getIdentity(identityId);
  }

  @Override
  public Identity get(String externalName) {
    return identityDao.getIdentity(externalName);
  }

  @Override
  public Long create(Identity identity) throws IOException {
    authorizationManager.throwIfNonAdmin();
    User user = authorizationManager.getCurrentUser();
    identity.setCreatedBy(user);
    identity.setUpdatedBy(user);
    return identityDao.addIdentity(identity);
  }

  @Override
  public void update(Identity identity) throws IOException {
    authorizationManager.throwIfNonAdmin();
    Identity updatedIdentity = get(identity.getIdentityId());
    updatedIdentity.setInternalName(identity.getInternalName());
    updatedIdentity.setExternalName(identity.getExternalName());
    User user = authorizationManager.getCurrentUser();
    updatedIdentity.setUpdatedBy(user);
    identityDao.update(updatedIdentity);
  }

  @Override
  public Set<Identity> getAll() throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return Sets.newHashSet(identityDao.getIdentity());
  }

  @Override
  public void delete(Long identityId) throws IOException {
    authorizationManager.throwIfNonAdmin();
    Identity identity = get(identityId);
    identityDao.deleteIdentity(identity);
  }

}

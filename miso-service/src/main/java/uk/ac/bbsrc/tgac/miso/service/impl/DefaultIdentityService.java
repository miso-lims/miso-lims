package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Identity;
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

  public void setIdentityDao(IdentityDao identityDao) {
    this.identityDao = identityDao;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  @Override
  public Identity get(Long identityId) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return identityDao.getIdentity(identityId);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public Identity get(String externalName) {
    return identityDao.getIdentity(externalName);
  }
  
  @Override
  public void applyChanges(Identity target, Identity source) {
    target.setInternalName(source.getInternalName());
    target.setExternalName(source.getExternalName());
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

package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Identity;
import uk.ac.bbsrc.tgac.miso.persistence.IdentityDao;
import uk.ac.bbsrc.tgac.miso.service.IdentityService;

@Transactional
@Service
public class DefaultIdentityService implements IdentityService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultIdentityService.class);

  @Autowired
  private IdentityDao identityDao;

  @Autowired
  private com.eaglegenomics.simlims.core.manager.SecurityManager securityManager;

  @Override
  public Identity get(Long identityId) {
    return identityDao.getIdentity(identityId);
  }

  @Override
  public Long create(Identity identity) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    identity.setCreatedBy(user);
    identity.setUpdatedBy(user);
    return identityDao.addIdentity(identity);
  }

  @Override
  public void update(Identity identity) throws IOException {
    Identity updatedIdentity = get(identity.getIdentityId());
    updatedIdentity.setInternalName(identity.getInternalName());
    updatedIdentity.setExternalName(identity.getExternalName());
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    updatedIdentity.setUpdatedBy(user);
    identityDao.update(updatedIdentity);
  }

  @Override
  public Set<Identity> getAll() {
    return Sets.newHashSet(identityDao.getIdentity());
  }

  @Override
  public void delete(Long identityId) {
    Identity identity = get(identityId);
    identityDao.deleteIdentity(identity);
  }

}

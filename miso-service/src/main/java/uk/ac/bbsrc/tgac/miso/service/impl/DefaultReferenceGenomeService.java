package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;
import uk.ac.bbsrc.tgac.miso.persistence.ReferenceGenomeDao;
import uk.ac.bbsrc.tgac.miso.service.ReferenceGenomeService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultReferenceGenomeService implements ReferenceGenomeService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultReferenceGenomeService.class);

  @Autowired
  private ReferenceGenomeDao referenceGenomeDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public Collection<ReferenceGenome> listAllReferenceGenomeTypes() throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return referenceGenomeDao.listAllReferenceGenomeTypes();
  }

  @Override
  public ReferenceGenome get(Long id) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return referenceGenomeDao.getReferenceGenome(id);
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  public void setReferenceGenomeDao(ReferenceGenomeDao referenceGenomeDao) {
    this.referenceGenomeDao = referenceGenomeDao;
  }

}

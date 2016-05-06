package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.persistence.TissueOriginDao;
import uk.ac.bbsrc.tgac.miso.service.TissueOriginService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional
@Service
public class DefaultTissueOriginService implements TissueOriginService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultTissueOriginService.class);

  @Autowired
  private TissueOriginDao tissueOriginDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public TissueOrigin get(Long tissueOriginId) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return tissueOriginDao.getTissueOrigin(tissueOriginId);
  }

  @Override
  public Long create(TissueOrigin tissueOrigin) throws IOException {
    authorizationManager.throwIfNonAdmin();
    User user = authorizationManager.getCurrentUser();
    log.error("user name : " + user.getFullName());
    log.error("user : " + user);
    tissueOrigin.setCreatedBy(user);
    tissueOrigin.setUpdatedBy(user);

    return tissueOriginDao.addTissueOrigin(tissueOrigin);
  }

  @Override
  public void update(TissueOrigin tissueOrigin) throws IOException {
    authorizationManager.throwIfNonAdmin();
    TissueOrigin updatedTissueOrigin = get(tissueOrigin.getId());
    log.error("update tissueOrigin: " + updatedTissueOrigin);
    updatedTissueOrigin.setAlias(tissueOrigin.getAlias());
    updatedTissueOrigin.setDescription(tissueOrigin.getDescription());
    User user = authorizationManager.getCurrentUser();
    updatedTissueOrigin.setUpdatedBy(user);
    tissueOriginDao.update(updatedTissueOrigin);
  }

  @Override
  public Set<TissueOrigin> getAll() throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return Sets.newHashSet(tissueOriginDao.getTissueOrigin());
  }

  @Override
  public void delete(Long tissueOriginId) throws IOException {
    authorizationManager.throwIfNonAdmin();
    TissueOrigin tissueOrigin = get(tissueOriginId);
    tissueOriginDao.deleteTissueOrigin(tissueOrigin);
  }

}

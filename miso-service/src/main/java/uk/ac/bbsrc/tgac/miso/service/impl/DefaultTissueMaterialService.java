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

import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;
import uk.ac.bbsrc.tgac.miso.persistence.TissueMaterialDao;
import uk.ac.bbsrc.tgac.miso.service.TissueMaterialService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultTissueMaterialService implements TissueMaterialService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultTissueMaterialService.class);

  @Autowired
  private TissueMaterialDao tissueMaterialDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public TissueMaterial get(Long tissueMaterialId) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return tissueMaterialDao.getTissueMaterial(tissueMaterialId);
  }

  @Override
  public Long create(TissueMaterial tissueMaterial) throws IOException {
    authorizationManager.throwIfNotInternal();
    User user = authorizationManager.getCurrentUser();
    tissueMaterial.setCreatedBy(user);
    tissueMaterial.setUpdatedBy(user);
    return tissueMaterialDao.addTissueMaterial(tissueMaterial);
  }

  @Override
  public void update(TissueMaterial tissueMaterial) throws IOException {
    authorizationManager.throwIfNonAdmin();
    TissueMaterial updatedTissueMaterial = get(tissueMaterial.getId());
    updatedTissueMaterial.setAlias(tissueMaterial.getAlias());
    User user = authorizationManager.getCurrentUser();
    updatedTissueMaterial.setUpdatedBy(user);
    tissueMaterialDao.update(updatedTissueMaterial);
  }

  @Override
  public Set<TissueMaterial> getAll() throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return Sets.newHashSet(tissueMaterialDao.getTissueMaterial());
  }

  @Override
  public void delete(Long tissueMaterialId) throws IOException {
    authorizationManager.throwIfNonAdmin();
    TissueMaterial tissueMaterial = get(tissueMaterialId);
    tissueMaterialDao.deleteTissueMaterial(tissueMaterial);
  }

}

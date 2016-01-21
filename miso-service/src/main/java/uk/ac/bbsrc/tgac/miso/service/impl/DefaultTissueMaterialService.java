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

import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;
import uk.ac.bbsrc.tgac.miso.persistence.TissueMaterialDao;
import uk.ac.bbsrc.tgac.miso.service.TissueMaterialService;

@Transactional
@Service
public class DefaultTissueMaterialService implements TissueMaterialService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultTissueMaterialService.class);

  @Autowired
  private TissueMaterialDao tissueMaterialDao;

  @Autowired
  private com.eaglegenomics.simlims.core.manager.SecurityManager securityManager;

  @Override
  public TissueMaterial get(Long tissueMaterialId) {
    return tissueMaterialDao.getTissueMaterial(tissueMaterialId);
  }

  @Override
  public Long create(TissueMaterial tissueMaterial) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    tissueMaterial.setCreatedBy(user);
    tissueMaterial.setUpdatedBy(user);
    return tissueMaterialDao.addTissueMaterial(tissueMaterial);
  }

  @Override
  public void update(TissueMaterial tissueMaterial) throws IOException {
    TissueMaterial updatedTissueMaterial = get(tissueMaterial.getTissueMaterialId());
    updatedTissueMaterial.setAlias(tissueMaterial.getAlias());
    updatedTissueMaterial.setDescription(tissueMaterial.getDescription());
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    updatedTissueMaterial.setUpdatedBy(user);
    tissueMaterialDao.update(updatedTissueMaterial);
  }

  @Override
  public Set<TissueMaterial> getAll() {
    return Sets.newHashSet(tissueMaterialDao.getTissueMaterial());
  }

  @Override
  public void delete(Long tissueMaterialId) {
    TissueMaterial tissueMaterial = get(tissueMaterialId);
    tissueMaterialDao.deleteTissueMaterial(tissueMaterial);
  }

}

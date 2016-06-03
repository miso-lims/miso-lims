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

import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.persistence.TissueTypeDao;
import uk.ac.bbsrc.tgac.miso.service.TissueTypeService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultTissueTypeService implements TissueTypeService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultTissueTypeService.class);

  @Autowired
  private TissueTypeDao tissueTypeDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public TissueType get(Long tissueTypeId) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return tissueTypeDao.getTissueType(tissueTypeId);
  }

  @Override
  public Long create(TissueType tissueType) throws IOException {
    authorizationManager.throwIfNonAdmin();
    User user = authorizationManager.getCurrentUser();
    tissueType.setCreatedBy(user);
    tissueType.setUpdatedBy(user);
    return tissueTypeDao.addTissueType(tissueType);
  }

  @Override
  public void update(TissueType tissueType) throws IOException {
    authorizationManager.throwIfNonAdmin();
    TissueType updatedTissueType = get(tissueType.getId());
    updatedTissueType.setAlias(tissueType.getAlias());
    updatedTissueType.setDescription(tissueType.getDescription());
    User user = authorizationManager.getCurrentUser();
    updatedTissueType.setUpdatedBy(user);
    tissueTypeDao.update(updatedTissueType);
  }

  @Override
  public Set<TissueType> getAll() throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return Sets.newHashSet(tissueTypeDao.getTissueType());
  }

  @Override
  public void delete(Long tissueTypeId) throws IOException {
    authorizationManager.throwIfNonAdmin();
    TissueType tissueType = get(tissueTypeId);
    tissueTypeDao.deleteTissueType(tissueType);
  }

}

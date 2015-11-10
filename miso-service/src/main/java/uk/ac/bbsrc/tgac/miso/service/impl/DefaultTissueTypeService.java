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

import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.persistence.TissueTypeDao;
import uk.ac.bbsrc.tgac.miso.service.TissueTypeService;

@Transactional
@Service
public class DefaultTissueTypeService implements TissueTypeService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultTissueTypeService.class);

  @Autowired
  private TissueTypeDao tissueTypeDao;

  @Autowired
  private com.eaglegenomics.simlims.core.manager.SecurityManager securityManager;

  @Override
  public TissueType get(Long tissueTypeId) {
    return tissueTypeDao.getTissueType(tissueTypeId);
  }

  @Override
  public Long create(TissueType tissueType) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    tissueType.setCreatedBy(user);
    tissueType.setUpdatedBy(user);
    return tissueTypeDao.addTissueType(tissueType);
  }

  @Override
  public void update(TissueType tissueType) throws IOException {
    TissueType updatedTissueType = get(tissueType.getTissueTypeId());
    updatedTissueType.setAlias(tissueType.getAlias());
    updatedTissueType.setDescription(tissueType.getDescription());
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    updatedTissueType.setUpdatedBy(user);
    tissueTypeDao.update(updatedTissueType);
  }

  @Override
  public Set<TissueType> getAll() {
    return Sets.newHashSet(tissueTypeDao.getTissueType());
  }

  @Override
  public void delete(Long tissueTypeId) {
    TissueType tissueType = get(tissueTypeId);
    tissueTypeDao.deleteTissueType(tissueType);
  }

}

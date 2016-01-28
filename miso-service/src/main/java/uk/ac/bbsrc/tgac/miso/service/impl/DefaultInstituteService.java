package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Institute;
import uk.ac.bbsrc.tgac.miso.persistence.InstituteDao;
import uk.ac.bbsrc.tgac.miso.service.InstituteService;

import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Sets;

@Transactional
@Service
public class DefaultInstituteService implements InstituteService {
  
  protected static final Logger log = LoggerFactory.getLogger(DefaultInstituteService.class);
  
  @Autowired
  private InstituteDao instituteDao;
  
  @Autowired
  private com.eaglegenomics.simlims.core.manager.SecurityManager securityManager;

  @Override
  public Institute get(Long id) {
    return instituteDao.getInstitute(id);
  }

  @Override
  public Long create(Institute institute) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    institute.setCreatedBy(user);
    institute.setUpdatedBy(user);
    return instituteDao.addInstitute(institute);
  }

  @Override
  public void update(Institute institute) throws IOException {
    Institute updatedInstitute = get(institute.getId());
    updatedInstitute.setAlias(institute.getAlias());
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    updatedInstitute.setUpdatedBy(user);
    instituteDao.update(updatedInstitute);
  }

  @Override
  public Set<Institute> getAll() {
    return Sets.newHashSet(instituteDao.getInstitute());
  }

  @Override
  public void delete(Long instituteId) {
    Institute institute = get(instituteId);
    instituteDao.deleteInstitute(institute);
  }

}

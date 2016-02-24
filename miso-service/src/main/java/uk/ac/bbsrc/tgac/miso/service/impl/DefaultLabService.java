package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.persistence.InstituteDao;
import uk.ac.bbsrc.tgac.miso.persistence.LabDao;
import uk.ac.bbsrc.tgac.miso.service.LabService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional
@Service
public class DefaultLabService implements LabService {

  @Autowired
  private LabDao labDao;

  @Autowired
  private InstituteDao instituteDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public Lab get(Long id) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return labDao.getLab(id);
  }

  @Override
  public Long create(Lab lab, Long instituteId) throws IOException {
    authorizationManager.throwIfNonAdmin();
    User user = authorizationManager.getCurrentUser();
    lab.setCreatedBy(user);
    lab.setUpdatedBy(user);
    lab.setInstitute(instituteDao.getInstitute(instituteId));
    return labDao.addLab(lab);
  }

  @Override
  public void update(Lab lab, Long instituteId) throws IOException {
    authorizationManager.throwIfNonAdmin();
    Lab updatedLab = get(lab.getId());
    updatedLab.setAlias(lab.getAlias());
    updatedLab.setInstitute(instituteDao.getInstitute(instituteId));
    User user = authorizationManager.getCurrentUser();
    updatedLab.setUpdatedBy(user);
    labDao.update(updatedLab);
  }

  @Override
  public Set<Lab> getAll() throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return Sets.newHashSet(labDao.getLabs());
  }

  @Override
  public void delete(Long labId) throws IOException {
    authorizationManager.throwIfNonAdmin();
    Lab lab = get(labId);
    labDao.deleteLab(lab);
  }

}

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

import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.persistence.SampleClassDao;
import uk.ac.bbsrc.tgac.miso.service.SampleClassService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultSampleClassService implements SampleClassService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultSampleClassService.class);

  @Autowired
  private SampleClassDao sampleClassDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  public void setSampleClassDao(SampleClassDao sampleClassDao) {
    this.sampleClassDao = sampleClassDao;
  }

  @Override
  public SampleClass get(Long sampleClassId) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return sampleClassDao.getSampleClass(sampleClassId);
  }

  @Override
  public Long create(SampleClass sampleClass) throws IOException {
    authorizationManager.throwIfNonAdmin();
    User user = authorizationManager.getCurrentUser();
    sampleClass.setCreatedBy(user);
    sampleClass.setUpdatedBy(user);
    return sampleClassDao.addSampleClass(sampleClass);
  }

  @Override
  public void update(SampleClass sampleClass) throws IOException {
    authorizationManager.throwIfNonAdmin();
    SampleClass updatedSampleClass = get(sampleClass.getId());
    updatedSampleClass.setAlias(sampleClass.getAlias());
    updatedSampleClass.setSampleCategory(sampleClass.getSampleCategory());
    updatedSampleClass.setSuffix(sampleClass.getSuffix());
    User user = authorizationManager.getCurrentUser();
    updatedSampleClass.setUpdatedBy(user);
    sampleClassDao.update(updatedSampleClass);
  }

  @Override
  public Set<SampleClass> getAll() throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return Sets.newHashSet(sampleClassDao.getSampleClass());
  }

  @Override
  public void delete(Long sampleClassId) throws IOException {
    authorizationManager.throwIfNonAdmin();
    SampleClass sampleClass = get(sampleClassId);
    sampleClassDao.deleteSampleClass(sampleClass);
  }

}

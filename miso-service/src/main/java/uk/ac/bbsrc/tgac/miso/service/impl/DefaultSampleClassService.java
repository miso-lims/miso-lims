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

import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.persistence.SampleClassDao;
import uk.ac.bbsrc.tgac.miso.service.SampleClassService;

@Transactional
@Service
public class DefaultSampleClassService implements SampleClassService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultSampleClassService.class);

  @Autowired
  private SampleClassDao sampleClassDao;

  @Autowired
  private com.eaglegenomics.simlims.core.manager.SecurityManager securityManager;

  @Override
  public SampleClass get(Long sampleClassId) {
    return sampleClassDao.getSampleClass(sampleClassId);
  }

  @Override
  public Long create(SampleClass sampleClass) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    sampleClass.setCreatedBy(user);
    sampleClass.setUpdatedBy(user);
    return sampleClassDao.addSampleClass(sampleClass);
  }

  @Override
  public void update(SampleClass sampleClass) throws IOException {
    SampleClass updatedSampleClass = get(sampleClass.getSampleClassId());
    updatedSampleClass.setAlias(sampleClass.getAlias());
    updatedSampleClass.setSampleCategory(sampleClass.getSampleCategory());
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    updatedSampleClass.setUpdatedBy(user);
    sampleClassDao.update(updatedSampleClass);
  }

  @Override
  public Set<SampleClass> getAll() {
    return Sets.newHashSet(sampleClassDao.getSampleClass());
  }

  @Override
  public void delete(Long sampleClassId) {
    SampleClass sampleClass = get(sampleClassId);
    sampleClassDao.deleteSampleClass(sampleClass);
  }

}

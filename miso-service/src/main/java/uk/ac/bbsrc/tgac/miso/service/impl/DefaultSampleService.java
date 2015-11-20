package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.persistence.SampleDao;
import uk.ac.bbsrc.tgac.miso.service.SampleService;

@Transactional
@Service
public class DefaultSampleService implements SampleService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultSampleService.class);

  @Autowired
  private SampleDao sampleDao;

  @Autowired
  private com.eaglegenomics.simlims.core.manager.SecurityManager securityManager;

  @Override
  public Sample get(Long sampleId) {
    return sampleDao.getSample(sampleId);
  }

  @Override
  public Long create(Sample sample) throws IOException {
    // User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    // sample.setCreatedBy(user);
    // sample.setUpdatedBy(user);
    return sampleDao.addSample(sample);
  }

  @Override
  public void update(Sample sample) throws IOException {
    Sample updatedSample = get(sample.getSampleId());
    updatedSample.setAlias(sample.getAlias());
    updatedSample.setDescription(sample.getDescription());
    // User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    // updatedSample.setUpdatedBy(user);
    sampleDao.update(updatedSample);
  }

  @Override
  public Set<Sample> getAll() {
    return Sets.newHashSet(sampleDao.getSample());
  }

  @Override
  public void delete(Long sampleId) {
    Sample sample = get(sampleId);
    sampleDao.deleteSample(sample);
  }

}

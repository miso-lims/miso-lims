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

import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;
import uk.ac.bbsrc.tgac.miso.persistence.SamplePurposeDao;
import uk.ac.bbsrc.tgac.miso.service.SamplePurposeService;

@Transactional
@Service
public class DefaultSamplePurposeService implements SamplePurposeService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultSamplePurposeService.class);

  @Autowired
  private SamplePurposeDao samplePurposeDao;

  @Autowired
  private com.eaglegenomics.simlims.core.manager.SecurityManager securityManager;

  @Override
  public SamplePurpose get(Long samplePurposeId) {
    return samplePurposeDao.getSamplePurpose(samplePurposeId);
  }

  @Override
  public Long create(SamplePurpose samplePurpose) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    samplePurpose.setCreatedBy(user);
    samplePurpose.setUpdatedBy(user);
    return samplePurposeDao.addSamplePurpose(samplePurpose);
  }

  @Override
  public void update(SamplePurpose samplePurpose) throws IOException {
    SamplePurpose updatedSamplePurpose = get(samplePurpose.getSamplePurposeId());
    updatedSamplePurpose.setAlias(samplePurpose.getAlias());
    updatedSamplePurpose.setDescription(samplePurpose.getDescription());
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    updatedSamplePurpose.setUpdatedBy(user);
    samplePurposeDao.update(updatedSamplePurpose);
  }

  @Override
  public Set<SamplePurpose> getAll() {
    return Sets.newHashSet(samplePurposeDao.getSamplePurpose());
  }

  @Override
  public void delete(Long samplePurposeId) {
    SamplePurpose samplePurpose = get(samplePurposeId);
    samplePurposeDao.deleteSamplePurpose(samplePurpose);
  }

}

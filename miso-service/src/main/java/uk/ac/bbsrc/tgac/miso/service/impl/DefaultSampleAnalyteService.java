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

import uk.ac.bbsrc.tgac.miso.core.data.SampleAnalyte;
import uk.ac.bbsrc.tgac.miso.persistence.SampleAnalyteDao;
import uk.ac.bbsrc.tgac.miso.service.SampleAnalyteService;

@Transactional
@Service
public class DefaultSampleAnalyteService implements SampleAnalyteService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultSampleAnalyteService.class);

  @Autowired
  private SampleAnalyteDao sampleAnalyteDao;

  @Autowired
  private com.eaglegenomics.simlims.core.manager.SecurityManager securityManager;

  @Override
  public SampleAnalyte get(Long sampleAnalyteId) {
    return sampleAnalyteDao.getSampleAnalyte(sampleAnalyteId);
  }

  @Override
  public Long create(SampleAnalyte sampleAnalyte) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    sampleAnalyte.setCreatedBy(user);
    sampleAnalyte.setUpdatedBy(user);
    return sampleAnalyteDao.addSampleAnalyte(sampleAnalyte);
  }

  @Override
  public void update(SampleAnalyte sampleAnalyte) throws IOException {
    SampleAnalyte updatedSampleAnalyte = get(sampleAnalyte.getSampleAnalyteId());
    updatedSampleAnalyte.setStockNumber(sampleAnalyte.getStockNumber());
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    updatedSampleAnalyte.setUpdatedBy(user);
    sampleAnalyteDao.update(updatedSampleAnalyte);
  }

  @Override
  public Set<SampleAnalyte> getAll() {
    return Sets.newHashSet(sampleAnalyteDao.getSampleAnalyte());
  }

  @Override
  public void delete(Long sampleAnalyteId) {
    SampleAnalyte sampleAnalyte = get(sampleAnalyteId);
    sampleAnalyteDao.deleteSampleAnalyte(sampleAnalyte);
  }

}

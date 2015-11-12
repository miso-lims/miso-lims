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

import uk.ac.bbsrc.tgac.miso.core.data.SampleAdditionalInfo;
import uk.ac.bbsrc.tgac.miso.persistence.SampleAdditionalInfoDao;
import uk.ac.bbsrc.tgac.miso.service.SampleAdditionalInfoService;

@Transactional
@Service
public class DefaultSampleAdditionalInfoService implements SampleAdditionalInfoService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultSampleAdditionalInfoService.class);

  @Autowired
  private SampleAdditionalInfoDao sampleAdditionalInfoDao;

  @Autowired
  private com.eaglegenomics.simlims.core.manager.SecurityManager securityManager;

  @Override
  public SampleAdditionalInfo get(Long sampleAdditionalInfoId) {
    return sampleAdditionalInfoDao.getSampleAdditionalInfo(sampleAdditionalInfoId);
  }

  @Override
  public Long create(SampleAdditionalInfo sampleAdditionalInfo) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    sampleAdditionalInfo.setCreatedBy(user);
    sampleAdditionalInfo.setUpdatedBy(user);
    return sampleAdditionalInfoDao.addSampleAdditionalInfo(sampleAdditionalInfo);
  }

  @Override
  public void update(SampleAdditionalInfo sampleAdditionalInfo) throws IOException {
    SampleAdditionalInfo updatedSampleAdditionalInfo = get(sampleAdditionalInfo.getSampleAdditionalInfoId());
    updatedSampleAdditionalInfo.setPassageNumber(sampleAdditionalInfo.getPassageNumber());
    updatedSampleAdditionalInfo.setTimesReceived(sampleAdditionalInfo.getTimesReceived());
    updatedSampleAdditionalInfo.setTubeNumber(sampleAdditionalInfo.getTubeNumber());
    updatedSampleAdditionalInfo.setVolume(sampleAdditionalInfo.getVolume());
    updatedSampleAdditionalInfo.setConcentration(sampleAdditionalInfo.getConcentration());
    updatedSampleAdditionalInfo.setArchived(sampleAdditionalInfo.getArchived());
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    updatedSampleAdditionalInfo.setUpdatedBy(user);
    sampleAdditionalInfoDao.update(updatedSampleAdditionalInfo);
  }

  @Override
  public Set<SampleAdditionalInfo> getAll() {
    return Sets.newHashSet(sampleAdditionalInfoDao.getSampleAdditionalInfo());
  }

  @Override
  public void delete(Long sampleAdditionalInfoId) {
    SampleAdditionalInfo sampleAdditionalInfo = get(sampleAdditionalInfoId);
    sampleAdditionalInfoDao.deleteSampleAdditionalInfo(sampleAdditionalInfo);
  }

}

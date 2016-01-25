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

import uk.ac.bbsrc.tgac.miso.core.data.QcPassedDetail;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAdditionalInfo;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;
import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.persistence.QcPassedDetailDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleAdditionalInfoDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleClassDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleDao;
import uk.ac.bbsrc.tgac.miso.persistence.SubprojectDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueOriginDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueTypeDao;
import uk.ac.bbsrc.tgac.miso.service.SampleAdditionalInfoService;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLKitDAO;

@Transactional
@Service
public class DefaultSampleAdditionalInfoService implements SampleAdditionalInfoService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultSampleAdditionalInfoService.class);

  @Autowired
  private SampleAdditionalInfoDao sampleAdditionalInfoDao;

  @Autowired
  private SampleDao sampleDao;

  @Autowired
  private TissueOriginDao tissueOriginDao;

  @Autowired
  private TissueTypeDao tissueTypeDao;

  @Autowired
  private QcPassedDetailDao qcPassedDetailDao;

  @Autowired
  private SubprojectDao subprojectDao;

  @Autowired
  private SQLKitDAO sqlKitDao;

  @Autowired
  private SampleClassDao sampleClassDao;

  @Autowired
  private com.eaglegenomics.simlims.core.manager.SecurityManager securityManager;

  @Override
  public SampleAdditionalInfo get(Long sampleAdditionalInfoId) {
    return sampleAdditionalInfoDao.getSampleAdditionalInfo(sampleAdditionalInfoId);
  }

  @Override
  public Long create(SampleAdditionalInfo sampleAdditionalInfo, Long sampleId, Long tissueOriginId, Long tissueTypeId,
      Long qcPassedDetailId, Long subprojectId, Long prepKitId, Long sampleClassId) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    Sample sample = sampleDao.getSample(sampleId);
    sampleAdditionalInfo.setSample(sample);

    if (tissueOriginId != null) {
      TissueOrigin tissueOrigin = tissueOriginDao.getTissueOrigin(tissueOriginId);
      sampleAdditionalInfo.setTissueOrigin(tissueOrigin);
    }
    if (tissueTypeId != null) {
      TissueType tissueType = tissueTypeDao.getTissueType(tissueTypeId);
      sampleAdditionalInfo.setTissueType(tissueType);
    }
    if (qcPassedDetailId != null) {
      QcPassedDetail qcPassedDetail = qcPassedDetailDao.getQcPassedDetails(qcPassedDetailId);
      sampleAdditionalInfo.setQcPassedDetail(qcPassedDetail);
    }
    if (subprojectId != null) {
      Subproject subproject = subprojectDao.getSubproject(subprojectId);
      sampleAdditionalInfo.setSubproject(subproject);
    }
    if (prepKitId != null) {
      // Need to resolve which kit we need to refer to in this case.
      // Kit prepKit = sqlKitDao.get(prepKitId);
      // sampleAdditionalInfo.setPrepKit(prepKit);
    }
    if (sampleClassId != null) {
      SampleClass sampleClass = sampleClassDao.getSampleClass(sampleClassId);
      sampleAdditionalInfo.setSampleClass(sampleClass);
    }

    sampleAdditionalInfo.setCreatedBy(user);
    sampleAdditionalInfo.setUpdatedBy(user);
    return sampleAdditionalInfoDao.addSampleAdditionalInfo(sampleAdditionalInfo);
  }

  @Override
  public void update(SampleAdditionalInfo sampleAdditionalInfo, Long tissueOriginId, Long tissueTypeId, Long qcPassedDetailId,
      Long prepKitId, Long sampleClassId) throws IOException {
    SampleAdditionalInfo updatedSampleAdditionalInfo = get(sampleAdditionalInfo.getSampleAdditionalInfoId());

    TissueOrigin tissueOrigin = null;
    TissueType tissueType = null;
    QcPassedDetail qcPassedDetail = null;
    SampleClass sampleClass = null;
    if (tissueOriginId != null) {
      tissueOrigin = tissueOriginDao.getTissueOrigin(tissueOriginId);
    }
    if (tissueTypeId != null) {
      tissueType = tissueTypeDao.getTissueType(tissueTypeId);
    }
    if (qcPassedDetailId != null) {
      qcPassedDetail = qcPassedDetailDao.getQcPassedDetails(qcPassedDetailId);
    }
    if (prepKitId != null) {
      // Need to resolve which kit we need to refer to in this case.
      // Kit prepKit = sqlKitDao.get(prepKitId);
      // sampleAdditionalInfo.setPrepKit(prepKit);
    }
    if (sampleClassId != null) {
      sampleClass = sampleClassDao.getSampleClass(sampleClassId);
    }
    updatedSampleAdditionalInfo.setTissueOrigin(tissueOrigin);
    updatedSampleAdditionalInfo.setTissueType(tissueType);
    updatedSampleAdditionalInfo.setQcPassedDetail(qcPassedDetail);
    updatedSampleAdditionalInfo.setSampleClass(sampleClass);

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

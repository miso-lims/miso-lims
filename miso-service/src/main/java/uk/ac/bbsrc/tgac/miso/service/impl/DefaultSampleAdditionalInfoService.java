package uk.ac.bbsrc.tgac.miso.service.impl;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.data.QcPassedDetail;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAdditionalInfo;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;
import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SampleAdditionalInfoDto;
import uk.ac.bbsrc.tgac.miso.persistence.QcPassedDetailDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleAdditionalInfoDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleClassDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleDao;
import uk.ac.bbsrc.tgac.miso.persistence.SubprojectDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueOriginDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueTypeDao;
import uk.ac.bbsrc.tgac.miso.service.LabService;
import uk.ac.bbsrc.tgac.miso.service.SampleAdditionalInfoService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;
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
  private AuthorizationManager authorizationManager;

  @Autowired
  private LabService labService;

  @Override
  public SampleAdditionalInfo get(Long sampleAdditionalInfoId) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return sampleAdditionalInfoDao.getSampleAdditionalInfo(sampleAdditionalInfoId);
  }

  @Override
  public Long create(SampleAdditionalInfo sampleAdditionalInfo, Long sampleId, Long tissueOriginId, Long tissueTypeId,
      Long qcPassedDetailId, Long subprojectId, Long prepKitId, Long sampleClassId) throws IOException {
    authorizationManager.throwIfNonAdmin();
    User user = authorizationManager.getCurrentUser();
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
      KitDescriptor prepKit = sqlKitDao.getKitDescriptorById(prepKitId);
      sampleAdditionalInfo.setPrepKit(prepKit);
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
  @Transactional(propagation = Propagation.REQUIRED)
  public Long create(SampleAdditionalInfoDto sampleAdditionalInfoDto) throws IOException {
    authorizationManager.throwIfNonAdmin();
    User user = authorizationManager.getCurrentUser();

    SampleAdditionalInfo sampleAdditionalInfo = Dtos.to(sampleAdditionalInfoDto);
    sampleAdditionalInfo.setCreatedBy(user);
    sampleAdditionalInfo.setUpdatedBy(user);

    sampleAdditionalInfo.setSampleClass(sampleClassDao.getSampleClass(sampleAdditionalInfoDto.getSampleClassId()));

    if (sampleAdditionalInfoDto.getTissueOriginId() != null) {
      sampleAdditionalInfo.setTissueOrigin(tissueOriginDao.getTissueOrigin(sampleAdditionalInfoDto.getTissueOriginId()));
    }
    if (sampleAdditionalInfoDto.getTissueTypeId() != null) {
      sampleAdditionalInfo.setTissueType(tissueTypeDao.getTissueType(sampleAdditionalInfoDto.getTissueTypeId()));
    }
    if (sampleAdditionalInfoDto.getQcPassedDetailId() != null) {
      sampleAdditionalInfo.setQcPassedDetail(qcPassedDetailDao.getQcPassedDetails(sampleAdditionalInfoDto.getQcPassedDetailId()));
    }
    if (sampleAdditionalInfoDto.getSubprojectId() != null) {
      sampleAdditionalInfo.setSubproject(subprojectDao.getSubproject(sampleAdditionalInfoDto.getSubprojectId()));
    }
    if (sampleAdditionalInfoDto.getPrepKitId() != null) {
      sampleAdditionalInfo.setPrepKit(sqlKitDao.getKitDescriptorById(sampleAdditionalInfoDto.getPrepKitId()));
    }

    return sampleAdditionalInfoDao.addSampleAdditionalInfo(sampleAdditionalInfo);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public SampleAdditionalInfo to(SampleAdditionalInfoDto sampleAdditionalInfoDto) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    checkArgument(sampleAdditionalInfoDto.getSampleClassId() != null,
        "A SampleAdditionalInfo.sampleClassId must be provided to construct SampleAdditionalInfo.");
    User user = authorizationManager.getCurrentUser();

    SampleAdditionalInfo sampleAdditionalInfo = Dtos.to(sampleAdditionalInfoDto);
    sampleAdditionalInfo.setCreatedBy(user);
    sampleAdditionalInfo.setUpdatedBy(user);
    Date now = new Date();
    sampleAdditionalInfo.setCreationDate(now);
    sampleAdditionalInfo.setLastUpdated(now);

    SampleClass sampleClass = sampleClassDao.getSampleClass(sampleAdditionalInfoDto.getSampleClassId());
    ServiceUtils.throwIfNull(sampleClass, "SampleAdditionalInfo.sampleClassId", sampleAdditionalInfoDto.getSampleClassId());
    sampleAdditionalInfo.setSampleClass(sampleClass);

    if (sampleAdditionalInfoDto.getTissueOriginId() != null) {
      TissueOrigin tissueOrigin = tissueOriginDao.getTissueOrigin(sampleAdditionalInfoDto.getTissueOriginId());
      ServiceUtils.throwIfNull(tissueOrigin, "SampleAdditionalInfo.tissueOriginId", sampleAdditionalInfoDto.getTissueOriginId());
      sampleAdditionalInfo.setTissueOrigin(tissueOrigin);
    }
    if (sampleAdditionalInfoDto.getTissueTypeId() != null) {
      TissueType tissueType = tissueTypeDao.getTissueType(sampleAdditionalInfoDto.getTissueTypeId());
      ServiceUtils.throwIfNull(tissueType, "SampleAdditionalInfo.tissueTypeId", sampleAdditionalInfoDto.getTissueTypeId());
      sampleAdditionalInfo.setTissueType(tissueType);
    }
    if (sampleAdditionalInfoDto.getQcPassedDetailId() != null) {
      QcPassedDetail qcPassedDetail = qcPassedDetailDao.getQcPassedDetails(sampleAdditionalInfoDto.getQcPassedDetailId());
      ServiceUtils.throwIfNull(qcPassedDetail, "SampleAdditionalInfo.qcPassedDetailId", sampleAdditionalInfoDto.getQcPassedDetailId());
      sampleAdditionalInfo.setQcPassedDetail(qcPassedDetail);
    }
    if (sampleAdditionalInfoDto.getSubprojectId() != null) {
      Subproject subproject = subprojectDao.getSubproject(sampleAdditionalInfoDto.getSubprojectId());
      ServiceUtils.throwIfNull(subproject, "SampleAdditionalInfo.subprojectId", sampleAdditionalInfoDto.getSubprojectId());
      sampleAdditionalInfo.setSubproject(subproject);
    }
    if (sampleAdditionalInfoDto.getPrepKitId() != null) {
      KitDescriptor kitDescriptor = sqlKitDao.getKitDescriptorById(sampleAdditionalInfoDto.getPrepKitId());
      ServiceUtils.throwIfNull(kitDescriptor, "SampleAdditionalInfo.prepKitId", sampleAdditionalInfoDto.getPrepKitId());
      sampleAdditionalInfo.setPrepKit(kitDescriptor);
    }

    if (sampleAdditionalInfoDto.getLabId() != null) {
      Lab lab = labService.get(sampleAdditionalInfoDto.getLabId());
      ServiceUtils.throwIfNull(lab, "SampleAdditionalInfo.labId", sampleAdditionalInfoDto.getLabId());
      sampleAdditionalInfo.setLab(labService.get(sampleAdditionalInfoDto.getLabId()));
    }
    return sampleAdditionalInfo;
  }

  @Override
  public void update(SampleAdditionalInfo sampleAdditionalInfo, Long tissueOriginId, Long tissueTypeId, Long qcPassedDetailId,
      Long prepKitId, Long sampleClassId) throws IOException {
    authorizationManager.throwIfNonAdmin();
    SampleAdditionalInfo updatedSampleAdditionalInfo = get(sampleAdditionalInfo.getSampleAdditionalInfoId());

    TissueOrigin tissueOrigin = null;
    TissueType tissueType = null;
    QcPassedDetail qcPassedDetail = null;
    SampleClass sampleClass = null;
    KitDescriptor prepKit = null;
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
      prepKit = sqlKitDao.getKitDescriptorById(prepKitId);
    }
    if (sampleClassId != null) {
      sampleClass = sampleClassDao.getSampleClass(sampleClassId);
    }
    updatedSampleAdditionalInfo.setTissueOrigin(tissueOrigin);
    updatedSampleAdditionalInfo.setTissueType(tissueType);
    updatedSampleAdditionalInfo.setQcPassedDetail(qcPassedDetail);
    updatedSampleAdditionalInfo.setSampleClass(sampleClass);
    updatedSampleAdditionalInfo.setPrepKit(prepKit);

    updatedSampleAdditionalInfo.setPassageNumber(sampleAdditionalInfo.getPassageNumber());
    updatedSampleAdditionalInfo.setTimesReceived(sampleAdditionalInfo.getTimesReceived());
    updatedSampleAdditionalInfo.setTubeNumber(sampleAdditionalInfo.getTubeNumber());
    updatedSampleAdditionalInfo.setVolume(sampleAdditionalInfo.getVolume());
    updatedSampleAdditionalInfo.setConcentration(sampleAdditionalInfo.getConcentration());
    updatedSampleAdditionalInfo.setArchived(sampleAdditionalInfo.getArchived());
    User user = authorizationManager.getCurrentUser();
    updatedSampleAdditionalInfo.setUpdatedBy(user);
    sampleAdditionalInfoDao.update(updatedSampleAdditionalInfo);
  }

  @Override
  public Set<SampleAdditionalInfo> getAll() throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return Sets.newHashSet(sampleAdditionalInfoDao.getSampleAdditionalInfo());
  }

  @Override
  public void delete(Long sampleAdditionalInfoId) throws IOException {
    authorizationManager.throwIfNonAdmin();
    SampleAdditionalInfo sampleAdditionalInfo = get(sampleAdditionalInfoId);
    sampleAdditionalInfoDao.deleteSampleAdditionalInfo(sampleAdditionalInfo);
  }

}

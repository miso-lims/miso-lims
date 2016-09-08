package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.SampleAdditionalInfo;
import uk.ac.bbsrc.tgac.miso.persistence.QcPassedDetailDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleAdditionalInfoDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleClassDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleDao;
import uk.ac.bbsrc.tgac.miso.persistence.SubprojectDao;
import uk.ac.bbsrc.tgac.miso.service.SampleAdditionalInfoService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLKitDAO;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultSampleAdditionalInfoService implements SampleAdditionalInfoService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultSampleAdditionalInfoService.class);

  @Autowired
  private SampleAdditionalInfoDao sampleAdditionalInfoDao;

  @Autowired
  private SampleDao sampleDao;

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

  public void setSampleAdditionalInfoDao(SampleAdditionalInfoDao sampleAdditionalInfoDao) {
    this.sampleAdditionalInfoDao = sampleAdditionalInfoDao;
  }

  public void setSampleDao(SampleDao sampleDao) {
    this.sampleDao = sampleDao;
  }

  public void setQcPassedDetailDao(QcPassedDetailDao qcPassedDetailDao) {
    this.qcPassedDetailDao = qcPassedDetailDao;
  }

  public void setSubprojectDao(SubprojectDao subprojectDao) {
    this.subprojectDao = subprojectDao;
  }

  public void setSqlKitDao(SQLKitDAO sqlKitDao) {
    this.sqlKitDao = sqlKitDao;
  }

  public void setSampleClassDao(SampleClassDao sampleClassDao) {
    this.sampleClassDao = sampleClassDao;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  @Override
  public SampleAdditionalInfo get(Long sampleAdditionalInfoId) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return sampleAdditionalInfoDao.getSampleAdditionalInfo(sampleAdditionalInfoId);
  }

  @Override
  public void applyChanges(SampleAdditionalInfo target, SampleAdditionalInfo source) throws IOException {
    target.setArchived(source.getArchived());
    target.setGroupDescription(source.getGroupDescription());
    target.setGroupId(source.getGroupId());
    loadMembers(target, source);
  }

  @Override
  public void loadMembers(SampleAdditionalInfo target) throws IOException {
    loadMembers(target, target);
  }

  @Override
  public void loadMembers(SampleAdditionalInfo target, SampleAdditionalInfo source) throws IOException {
    if (source.getQcPassedDetail() != null) {
      target.setQcPassedDetail(qcPassedDetailDao.getQcPassedDetails(source.getQcPassedDetail().getId()));
      ServiceUtils.throwIfNull(target.getQcPassedDetail(), "qcPassedDetailId", source.getQcPassedDetail().getId());
    } else {
      target.setQcPassedDetail(null);
    }
    if (source.getPrepKit() != null) {
      target.setPrepKit(sqlKitDao.getKitDescriptorById(source.getPrepKit().getId()));
      ServiceUtils.throwIfNull(target.getPrepKit(), "prepKitId", source.getPrepKit().getId());
    }
    if (source.getSampleClass() != null) {
      target.setSampleClass(sampleClassDao.getSampleClass(source.getSampleClass().getId()));
      ServiceUtils.throwIfNull(target.getSampleClass(), "sampleClassId", source.getSampleClass().getId());
    }
    if (source.getSubproject() != null) {
      target.setSubproject(subprojectDao.getSubproject(source.getSubproject().getId()));
      ServiceUtils.throwIfNull(target.getSubproject(), "subprojectId", source.getSubproject().getId());
    }
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

package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.persistence.DetailedQcStatusDao;
import uk.ac.bbsrc.tgac.miso.persistence.DetailedSampleDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleClassDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleDao;
import uk.ac.bbsrc.tgac.miso.persistence.SubprojectDao;
import uk.ac.bbsrc.tgac.miso.service.DetailedSampleService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLKitDAO;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultDetailedSampleService implements DetailedSampleService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultDetailedSampleService.class);

  @Autowired
  private DetailedSampleDao detailedSampleDao;

  @Autowired
  private SampleDao sampleDao;

  @Autowired
  private DetailedQcStatusDao detailedQcStatusDao;

  @Autowired
  private SubprojectDao subprojectDao;

  @Autowired
  private SQLKitDAO sqlKitDao;

  @Autowired
  private SampleClassDao sampleClassDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  public void setDetailedSampleDao(DetailedSampleDao detailedSampleDao) {
    this.detailedSampleDao = detailedSampleDao;
  }

  public void setSampleDao(SampleDao sampleDao) {
    this.sampleDao = sampleDao;
  }

  public void setDetailedQcStatusDao(DetailedQcStatusDao detailedQcStatusDao) {
    this.detailedQcStatusDao = detailedQcStatusDao;
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
  public DetailedSample get(Long detailedSampleId) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return detailedSampleDao.getDetailedSample(detailedSampleId);
  }

  @Override
  public void applyChanges(DetailedSample target, DetailedSample source) throws IOException {
    target.setArchived(source.getArchived());
    target.setGroupDescription(source.getGroupDescription());
    target.setGroupId(source.getGroupId());
    target.setDetailedQcStatusNote(source.getDetailedQcStatusNote());
    loadMembers(target, source);
  }

  @Override
  public void loadMembers(DetailedSample target) throws IOException {
    loadMembers(target, target);
  }

  @Override
  public void loadMembers(DetailedSample target, DetailedSample source) throws IOException {
    if (source.getDetailedQcStatus() != null) {
      target.setDetailedQcStatus(detailedQcStatusDao.getDetailedQcStatus(source.getDetailedQcStatus().getId()));
      ServiceUtils.throwIfNull(target.getDetailedQcStatus(), "detailedQcStatusId", source.getDetailedQcStatus().getId());
    } else {
      target.setDetailedQcStatus(null);
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
  public Set<DetailedSample> getAll() throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return Sets.newHashSet(detailedSampleDao.getDetailedSample());
  }

  @Override
  public void delete(Long detailedSampleId) throws IOException {
    authorizationManager.throwIfNonAdmin();
    DetailedSample detailedSample = get(detailedSampleId);
    detailedSampleDao.deleteDetailedSample(detailedSample);
  }

}

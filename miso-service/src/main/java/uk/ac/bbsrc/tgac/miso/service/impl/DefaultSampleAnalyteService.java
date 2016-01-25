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

import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAnalyte;
import uk.ac.bbsrc.tgac.miso.core.data.SampleGroupId;
import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;
import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;
import uk.ac.bbsrc.tgac.miso.persistence.SampleAnalyteDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleGroupDao;
import uk.ac.bbsrc.tgac.miso.persistence.SamplePurposeDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueMaterialDao;
import uk.ac.bbsrc.tgac.miso.service.SampleAnalyteService;

@Transactional
@Service
public class DefaultSampleAnalyteService implements SampleAnalyteService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultSampleAnalyteService.class);

  @Autowired
  private SampleAnalyteDao sampleAnalyteDao;

  @Autowired
  private SampleDao sampleDao;

  @Autowired
  private SamplePurposeDao samplePurposeDao;

  @Autowired
  private SampleGroupDao sampleGroupDao;

  @Autowired
  private TissueMaterialDao tissueMaterialDao;

  @Autowired
  private com.eaglegenomics.simlims.core.manager.SecurityManager securityManager;

  @Override
  public SampleAnalyte get(Long sampleAnalyteId) {
    return sampleAnalyteDao.getSampleAnalyte(sampleAnalyteId);
  }

  @Override
  public Long create(SampleAnalyte sampleAnalyte, Long sampleId, Long samplePurposeId, Long sampleGroupId, Long tissueMaterialId)
      throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    Sample sample = sampleDao.getSample(sampleId);

    sampleAnalyte.setCreatedBy(user);
    sampleAnalyte.setUpdatedBy(user);
    sampleAnalyte.setSample(sample);

    if (samplePurposeId != null) {
      SamplePurpose samplePurpose = samplePurposeDao.getSamplePurpose(samplePurposeId);
      sampleAnalyte.setSamplePurpose(samplePurpose);
    }
    if (sampleGroupId != null) {
      SampleGroupId sampleGroup = sampleGroupDao.getSampleGroup(sampleGroupId);
      sampleAnalyte.setSampleGroup(sampleGroup);
    }
    if (tissueMaterialId != null) {
      TissueMaterial tissueMaterial = tissueMaterialDao.getTissueMaterial(tissueMaterialId);
      sampleAnalyte.setTissueMaterial(tissueMaterial);
    }
    return sampleAnalyteDao.addSampleAnalyte(sampleAnalyte);
  }

  @Override
  public void update(SampleAnalyte sampleAnalyte, Long samplePurposeId, Long sampleGroupId, Long tissueMaterialId) throws IOException {
    SampleAnalyte updatedSampleAnalyte = get(sampleAnalyte.getSampleAnalyteId());
    updatedSampleAnalyte.setRegion(sampleAnalyte.getRegion());
    updatedSampleAnalyte.setTubeId(sampleAnalyte.getTubeId());
    updatedSampleAnalyte.setStockNumber(sampleAnalyte.getStockNumber());
    updatedSampleAnalyte.setAliquotNumber(sampleAnalyte.getAliquotNumber());

    SamplePurpose samplePurpose = null;
    SampleGroupId sampleGroup = null;
    TissueMaterial tissueMaterial = null;
    if (samplePurposeId != null) {
      samplePurpose = samplePurposeDao.getSamplePurpose(samplePurposeId);
    }
    if (sampleGroupId != null) {
      sampleGroup = sampleGroupDao.getSampleGroup(sampleGroupId);
    }
    if (tissueMaterialId != null) {
      tissueMaterial = tissueMaterialDao.getTissueMaterial(tissueMaterialId);
    }
    updatedSampleAnalyte.setSamplePurpose(samplePurpose);
    updatedSampleAnalyte.setSampleGroup(sampleGroup);
    updatedSampleAnalyte.setTissueMaterial(tissueMaterial);

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

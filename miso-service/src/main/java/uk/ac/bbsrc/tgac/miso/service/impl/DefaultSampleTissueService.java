package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.persistence.SampleTissueDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueOriginDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueTypeDao;
import uk.ac.bbsrc.tgac.miso.service.LabService;
import uk.ac.bbsrc.tgac.miso.service.SampleTissueService;
import uk.ac.bbsrc.tgac.miso.service.TissueMaterialService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultSampleTissueService implements SampleTissueService {
  protected static final Logger log = LoggerFactory.getLogger(DefaultSampleTissueService.class);
  @Autowired
  private SampleTissueDao sampleTissueDao;
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private TissueOriginDao tissueOriginDao;
  @Autowired
  private TissueTypeDao tissueTypeDao;
  @Autowired
  private LabService labService;
  @Autowired
  private TissueMaterialService tissueMaterialService;

  @Override
  public void delete(Long sampleTissueId) {
    SampleTissue sampleTissue = get(sampleTissueId);
    sampleTissueDao.deleteSampleTissue(sampleTissue);
  }

  @Override
  public SampleTissue get(Long sampleTissueId) {
    return sampleTissueDao.getSampleTissue(sampleTissueId);
  }

  @Override
  public Set<SampleTissue> getAll() {
    return Sets.newHashSet(sampleTissueDao.getSampleTissue());
  }

  public SampleTissueDao getSampleTissueDao() {
    return sampleTissueDao;
  }

  public SecurityManager getSecurityManager() {
    return securityManager;
  }

  public void setSampleTissueDao(SampleTissueDao sampleTissueDao) {
    this.sampleTissueDao = sampleTissueDao;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setTissueOriginDao(TissueOriginDao tissueOriginDao) {
    this.tissueOriginDao = tissueOriginDao;
  }

  public void setTissueTypeDao(TissueTypeDao tissueTypeDao) {
    this.tissueTypeDao = tissueTypeDao;
  }

  public void setLabService(LabService labService) {
    this.labService = labService;
  }

  @Override
  public void loadMembers(SampleTissue target, SampleTissue source) throws IOException {
    if (source.getTissueOrigin() != null) {
      target.setTissueOrigin(tissueOriginDao.getTissueOrigin(source.getTissueOrigin().getId()));
      ServiceUtils.throwIfNull(target.getTissueOrigin(), "DetailedSample.tissueOriginId", source.getTissueOrigin().getId());
    }
    if (source.getTissueType() != null) {
      target.setTissueType(tissueTypeDao.getTissueType(source.getTissueType().getId()));
      ServiceUtils.throwIfNull(target.getTissueType(), "DetailedSample.tissueTypeId", source.getTissueType().getId());
    }
    if (source.getLab() != null) {
      target.setLab(labService.get(source.getLab().getId()));
      ServiceUtils.throwIfNull(target.getLab(), "DetailedSample.labId", source.getLab().getId());
    }
    if (source.getTissueMaterial() != null) {
      target.setTissueMaterial(tissueMaterialService.get(source.getTissueMaterial().getId()));
    }
  }

  @Override
  public void applyChanges(SampleTissue target, SampleTissue source) throws IOException {
    loadMembers(target, source);
    target.setPassageNumber(source.getPassageNumber());
    target.setTimesReceived(source.getTimesReceived());
    target.setTubeNumber(source.getTubeNumber());
    target.setExternalInstituteIdentifier(source.getExternalInstituteIdentifier());
    target.setRegion(source.getRegion());
  }

}

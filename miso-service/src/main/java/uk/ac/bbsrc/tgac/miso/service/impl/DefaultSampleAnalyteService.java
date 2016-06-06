package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.SampleAnalyte;
import uk.ac.bbsrc.tgac.miso.persistence.SampleAnalyteDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleDao;
import uk.ac.bbsrc.tgac.miso.persistence.SamplePurposeDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueMaterialDao;
import uk.ac.bbsrc.tgac.miso.service.SampleAnalyteService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

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
  private TissueMaterialDao tissueMaterialDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  public void setSampleAnalyteDao(SampleAnalyteDao sampleAnalyteDao) {
    this.sampleAnalyteDao = sampleAnalyteDao;
  }

  public void setSampleDao(SampleDao sampleDao) {
    this.sampleDao = sampleDao;
  }

  public void setSamplePurposeDao(SamplePurposeDao samplePurposeDao) {
    this.samplePurposeDao = samplePurposeDao;
  }

  public void setTissueMaterialDao(TissueMaterialDao tissueMaterialDao) {
    this.tissueMaterialDao = tissueMaterialDao;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  @Override
  public SampleAnalyte get(Long sampleAnalyteId) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return sampleAnalyteDao.getSampleAnalyte(sampleAnalyteId);
  }

  @Override
  public void applyChanges(SampleAnalyte target, SampleAnalyte source) throws IOException {
    target.setRegion(source.getRegion());
    target.setTubeId(source.getTubeId());
    target.setStrStatus(source.getStrStatus());
    loadMembers(target, source);
  }

  @Override
  public Set<SampleAnalyte> getAll() throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return Sets.newHashSet(sampleAnalyteDao.getSampleAnalyte());
  }

  @Override
  public void delete(Long sampleAnalyteId) throws IOException {
    authorizationManager.throwIfNonAdmin();
    SampleAnalyte sampleAnalyte = get(sampleAnalyteId);
    sampleAnalyteDao.deleteSampleAnalyte(sampleAnalyte);
  }

  @Override
  public void loadMembers(SampleAnalyte target) throws IOException {
    loadMembers(target, target);
  }

  @Override
  public void loadMembers(SampleAnalyte target, SampleAnalyte source) throws IOException {
    if (source.getSamplePurpose() != null) {
      target.setSamplePurpose(samplePurposeDao.getSamplePurpose(source.getSamplePurpose().getId()));
    }
    if (source.getTissueMaterial() != null) {
      target.setTissueMaterial(tissueMaterialDao.getTissueMaterial(source.getTissueMaterial().getId()));
    }
  }

}

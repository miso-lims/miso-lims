package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAnalyte;
import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;
import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SampleAnalyteDto;
import uk.ac.bbsrc.tgac.miso.persistence.SampleAnalyteDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleDao;
import uk.ac.bbsrc.tgac.miso.persistence.SamplePurposeDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueMaterialDao;
import uk.ac.bbsrc.tgac.miso.service.SampleAnalyteService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
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
  public Long create(SampleAnalyte sampleAnalyte) throws IOException {
    authorizationManager.throwIfNonAdmin();
    User user = authorizationManager.getCurrentUser();
    Sample sample = sampleDao.getSample(sampleAnalyte.getId());

    sampleAnalyte.setCreatedBy(user);
    sampleAnalyte.setUpdatedBy(user);
    sampleAnalyte.setSample(sample);

    loadMembers(sampleAnalyte);
    return sampleAnalyteDao.addSampleAnalyte(sampleAnalyte);
  }

  @Override
  public Long create(SampleAnalyteDto sampleAnalyteDto) throws IOException {
    authorizationManager.throwIfNonAdmin();
    User user = authorizationManager.getCurrentUser();

    SampleAnalyte sampleAnalyte = Dtos.to(sampleAnalyteDto);
    sampleAnalyte.setCreatedBy(user);
    sampleAnalyte.setUpdatedBy(user);

    if (sampleAnalyteDto.getSamplePurposeId() != null) {
      sampleAnalyte.setSamplePurpose(samplePurposeDao.getSamplePurpose(sampleAnalyteDto.getSamplePurposeId()));
    }
    if (sampleAnalyteDto.getTissueMaterialId() != null) {
      sampleAnalyte.setTissueMaterial(tissueMaterialDao.getTissueMaterial(sampleAnalyteDto.getTissueMaterialId()));
    }

    return sampleAnalyteDao.addSampleAnalyte(sampleAnalyte);
  }

  @Override
  public SampleAnalyte to(SampleAnalyteDto sampleAnalyteDto) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    User user = authorizationManager.getCurrentUser();

    SampleAnalyte sampleAnalyte = Dtos.to(sampleAnalyteDto);
    sampleAnalyte.setCreatedBy(user);
    sampleAnalyte.setUpdatedBy(user);
    Date now = new Date();
    sampleAnalyte.setCreationDate(now);
    sampleAnalyte.setLastUpdated(now);

    if (sampleAnalyteDto.getSamplePurposeId() != null) {
      SamplePurpose samplePurpose = samplePurposeDao.getSamplePurpose(sampleAnalyteDto.getSamplePurposeId());
      ServiceUtils.throwIfNull(samplePurpose, "SampleAnalyte.samplePurposeId", sampleAnalyteDto.getSamplePurposeId());
      sampleAnalyte.setSamplePurpose(samplePurpose);
    }
    if (sampleAnalyteDto.getTissueMaterialId() != null) {
      TissueMaterial tissueMaterial = tissueMaterialDao.getTissueMaterial(sampleAnalyteDto.getTissueMaterialId());
      ServiceUtils.throwIfNull(tissueMaterial, "SampleAnalyte.tissueMaterialId", sampleAnalyteDto.getTissueMaterialId());
      sampleAnalyte.setTissueMaterial(tissueMaterial);
    }

    return sampleAnalyte;
  }

  @Override
  public void update(SampleAnalyte sampleAnalyte) throws IOException {
    authorizationManager.throwIfNonAdmin();
    SampleAnalyte updatedSampleAnalyte = get(sampleAnalyte.getId());
    applyChanges(updatedSampleAnalyte, sampleAnalyte);

    User user = authorizationManager.getCurrentUser();
    updatedSampleAnalyte.setUpdatedBy(user);
    sampleAnalyteDao.update(updatedSampleAnalyte);
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

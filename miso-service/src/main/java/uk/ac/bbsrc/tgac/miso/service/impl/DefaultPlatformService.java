package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.store.PlatformStore;
import uk.ac.bbsrc.tgac.miso.service.PlatformService;
import uk.ac.bbsrc.tgac.miso.service.SequencerReferenceService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultPlatformService implements PlatformService {

  @Autowired
  private PlatformStore platformDao;
  @Autowired
  private SequencerReferenceService sequencerReferenceService;

  @Override
  public Platform get(long platformId) throws IOException {
    return platformDao.get(platformId);
  }

  @Override
  public Collection<Platform> list() throws IOException {
    return platformDao.listAll();
  }

  @Override
  public Collection<String> listDistinctPlatformTypeNames() throws IOException {
    List<String> names = new ArrayList<>();
    for (PlatformType type : platformDao.listDistinctPlatformNames()) {
      names.add(type.getKey());
    }
    return names;
  }

  @Override
  public Collection<PlatformType> listActivePlatformTypes() throws IOException {
    Collection<PlatformType> activePlatformTypes = new ArrayList<>();
    for (PlatformType platformType : PlatformType.values()) {
      for (SequencerReference sequencer : sequencerReferenceService.listByPlatformType(platformType)) {
        if (sequencer.isActive()) {
          activePlatformTypes.add(platformType);
          break;
        }
      }
    }
    return activePlatformTypes;
  }

  public void setPlatformDao(PlatformStore platformDao) {
    this.platformDao = platformDao;
  }

  public void setSequencerReferenceService(SequencerReferenceService sequencerReferenceService) {
    this.sequencerReferenceService = sequencerReferenceService;
  }

}

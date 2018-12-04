package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.PlatformPosition;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.store.PlatformStore;
import uk.ac.bbsrc.tgac.miso.service.PlatformService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultPlatformService implements PlatformService {

  @Autowired
  private PlatformStore platformDao;

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
  public Set<PlatformType> listActivePlatformTypes() throws IOException {
    return platformDao.listActivePlatformTypes();
  }

  @Override
  public PlatformPosition getPlatformPosition(long positionId) throws IOException {
    return platformDao.getPlatformPosition(positionId);
  }

  public void setPlatformDao(PlatformStore platformDao) {
    this.platformDao = platformDao;
  }

}

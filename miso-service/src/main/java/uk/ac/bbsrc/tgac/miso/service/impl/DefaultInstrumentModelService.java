package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentPosition;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.store.InstrumentModelStore;
import uk.ac.bbsrc.tgac.miso.service.InstrumentModelService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultInstrumentModelService implements InstrumentModelService {

  @Autowired
  private InstrumentModelStore instrumentModelDao;

  @Override
  public InstrumentModel get(long instrumentModelId) throws IOException {
    return instrumentModelDao.get(instrumentModelId);
  }

  @Override
  public Collection<InstrumentModel> list() throws IOException {
    return instrumentModelDao.listAll();
  }

  @Override
  public Collection<String> listDistinctPlatformTypeNames() throws IOException {
    List<String> names = new ArrayList<>();
    for (PlatformType type : instrumentModelDao.listDistinctPlatformNames()) {
      names.add(type.getKey());
    }
    return names;
  }

  @Override
  public Set<PlatformType> listActivePlatformTypes() throws IOException {
    return instrumentModelDao.listActivePlatformTypes();
  }

  @Override
  public InstrumentPosition getInstrumentPosition(long positionId) throws IOException {
    return instrumentModelDao.getInstrumentPosition(positionId);
  }

  public void setPlatformDao(InstrumentModelStore platformDao) {
    this.instrumentModelDao = platformDao;
  }

}

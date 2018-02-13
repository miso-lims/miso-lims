package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.InstrumentStatus;
import uk.ac.bbsrc.tgac.miso.core.store.InstrumentStatusStore;
import uk.ac.bbsrc.tgac.miso.service.InstrumentStatusService;

@Service
@Transactional(rollbackFor = Exception.class)

public class DefaultInstrumentStatusService implements InstrumentStatusService {
  @Autowired
  private InstrumentStatusStore insturmentStatusStore;

  @Override
  public List<InstrumentStatus> list() throws IOException {
    return insturmentStatusStore.list();
  }

}

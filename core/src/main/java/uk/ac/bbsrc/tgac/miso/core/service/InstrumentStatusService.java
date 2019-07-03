package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.InstrumentStatus;

public interface InstrumentStatusService {
  public List<InstrumentStatus> list() throws IOException;
}

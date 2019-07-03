package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.InstrumentStatus;

public interface InstrumentStatusStore {

  public List<InstrumentStatus> list() throws IOException;
}

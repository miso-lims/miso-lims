package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class InstrumentStatus implements Serializable {

  private static final long serialVersionUID = 1L;

  private long id;

  private Instrument instrument;

  private final Map<InstrumentPosition, Run> positions = new HashMap<>();

  public long getId() {
    return id;
  }

  public Instrument getInstrument() {
    return instrument;
  }

  public void setInstrument(Instrument instrument) {
    this.instrument = instrument;
  }

  public Map<InstrumentPosition, Run> getPositions() {
    return positions;
  }

}

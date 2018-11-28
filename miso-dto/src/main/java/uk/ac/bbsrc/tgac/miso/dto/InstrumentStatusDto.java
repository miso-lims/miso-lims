package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

public class InstrumentStatusDto {
  private InstrumentDto instrument;
  private List<InstrumentPositionStatusDto> positions;

  public InstrumentDto getInstrument() {
    return instrument;
  }

  public void setInstrument(InstrumentDto instrument) {
    this.instrument = instrument;
  }

  public List<InstrumentPositionStatusDto> getPositions() {
    return positions;
  }

  public void setPositions(List<InstrumentPositionStatusDto> positions) {
    this.positions = positions;
  }

}

package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquotSingleCell;

@Entity
@Table(name = "SampleAliquotSingleCell")
public class SampleAliquotSingleCellImpl extends SampleAliquotImpl implements SampleAliquotSingleCell {

  private static final long serialVersionUID = 1L;

  private BigDecimal inputIntoLibrary;

  @Override
  public BigDecimal getInputIntoLibrary() {
    return inputIntoLibrary;
  }

  @Override
  public void setInputIntoLibrary(BigDecimal inputIntoLibrary) {
    this.inputIntoLibrary = inputIntoLibrary;
  }

}

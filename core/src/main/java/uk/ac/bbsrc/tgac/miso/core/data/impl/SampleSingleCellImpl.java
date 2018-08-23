package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.SampleSingleCell;

@Entity
@Table(name = "SampleSingleCell")
public class SampleSingleCellImpl extends SampleTissueProcessingImpl implements SampleSingleCell {

  private static final long serialVersionUID = 1L;

  @Column
  private BigDecimal initialCellConcentration;
  private String digestion;

  @Override
  public BigDecimal getInitialCellConcentration() {
    return initialCellConcentration;
  }

  @Override
  public void setInitialCellConcentration(BigDecimal initialCellConcentration) {
    this.initialCellConcentration = initialCellConcentration;
  }

  @Override
  public String getDigestion() {
    return digestion;
  }

  @Override
  public void setDigestion(String digestion) {
    this.digestion = digestion;
  }

}

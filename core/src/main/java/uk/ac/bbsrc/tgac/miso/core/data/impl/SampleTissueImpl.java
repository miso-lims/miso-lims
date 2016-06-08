package uk.ac.bbsrc.tgac.miso.core.data.impl;

import javax.persistence.Entity;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;

@Entity
@Table(name = "SampleTissue")
public class SampleTissueImpl extends SampleAdditionalInfoImpl implements SampleTissue {

  private static final long serialVersionUID = 1L;

  private Integer cellularity;

  @Override
  public Integer getCellularity() {
    return cellularity;
  }

  @Override
  public void setCellularity(Integer cellularity) {
    this.cellularity = cellularity;
  }

  @Override
  public String toString() {
    return "SampleTissueImpl [cellularity=" + cellularity + "]";
  }

}

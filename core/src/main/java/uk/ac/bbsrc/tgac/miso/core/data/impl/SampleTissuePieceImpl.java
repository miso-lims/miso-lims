package uk.ac.bbsrc.tgac.miso.core.data.impl;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.SampleTissuePiece;
import uk.ac.bbsrc.tgac.miso.core.data.type.TissuePieceType;

@Entity
@Table(name = "SampleTissuePiece")
public class SampleTissuePieceImpl extends SampleTissueProcessingImpl implements SampleTissuePiece {

  private static final long serialVersionUID = 1L;

  private Integer slidesConsumed;
  @ManyToOne
  @JoinColumn(name = "tissuePieceType")
  private TissuePieceType tissuePieceType;

  @Override
  public Integer getSlidesConsumed() {
    return slidesConsumed;
  }

  @Override
  public void setSlidesConsumed(Integer slidesConsumed) {
    this.slidesConsumed = slidesConsumed;
  }

  @Override
  public TissuePieceType getTissuePieceType() {
    return tissuePieceType;
  }

  @Override
  public void setTissuePieceType(TissuePieceType tissuePieceType) {
    this.tissuePieceType = tissuePieceType;
  }

}

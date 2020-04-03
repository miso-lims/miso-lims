package uk.ac.bbsrc.tgac.miso.core.data.impl;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import uk.ac.bbsrc.tgac.miso.core.data.SampleSlide;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissuePiece;
import uk.ac.bbsrc.tgac.miso.core.data.type.TissuePieceType;

@Entity
@DiscriminatorValue("TissuePiece")
public class SampleTissuePieceImpl extends SampleTissueProcessingImpl implements SampleTissuePiece {

  private static final long serialVersionUID = 1L;

  private Integer slidesConsumed;

  @ManyToOne
  @JoinColumn(name = "tissuePieceType")
  private TissuePieceType tissuePieceType;

  @ManyToOne(targetEntity = SampleSlideImpl.class)
  @JoinColumn(name = "referenceSlideId")
  private SampleSlide referenceSlide;

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

  @Override
  public SampleSlide getReferenceSlide() {
    return referenceSlide;
  }

  @Override
  public void setReferenceSlide(SampleSlide referenceSlide) {
    this.referenceSlide = referenceSlide;
  }

}

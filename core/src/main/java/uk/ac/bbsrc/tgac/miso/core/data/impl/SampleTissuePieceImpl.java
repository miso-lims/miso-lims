package uk.ac.bbsrc.tgac.miso.core.data.impl;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

  @ManyToOne(targetEntity = SampleSlideImpl.class, fetch = FetchType.LAZY)
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

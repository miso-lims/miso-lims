package uk.ac.bbsrc.tgac.miso.core.data.impl;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

  private Long referenceSlideId;

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
  public Long getReferenceSlideId() {
    return referenceSlideId;
  }

  @Override
  public void setReferenceSlideId(Long referenceSlideId) {
    this.referenceSlideId = referenceSlideId;
  }

}

package uk.ac.bbsrc.tgac.miso.core.data;

import uk.ac.bbsrc.tgac.miso.core.data.type.TissuePieceType;

public interface SampleTissuePiece extends SampleTissueProcessing {

  public static final String SUBCATEGORY_NAME = "Tissue Piece";

  public TissuePieceType getTissuePieceType();

  public void setTissuePieceType(TissuePieceType type);

  public Integer getSlidesConsumed();

  public void setSlidesConsumed(Integer slidesConsumed);

  public SampleSlide getReferenceSlide();

  public void setReferenceSlide(SampleSlide referenceSlide);

}

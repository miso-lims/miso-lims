package uk.ac.bbsrc.tgac.miso.core.data;

import uk.ac.bbsrc.tgac.miso.core.data.type.TissuePieceType;

public interface SampleTissuePiece extends SampleTissueProcessing {

  static final String SUBCATEGORY_NAME = "Tissue Piece";

  TissuePieceType getTissuePieceType();

  void setTissuePieceType(TissuePieceType type);

  Integer getSlidesConsumed();

  void setSlidesConsumed(Integer slidesConsumed);

  Long getReferenceSlideId();

  void setReferenceSlideId(Long referenceSlideId);

}

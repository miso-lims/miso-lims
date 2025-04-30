package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIndex;

public interface SampleTissueProcessing extends DetailedSample {

  public static final String CATEGORY_NAME = "Tissue Processing";

  public static final List<String> SUBCATEGORIES = Collections.unmodifiableList(Arrays
      .asList(SampleSlide.SUBCATEGORY_NAME, SampleTissuePiece.SUBCATEGORY_NAME, SampleSingleCell.SUBCATEGORY_NAME));

  SampleIndex getIndex();

  void setIndex(SampleIndex index);

}

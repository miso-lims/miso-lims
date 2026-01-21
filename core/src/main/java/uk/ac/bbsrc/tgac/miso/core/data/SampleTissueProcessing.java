package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIndex;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleProbe;

public interface SampleTissueProcessing extends DetailedSample {

  public static final String CATEGORY_NAME = "Tissue Processing";

  public static final List<String> SUBCATEGORIES = Collections.unmodifiableList(Arrays
      .asList(SampleSlide.SUBCATEGORY_NAME, SampleTissuePiece.SUBCATEGORY_NAME, SampleSingleCell.SUBCATEGORY_NAME));

  SampleIndex getIndex();

  void setIndex(SampleIndex index);

  Set<SampleProbe> getProbes();

  void setProbes(Set<SampleProbe> probes);

}

package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface SampleAliquot extends DetailedSample {

  public static String CATEGORY_NAME = "Aliquot";

  public static final List<String> SUBCATEGORIES = Collections.unmodifiableList(Arrays.asList(SampleAliquotSingleCell.SUBCATEGORY_NAME));

  SamplePurpose getSamplePurpose();

  void setSamplePurpose(SamplePurpose samplePurpose);

}
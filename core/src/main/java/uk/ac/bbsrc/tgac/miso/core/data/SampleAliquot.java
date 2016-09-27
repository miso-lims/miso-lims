package uk.ac.bbsrc.tgac.miso.core.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties({ "sample" })
public interface SampleAliquot extends DetailedSample {

  public static String CATEGORY_NAME = "Aliquot";

  SamplePurpose getSamplePurpose();

  void setSamplePurpose(SamplePurpose samplePurpose);

}
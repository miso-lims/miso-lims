package uk.ac.bbsrc.tgac.miso.core.data.impl;

import uk.ac.bbsrc.tgac.miso.core.data.SampleCategory;

@SampleCategory(alias = "Tissue")
public class SampleTissueNode extends SampleImpl {

  private static final long serialVersionUID = 1L;

  public SampleTissueNode(SampleFactoryBuilder builder) {
    super(builder);
    setSampleAdditionalInfo(builder.getSampleAdditionalInfo());
    setSampleTissue(builder.getSampleTissue());
  }
}

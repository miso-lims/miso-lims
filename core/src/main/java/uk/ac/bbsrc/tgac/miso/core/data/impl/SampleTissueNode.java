package uk.ac.bbsrc.tgac.miso.core.data.impl;

import uk.ac.bbsrc.tgac.miso.core.data.SampleCategory;

@SampleCategory(alias = "Tissue")
public class SampleTissueNode extends SampleImpl {

  private static final long serialVersionUID = 1L;

  public SampleTissueNode(SampleFactoryBuilder builder) {
    super(builder);
    setSampleAdditionalInfo(builder.getSampleAdditionalInfo());
    getSampleAdditionalInfo().setSample(this);
    getSampleAdditionalInfo().setParent(builder.getParent());
    getSampleAdditionalInfo().getParent().getSampleAdditionalInfo().getChildren().add(this);
    setSampleTissue(builder.getSampleTissue());
    getSampleTissue().setSample(this);
  }
}

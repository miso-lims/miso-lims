package uk.ac.bbsrc.tgac.miso.core.data.impl;

import uk.ac.bbsrc.tgac.miso.core.data.SampleCategory;

@SampleCategory(alias = "Identity")
public class SampleIdentityNode extends SampleImpl {

  private static final long serialVersionUID = 1L;

  public SampleIdentityNode(SampleFactoryBuilder builder) {
    super(builder);
    setSampleAdditionalInfo(builder.getSampleAdditionalInfo());
    setIdentity(builder.getIdentity());
  }
}

package uk.ac.bbsrc.tgac.miso.core.data.impl;

public class SampleAnalyteNode extends SampleImpl {

  private static final long serialVersionUID = 1L;

  public SampleAnalyteNode(SampleFactoryBuilder builder) {
    super(builder);
    setSampleAdditionalInfo(builder.getSampleAdditionalInfo());
    setSampleAnalyte(builder.getSampleAnalyte());
  }

}

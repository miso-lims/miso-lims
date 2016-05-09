package uk.ac.bbsrc.tgac.miso.core.service.naming;

import java.security.InvalidParameterException;

import net.sourceforge.fluxion.spi.ServiceProvider;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAdditionalInfo;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;

@ServiceProvider
public class OicrSampleAliasGenerator implements NameGenerator<Sample> {

  private static final String SEPARATOR = "_";
  private static final String DASH = "-";
  private static final String NO_PASSAGE = "nn";
  private static final String TISSUE_ORIGIN_UNKNOWN = "nn";
  private static final String TISSUE_TYPE_UNKNOWN = "n";

  @Override
  public String getGeneratorName() {
    return "OicrSampleAliasGenerator";
  }

  @Override
  public String generateName(Sample t) {
    SampleAdditionalInfo sai = t.getSampleAdditionalInfo();
    if (sai == null) {
      throw new IllegalArgumentException("SampleAdditionalInfo missing. Can only generate an alias for detailed samples");
    }
    
    for (Sample parent = t.getParent(); parent != null; parent = parent.getParent()) {
      if (parent.getSampleAnalyte() != null && !parent.getSampleAdditionalInfo().getSampleClass().isStock()) {
        return parent.getAlias() + getSiblingTag(t);
      }
      if (parent.getSampleTissue() != null) {
        return parent.getAlias() + getSiblingTag(t);
      }
      if (parent.getIdentity() != null) {
        String alias = generateTissueAlias(t, parent);
        if (t.getSampleTissue() == null) {
          alias += getSiblingTag(t);
        }
        return alias;
      }
    }
    // Identity name generation requires access to SampleNumberPerProjectDao
    throw new IllegalArgumentException("Cannot generate alias for Identities");
  }
  
  private String generateTissueAlias(Sample sample, Sample identityParent) {
    StringBuilder sb = new StringBuilder();
    SampleAdditionalInfo sai = sample.getSampleAdditionalInfo();
    
    sb.append(identityParent.getAlias())
    .append(SEPARATOR)
    .append(sai.getTissueOrigin() == null ? TISSUE_ORIGIN_UNKNOWN : sai.getTissueOrigin().getAlias())
    .append(SEPARATOR)
    .append(sai.getTissueType() == null ? TISSUE_TYPE_UNKNOWN : sai.getTissueType().getAlias())
    .append(SEPARATOR)
    .append(passageNumber(sai.getPassageNumber()))
    .append(SEPARATOR)
    .append(sai.getTimesReceived());
    
    if (sai.getTubeNumber() != null) {
      sb.append(DASH)
      .append(sai.getTubeNumber());
    }
    return sb.toString();
  }
  
  private String getSiblingTag(Sample sample) {
    SampleAdditionalInfo sai = sample.getSampleAdditionalInfo();
    SampleClass sc = sai.getSampleClass();
    if (sc == null || sc.getSuffix() == null) {
      throw new InvalidParameterException("Unexpected null SampleClass or suffix");
    }
    if (sai.getSiblingNumber() == null) {
      throw new InvalidParameterException("Cannot generate alias for " + sc.getAlias() + " without a siblingNumber");
    }
    String siblingNum = sai.getSiblingNumber().toString();
    // Sibling number is only padded for Tissue Processing
    if (isTissueProcessing(sample)) {
      while (siblingNum.length() < 2) siblingNum = "0" + siblingNum;
    }
    return SEPARATOR + sc.getSuffix() + siblingNum;
  }
  
  private boolean isTissueProcessing(Sample sample) {
    return sample.getIdentity() == null && sample.getSampleTissue() == null && sample.getSampleAnalyte() == null;
  }

  @Override
  public Class<Sample> nameGeneratorFor() {
    return Sample.class;
  }
  
  private String passageNumber(Integer passageNumber) {
    if (passageNumber == null) return NO_PASSAGE;
    String p = passageNumber.toString();
    switch (p.length()) {
    case 1:
      return "0" + p;
    case 2:
      return p;
    default:
      throw new IllegalArgumentException("Invalid passage number. Must be 1-2 digits, or null");
    }
  }

}

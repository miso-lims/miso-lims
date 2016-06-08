package uk.ac.bbsrc.tgac.miso.core.service.naming;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;

import java.security.InvalidParameterException;

import net.sourceforge.fluxion.spi.ServiceProvider;

import uk.ac.bbsrc.tgac.miso.core.data.Identity;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAdditionalInfo;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAnalyte;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

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
    if (!LimsUtils.isDetailedSample(t)) {
      throw new IllegalArgumentException("Can only generate an alias for detailed samples");
    }
    SampleAdditionalInfo detailed = (SampleAdditionalInfo) t;
    
    for (SampleAdditionalInfo parent = (SampleAdditionalInfo) detailed.getParent(); parent != null;
        parent = (SampleAdditionalInfo) parent.getParent()) {
      if (isAnalyteSample(parent) && !((SampleAnalyte) parent).getSampleClass().isStock()) {
        return parent.getAlias() + getSiblingTag(detailed);
      }
      if (isTissueSample(parent)) {
        return parent.getAlias() + getSiblingTag(detailed);
      }
      if (isIdentitySample(parent)) {
        String alias = generateTissueAlias(detailed, (Identity) parent);
        if (!isTissueSample(detailed)) {
          alias += getSiblingTag(detailed);
        }
        return alias;
      }
    }
    // Identity name generation requires access to SampleNumberPerProjectDao
    throw new IllegalArgumentException("Cannot generate alias for Identities");
  }
  
  private String generateTissueAlias(SampleAdditionalInfo sample, Identity parent) {
    StringBuilder sb = new StringBuilder();
    
    sb.append(parent.getAlias())
    .append(SEPARATOR)
    .append(sample.getTissueOrigin() == null ? TISSUE_ORIGIN_UNKNOWN : sample.getTissueOrigin().getAlias())
    .append(SEPARATOR)
    .append(sample.getTissueType() == null ? TISSUE_TYPE_UNKNOWN : sample.getTissueType().getAlias())
    .append(SEPARATOR)
    .append(passageNumber(sample.getPassageNumber()))
    .append(SEPARATOR)
    .append(sample.getTimesReceived());
    
    if (sample.getTubeNumber() != null) {
      sb.append(DASH)
      .append(sample.getTubeNumber());
    }
    return sb.toString();
  }
  
  private String getSiblingTag(SampleAdditionalInfo sample) {
    SampleClass sc = sample.getSampleClass();
    if (sc == null || sc.getSuffix() == null) {
      throw new InvalidParameterException("Unexpected null SampleClass or suffix");
    }
    if (sample.getSiblingNumber() == null) {
      throw new InvalidParameterException("Cannot generate alias for " + sc.getAlias() + " without a siblingNumber");
    }
    String siblingNum = sample.getSiblingNumber().toString();
    // Sibling number is only padded for Tissue Processing
    if (isTissueProcessing(sample)) {
      while (siblingNum.length() < 2) siblingNum = "0" + siblingNum;
    }
    return SEPARATOR + sc.getSuffix() + siblingNum;
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

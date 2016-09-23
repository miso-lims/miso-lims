package uk.ac.bbsrc.tgac.miso.core.service.naming;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;

import java.security.InvalidParameterException;

import net.sourceforge.fluxion.spi.ServiceProvider;

import uk.ac.bbsrc.tgac.miso.core.data.Identity;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
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
    DetailedSample detailed = (DetailedSample) t;
    
    for (DetailedSample parent = detailed.getParent(); parent != null;
        parent = parent.getParent()) {
      if (parent.hasNonStandardAlias()) throw new IllegalArgumentException("Cannot generate alias due to nonstandard alias on a parent");
      if (isAliquotSample(parent)) {
        return parent.getAlias() + getSiblingTag(detailed);
      }
      if (isTissueSample(parent)) {
        return parent.getAlias() + getSiblingTag(detailed);
      }
      if (isIdentitySample(parent)) {
        if (!isTissueSample(detailed)) throw new IllegalArgumentException("Missing parent tissue");
        return generateTissueAlias((SampleTissue) detailed, (Identity) parent);
      }
    }
    // Identity name generation requires access to SampleNumberPerProjectDao
    throw new IllegalArgumentException("Cannot generate alias for Identities");
  }
  
  private String generateTissueAlias(SampleTissue tissue, Identity identity) {
    StringBuilder sb = new StringBuilder();
    
    sb.append(identity.getAlias())
    .append(SEPARATOR)
    .append(tissue.getTissueOrigin() == null ? TISSUE_ORIGIN_UNKNOWN : tissue.getTissueOrigin().getAlias())
    .append(SEPARATOR)
    .append(tissue.getTissueType() == null ? TISSUE_TYPE_UNKNOWN : tissue.getTissueType().getAlias())
    .append(SEPARATOR)
    .append(passageNumber(tissue.getPassageNumber()))
    .append(SEPARATOR)
    .append(tissue.getTimesReceived());
    
    if (tissue.getTubeNumber() != null) {
      sb.append(DASH)
      .append(tissue.getTubeNumber());
    }
    return sb.toString();
  }
  
  private String getSiblingTag(DetailedSample sample) {
    SampleClass sc = sample.getSampleClass();
    if (sc == null || sc.getSuffix() == null) {
      throw new InvalidParameterException("Unexpected null SampleClass or suffix");
    }
    if (sample.getSiblingNumber() == null) {
      throw new InvalidParameterException("Cannot generate alias for " + sc.getAlias() + " without a siblingNumber");
    }
    String siblingNum = sample.getSiblingNumber().toString();
    // Sibling number is only padded for Tissue Processing
    if (isTissueProcessingSample(sample)) {
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

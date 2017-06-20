package uk.ac.bbsrc.tgac.miso.core.service.naming.generation;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;

import java.io.IOException;
import java.security.InvalidParameterException;

import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Identity;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.SiblingNumberGenerator;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public class OicrSampleAliasGenerator implements NameGenerator<Sample> {

  @Autowired
  private SiblingNumberGenerator siblingNumberGenerator;

  private static final String SEPARATOR = "_";
  private static final String DASH = "-";
  private static final String NO_PASSAGE = "nn";
  private static final String TISSUE_ORIGIN_UNKNOWN = "nn";
  private static final String TISSUE_TYPE_UNKNOWN = "n";

  public void setSiblingNumberGenerator(SiblingNumberGenerator siblingNumberGenerator) {
    this.siblingNumberGenerator = siblingNumberGenerator;
  }

  @Override
  public String generate(Sample sample) throws MisoNamingException, IOException {
    if (!LimsUtils.isDetailedSample(sample)) {
      throw new IllegalArgumentException("Can only generate an alias for detailed samples");
    }
    DetailedSample detailed = (DetailedSample) sample;

    for (DetailedSample parent = detailed.getParent(); parent != null; parent = parent.getParent()) {
      if (isAliquotSample(parent)) {
        return addSiblingTag(parent.getAlias(), detailed);
      }
      if (isTissueSample(parent)) {
        return addSiblingTag(parent.getAlias(), detailed);
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

  private String addSiblingTag(String parentAlias, DetailedSample sample) throws IOException {
    SampleClass sc = sample.getSampleClass();
    if (sc == null || sc.getSuffix() == null) {
      throw new InvalidParameterException("Unexpected null SampleClass or suffix");
    }
    String partialAlias = parentAlias + SEPARATOR + sc.getSuffix();
    if (sample.getSiblingNumber() == null) {
      if (siblingNumberGenerator == null) {
        throw new IllegalStateException("No SiblingNumberGenerator configured");
      }
      sample.setSiblingNumber(siblingNumberGenerator.getNextSiblingNumber(partialAlias));
    }
    String siblingNum = sample.getSiblingNumber().toString();
    // Sibling number is only padded for Tissue Processing
    if (isTissueProcessingSample(sample)) {
      while (siblingNum.length() < 2)
        siblingNum = "0" + siblingNum;
    }
    return partialAlias + siblingNum;
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

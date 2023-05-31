package uk.ac.bbsrc.tgac.miso.core.service.naming.generation;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;

import java.io.IOException;
import java.security.InvalidParameterException;

import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissuePiece;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.SampleNumberPerProjectService;
import uk.ac.bbsrc.tgac.miso.core.service.naming.SiblingNumberGenerator;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public class OicrSampleAliasGenerator implements NameGenerator<Sample> {

  @Autowired
  private SiblingNumberGenerator siblingNumberGenerator;

  @Autowired
  private SampleNumberPerProjectService sampleNumberPerProjectService;

  private static final String SEPARATOR = "_";
  private static final String DASH = "-";
  private static final String NO_PASSAGE = "nn";
  private static final String TISSUE_ORIGIN_UNKNOWN = "nn";
  private static final String TISSUE_TYPE_UNKNOWN = "n";

  public void setSiblingNumberGenerator(SiblingNumberGenerator siblingNumberGenerator) {
    this.siblingNumberGenerator = siblingNumberGenerator;
  }

  public void setSampleNumberPerProjectService(SampleNumberPerProjectService sampleNumberPerProjectService) {
    this.sampleNumberPerProjectService = sampleNumberPerProjectService;
  }

  @Override
  public String generate(Sample sample) throws MisoNamingException, IOException {
    if (!LimsUtils.isDetailedSample(sample)) {
      throw new IllegalArgumentException("Can only generate an alias for detailed samples");
    }
    DetailedSample detailed = (DetailedSample) sample;

    if (isIdentitySample(detailed)) {
      return generateIdentityAlias((SampleIdentity) detailed);
    }
    for (DetailedSample parent = detailed.getParent(); parent != null; parent = parent.getParent()) {
      if (isAliquotSample(parent)) {
        continue;
      }
      if (isTissueSample(parent)) {
        if (isTissueSample(detailed)) {
          // tissues parented to tissues
          return generateTissueAlias((SampleTissue) detailed, LimsUtils.getParent(SampleIdentity.class, parent));
        } else {
          return addSiblingTag(parent.getAlias(), detailed);
        }
      }
      if (isIdentitySample(parent)) {
        if (!isTissueSample(detailed))
          throw new IllegalArgumentException("Missing parent tissue");
        return generateTissueAlias((SampleTissue) detailed, (SampleIdentity) LimsUtils.deproxify(parent));
      }
    }
    throw new IllegalStateException("Unexpected conditions for alias generation");
  }

  private String generateIdentityAlias(SampleIdentity identity) throws IOException {
    if (identity.getProject().getCode() == null) {
      throw new NullPointerException("Project code required to generate Identity alias");
    }
    String internalName = identity.getProject().getCode() + "_";
    String number = sampleNumberPerProjectService.nextNumber(identity.getProject(), internalName);
    internalName += number;
    return internalName;
  }

  private String generateTissueAlias(SampleTissue tissue, SampleIdentity identity) throws MisoNamingException {
    StringBuilder sb = new StringBuilder();

    if (tissue.getTimesReceived() == null) {
      throw new MisoNamingException("Cannot generate an alias without times received set");
    }
    if (tissue.getTubeNumber() == null) {
      throw new MisoNamingException("Cannot generate an alias without tube number set");
    }

    sb.append(identity.getAlias())
        .append(SEPARATOR)
        .append(tissue.getTissueOrigin() == null ? TISSUE_ORIGIN_UNKNOWN : tissue.getTissueOrigin().getAlias())
        .append(SEPARATOR)
        .append(tissue.getTissueType() == null ? TISSUE_TYPE_UNKNOWN : tissue.getTissueType().getAlias())
        .append(SEPARATOR)
        .append(passageNumber(tissue.getPassageNumber()))
        .append(SEPARATOR)
        .append(tissue.getTimesReceived())
        .append(DASH)
        .append(tissue.getTubeNumber());

    return sb.toString();
  }

  private String addSiblingTag(String parentAlias, DetailedSample sample) throws IOException {
    SampleClass sc = sample.getSampleClass();
    if (sc == null) {
      throw new InvalidParameterException("Unexpected null SampleClass");
    }
    final String suffix;
    if (SampleTissueProcessing.CATEGORY_NAME.equals(sc.getSampleCategory())
        && SampleTissuePiece.SUBCATEGORY_NAME.equals(sc.getSampleSubcategory())) {
      suffix = ((SampleTissuePiece) sample).getTissuePieceType().getAbbreviation();
    } else {
      suffix = sc.getSuffix();
    }
    if (suffix == null) {
      throw new InvalidParameterException("Unexpected null suffix");
    }
    String partialAlias = parentAlias + SEPARATOR + suffix;
    if (sample.getSiblingNumber() == null) {
      if (siblingNumberGenerator == null) {
        throw new IllegalStateException("No SiblingNumberGenerator configured");
      }
      sample.setSiblingNumber(siblingNumberGenerator.getNextSiblingNumber(SampleImpl.class, partialAlias));
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
    if (passageNumber == null)
      return NO_PASSAGE;
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

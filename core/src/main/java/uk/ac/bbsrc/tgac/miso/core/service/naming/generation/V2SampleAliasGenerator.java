package uk.ac.bbsrc.tgac.miso.core.service.naming.generation;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissuePiece;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.SampleNumberPerProjectService;
import uk.ac.bbsrc.tgac.miso.core.service.naming.SiblingNumberGenerator;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public class V2SampleAliasGenerator implements NameGenerator<Sample> {

  private static final String UNDERSCORE = "_";
  private static final String DASH = "-";

  @Autowired
  private SiblingNumberGenerator siblingNumberGenerator;

  @Autowired
  private SampleNumberPerProjectService sampleNumberPerProjectService;

  public void setSiblingNumberGenerator(SiblingNumberGenerator siblingNumberGenerator) {
    this.siblingNumberGenerator = siblingNumberGenerator;
  }

  public void setSampleNumberPerProjectService(SampleNumberPerProjectService sampleNumberPerProjectService) {
    this.sampleNumberPerProjectService = sampleNumberPerProjectService;
  }

  @Override
  public String generate(Sample sample) throws MisoNamingException, IOException {
    if (!LimsUtils.isDetailedSample(sample)) {
      throw new MisoNamingException("Can only generate an alias for detailed samples");
    }
    DetailedSample detailed = (DetailedSample) sample;

    if (isIdentitySample(detailed)) {
      return generateIdentityAlias(detailed);
    } else if (isTissueSample(detailed)) {
      return generateTissueAlias(detailed);
    } else if (isAliquotSample(detailed)) {
      return generateAliquotAlias(detailed);
    } else {
      // Tissue processing or stock
      return generateLevel3Alias(detailed);
    }
  }

  private String generateIdentityAlias(DetailedSample sample) throws IOException, MisoNamingException {
    if (sample.getProject().getCode() == null) {
      throw new MisoNamingException("Project shortname required to generate Identity alias");
    }
    String partialAlias = sample.getProject().getCode() + UNDERSCORE;
    String number = sampleNumberPerProjectService.nextNumber(sample.getProject(), partialAlias);
    return partialAlias + number;
  }

  private String generateTissueAlias(DetailedSample sample) throws IOException {
    DetailedSample identity = getParent(SampleIdentity.class, sample);
    return constructAlias(identity.getAlias(), UNDERSCORE, 2);
  }

  private String generateLevel3Alias(DetailedSample sample) throws IOException, MisoNamingException {
    DetailedSample tissue = getParent(SampleTissue.class, sample);
    if (isTissuePieceSample(sample)) {
      SampleTissuePiece tissuePiece = (SampleTissuePiece) sample;
      if (isStringEmptyOrNull(tissuePiece.getTissuePieceType().getV2NamingCode())) {
        throw new MisoNamingException("Tissue piece type is missing V2 naming code");
      }
      return constructAlias(tissue.getAlias(), UNDERSCORE, tissuePiece.getTissuePieceType().getV2NamingCode(), 2);
    } else {
      if (isStringEmptyOrNull(sample.getSampleClass().getV2NamingCode())) {
        throw new MisoNamingException("Sample class is missing V2 naming code");
      }
      return constructAlias(tissue.getAlias(), UNDERSCORE, sample.getSampleClass().getV2NamingCode(), 2);
    }
  }

  private String generateAliquotAlias(DetailedSample sample) throws IOException {
    DetailedSample stock = getParent(SampleStock.class, sample);
    return constructAlias(stock.getAlias(), DASH, 2);
  }

  private String constructAlias(String parentAlias, String separator, int siblingNumberMinLength) throws IOException {
    String partialAlias = parentAlias + separator;
    return addSiblingNumber(partialAlias, siblingNumberMinLength);
  }

  private String constructAlias(String parentAlias, String separator, String typeCode, int siblingNumberMinLength)
      throws IOException {
    String partialAlias = parentAlias + separator + typeCode;
    return addSiblingNumber(partialAlias, siblingNumberMinLength);
  }

  private String addSiblingNumber(String partialAlias, int minLength) throws IOException {
    int next = siblingNumberGenerator.getFirstAvailableSiblingNumber(SampleImpl.class, partialAlias);
    String siblingNumber = zeroPad(next, minLength);
    return partialAlias + siblingNumber;
  }

}

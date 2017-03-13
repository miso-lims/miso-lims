package uk.ac.bbsrc.tgac.miso.migration.util;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.OicrNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.NameGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.OicrSampleAliasGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.NameValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.OicrSampleAliasValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public class OicrMigrationNamingScheme extends OicrNamingScheme {

  private final NameValidator sampleAliasValidator = new OicrMigrationSampleAliasValidator();
  private final NameGenerator<Sample> sampleAliasGenerator = new OicrMigrationSampleAliasGenerator();

  @Override
  protected NameValidator getSampleAliasValidator() {
    return sampleAliasValidator;
  }

  @Override
  protected NameGenerator<Sample> getSampleAliasGenerator() {
    return sampleAliasGenerator;
  }

  private static class OicrMigrationSampleAliasValidator extends OicrSampleAliasValidator {

    @Override
    public ValidationResult validate(String value) {
      ValidationResult result = super.validate(value);
      if (result.isValid()) return result;
      return validateAdditionalPatterns(value);
    }

    private static final String TISSUE_PART = "^[A-Z\\d]{3,5}_\\d{3,5}_([A-Z][a-z]|nn)_[A-Zn]_(nn|\\d{2})_\\d{1,2}-\\d{1,2}";
    private static final String GSLE_GHOST_STOCK_REGEX = TISSUE_PART + "_[DR]$";
    private static final String GSLE_RNA_SUBTYPE_REGEX = TISSUE_PART + "_R_(S?\\d+_)?(MR|SM|WT)$";

    /**
     * Validates additional patterns which are not exactly valid according to OicrSampleAliasValidator, but which were valid in the old
     * system, and will not break OicrSampleAliasGenerator
     * 
     * @param value
     * @return
     */
    private ValidationResult validateAdditionalPatterns(String value) {
      if (value.matches(GSLE_GHOST_STOCK_REGEX) || value.matches(GSLE_RNA_SUBTYPE_REGEX)) {
        return ValidationResult.success();
      } else {
        return ValidationResult.failed("Invalid sample alias: " + value);
      }
    }

  }

  private static class OicrMigrationSampleAliasGenerator extends OicrSampleAliasGenerator {

    @Override
    public String generate(Sample sample) throws MisoNamingException {
      // Attempt to ignore non-standard aliases in the hierarchy. Since the child-portion of the alias is appended
      // to the parent's or grandparent's alias in most cases, this will allow the generation of non-standard aliases

      int changed = 0;
      DetailedSample detailed = null;
      if (LimsUtils.isDetailedSample(sample)) {
        detailed = (DetailedSample) sample;
        for (DetailedSample parent = detailed.getParent(); parent != null && parent.hasNonStandardAlias(); parent = parent.getParent()) {
          changed++;
          parent.setNonStandardAlias(false);
        }
      }
      String alias = super.generate(sample);
      if (changed > 0) {
        detailed.setNonStandardAlias(true);
        DetailedSample parent = detailed;
        for (int i = 0; i < changed; i++) {
          parent = parent.getParent();
          parent.setNonStandardAlias(true);
        }
      }
      return alias;
    }

  }

}

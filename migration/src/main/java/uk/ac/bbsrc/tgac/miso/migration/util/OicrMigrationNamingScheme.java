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
      // attempt to ignore up to two levels of non-standard aliases. Since the child-portion of the alias is appended
      // to the parent's or grandparent's alias in most cases, this will allow the generation of non-standard aliases
      // when there are non-standard aliases in the hierarchy

      int changed = 0;
      DetailedSample detailed = null;
      DetailedSample parent = null;
      DetailedSample grandparent = null;
      if (LimsUtils.isDetailedSample(sample)) {
        detailed = (DetailedSample) sample;
        parent = detailed.getParent();
        if (parent != null && parent.hasNonStandardAlias()) {
          changed = 1;
          parent.setNonStandardAlias(false);
          grandparent = parent.getParent();
          if (grandparent != null && grandparent.hasNonStandardAlias()) {
            changed = 2;
            grandparent.setNonStandardAlias(false);
          }
        }
      }
      String alias = super.generate(sample);
      if (changed > 0) {
        detailed.setNonStandardAlias(true);
        parent.setNonStandardAlias(true);
      }
      if (changed > 1) {
        grandparent.setNonStandardAlias(true);
      }
      return alias;
    }

  }

}

package uk.ac.bbsrc.tgac.miso.migration.util;

import uk.ac.bbsrc.tgac.miso.core.service.naming.OicrNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.NameValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.OicrSampleAliasValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;

public class OicrMigrationNamingScheme extends OicrNamingScheme {

  private final NameValidator sampleAliasValidator = new OicrMigrationSampleAliasValidator();

  @Override
  protected NameValidator getSampleAliasValidator() {
    return sampleAliasValidator;
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

}

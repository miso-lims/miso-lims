package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;

import java.util.regex.Pattern;

import uk.ac.bbsrc.tgac.miso.core.service.naming.DefaultMisoEntityPrefix;

public class DefaultNameValidator implements NameValidator {

  private static final String defaultValidationMessagePrefix = "Name must match the pattern: ";
  private static final Pattern defaultPattern = Pattern.compile("^[A-Z]{3}[0-9]+$");

  private Pattern customPattern = null;
  private String customValidationMessage = null;
  private boolean allowDuplicates = false;

  @Override
  public void setValidationRegex(String validationRegex) {
    this.customPattern = validationRegex == null ? null : Pattern.compile(validationRegex);
  }

  @Override
  public void setValidationMessage(String message) {
    this.customValidationMessage = message;
  }

  @Override
  public void setDuplicateAllowed(boolean allow) {
    this.allowDuplicates = allow;
  }

  @Override
  public void setNullAllowed(boolean allow) {
    if (allow) throw new UnsupportedOperationException("null names not supported by this validator");
  }

  @Override
  public ValidationResult validate(String value) {
    if (value == null || value.isEmpty()) return ValidationResult.failed("Name cannot be empty");
    if (customPattern != null) {
      if (!customPattern.matcher(value).matches()) return ValidationResult.failed(getValidationMessage());
    } else {
      String prefix = value.substring(0, 3);
      if (DefaultMisoEntityPrefix.getByName(prefix) == null || !defaultPattern.matcher(value).matches()) {
        return ValidationResult.failed(getValidationMessage());
      }
    }
    return ValidationResult.success();
  }

  private String getValidationMessage() {
    if (customValidationMessage != null) return customValidationMessage;
    else return defaultValidationMessagePrefix + (customPattern != null ? customPattern.pattern() : defaultPattern.pattern());
  }

  @Override
  public boolean duplicatesAllowed() {
    return allowDuplicates;
  }

  @Override
  public boolean nullsAllowed() {
    return false;
  }

}

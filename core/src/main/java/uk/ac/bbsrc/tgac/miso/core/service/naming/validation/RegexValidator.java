package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;

import java.util.regex.Pattern;

public abstract class RegexValidator implements NameValidator {

  private static final String defaultValidationMessagePart = " must match the pattern: ";

  private Pattern pattern = null;
  private String customValidationMessage = null;
  private boolean allowDuplicates = false;
  private boolean allowNulls = false;

  public RegexValidator(String regex, boolean allowDuplicates, boolean allowNulls) {
    this.pattern = Pattern.compile(regex);
    this.allowDuplicates = allowDuplicates;
    this.allowNulls = allowNulls;
  }

  @Override
  public void setValidationRegex(String validationRegex) {
    if (!customRegexOptionEnabled()) {
      throw new UnsupportedOperationException("customization not supported by this validator");
    }
    this.pattern = Pattern.compile(validationRegex);
  }

  @Override
  public void setValidationMessage(String message) {
    this.customValidationMessage = message;
  }

  @Override
  public void setDuplicateAllowed(boolean allow) {
    if (!enableDuplicatesOptionEnabled() && allow) {
      throw new UnsupportedOperationException("duplicate " + getFieldName() + " not supported by this validator");
    }
    this.allowDuplicates = allow;
  }

  @Override
  public boolean duplicatesAllowed() {
    return allowDuplicates;
  }

  @Override
  public void setNullAllowed(boolean allow) {
    if (!nullabilityOptionEnabled() && allow) {
      throw new UnsupportedOperationException("null " + getFieldName() + " not supported by this validator");
    }
    this.allowNulls = allow;
  }

  @Override
  public boolean nullsAllowed() {
    return allowNulls;
  }

  @Override
  public ValidationResult validate(String value) {
    if (!allowNulls && (value == null || value.isEmpty())) return ValidationResult.failed(getFieldName() + " cannot be empty");
    if (pattern != null && !pattern.matcher(value).matches()) {
      return ValidationResult.failed(getValidationMessage());
    }
    return ValidationResult.success();
  }

  private String getValidationMessage() {
    if (customValidationMessage != null) return customValidationMessage;
    else return getFieldName() + defaultValidationMessagePart + pattern.pattern();
  }

  protected abstract String getFieldName();

  protected abstract boolean customRegexOptionEnabled();

  protected abstract boolean nullabilityOptionEnabled();

  protected abstract boolean enableDuplicatesOptionEnabled();

}

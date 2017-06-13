package uk.ac.bbsrc.tgac.miso.core.service.naming;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.NameGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.NameValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;

public abstract class AbstractNamingScheme implements NamingScheme {

  @Override
  public boolean hasNameGenerator() {
    return getNameGenerator() != null;
  }

  @Override
  public String generateNameFor(Nameable nameable) throws MisoNamingException, IOException {
    if (getNameGenerator() == null) throw new UnsupportedOperationException("check hasNameGenerator() to determine availability");
    String name = getNameGenerator().generate(nameable);
    ValidationResult vr = validateName(name);
    if (vr.isValid()) return name;
    throw new MisoNamingException("failed to generate a valid name: " + vr.getMessage());
  }

  @Override
  public ValidationResult validateName(String name) {
    if (getNameValidator() == null) return ValidationResult.success();
    return getNameValidator().validate(name);
  }

  @Override
  public boolean hasSampleAliasGenerator() {
    return getSampleAliasGenerator() != null;
  }

  @Override
  public String generateSampleAlias(Sample sample) throws MisoNamingException, IOException {
    if (getSampleAliasGenerator() == null)
      throw new UnsupportedOperationException("check hasSampleAliasGenerator() to determine availability");
    return getSampleAliasGenerator().generate(sample);
  }

  @Override
  public ValidationResult validateSampleAlias(String alias) {
    if (getSampleAliasValidator() == null) return ValidationResult.success();
    return getSampleAliasValidator().validate(alias);
  }

  @Override
  public boolean hasLibraryAliasGenerator() {
    return getLibraryAliasGenerator() != null;
  }

  @Override
  public String generateLibraryAlias(Library library) throws MisoNamingException, IOException {
    if (getLibraryAliasGenerator() == null)
      throw new UnsupportedOperationException("check hasLibraryAliasGenerator() to determine availability");
    return getLibraryAliasGenerator().generate(library);
  }

  @Override
  public ValidationResult validateLibraryAlias(String alias) {
    if (getLibraryAliasValidator() == null) return ValidationResult.success();
    return getLibraryAliasValidator().validate(alias);
  }

  @Override
  public boolean duplicateNamesAllowed() {
    return getNameValidator() == null ? true : getNameValidator().duplicatesAllowed();
  }

  @Override
  public boolean duplicateSampleAliasAllowed() {
    return getSampleAliasValidator() == null ? true : getSampleAliasValidator().duplicatesAllowed();
  }

  @Override
  public boolean duplicateLibraryAliasAllowed() {
    return getLibraryAliasValidator() == null ? true : getLibraryAliasValidator().duplicatesAllowed();
  }

  @Override
  public ValidationResult validateProjectShortName(String shortName) {
    if (getProjectShortNameValidator() == null) return ValidationResult.success();
    return getProjectShortNameValidator().validate(shortName);
  }

  @Override
  public boolean duplicateProjectShortNamesAllowed() {
    return getProjectShortNameValidator() == null ? null : getProjectShortNameValidator().duplicatesAllowed();
  }

  @Override
  public boolean nullProjectShortNameAllowed() {
    return getProjectShortNameValidator() == null ? false : getProjectShortNameValidator().nullsAllowed();
  }

  protected abstract NameValidator getNameValidator();

  protected abstract NameGenerator<Nameable> getNameGenerator();

  protected abstract NameValidator getSampleAliasValidator();

  protected abstract NameGenerator<Sample> getSampleAliasGenerator();

  protected abstract NameValidator getLibraryAliasValidator();

  protected abstract NameGenerator<Library> getLibraryAliasGenerator();

  protected abstract NameValidator getProjectShortNameValidator();

}

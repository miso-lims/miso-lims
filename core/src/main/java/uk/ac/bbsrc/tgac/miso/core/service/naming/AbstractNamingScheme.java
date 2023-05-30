package uk.ac.bbsrc.tgac.miso.core.service.naming;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
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
    if (getNameGenerator() == null)
      throw new UnsupportedOperationException("check hasNameGenerator() to determine availability");
    String name = getNameGenerator().generate(nameable);
    ValidationResult vr = validateName(name);
    if (vr.isValid())
      return name;
    throw new MisoNamingException("failed to generate a valid name: " + vr.getMessage());
  }

  @Override
  public ValidationResult validateName(String name) {
    if (getNameValidator() == null)
      return ValidationResult.success();
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
    if (getSampleAliasValidator() == null)
      return ValidationResult.success();
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
    if (getLibraryAliasValidator() == null)
      return ValidationResult.success();
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
  public boolean hasLibraryAliquotAliasGenerator() {
    return getLibraryAliquotAliasGenerator() != null;
  }

  @Override
  public String generateLibraryAliquotAlias(LibraryAliquot aliquot) throws MisoNamingException, IOException {
    if (getLibraryAliquotAliasGenerator() == null)
      throw new UnsupportedOperationException("check hasLibraryAliquotAliasGenerator() to determine availability");
    return getLibraryAliquotAliasGenerator().generate(aliquot);
  }

  @Override
  public ValidationResult validateLibraryAliquotAlias(String alias) {
    if (getLibraryAliquotAliasValidator() != null) {
      return getLibraryAliquotAliasValidator().validate(alias);
    } else {
      return ValidationResult.success();
    }
  }

  @Override
  public boolean duplicateLibraryAliquotAliasAllowed() {
    return getLibraryAliquotAliasValidator() == null ? true : getLibraryAliquotAliasValidator().duplicatesAllowed();
  }

  @Override
  public ValidationResult validateProjectCode(String code) {
    if (getProjectCodeValidator() == null)
      return ValidationResult.success();
    return getProjectCodeValidator().validate(code);
  }

  @Override
  public boolean duplicateProjectCodesAllowed() {
    return getProjectCodeValidator() == null ? true : getProjectCodeValidator().duplicatesAllowed();
  }

  @Override
  public boolean nullProjectCodeAllowed() {
    return getProjectCodeValidator() == null ? false : getProjectCodeValidator().nullsAllowed();
  }

  protected abstract NameValidator getNameValidator();

  protected abstract NameGenerator<Nameable> getNameGenerator();

  protected abstract NameValidator getSampleAliasValidator();

  protected abstract NameGenerator<Sample> getSampleAliasGenerator();

  protected abstract NameValidator getLibraryAliasValidator();

  protected abstract NameGenerator<Library> getLibraryAliasGenerator();

  protected abstract NameValidator getLibraryAliquotAliasValidator();

  protected abstract NameGenerator<LibraryAliquot> getLibraryAliquotAliasGenerator();

  protected abstract NameValidator getProjectCodeValidator();

}

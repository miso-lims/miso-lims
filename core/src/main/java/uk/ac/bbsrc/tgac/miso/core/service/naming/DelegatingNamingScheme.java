package uk.ac.bbsrc.tgac.miso.core.service.naming;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.NameGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.NameValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;

/**
 * Just a wrapper that delegates to another naming scheme. The purpose of this is to simplify
 * configuration within the IoC container - this bean can be wired into everything and
 * configured later instead of having to worry about rewiring complications. If the actual
 * naming scheme is NOT configured, all methods will throw NullPointerException
 */
public class DelegatingNamingScheme implements NamingScheme {

  private NamingScheme actualNamingScheme;

  public void setActualNamingScheme(NamingScheme namingScheme) {
    this.actualNamingScheme = namingScheme;
  }

  @Override
  public void setNameGenerator(NameGenerator<Nameable> generator) {
    actualNamingScheme.setNameGenerator(generator);
  }

  @Override
  public void setNameValidator(NameValidator validator) {
    actualNamingScheme.setNameValidator(validator);
  }

  @Override
  public void setSampleAliasGenerator(NameGenerator<Sample> generator) {
    actualNamingScheme.setSampleAliasGenerator(generator);
  }

  @Override
  public void setSampleAliasValidator(NameValidator validator) {
    actualNamingScheme.setSampleAliasValidator(validator);
  }

  @Override
  public void setLibraryAliasGenerator(NameGenerator<Library> generator) {
    actualNamingScheme.setLibraryAliasGenerator(generator);
  }

  @Override
  public void setLibraryAliasValidator(NameValidator validator) {
    actualNamingScheme.setLibraryAliasValidator(validator);
  }

  @Override
  public void setProjectShortNameValidator(NameValidator validator) {
    actualNamingScheme.setProjectShortNameValidator(validator);
  }

  @Override
  public boolean hasNameGenerator() {
    return actualNamingScheme.hasNameGenerator();
  }

  @Override
  public String generateNameFor(Nameable nameable) throws MisoNamingException {
    return actualNamingScheme.generateNameFor(nameable);
  }

  @Override
  public ValidationResult validateName(String name) {
    return actualNamingScheme.validateName(name);
  }

  @Override
  public boolean duplicateNamesAllowed() {
    return actualNamingScheme.duplicateNamesAllowed();
  }

  @Override
  public boolean hasSampleAliasGenerator() {
    return actualNamingScheme.hasSampleAliasGenerator();
  }

  @Override
  public String generateSampleAlias(Sample sample) throws MisoNamingException {
    return actualNamingScheme.generateSampleAlias(sample);
  }

  @Override
  public ValidationResult validateSampleAlias(String alias) {
    return actualNamingScheme.validateSampleAlias(alias);
  }

  @Override
  public boolean duplicateSampleAliasAllowed() {
    return actualNamingScheme.duplicateSampleAliasAllowed();
  }

  @Override
  public boolean hasLibraryAliasGenerator() {
    return actualNamingScheme.hasLibraryAliasGenerator();
  }

  @Override
  public String generateLibraryAlias(Library library) throws MisoNamingException {
    return actualNamingScheme.generateLibraryAlias(library);
  }

  @Override
  public ValidationResult validateLibraryAlias(String alias) {
    return actualNamingScheme.validateLibraryAlias(alias);
  }

  @Override
  public boolean duplicateLibraryAliasAllowed() {
    return actualNamingScheme.duplicateLibraryAliasAllowed();
  }

  @Override
  public ValidationResult validateProjectShortName(String shortName) {
    return actualNamingScheme.validateProjectShortName(shortName);
  }

  @Override
  public boolean duplicateProjectShortNamesAllowed() {
    return actualNamingScheme.duplicateProjectShortNamesAllowed();
  }

  @Override
  public boolean nullProjectShortNameAllowed() {
    return actualNamingScheme.nullProjectShortNameAllowed();
  }

}

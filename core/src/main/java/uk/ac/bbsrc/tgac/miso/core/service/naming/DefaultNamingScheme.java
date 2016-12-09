package uk.ac.bbsrc.tgac.miso.core.service.naming;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.DefaultLibraryAliasGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.DefaultNameGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.NameGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.AllowAnythingValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.DefaultLibraryAliasValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.DefaultNameValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.DefaultSampleAliasValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.NameValidator;

/**
 * Customizeable NamingScheme which defaults to all default generators and validators
 */
public class DefaultNamingScheme extends AbstractNamingScheme {

  private NameValidator nameValidator = new DefaultNameValidator();
  private NameGenerator<Nameable> nameGenerator = new DefaultNameGenerator();
  private NameValidator sampleAliasValidator = new DefaultSampleAliasValidator();
  private NameGenerator<Sample> sampleAliasGenerator = null;
  private NameValidator libraryAliasValidator = new DefaultLibraryAliasValidator();
  private NameGenerator<Library> libraryAliasGenerator = new DefaultLibraryAliasGenerator();
  private NameValidator projectShortNameValidator = new AllowAnythingValidator();

  @Override
  public void setNameGenerator(NameGenerator<Nameable> generator) {
    this.nameGenerator = generator;
  }

  @Override
  public void setNameValidator(NameValidator validator) {
    this.nameValidator = validator;
  }

  @Override
  public void setSampleAliasGenerator(NameGenerator<Sample> generator) {
    this.sampleAliasGenerator = generator;
  }

  @Override
  public void setSampleAliasValidator(NameValidator validator) {
    this.sampleAliasValidator = validator;
  }

  @Override
  public void setLibraryAliasGenerator(NameGenerator<Library> generator) {
    this.libraryAliasGenerator = generator;
  }

  @Override
  public boolean hasLibraryAliasGenerator() {
    return libraryAliasGenerator != null;
  }

  @Override
  public void setLibraryAliasValidator(NameValidator validator) {
    this.libraryAliasValidator = validator;
  }

  @Override
  protected NameValidator getNameValidator() {
    return nameValidator;
  }

  @Override
  protected NameGenerator<Nameable> getNameGenerator() {
    return nameGenerator;
  }

  @Override
  protected NameValidator getSampleAliasValidator() {
    return sampleAliasValidator;
  }

  @Override
  protected NameGenerator<Sample> getSampleAliasGenerator() {
    return sampleAliasGenerator;
  }

  @Override
  protected NameValidator getLibraryAliasValidator() {
    return libraryAliasValidator;
  }

  @Override
  protected NameGenerator<Library> getLibraryAliasGenerator() {
    return libraryAliasGenerator;
  }

  @Override
  public void setProjectShortNameValidator(NameValidator validator) {
    this.projectShortNameValidator = validator;
  }

  @Override
  protected NameValidator getProjectShortNameValidator() {
    return projectShortNameValidator;
  }

}

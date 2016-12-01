package uk.ac.bbsrc.tgac.miso.core.service.naming;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.DefaultNameGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.NameGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.OicrLibraryAliasGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.OicrSampleAliasGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.DefaultNameValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.NameValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.OicrLibraryAliasValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.OicrSampleAliasValidator;

/**
 * Non-customizeable NamingScheme which conforms to OICR's standard naming scheme
 */
public class OicrNamingScheme extends AbstractNamingScheme {

  private final NameValidator nameValidator = new DefaultNameValidator();
  private final NameGenerator<Nameable> nameGenerator = new DefaultNameGenerator();
  private final NameValidator sampleAliasValidator = new OicrSampleAliasValidator();
  private final NameGenerator<Sample> sampleAliasGenerator = new OicrSampleAliasGenerator();
  private final NameValidator libraryAliasValidator = new OicrLibraryAliasValidator();
  private final NameGenerator<Library> libraryAliasGenerator = new OicrLibraryAliasGenerator();

  @Override
  public void setNameGenerator(NameGenerator<Nameable> generator) {
    throwUnsupported();
  }

  @Override
  public void setNameValidator(NameValidator validator) {
    throwUnsupported();
  }

  @Override
  public void setSampleAliasGenerator(NameGenerator<Sample> generator) {
    throwUnsupported();
  }

  @Override
  public void setSampleAliasValidator(NameValidator validator) {
    throwUnsupported();
  }

  @Override
  public void setLibraryAliasGenerator(NameGenerator<Library> generator) {
    throwUnsupported();
  }

  @Override
  public void setLibraryAliasValidator(NameValidator validator) {
    throwUnsupported();
  }

  private void throwUnsupported() {
    throw new UnsupportedOperationException("runtime customization not supported by this naming scheme");
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

}

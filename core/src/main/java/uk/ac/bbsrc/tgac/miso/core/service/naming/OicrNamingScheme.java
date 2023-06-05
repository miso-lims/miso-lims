package uk.ac.bbsrc.tgac.miso.core.service.naming;

import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.service.SampleNumberPerProjectService;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.DefaultNameGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.NameGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.OicrLibraryAliasGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.OicrLibraryAliquotAliasGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.OicrSampleAliasGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.DefaultNameValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.NameValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.OicrLibraryAliasValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.OicrProjectCodeValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.OicrSampleAliasValidator;

/**
 * Non-customizeable NamingScheme which conforms to OICR's standard naming scheme
 */
public class OicrNamingScheme extends AbstractNamingScheme {

  private final DefaultNameValidator nameValidator = new DefaultNameValidator();
  private final DefaultNameGenerator nameGenerator = new DefaultNameGenerator();
  private final OicrSampleAliasValidator sampleAliasValidator = new OicrSampleAliasValidator();
  private final OicrSampleAliasGenerator sampleAliasGenerator = new OicrSampleAliasGenerator();
  private final OicrLibraryAliasValidator libraryAliasValidator = new OicrLibraryAliasValidator();
  private final OicrLibraryAliasGenerator libraryAliasGenerator = new OicrLibraryAliasGenerator();
  private final OicrLibraryAliquotAliasGenerator libraryAliquotAliasGenerator = new OicrLibraryAliquotAliasGenerator();
  private final OicrProjectCodeValidator projectCodeValidator = new OicrProjectCodeValidator();

  /**
   * Creates a new OicrNamingScheme and attempts to autowire all of its validators' and generators'
   * dependencies. If no WebApplicationContext is available, wiring will be skipped, and setters
   * within this class (e.g. {@link #setSiblingNumberGenerator(SiblingNumberGenerator)}) should be
   * used to complete the necessary wiring manually
   */
  public OicrNamingScheme() {
    SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(sampleAliasGenerator);
    SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(libraryAliasGenerator);
    SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(libraryAliasValidator);
    SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(libraryAliquotAliasGenerator);
  }

  /**
   * Sets the SiblingNumberGenerator to use in generating aliases. Within a Spring context, this will
   * be autowired. This method exists for cases where the OicrNamingScheme is not a Spring-managed
   * bean
   * 
   * @param siblingNumberGenerator
   */
  public void setSiblingNumberGenerator(SiblingNumberGenerator siblingNumberGenerator) {
    sampleAliasGenerator.setSiblingNumberGenerator(siblingNumberGenerator);
    libraryAliasGenerator.setSiblingNumberGenerator(siblingNumberGenerator);
    libraryAliquotAliasGenerator.setSiblingNumberGenerator(siblingNumberGenerator);
  }

  /**
   * Sets the SampleNumberPerProjectService to use in generating aliases. Within a Spring context,
   * this will be autowired. This method exists for cases where the OicrNamingScheme is not a
   * Spring-managed bean
   * 
   * @param sampleNumberPerProjectService
   */
  public void setSampleNumberPerProjectService(SampleNumberPerProjectService sampleNumberPerProjectService) {
    sampleAliasGenerator.setSampleNumberPerProjectService(sampleNumberPerProjectService);
  }

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

  @Override
  public void setLibraryAliquotAliasGenerator(NameGenerator<LibraryAliquot> generator) {
    throwUnsupported();
  }

  @Override
  public void setLibraryAliquotAliasValidator(NameValidator validator) {
    throwUnsupported();
  }

  @Override
  public void setProjectCodeValidator(NameValidator validator) {
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

  @Override
  protected NameValidator getLibraryAliquotAliasValidator() {
    return libraryAliasValidator;
  }

  @Override
  protected NameGenerator<LibraryAliquot> getLibraryAliquotAliasGenerator() {
    return libraryAliquotAliasGenerator;
  }

  @Override
  protected NameValidator getProjectCodeValidator() {
    return projectCodeValidator;
  }

}

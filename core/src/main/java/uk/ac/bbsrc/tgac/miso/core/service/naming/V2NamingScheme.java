package uk.ac.bbsrc.tgac.miso.core.service.naming;

import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.service.SampleNumberPerProjectService;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.DefaultNameGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.NameGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.V2LibraryAliasGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.V2LibraryAliquotAliasGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.V2SampleAliasGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.DefaultNameValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.NameValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.OicrProjectCodeValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.V2LibraryAliasValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.V2LibraryAliquotAliasValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.V2SampleAliasValidator;

/**
 * Non-customizeable NamingScheme which conforms to OICR's V2 standard naming scheme
 */
public class V2NamingScheme extends AbstractNamingScheme {

  private final DefaultNameValidator nameValidator = new DefaultNameValidator();
  private final DefaultNameGenerator nameGenerator = new DefaultNameGenerator();
  private final V2SampleAliasValidator sampleAliasValidator = new V2SampleAliasValidator();
  private final V2SampleAliasGenerator sampleAliasGenerator = new V2SampleAliasGenerator();
  private final V2LibraryAliasValidator libraryAliasValidator = new V2LibraryAliasValidator();
  private final V2LibraryAliasGenerator libraryAliasGenerator = new V2LibraryAliasGenerator();
  private final V2LibraryAliquotAliasValidator libraryAliquotAliasValidator = new V2LibraryAliquotAliasValidator();
  private final V2LibraryAliquotAliasGenerator libraryAliquotAliasGenerator = new V2LibraryAliquotAliasGenerator();
  private final OicrProjectCodeValidator projectShortNameValidator = new OicrProjectCodeValidator();

  /**
   * Creates a new V2NamingScheme and attempts to autowire all of its validators' and generators'
   * dependencies. If no WebApplicationContext is available, wiring will be skipped, and setters
   * within this class (e.g. {@link #setSiblingNumberGenerator(SiblingNumberGenerator)}) should be
   * used to complete the necessary wiring manually
   */
  public V2NamingScheme() {
    SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(sampleAliasGenerator);
    SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(libraryAliasGenerator);
    SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(libraryAliquotAliasGenerator);
  }

  /**
   * Sets the SiblingNumberGenerator to use in generating aliases. Within a Spring context, this will
   * be autowired. This method exists for cases where the V2NamingScheme is not a Spring-managed bean
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
   * this will be autowired. This method exists for cases where the V2NamingScheme is not a
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
    return libraryAliquotAliasValidator;
  }

  @Override
  protected NameGenerator<LibraryAliquot> getLibraryAliquotAliasGenerator() {
    return libraryAliquotAliasGenerator;
  }

  @Override
  protected NameValidator getProjectCodeValidator() {
    return projectShortNameValidator;
  }

}

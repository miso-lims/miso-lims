package uk.ac.bbsrc.tgac.miso.core.service.naming;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.NameGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.NameValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;

/**
 * Master naming scheme to coordinate all name generation and validation
 */
public interface NamingScheme {

  /**
   * Optional method. Sets a generator to be used for all {@link Nameable} object names
   * 
   * @param generator
   * @throws UnsupportedOperationException if this NamingScheme does not accept custom
   *         {@link Nameable} name generators
   */
  public void setNameGenerator(NameGenerator<Nameable> generator);

  /**
   * @return true if name generation for {@link Nameable} objects is supported; false otherwise
   */
  public boolean hasNameGenerator();

  /**
   * Generates a name for a {@link Nameable} object
   * 
   * @param nameable the object to generate a name for
   * @return the generated name
   * @throws MisoNamingException if name generation fails
   * @throws IOException if database access is required and fails
   * @throws UnsupportedOperationException if name generation is not supported. this can be avoided by
   *         checking {@link #hasNameGenerator()} first
   */
  public String generateNameFor(Nameable nameable) throws MisoNamingException, IOException;

  /**
   * Optional method. Sets a validator to be used for all {@link Nameable} object names
   * 
   * @param validator
   * @throws UnsupportedOperationException if this NamingScheme does not accept custom
   *         {@link Nameable} name validators
   */
  public void setNameValidator(NameValidator validator);

  /**
   * Checks that the provided name conforms to the naming scheme
   * 
   * @param name
   * @return the {@link ValidationResult}
   */
  public ValidationResult validateName(String name);

  /**
   * @return true if duplicate {@link Nameable} names are allowed by this NamingScheme
   */
  public boolean duplicateNamesAllowed();

  /**
   * Optional method. Sets a generator to be used for {@link Sample} aliases
   * 
   * @param generator
   * @throws UnsupportedOperationException if this NamingScheme does not accept custom {@link Sample}
   *         alias generators
   */
  public void setSampleAliasGenerator(NameGenerator<Sample> generator);

  /**
   * @return true if {@link Sample} alias generation is supported; false otherwise
   */
  public boolean hasSampleAliasGenerator();

  /**
   * Generates a {@link Sample} alias
   * 
   * @param sample the {@link Sample} to generate an alias for
   * @return the generated alias
   * @throws MisoNamingException if alias generation fails
   * @throws IOException if database access is required and fails
   * @throws UnsupportedOperationException if {@link Sample} alias generation is not supported. this
   *         can be avoided by checking {@link #hasSampleAliasGenerator()} first
   */
  public String generateSampleAlias(Sample sample) throws MisoNamingException, IOException;

  /**
   * Optional method. Sets a validator to be used for {@link Sample} aliases
   * 
   * @param validator
   * @throws UnsupportedOperationException if this NamingScheme does not accept custom {@link Sample}
   *         alias validators
   */
  public void setSampleAliasValidator(NameValidator validator);

  /**
   * Checks that the provided {@link Sample} alias conforms to the naming scheme
   * 
   * @param alias
   * @return the {@link ValidationResult}
   */
  public ValidationResult validateSampleAlias(String alias);

  /**
   * @return true if duplicate {@link Sample} aliases are allowed by this NamingScheme
   */
  public boolean duplicateSampleAliasAllowed();

  /**
   * Optional method. Sets a generator to be used for {@link Library} aliases
   * 
   * @param generator
   * @throws UnsupportedOperationException if this NamingScheme does not accept custom {@link Library}
   *         alias generators
   */
  public void setLibraryAliasGenerator(NameGenerator<Library> generator);

  /**
   * @return true if {@link Library} alias generation is supported; false otherwise
   */
  public boolean hasLibraryAliasGenerator();

  /**
   * Generates a {@link Library} alias
   * 
   * @param library the {@link Library} to generate an alias for
   * @return the generated alias
   * @throws MisoNamingException if alias generation fails
   * @throws IOException if database access is required and fails
   * @throws UnsupportedOperationException if {@link Library} alias generation is not supported. this
   *         can be avoided by checking {@link #hasLibraryAliasGenerator()} first
   */
  public String generateLibraryAlias(Library library) throws MisoNamingException, IOException;

  /**
   * Optional method. Sets a validator to be used for {@link Library} aliases
   * 
   * @param validator
   * @throws UnsupportedOperationException if this NamingScheme does not accept custom {@link Library}
   *         alias validators
   */
  public void setLibraryAliasValidator(NameValidator validator);

  /**
   * Checks that the provided {@link Library} alias conforms to the naming scheme
   * 
   * @param alias
   * @return the {@link ValidationResult}
   */
  public ValidationResult validateLibraryAlias(String alias);

  /**
   * @return true if duplicate {@link Library} aliases are allowed by this NamingScheme
   */
  public boolean duplicateLibraryAliasAllowed();

  /**
   * Optional method. Sets a generator to be used for {@link LibraryAliquot} aliases
   * 
   * @param generator
   * @throws UnsupportedOperationException if this NamingScheme does not accept custom
   *         {@link LibraryAliquot} alias generators
   */
  public void setLibraryAliquotAliasGenerator(NameGenerator<LibraryAliquot> generator);

  /**
   * @return true if {@link LibraryAloquot} alias generation is supported; false otherwise
   */
  public boolean hasLibraryAliquotAliasGenerator();

  /**
   * Generates a {@link LibraryAliquot} alias
   * 
   * @param aliquot the {@link LibraryAliquot} to generate an alias for
   * @return the generated alias
   * @throws MisoNamingException if alias generation fails
   * @throws IOException if database access is required and fails
   * @throws UnsupportedOperationException if {@link LibraryAliquot} alias generation is not
   *         supported. this can be avoided by checking {@link #hasLibraryAliquotAliasGenerator()}
   *         first
   */
  public String generateLibraryAliquotAlias(LibraryAliquot aliquot) throws MisoNamingException, IOException;

  /**
   * Optional method. Sets a validator to be used for {@link LibraryAliquot} aliases
   * 
   * @param validator
   * @throws UnsupportedOperationException if this NamingScheme does not accept custom
   *         {@link LibraryAliquot} alias validators
   */
  public void setLibraryAliquotAliasValidator(NameValidator validator);

  /**
   * Checks that the provided {@link LibraryAliquot} alias conforms to the naming scheme
   * 
   * @param alias
   * @return the {@link ValidationResult}
   */
  public ValidationResult validateLibraryAliquotAlias(String alias);

  /**
   * @return true if duplicate {@link LibraryAliquot} aliases are allowed by this NamingScheme
   */
  public boolean duplicateLibraryAliquotAliasAllowed();

  /**
   * Optional method. Sets a validator to be used for {@link Project} codes
   * 
   * @param validator
   * @throws UnsupportedOperationException if this NamingScheme does not accept custom {@link Project}
   *         code validators
   */
  public void setProjectCodeValidator(NameValidator validator);

  /**
   * Checks that the provided {@link Project} code conforms to the naming scheme
   * 
   * @param name
   * @return the {@link ValidationResult}
   */
  public ValidationResult validateProjectCode(String code);

  /**
   * @return true if duplicate {@link Project} codes are allowed by this NamingScheme
   */
  public boolean duplicateProjectCodesAllowed();

  /**
   * @return true if {@link Project} code is a mandatory field; false if null is allowed
   */
  public boolean nullProjectCodeAllowed();

}

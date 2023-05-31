package uk.ac.bbsrc.tgac.miso.core.service.naming.resolvers;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.service.naming.DefaultNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.OicrNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.V2NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.ClassnameNameGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.DefaultLibraryAliasGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.DefaultLibraryAliquotAliasGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.DefaultNameGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.NameGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.OicrLibraryAliasGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.OicrLibraryAliquotAliasGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.OicrSampleAliasGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.V2LibraryAliasGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.V2LibraryAliquotAliasGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.V2SampleAliasGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.AllowAnythingValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.DefaultLibraryAliasValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.DefaultLibraryAliquotAliasValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.DefaultNameValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.DefaultSampleAliasValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.NameValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.OicrLibraryAliasValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.OicrProjectCodeValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.OicrSampleAliasValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.V2LibraryAliasValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.V2LibraryAliquotAliasValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.V2SampleAliasValidator;

/**
 * Resolves NamingSchemes, NameGenerators, and NameValidators using statically-defined mappings
 */
public class StaticMappedNamingSchemeResolverService implements NamingSchemeResolverService {
  protected static final Logger log = LoggerFactory.getLogger(StaticMappedNamingSchemeResolverService.class);

  private static final Map<String, Class<? extends NamingScheme>> namingSchemes = new HashMap<>();
  private static final Map<String, Class<? extends NameGenerator<Nameable>>> nameGenerators = new HashMap<>();
  private static final Map<String, Class<? extends NameGenerator<Sample>>> sampleAliasGenerators = new HashMap<>();
  private static final Map<String, Class<? extends NameGenerator<Library>>> libraryAliasGenerators = new HashMap<>();
  private static final Map<String, Class<? extends NameGenerator<LibraryAliquot>>> libraryAliquotAliasGenerators =
      new HashMap<>();
  private static final Map<String, Class<? extends NameValidator>> nameValidators = new HashMap<>();
  private static final Map<String, Class<? extends NameValidator>> sampleAliasValidators = new HashMap<>();
  private static final Map<String, Class<? extends NameValidator>> libraryAliasValidators = new HashMap<>();
  private static final Map<String, Class<? extends NameValidator>> libraryAliquotAliasValidators = new HashMap<>();
  private static final Map<String, Class<? extends NameValidator>> projectCodeValidators = new HashMap<>();

  static {
    // Add new naming schemes/generators/validators to the relevant map(s). Use only lowercase for keys

    namingSchemes.put("default", DefaultNamingScheme.class);
    namingSchemes.put("oicr", OicrNamingScheme.class);
    namingSchemes.put("v2", V2NamingScheme.class);

    nameGenerators.put("default", DefaultNameGenerator.class);
    nameGenerators.put("classname", ClassnameNameGenerator.class);

    sampleAliasGenerators.put("oicr", OicrSampleAliasGenerator.class);
    sampleAliasGenerators.put("v2", V2SampleAliasGenerator.class);

    libraryAliasGenerators.put("default", DefaultLibraryAliasGenerator.class);
    libraryAliasGenerators.put("oicr", OicrLibraryAliasGenerator.class);
    libraryAliasGenerators.put("v2", V2LibraryAliasGenerator.class);

    libraryAliquotAliasGenerators.put("default", DefaultLibraryAliquotAliasGenerator.class);
    libraryAliquotAliasGenerators.put("oicr", OicrLibraryAliquotAliasGenerator.class);
    libraryAliquotAliasGenerators.put("v2", V2LibraryAliquotAliasGenerator.class);

    nameValidators.put("default", DefaultNameValidator.class);
    nameValidators.put("allowany", AllowAnythingValidator.class);

    sampleAliasValidators.put("default", DefaultSampleAliasValidator.class);
    sampleAliasValidators.put("allowany", AllowAnythingValidator.class);
    sampleAliasValidators.put("oicr", OicrSampleAliasValidator.class);
    sampleAliasValidators.put("v2", V2SampleAliasValidator.class);

    libraryAliasValidators.put("default", DefaultLibraryAliasValidator.class);
    libraryAliasValidators.put("allowany", AllowAnythingValidator.class);
    libraryAliasValidators.put("oicr", OicrLibraryAliasValidator.class);
    libraryAliasValidators.put("v2", V2LibraryAliasValidator.class);

    libraryAliquotAliasValidators.put("default", DefaultLibraryAliquotAliasValidator.class);
    libraryAliquotAliasValidators.put("allowAny", AllowAnythingValidator.class);
    libraryAliquotAliasValidators.put("oicr", OicrLibraryAliasValidator.class);
    libraryAliquotAliasValidators.put("v2", V2LibraryAliquotAliasValidator.class);

    projectCodeValidators.put("allowany", AllowAnythingValidator.class);
    projectCodeValidators.put("oicr", OicrProjectCodeValidator.class);
  }

  @Override
  public NamingScheme getNamingScheme(String schemeName) {
    Class<? extends NamingScheme> clazz = namingSchemes.get(schemeName.toLowerCase());
    return loadClass(clazz, "naming scheme", schemeName);
  }

  @Override
  public NameGenerator<Nameable> getNameGenerator(String generatorName) {
    Class<? extends NameGenerator<Nameable>> clazz = nameGenerators.get(generatorName.toLowerCase());
    return loadClass(clazz, "name generator", generatorName);
  }

  @Override
  public NameGenerator<Sample> getSampleAliasGenerator(String generatorName) {
    Class<? extends NameGenerator<Sample>> clazz = sampleAliasGenerators.get(generatorName.toLowerCase());
    return loadClass(clazz, "sample alias generator", generatorName);
  }

  @Override
  public NameGenerator<Library> getLibraryAliasGenerator(String generatorName) {
    Class<? extends NameGenerator<Library>> clazz = libraryAliasGenerators.get(generatorName.toLowerCase());
    return loadClass(clazz, "library alias generator", generatorName);
  }

  @Override
  public NameGenerator<LibraryAliquot> getLibraryAliquotAliasGenerator(String generatorName) {
    Class<? extends NameGenerator<LibraryAliquot>> clazz =
        libraryAliquotAliasGenerators.get(generatorName.toLowerCase());
    return loadClass(clazz, "library aliquot alias generator", generatorName);
  }

  @Override
  public NameValidator getNameValidator(String validatorName) {
    Class<? extends NameValidator> clazz = nameValidators.get(validatorName.toLowerCase());
    return loadClass(clazz, "name validator", validatorName);
  }

  @Override
  public NameValidator getSampleAliasValidator(String validatorName) {
    Class<? extends NameValidator> clazz = sampleAliasValidators.get(validatorName.toLowerCase());
    return loadClass(clazz, "sample alias validator", validatorName);
  }

  @Override
  public NameValidator getLibraryAliasValidator(String validatorName) {
    Class<? extends NameValidator> clazz = libraryAliasValidators.get(validatorName.toLowerCase());
    return loadClass(clazz, "library alias validator", validatorName);
  }

  @Override
  public NameValidator getLibraryAliquotAliasValidator(String validatorName) {
    Class<? extends NameValidator> clazz = libraryAliquotAliasValidators.get(validatorName.toLowerCase());
    return loadClass(clazz, "library aliquot alias validator", validatorName);
  }

  @Override
  public NameValidator getProjectCodeValidator(String validatorName) {
    Class<? extends NameValidator> clazz = projectCodeValidators.get(validatorName.toLowerCase());
    return loadClass(clazz, "project short name validator", validatorName);
  }

  private <T> T loadClass(Class<T> clazz, String property, String value) {
    if (clazz == null) {
      log.error("No " + property + " found with name '" + value + "'");
      return null;
    }
    try {
      return clazz.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      log.error("Failed to load " + property + " '" + value + "'", e);
      return null;
    }
  }

}

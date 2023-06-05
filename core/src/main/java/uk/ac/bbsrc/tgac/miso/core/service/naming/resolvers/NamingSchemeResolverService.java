package uk.ac.bbsrc.tgac.miso.core.service.naming.resolvers;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.NameGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.NameValidator;

/**
 * Interface used for loading NamingSchemes, NameGenerators, and NameValidators by name
 */
public interface NamingSchemeResolverService {
  NamingScheme getNamingScheme(String schemeName);

  NameGenerator<Nameable> getNameGenerator(String generatorName);

  NameGenerator<Sample> getSampleAliasGenerator(String generatorName);

  NameGenerator<Library> getLibraryAliasGenerator(String generatorName);

  NameGenerator<LibraryAliquot> getLibraryAliquotAliasGenerator(String generatorName);

  NameValidator getNameValidator(String validatorName);

  NameValidator getSampleAliasValidator(String validatorName);

  NameValidator getLibraryAliasValidator(String validatorName);

  NameValidator getLibraryAliquotAliasValidator(String validatorName);

  NameValidator getProjectCodeValidator(String validatorName);
}

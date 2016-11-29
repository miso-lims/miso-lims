package uk.ac.bbsrc.tgac.miso.core.service.naming;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.NameGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.NameValidator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;

public interface NamingScheme {

  public void setNameGenerator(NameGenerator<Nameable> generator);
  
  public boolean hasNameGenerator();
  
  public String generateNameFor(Nameable nameable) throws MisoNamingException;
  
  public void setNameValidator(NameValidator validator);
  
  public ValidationResult validateName(String name);
  
  public boolean duplicateNamesAllowed();

  public void setSampleAliasGenerator(NameGenerator<Sample> generator);
  
  public boolean hasSampleAliasGenerator();
  
  public String generateSampleAlias(Sample sample) throws MisoNamingException;

  public void setSampleAliasValidator(NameValidator validator);

  public ValidationResult validateSampleAlias(String alias);

  public boolean duplicateSampleAliasAllowed();

  public void setLibraryAliasGenerator(NameGenerator<Library> generator);

  public boolean hasLibraryAliasGenerator();

  public String generateLibraryAlias(Library library) throws MisoNamingException;

  public void setLibraryAliasValidator(NameValidator validator);

  public ValidationResult validateLibraryAlias(String alias);
  
  public boolean duplicateLibraryAliasAllowed();

}

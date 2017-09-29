package uk.ac.bbsrc.tgac.miso.core.service.naming;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.Aliasable;

public interface SiblingNumberGenerator {

  /**
   * Determines the next available siblingNumber to use for the provided partial alias. Assumes that siblingNumber will
   * be appended to the end of the partialAlias to form the complete alias
   * 
   * @param partialAlias the alias being generated, missing only a siblingNumber
   * @return the lowest useable siblingNumber where the alias formed by {@code (partialAlias + siblingNumber)} does not yet exist
   * @throws IOException
   */
  public <T extends Aliasable> int getNextSiblingNumber(Class<T> clazz, String partialAlias) throws IOException;

}

package uk.ac.bbsrc.tgac.miso.core.service.naming;

import java.io.IOException;

public interface SiblingNumberGenerator {

  /**
   * Determines the next available siblingNumber to use for this sample name. Assumes that siblingNumber will
   * be appended to the end of the partialAlias to form the complete alias
   * 
   * @param partialAlias the Sample alias being generated, missing only a siblingNumber
   * @return the lowest useable siblingNumber where the alias formed by {@code (partialAlias + siblingNumber)} does not yet exist
   * @throws IOException
   */
  public int getNextSiblingNumber(String partialAlias) throws IOException;

}

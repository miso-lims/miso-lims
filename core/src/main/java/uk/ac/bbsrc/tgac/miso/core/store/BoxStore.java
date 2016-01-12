package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeAware;

/**
 * This interface defines a DAO for storing Boxes
 * 
 */
public interface BoxStore extends Store<Box>, Remover<Box>, NamingSchemeAware<Box> {
  /**
   * Retrieve a Box from data store given a Box alias.
   * 
   * @param String
   *          alias
   * @return Box
   * @throws IOException
   */
  Box getBoxByAlias(String alias) throws IOException;

  /**
   * Retrieve a Box from data store given a Box barcode
   * 
   * @param String
   *          barcode
   * @return Box
   * @throws IOException
   */
  Box getByBarcode(String barcode) throws IOException;

  /**
   * List all the boxes
   * 
   * @return Collection<Box> boxes
   * @throws IOException
   */
  @Override
  Collection<Box> listAll() throws IOException;

  /**
   * List all the boxes with a limit.
   * 
   * @param long limit
   * @return Collection<Box> boxes
   * @throws IOException
   */
  Collection<Box> listWithLimit(long limit) throws IOException;

  /**
   * List all the boxes by alias
   * 
   * @param String
   *          alias
   * @returns Collection<Box> boxes
   * @throws IOException
   */
  Collection<Box> listByAlias(String alias) throws IOException;

  /**
   * List all the boxes matching a query
   * 
   * @param String
   *          query
   * @return Collection<Box> boxes
   * @throws IOException
   */
  Collection<Box> listBySearch(String query) throws IOException;

  BoxUse getUseById(long id) throws IOException;

  BoxSize getSizeById(long id) throws IOException;

  Collection<BoxUse> listAllBoxUses() throws IOException;
  
  Collection<String> listAllBoxUsesStrings() throws IOException;

  Collection<BoxSize> listAllBoxSizes() throws IOException;

  void emptySingleTube(Box box, String position) throws IOException;

  void emptyAllTubes(Box box) throws IOException;
}

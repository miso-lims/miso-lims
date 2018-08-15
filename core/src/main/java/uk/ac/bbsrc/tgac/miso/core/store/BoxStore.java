package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.BoxableId;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

/**
 * This interface defines a DAO for storing Boxes
 * 
 */
public interface BoxStore extends Store<Box>, PaginatedDataSource<Box> {

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
   * List all Boxes associated with ids from the given id list
   * 
   * @return List<Box>
   * @throws IOException
   *           when the objects cannot be retrieved
   */
  List<Box> getByIdList(List<Long> idList) throws IOException;

  BoxUse getUseById(long id) throws IOException;

  BoxSize getSizeById(long id) throws IOException;

  public List<Box> getBySearch(String search);

  public List<Box> getByPartialSearch(String search, boolean onlyMatchBeginning);

  Collection<BoxUse> listAllBoxUses() throws IOException;
  
  Collection<String> listAllBoxUsesStrings() throws IOException;

  Collection<BoxSize> listAllBoxSizes() throws IOException;
  
  void removeBoxableFromBox(Boxable boxable) throws IOException;

  void removeBoxableFromBox(BoxableView boxable) throws IOException;

  /**
   * @return a map containing all column names and max lengths from the Box table
   * @throws IOException
   */
  public Map<String, Integer> getBoxColumnSizes() throws IOException;

  public BoxableView getBoxableView(BoxableId id) throws IOException;

  public BoxableView getBoxableViewByPreMigrationId(Long preMigrationId) throws IOException;

  public List<BoxableView> getBoxableViewsByBarcodeList(Collection<String> barcodes) throws IOException;

  public List<BoxableView> getBoxableViewsByIdList(Collection<BoxableId> ids) throws IOException;

  public List<BoxableView> getBoxContents(long boxId) throws IOException;

  /**
   * Finds BoxableViews with identificationBarcode, name, or alias matching the provided search string. Returns exact matches only,
   * and excludes any discarded items
   * 
   * @param search string to search for
   * @return all matches
   */
  public List<BoxableView> getBoxableViewsBySearch(String search);

  public Boxable getBoxable(BoxableId id);

  public void saveBoxable(Boxable boxable);

}

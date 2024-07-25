package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.BoxableId;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.box.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

/**
 * This interface defines a DAO for storing Boxes
 * 
 */
public interface BoxStore extends ProviderDao<Box>, PaginatedDataSource<Box> {

  public long save(Box box) throws IOException;

  /**
   * Retrieve a Box from data store given a Box alias.
   * 
   * @param String alias
   * @return Box
   * @throws IOException
   */
  Box getBoxByAlias(String alias) throws IOException;

  /**
   * List all Boxes associated with ids from the given id list
   * 
   * @return List<Box>
   * @throws IOException when the objects cannot be retrieved
   */
  List<Box> listByIdList(List<Long> idList) throws IOException;

  public List<Box> getBySearch(String search);

  public List<Box> getByPartialSearch(String search, boolean onlyMatchBeginning);

  void removeBoxableFromBox(BoxableView boxable) throws IOException;

  public BoxableView getBoxableView(BoxableId id) throws IOException;

  public List<BoxableView> getBoxableViewsByBarcodeList(Collection<String> barcodes) throws IOException;

  public List<BoxableView> getBoxableViewsByIdList(Collection<BoxableId> ids) throws IOException;

  public List<BoxableView> getBoxContents(long boxId) throws IOException;

  /**
   * Finds BoxableViews with identificationBarcode, name, or alias matching the provided search
   * string. Returns exact matches only, and excludes any discarded items
   * 
   * @param search string to search for
   * @return all matches
   */
  public List<BoxableView> getBoxableViewsBySearch(String search);

  public Boxable getBoxable(BoxableId id);

  public void saveBoxable(Boxable boxable);

}

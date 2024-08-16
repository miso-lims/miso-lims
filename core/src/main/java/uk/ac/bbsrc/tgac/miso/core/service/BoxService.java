package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.BoxableId;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.box.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface BoxService
    extends PaginatedDataSource<Box>, BarcodableService<Box>, DeleterService<Box>, BulkSaveService<Box> {
  @Override
  public default EntityType getEntityType() {
    return EntityType.BOX;
  }

  public void discardAllContents(Box box) throws IOException;

  public void discardSingleItem(Box box, String position) throws IOException;

  public Box getByAlias(String alias) throws IOException;

  public List<BoxableView> getBoxContents(long id) throws IOException;

  @Override
  public List<Box> listByIdList(List<Long> idList) throws IOException;

  /**
   * Obtain a list of Boxables by supplied identificationBarcode list
   */
  public Collection<BoxableView> getViewsFromBarcodeList(Collection<String> barcodeList) throws IOException;

  public List<Box> getBySearch(String search);

  public List<Box> getByPartialSearch(String search, boolean onlyMatchBeginning);

  public long save(Box box) throws IOException;

  /**
   * Finds BoxableViews with identificationBarcode, name, or alias matching the provided search
   * string. Returns exact matches only, and excludes any discarded items
   *
   * @param search string to search for
   * @return all matches
   */
  public List<BoxableView> getBoxableViewsBySearch(String search);

  public BoxableView getBoxableView(BoxableId id) throws IOException;

  /**
   * Moves a Boxable from its current (persisted) location to the location specified in the object's
   * box and boxPosition
   *
   * @param boxable Boxable to update, with its box (id) and boxPosition set accordingly
   * @throws IOException
   */
  public void updateBoxableLocation(Boxable boxable) throws IOException;

  public void throwIfBoxPositionIsFilled(Boxable boxable) throws IOException;

}

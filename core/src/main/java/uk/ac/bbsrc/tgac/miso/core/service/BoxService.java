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
  default EntityType getEntityType() {
    return EntityType.BOX;
  }

  void discardAllContents(Box box) throws IOException;

  void discardSingleItem(Box box, String position) throws IOException;

  Box getByAlias(String alias) throws IOException;

  List<BoxableView> getBoxContents(long id) throws IOException;

  @Override
  List<Box> listByIdList(List<Long> idList) throws IOException;

  /**
   * Obtain a list of Boxables by supplied identificationBarcode list
   */
  Collection<BoxableView> getViewsFromBarcodeList(Collection<String> barcodeList) throws IOException;

  List<Box> getBySearch(String search);

  List<Box> getByPartialSearch(String search, boolean onlyMatchBeginning);

  long save(Box box) throws IOException;

  /**
   * Finds BoxableViews with identificationBarcode, name, or alias matching the provided search
   * string. Returns exact matches only, and excludes any discarded items
   *
   * @param search string to search for
   * @return all matches
   */
  List<BoxableView> getBoxableViewsBySearch(String search);

  BoxableView getBoxableView(BoxableId id) throws IOException;

  public void prepareBoxableLocation(Boxable pendingBoxable, boolean existingDistributionTransfer) throws IOException;

  /**
   * Moves a Boxable from its current (persisted) location to the location specified in the object's
   * pendingBoxId and pendingBoxPosition. prepareBoxableLocation MUST be called at some point before
   * persisting, and before this method is called
   *
   * @param boxable Boxable to update, with its box (id) and boxPosition set accordingly
   * @throws IOException
   */
  void updateBoxableLocation(Boxable boxable) throws IOException;

}

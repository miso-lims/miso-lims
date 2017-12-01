package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface BoxService extends PaginatedDataSource<Box> {

  public void deleteBox(Box box) throws IOException;

  public void discardAllTubes(Box box) throws IOException;

  public void discardSingleTube(Box box, String position) throws IOException;

  public Box get(long boxId) throws IOException;

  public Box getByAlias(String alias) throws IOException;

  public Box getByBarcode(String barcode) throws IOException;

  public Map<String, Integer> getColumnSizes() throws IOException;

  public BoxableView getViewByBarcode(String barcode) throws IOException;

  /**
   * Obtain a list of Boxables by supplied identificationBarcode list
   */
  public Collection<BoxableView> getViewsFromBarcodeList(Collection<String> barcodeList) throws IOException;

  public Collection<BoxSize> listSizes() throws IOException;

  /**
   * Obtain a list of all of the box uses
   */
  public Collection<BoxUse> listUses() throws IOException;

  public long save(Box box) throws IOException;

}

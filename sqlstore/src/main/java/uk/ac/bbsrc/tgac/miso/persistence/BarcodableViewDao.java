package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BarcodableReference;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BarcodableView;

public interface BarcodableViewDao {

  public List<BarcodableView> searchByBarcode(String barcode, Collection<Barcodable.EntityType> typeFilter);

  /**
   * Searches against all Barcodable entities using exact matches
   * @param query name, alias, or barcode of a Barcodable entity
   */
  public List<BarcodableView> search(String query);

  public List<BarcodableView> searchByAlias(String alias, Collection<Barcodable.EntityType> typeFilter);

  public BarcodableReference checkForExisting(String identificationBarcode) throws IOException;
}

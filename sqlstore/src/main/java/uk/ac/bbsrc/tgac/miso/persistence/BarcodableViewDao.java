package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BarcodableView;

public interface BarcodableViewDao {
  List<BarcodableView> searchByBarcode(String barcode);

  List<BarcodableView> searchByBarcode(String barcode, Collection<Barcodable.EntityType> typeFilter);

  /**
   * Searches against all Barcodable entities using exact matches
   * @param query name, alias, or barcode of a Barcodable entity
   */
  List<BarcodableView> search(String query);

  List<BarcodableView> searchByAlias(String alias);

  List<BarcodableView> searchByAlias(String alias, Collection<Barcodable.EntityType> typeFilter);
}

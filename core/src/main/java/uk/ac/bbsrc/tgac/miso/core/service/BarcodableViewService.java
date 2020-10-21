package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BarcodableView;

public interface BarcodableViewService {
  public List<BarcodableView> searchByBarcode(String barcode);

  public List<BarcodableView> searchByBarcode(String barcode, Collection<Barcodable.EntityType> typeFilter);

  /**
   * Searches against all Barcodable entities using exact matches
   * @param query name, alias, or barcode of a Barcodable entity
   */
  public List<BarcodableView> search(String query);

  /**
   * Fetch the full entity object represented by the provided BarcodableView.
   * Return type depends on the target type of the provided view.
   * Incorrect usage (e.g. view.id.targetType != SAMPLE && Sample s = getEntity(view)) will result in a ClassCastException
   */
  public <T extends Barcodable> T getEntity(BarcodableView view) throws IOException;

  public List<BarcodableView> searchByAlias(String alias);

  public List<BarcodableView> searchByAlias(String alias, Collection<Barcodable.EntityType> typeFilter);

}

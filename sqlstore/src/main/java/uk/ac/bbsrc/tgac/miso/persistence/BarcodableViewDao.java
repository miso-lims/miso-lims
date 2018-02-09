package uk.ac.bbsrc.tgac.miso.persistence;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BarcodableView;

import java.util.Collection;
import java.util.List;

public interface BarcodableViewDao {
  List<BarcodableView> searchByBarcode(String barcode);

  List<BarcodableView> searchByBarcode(String barcode, Collection<Barcodable.EntityType> typeFilter);
}

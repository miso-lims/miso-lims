package uk.ac.bbsrc.tgac.miso.core.service;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;

public interface BarcodableService<T extends Barcodable> extends ProviderService<T> {

  EntityType getEntityType();

}

package uk.ac.bbsrc.tgac.miso.service;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;

public interface BarcodableService<T extends Barcodable> extends ProviderService<T> {

  EntityType getEntityType();

}

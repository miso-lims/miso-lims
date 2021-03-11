package uk.ac.bbsrc.tgac.miso.core.service;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.Workstation;

public interface WorkstationService
    extends BarcodableService<Workstation>, DeleterService<Workstation>, ListService<Workstation>, BulkSaveService<Workstation> {

  @Override
  public default EntityType getEntityType() {
    return EntityType.WORKSTATION;
  }

}

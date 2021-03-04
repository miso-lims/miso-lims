package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.Kit;

public interface KitService extends ListService<Kit> {

  public long saveKit(Kit kit) throws IOException;

  public Kit getKitByLotNumber(String lotNumber) throws IOException;

}

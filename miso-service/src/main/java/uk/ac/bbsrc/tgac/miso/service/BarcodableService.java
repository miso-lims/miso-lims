package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;

public interface BarcodableService {
  EntityType getEntityType();

  Barcodable get(long id) throws IOException;
}

package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;

public interface BoxSizeDao extends BulkSaveDao<BoxSize> {

  public long getUsage(BoxSize boxSize) throws IOException;

}

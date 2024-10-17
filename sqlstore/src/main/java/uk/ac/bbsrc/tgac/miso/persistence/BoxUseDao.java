package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;

public interface BoxUseDao extends BulkSaveDao<BoxUse> {

  public BoxUse getByAlias(String alias) throws IOException;

  public long getUsage(BoxUse boxUse) throws IOException;

}

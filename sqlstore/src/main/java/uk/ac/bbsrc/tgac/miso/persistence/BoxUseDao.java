package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;

public interface BoxUseDao extends SaveDao<BoxUse> {

  public BoxUse getByAlias(String alias) throws IOException;

  public List<BoxUse> listByIdList(List<Long> idList) throws IOException;

  public long getUsage(BoxUse boxUse) throws IOException;

}

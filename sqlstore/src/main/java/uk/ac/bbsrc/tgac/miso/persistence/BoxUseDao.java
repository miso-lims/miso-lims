package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;

public interface BoxUseDao {

  public BoxUse get(long id);

  public BoxUse getByAlias(String alias);

  public List<BoxUse> list();

  public long create(BoxUse boxUse);

  public long update(BoxUse boxUse);

  public long getUsage(BoxUse boxUse);

}

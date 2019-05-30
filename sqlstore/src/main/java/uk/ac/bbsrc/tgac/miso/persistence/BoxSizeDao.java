package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;

public interface BoxSizeDao {

  public BoxSize get(long id);

  public List<BoxSize> list();

  public long create(BoxSize boxSize);

  public long update(BoxSize boxSize);

  public long getUsage(BoxSize boxSize);

}

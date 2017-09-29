package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Stain;

public interface StainDao {

  public List<Stain> list();

  public Stain get(long id);
}

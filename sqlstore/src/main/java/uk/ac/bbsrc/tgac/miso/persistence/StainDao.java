package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Stain;

public interface StainDao {

  public List<Stain> list() throws IOException;

  public Stain get(long id) throws IOException;

  public Stain getByName(String name) throws IOException;

  public long create(Stain stain) throws IOException;

  public long update(Stain stain) throws IOException;

  public long getUsage(Stain stain) throws IOException;

}

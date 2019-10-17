package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;

public interface IndexFamilyDao extends SaveDao<IndexFamily> {

  public IndexFamily getByName(String name) throws IOException;

  public long getUsage(IndexFamily indexFamily) throws IOException;

}

package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.type.TissuePieceType;

public interface TissuePieceTypeDao extends SaveDao<TissuePieceType> {

  public long getUsage(TissuePieceType type) throws IOException;

  public TissuePieceType getByName(String name);

}

package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.type.TissuePieceType;

public interface TissuePieceTypeDao extends BulkSaveDao<TissuePieceType> {

  long getUsage(TissuePieceType type) throws IOException;

  TissuePieceType getByName(String name) throws IOException;

}

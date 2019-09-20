package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface TargetedSequencingStore extends SaveDao<TargetedSequencing>, PaginatedDataSource<TargetedSequencing> {

  public TargetedSequencing getByAlias(String alias) throws IOException;

  public long getUsage(TargetedSequencing targetedSequencing) throws IOException;

}

package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface TargetedSequencingStore extends BulkSaveDao<TargetedSequencing>,
    PaginatedDataSource<TargetedSequencing> {

  TargetedSequencing getByAlias(String alias) throws IOException;

  long getUsage(TargetedSequencing targetedSequencing) throws IOException;

}

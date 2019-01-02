package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface TargetedSequencingStore extends Store<TargetedSequencing>, PaginatedDataSource<TargetedSequencing> {

  List<TargetedSequencing> list(List<Long> targetedSequencingIds) throws IOException;

}

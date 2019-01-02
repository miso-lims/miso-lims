package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface TargetedSequencingService extends PaginatedDataSource<TargetedSequencing> {

  TargetedSequencing get(long targetedSequencingId) throws IOException;

  Collection<TargetedSequencing> list() throws IOException;

  List<TargetedSequencing> list(List<Long> targetedSequencingIds) throws IOException;
}

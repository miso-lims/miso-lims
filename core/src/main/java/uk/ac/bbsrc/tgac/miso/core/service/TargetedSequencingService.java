package uk.ac.bbsrc.tgac.miso.core.service;

import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface TargetedSequencingService extends DeleterService<TargetedSequencing>, ListService<TargetedSequencing>,
    SaveService<TargetedSequencing>, PaginatedDataSource<TargetedSequencing> {

}

package uk.ac.bbsrc.tgac.miso.core.service;

import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface TargetedSequencingService extends BulkSaveService<TargetedSequencing>,
    DeleterService<TargetedSequencing>, ListService<TargetedSequencing>, PaginatedDataSource<TargetedSequencing> {

}

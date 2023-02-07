package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.PartitionQCType;

public interface PartitionQcTypeService extends BulkSaveService<PartitionQCType>, DeleterService<PartitionQCType>,
    ListService<PartitionQCType> {

}

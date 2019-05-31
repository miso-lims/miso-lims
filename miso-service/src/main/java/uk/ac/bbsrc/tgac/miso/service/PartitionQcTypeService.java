package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.PartitionQCType;
import uk.ac.bbsrc.tgac.miso.core.service.SaveService;

public interface PartitionQcTypeService extends DeleterService<PartitionQCType>, SaveService<PartitionQCType> {

  public List<PartitionQCType> list() throws IOException;

}

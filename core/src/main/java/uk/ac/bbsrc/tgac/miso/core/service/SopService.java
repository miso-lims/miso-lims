package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop.SopCategory;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface SopService extends DeleterService<Sop>, ListService<Sop>, BulkSaveService<Sop>, PaginatedDataSource<Sop> {

  public List<Sop> listByCategory(SopCategory category) throws IOException;

}

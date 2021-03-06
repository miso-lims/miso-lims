package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibraryAliquotView;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface ListLibraryAliquotViewService extends PaginatedDataSource<ListLibraryAliquotView> {

  public ListLibraryAliquotView get(Long aliquotId) throws IOException;

  public List<ListLibraryAliquotView> listByIdList(List<Long> aliquotIds) throws IOException;
}

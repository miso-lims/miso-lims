package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibaryAliquotView;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface ListLibraryAliquotViewDao extends PaginatedDataSource<ListLibaryAliquotView> {

  public ListLibaryAliquotView get(Long aliquotId) throws IOException;

  public List<ListLibaryAliquotView> listByIdList(List<Long> aliquotIds) throws IOException;

}

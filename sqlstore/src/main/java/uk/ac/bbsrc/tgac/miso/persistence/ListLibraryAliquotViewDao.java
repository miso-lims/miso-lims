package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibraryAliquotView;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface ListLibraryAliquotViewDao extends PaginatedDataSource<ListLibraryAliquotView> {

  public ListLibraryAliquotView get(Long aliquotId) throws IOException;

  public List<ListLibraryAliquotView> listByIdList(Collection<Long> aliquotIds) throws IOException;

}

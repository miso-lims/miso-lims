package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListWorksetView;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface ListWorksetViewStore extends PaginatedDataSource<ListWorksetView> {

  public List<ListWorksetView> listBySearch(String query) throws IOException;

}

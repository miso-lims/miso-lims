package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListTransferView;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface ListTransferViewService extends PaginatedDataSource<ListTransferView> {

  public List<ListTransferView> listBySample(Sample sample) throws IOException;

  public List<ListTransferView> listByLibrary(Library library) throws IOException;

  public List<ListTransferView> listByLibraryAliquot(LibraryAliquot aliquot) throws IOException;

  public List<ListTransferView> listByPool(Pool pool) throws IOException;

}

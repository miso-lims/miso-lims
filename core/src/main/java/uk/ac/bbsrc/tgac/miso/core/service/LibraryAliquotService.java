package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface LibraryAliquotService extends PaginatedDataSource<LibraryAliquot>, BarcodableService<LibraryAliquot>,
    DeleterService<LibraryAliquot>, SaveService<LibraryAliquot> {

  @Override
  default EntityType getEntityType() {
    return EntityType.LIBRARY_ALIQUOT;
  }

  int count() throws IOException;

  List<LibraryAliquot> listByLibraryId(Long libraryId) throws IOException;

  List<LibraryAliquot> listByIdList(List<Long> idList) throws IOException;

  List<LibraryAliquot> list() throws IOException;

  LibraryAliquot getByBarcode(String barcode) throws IOException;

}

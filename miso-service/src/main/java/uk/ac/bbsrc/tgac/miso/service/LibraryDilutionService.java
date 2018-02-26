package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface LibraryDilutionService extends PaginatedDataSource<LibraryDilution>, BarcodableService {
  @Override
  default EntityType getEntityType() {
    return EntityType.DILUTION;
  }

  @Override
  LibraryDilution get(long dilutionId) throws IOException;

  Long create(LibraryDilution dilution) throws IOException;

  void update(LibraryDilution dilution) throws IOException;

  int count() throws IOException;

  List<LibraryDilution> listByLibraryId(Long libraryId) throws IOException;

  List<LibraryDilution> listByIdList(List<Long> idList) throws IOException;

  List<LibraryDilution> list() throws IOException;

  LibraryDilution getByBarcode(String barcode) throws IOException;

}

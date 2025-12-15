package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.jena.atlas.lib.Lib;
import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface LibraryAliquotService extends PaginatedDataSource<LibraryAliquot>, BarcodableService<LibraryAliquot>,
    DeleterService<LibraryAliquot>, BulkSaveService<LibraryAliquot> {

  @Override
  public default EntityType getEntityType() {
    return EntityType.LIBRARY_ALIQUOT;
  }

  public List<LibraryAliquot> listByLibraryId(Long libraryId) throws IOException;

  public List<LibraryAliquot> listByPoolIds(Collection<Long> poolIds) throws IOException;

  public List<LibraryAliquot> list() throws IOException;

  public LibraryAliquot getByBarcode(String barcode) throws IOException;

  public LibraryAliquot save(LibraryAliquot libraryAliquot) throws IOException;

}

package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.EntityReference;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface LibraryService extends PaginatedDataSource<Library>, BarcodableService<Library>, DeleterService<Library>,
    NoteService<Library>, BulkSaveService<Library> {

  @Override
  public default EntityType getEntityType() {
    return EntityType.LIBRARY;
  }

  public int count() throws IOException;

  public long countBySearch(String querystr) throws IOException;

  public List<Library> list() throws IOException;

  public EntityReference getAdjacentLibrary(Library library, boolean before) throws IOException;

  public Library getByBarcode(String barcode) throws IOException;

  public List<Library> listByBarcodeList(List<String> barcodeList) throws IOException;

  public List<Library> listByIdList(List<Long> idList) throws IOException;

  public Library getByPositionId(long positionId) throws IOException;

  public List<Library> listBySearch(String querystr) throws IOException;

  public List<Library> listByAlias(String alias) throws IOException;

  public List<Library> searchByCreationDate(Date from, Date to) throws IOException;

  public List<Library> listBySampleId(long sampleId) throws IOException;

  public List<Library> listByProjectId(long projectId) throws IOException;

}

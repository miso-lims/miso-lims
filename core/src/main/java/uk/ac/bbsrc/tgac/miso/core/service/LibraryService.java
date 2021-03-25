package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
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

  public List<Library> list() throws IOException;

  public EntityReference getAdjacentLibrary(Library library, boolean before) throws IOException;

  public List<Library> listBySampleId(long sampleId) throws IOException;

  public List<Library> listByProjectId(long projectId) throws IOException;

}

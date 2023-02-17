package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.EntityReference;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface LibraryService
    extends PaginatedDataSource<Library>, BarcodableService<Library>, DeleterService<Library>,
    NoteService<Library>, BulkSaveService<Library> {

  @Override
  default EntityType getEntityType() {
    return EntityType.LIBRARY;
  }

  List<Library> list() throws IOException;

  EntityReference getAdjacentLibrary(Library library, boolean before) throws IOException;

  List<Library> listBySampleId(long sampleId) throws IOException;

  List<Library> listByProjectId(long projectId) throws IOException;

  /**
   * Get all library IDs related to a specified requisition
   * 
   * @param requisitionId ID of the requisition
   * @return a list of all library IDs descended from the requisition's requisitioned and
   *         supplementary samples
   * @throws IOException
   */
  List<Long> listIdsByRequisitionId(long requisitionId) throws IOException;

  List<Long> listIdsByAncestorSampleIds(Collection<Long> sampleIds) throws IOException;

}

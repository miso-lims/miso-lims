package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Requisitionable;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface RequisitionService
    extends DeleterService<Requisition>, NoteService<Requisition>, PaginatedDataSource<Requisition>,
    BulkSaveService<Requisition> {

  Requisition getByAlias(String alias) throws IOException;

  List<Requisition> listByIdList(List<Long> ids) throws IOException;

  Requisition moveSamplesToRequisition(Requisition requisition, List<Sample> samples) throws IOException;

  void addSupplementalSamples(Requisition requisition, Collection<Sample> samples) throws IOException;

  void removeSupplementalSamples(Requisition requisition, Collection<Sample> samples) throws IOException;

  Requisition moveLibrariesToRequisition(Requisition requisition, List<Library> libraries) throws IOException;

  void addSupplementalLibraries(Requisition requisition, Collection<Library> libraries) throws IOException;

  void removeSupplementalLibraries(Requisition requisition, Collection<Library> libraries) throws IOException;

  List<Long> getSamplesDescendantslList(List<Long> sampleIDs, long requisitonId) throws IOException;

  /**
   * Loads the item's requisition, creating it if necessary, and populates it into the Requisitionable
   * object. Does nothing if the item does not have a requisition
   * 
   * @param item
   * @throws IOException
   */
  void findOrCreateRequisition(Requisitionable item) throws IOException;

}

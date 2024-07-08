package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.EntityReference;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

/**
 * Defines a DAO interface for storing Libraries
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public interface LibraryStore extends SaveDao<Library>, PaginatedDataSource<Library> {

  List<Long> listByAlias(String alias) throws IOException;

  /**
   * List all Libraries generated from a Sample given a parent Sample ID
   *
   * @param sampleId of type long
   * @return Collection<Library>
   * @throws IOException
   */
  List<Library> listBySampleId(long sampleId) throws IOException;

  /**
   * List all Libraries that are related to a Project given a Project ID
   *
   * @param projectId of type long
   * @return Collection<Library>
   * @throws IOException
   */
  List<Library> listByProjectId(long projectId) throws IOException;

  /**
   * List all Libraries associated with ids from the given id list
   *
   * @return Collection<Library>
   * @throws IOException when the objects cannot be retrieved or read
   */
  List<Library> listByIdList(List<Long> idList) throws IOException;

  EntityReference getAdjacentLibrary(Library library, boolean before) throws IOException;

  /**
   * Get all library IDs related to a specified requisition
   * 
   * @param requisitionId ID of the requisition
   * @return a list of all library IDs descended from the requisition's requisitioned and
   *         supplementary samples
   * @throws IOException
   */
  List<Long> listIdsBySampleRequisitionId(long requisitionId) throws IOException;

  List<Long> listIdsByAncestorSampleIdList(Collection<Long> sampleIds, Long effectiveRequisitionId) throws IOException;

}

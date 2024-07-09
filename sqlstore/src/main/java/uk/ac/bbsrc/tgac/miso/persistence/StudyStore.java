package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

/**
 * Defines a DAO interface for storing Studies
 * 
 * @author Rob Davey
 * @since version
 */
public interface StudyStore extends SaveDao<Study>, PaginatedDataSource<Study> {

  public Study getByAlias(String alias) throws IOException;


  /**
   * List all Studies that are carried out as part of a parent Project given a Project ID
   * 
   * @param projectId of type long
   * @return Collection<Study>
   * @throws IOException
   */
  public Collection<Study> listByProjectId(long projectId) throws IOException;

  /**
   * List all persisted objects
   * 
   * @return Collection<Study>
   * @throws IOException when the objects cannot be retrieved
   */
  public Collection<Study> listAllWithLimit(long limit) throws IOException;

  public long getUsage(Study study) throws IOException;

}

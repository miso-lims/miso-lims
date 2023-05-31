package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

/**
 * Defines a DAO interface for storing Projects
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface ProjectStore extends PaginatedDataSource<Project>, SaveDao<Project> {
  /**
   * Get a Project given a title
   * 
   * @param title of type String
   * @return Project
   * @throws IOException when
   */
  Project getByTitle(String title) throws IOException;

  Project getByCode(String code) throws IOException;

  long getUsage(Project project) throws IOException;

}

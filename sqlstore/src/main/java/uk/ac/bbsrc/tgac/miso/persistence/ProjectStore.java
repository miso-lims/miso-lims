package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.Project;

/**
 * Defines a DAO interface for storing Projects
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface ProjectStore extends Store<Project> {
  /**
   * Get a Project given a title
   * 
   * @param title of type String
   * @return Project
   * @throws IOException when
   */
  Project getByTitle(String title) throws IOException;

  Project getByCode(String code) throws IOException;

  /**
   * List all Projects that match a search criteria
   * 
   * @param query of type String
   * @return Collection<Project>
   * @throws IOException when
   */
  Collection<Project> listBySearch(String query) throws IOException;

  long getUsage(Project project) throws IOException;

}

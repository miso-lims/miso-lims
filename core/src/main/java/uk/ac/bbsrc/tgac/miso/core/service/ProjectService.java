package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.Project;

public interface ProjectService extends DeleterService<Project>, ListService<Project>, SaveService<Project> {


  public Project getProjectByCode(String projectCode) throws IOException;

  public Collection<Project> listAllProjectsBySearch(String query) throws IOException;

  public boolean hasSamples(Project project) throws IOException;

}

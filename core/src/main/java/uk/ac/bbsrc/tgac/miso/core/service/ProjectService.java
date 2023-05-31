package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface ProjectService
    extends DeleterService<Project>, ListService<Project>, PaginatedDataSource<Project>, SaveService<Project> {

  public Project getProjectByCode(String projectCode) throws IOException;

  public boolean hasSamples(Project project) throws IOException;

}

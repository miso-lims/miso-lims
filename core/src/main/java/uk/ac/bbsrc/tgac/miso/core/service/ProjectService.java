package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ProjectView;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface ProjectService
    extends DeleterService<Project>, ListService<Project>, PaginatedDataSource<Project>, SaveService<Project> {

  Project getProjectByCode(String projectCode) throws IOException;

  boolean hasSamples(Project project) throws IOException;

  List<ProjectView> listMinimalViews() throws IOException;

}

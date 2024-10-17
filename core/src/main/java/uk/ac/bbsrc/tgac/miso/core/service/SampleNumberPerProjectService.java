package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.SampleNumberPerProject;

public interface SampleNumberPerProjectService {

  SampleNumberPerProject get(Long sampleNumberPerProjectId) throws IOException;

  SampleNumberPerProject getByProject(Project project) throws IOException;

  Long create(SampleNumberPerProject sampleNumberPerProject, Long projectId) throws IOException;

  void update(SampleNumberPerProject sampleNumberPerProject) throws IOException;

  void delete(SampleNumberPerProject sampleNumberPerProject) throws IOException;

  Set<SampleNumberPerProject> getAll() throws IOException;

  String nextNumber(Project project, String partialAlias) throws IOException;
}

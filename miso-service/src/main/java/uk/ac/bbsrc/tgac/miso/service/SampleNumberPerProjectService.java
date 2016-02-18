package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.SampleNumberPerProject;

public interface SampleNumberPerProjectService {

  SampleNumberPerProject get(Long sampleNumberPerProjectId) throws IOException;

  Long create(SampleNumberPerProject sampleNumberPerProject, Long projectId) throws IOException;

  void update(SampleNumberPerProject sampleNumberPerProject) throws IOException;

  Set<SampleNumberPerProject> getAll() throws IOException;

  void delete(Long sampleNumberPerProjectId) throws IOException;

}
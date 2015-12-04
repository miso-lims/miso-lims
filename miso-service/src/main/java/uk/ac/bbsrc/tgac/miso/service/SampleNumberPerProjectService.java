package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.SampleNumberPerProject;

public interface SampleNumberPerProjectService {

  SampleNumberPerProject get(Long sampleNumberPerProjectId);

  Long create(SampleNumberPerProject sampleNumberPerProject) throws IOException;

  void update(SampleNumberPerProject sampleNumberPerProject) throws IOException;

  Set<SampleNumberPerProject> getAll();

  void delete(Long sampleNumberPerProjectId);

}
package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.Subproject;

public interface SubprojectService extends DeleterService<Subproject> {

  Long create(Subproject subproject, Long parentProjectId) throws IOException;

  void update(Subproject subproject) throws IOException;

  Set<Subproject> getAll() throws IOException;

  Set<Subproject> getByProjectId(Long projectId) throws IOException;

}
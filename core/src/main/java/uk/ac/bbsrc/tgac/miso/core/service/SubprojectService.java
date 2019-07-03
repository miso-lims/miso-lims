package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.Subproject;

public interface SubprojectService extends DeleterService<Subproject>, ListService<Subproject> {

  Long create(Subproject subproject, Long parentProjectId) throws IOException;

  void update(Subproject subproject) throws IOException;

  Set<Subproject> getByProjectId(Long projectId) throws IOException;

}
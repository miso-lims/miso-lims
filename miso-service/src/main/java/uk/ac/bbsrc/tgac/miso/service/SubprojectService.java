package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.Subproject;

public interface SubprojectService {

  Subproject get(Long subprojectId) throws IOException;

  Long create(Subproject subproject, Long parentProjectId) throws IOException;

  void update(Subproject subproject) throws IOException;

  Set<Subproject> getAll() throws IOException;

  void delete(Long subprojectId) throws IOException;

}
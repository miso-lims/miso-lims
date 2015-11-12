package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.Subproject;

public interface SubprojectService {

  Subproject get(Long subprojectId);

  Long create(Subproject subproject) throws IOException;

  void update(Subproject subproject) throws IOException;

  Set<Subproject> getAll();

  void delete(Long subprojectId);

}
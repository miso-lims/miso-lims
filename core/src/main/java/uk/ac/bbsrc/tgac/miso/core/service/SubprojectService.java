package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.Subproject;

public interface SubprojectService extends DeleterService<Subproject>, ListService<Subproject>, SaveService<Subproject> {

  public Set<Subproject> listByProjectId(Long projectId) throws IOException;

}
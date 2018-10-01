package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Subproject;

public interface SubprojectDao {

  List<Subproject> getSubproject();

  List<Subproject> getByProjectId(Long projectId);

  Subproject getSubproject(Long id);

  Long addSubproject(Subproject subproject);

  void deleteSubproject(Subproject subproject);

  void update(Subproject subproject);

  public long getUsage(Subproject subproject);

}
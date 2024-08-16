package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;

public interface SubprojectDao extends BulkSaveDao<Subproject> {

  Subproject getByProjectAndAlias(Project project, String alias);

  List<Subproject> listByProjectId(Long projectId);

  long getUsage(Subproject subproject);

}

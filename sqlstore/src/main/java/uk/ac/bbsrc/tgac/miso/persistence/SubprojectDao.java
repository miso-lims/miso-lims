package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;

public interface SubprojectDao extends SaveDao<Subproject> {

  Subproject getByProjectAndAlias(Project project, String alias);

  List<Subproject> listByProjectId(Long projectId);

  List<Subproject> listByIdList(Collection<Long> ids) throws IOException;

  long getUsage(Subproject subproject);

}
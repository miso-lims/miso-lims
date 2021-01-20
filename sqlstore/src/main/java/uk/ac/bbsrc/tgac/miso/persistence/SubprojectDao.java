package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;

public interface SubprojectDao extends SaveDao<Subproject> {

  public Subproject getByProjectAndAlias(Project project, String alias);

  public List<Subproject> listByProjectId(Long projectId);

  public void delete(Subproject subproject);

  public long getUsage(Subproject subproject);

}
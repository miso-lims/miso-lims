package uk.ac.bbsrc.tgac.miso.sqlstore;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.data.Project;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 08/10/12
 * @since 0.1.9
 */
public class MockSQLProjectDAO extends SQLProjectDAO {
  private final Map<Long, Project> projectmap = Collections.synchronizedMap(new HashMap<Long, Project>());
  private final Map<Long, Project> lazyprojectmap = Collections.synchronizedMap(new HashMap<Long, Project>());

  @Override
  public Project get(long projectId) throws IOException {
    synchronized (projectmap) {
      if (!projectmap.containsKey(projectId)) {
        Project u = super.get(projectId);
        if (u != null) {
          projectmap.put(projectId, u);
        }
        return u;
      } else {
        return projectmap.get(projectId);
      }
    }
  }

  @Override
  public Project lazyGet(long projectId) throws IOException {
    synchronized (lazyprojectmap) {
      if (!lazyprojectmap.containsKey(projectId)) {
        Project u = super.lazyGet(projectId);
        if (u != null) {
          lazyprojectmap.put(projectId, u);
        }
        return u;
      } else {
        return lazyprojectmap.get(projectId);
      }
    }
  }

  public void clearCaches() {
    projectmap.clear();
    lazyprojectmap.clear();
  }
}

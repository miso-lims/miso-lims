package uk.ac.bbsrc.tgac.miso.core.manager;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import uk.ac.bbsrc.tgac.miso.core.data.Issue;

/**
 * uk.ac.bbsrc.tgac.miso.core.manager
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 21/06/12
 * @since 0.1.6
 */
public class DummyIssueTrackerManager implements IssueTrackerManager {

  @Override
  public List<Issue> getIssuesByTag(String tag) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public List<Issue> searchIssues(String query) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public void setConfiguration(Properties properties) {
    // do nothing for dummy class
  }
}
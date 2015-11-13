package uk.ac.bbsrc.tgac.miso.core.manager;

import java.io.IOException;

import net.sf.json.JSONObject;

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
  public String getType() {
    return "DUMMY";
  }

  @Override
  public JSONObject getIssue(String issueKey) throws IOException {
    return null;
  }

  @Override
  public String getBaseTrackerUrl() {
    return null;
  }
}
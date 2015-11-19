package uk.ac.bbsrc.tgac.miso.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONObject;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationException;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationUtils;

/**
 * Service to enable queries to a notification-server instance
 * 
 * @author Rob Davey
 * @date 10/04/14
 * @since 0.2.1-SNAPSHOT
 */
public class NotificationQueryService {
  protected static final Logger log = LoggerFactory.getLogger(NotificationQueryService.class);

  private String notificationServerHost;
  private int notificationServerPort;

  public void setNotificationServerHost(String notificationServerHost) {
    this.notificationServerHost = notificationServerHost;
  }

  public void setNotificationServerPort(int notificationServerPort) {
    this.notificationServerPort = notificationServerPort;
  }

  public JSONObject getRunProgress(String runAlias, String platformType) throws IntegrationException {
    JSONObject q = new JSONObject();
    q.put("query", "queryRunProgress");
    q.put("run", runAlias);
    q.put("platform", platformType);
    return doQuery(q.toString());
  }

  public JSONObject getInterOpMetrics(String runAlias, String platformType) throws IntegrationException {
    JSONObject q = new JSONObject();
    q.put("query", "queryInterOpMetrics");
    q.put("run", runAlias);
    q.put("platform", platformType);
    return doQuery(q.toString());
  }

  public JSONObject getInterOpMetricsForLane(String runAlias, String platformType, int lane) throws IntegrationException {
    JSONObject q = new JSONObject();
    q.put("query", "queryInterOpMetrics");
    q.put("run", runAlias);
    q.put("platform", platformType);
    q.put("lane", lane);
    return doQuery(q.toString());
  }

  private JSONObject doQuery(String query) throws IntegrationException {
    String response = IntegrationUtils.sendMessage(IntegrationUtils.prepareSocket(notificationServerHost, notificationServerPort), query);
    if (!"".equals(response)) {
      JSONObject r = JSONObject.fromObject(response);
      if (!r.isEmpty()) {
        if (r.size() == 1 && r.has("error")) {
          String error = r.getString("error");
          log.error(error);
          throw new IntegrationException("Notification query returned an error: " + error);
        }

        return r;
      }
    }
    throw new IntegrationException("No such run.");
  }
}

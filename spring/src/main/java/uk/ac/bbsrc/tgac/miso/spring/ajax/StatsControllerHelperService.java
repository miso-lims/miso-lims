/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.spring.ajax;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;

import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.integration.NotificationQueryService;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationException;
import uk.ac.bbsrc.tgac.miso.runstats.client.RunStatsException;
import uk.ac.bbsrc.tgac.miso.runstats.client.manager.RunStatsManager;
import uk.ac.bbsrc.tgac.miso.service.impl.RunService;

/**
 * uk.ac.bbsrc.tgac.miso.spring.ajax
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @since 0.1.6
 */
@Ajaxified
public class StatsControllerHelperService {
  protected static final Logger log = LoggerFactory.getLogger(StatsControllerHelperService.class);

  private RunStatsManager runStatsManager;
  @Autowired
  private RunService runService;

  @Autowired
  private NotificationQueryService notificationQueryService;

  public void setRunStatsManager(RunStatsManager runStatsManager) {
    this.runStatsManager = runStatsManager;
  }

  public void setNotificationQueryService(NotificationQueryService notificationQueryService) {
    this.notificationQueryService = notificationQueryService;
  }

  public JSONObject getRunStats(HttpSession session, JSONObject json) {
    if (runStatsManager != null) {
      Long runId = json.getLong("runId");
      try {
        Run run = runService.get(runId);
        return runStatsManager.getSummaryStatsForRun(run);
      } catch (IOException e) {
        log.error("cannot retrieve run", e);
        return JSONUtils.SimpleJSONError("Cannot retrieve run: " + e.getMessage());
      } catch (RunStatsException e) {
        log.error("cannot retrieve run", e);
        return JSONUtils.SimpleJSONError("Cannot get stats for run: " + e.getMessage());
      }
    } else {
      return JSONUtils.SimpleJSONError("Run stats manager is not set. No stats available.");
    }
  }

  public JSONObject getPartitionStats(HttpSession session, JSONObject json) {
    if (runStatsManager != null) {
      Long runId = json.getLong("runId");
      Integer partitionNumber = json.getInt("partitionNumber");
      try {
        Run run = runService.get(runId);
        return runStatsManager.getSummaryStatsForLane(run, partitionNumber);
      } catch (IOException e) {
        log.error("cannot retrieve run", e);
        return JSONUtils.SimpleJSONError("Cannot retrieve run: " + e.getMessage());
      } catch (RunStatsException e) {
        log.error("cannot retrieve run", e);
        return JSONUtils.SimpleJSONError("Cannot get stats for lane: " + e.getMessage());
      }
    } else {
      return JSONUtils.SimpleJSONError("Run stats manager is not set. No stats available.");
    }
  }

  public JSONObject getSummaryRunstatsDiagram(HttpSession session, JSONObject json) {
    Long runId = json.getLong("runId");
    Integer lane = json.getInt("lane");
    try {
      Run run = runService.get(runId);
      JSONObject resultJson = runStatsManager.getPerPositionBaseSequenceQualityForLane(run, lane);
      return resultJson;
    } catch (IOException e) {
      log.error("Failed", e);
      return JSONUtils.SimpleJSONError("Failed");
    } catch (RunStatsException e) {
      log.error("Failed", e);
      return JSONUtils.SimpleJSONError("Failed");
    }
  }

  public JSONObject getInterOpMetrics(HttpSession session, JSONObject json) {
    String runAlias = json.getString("runAlias");
    String platformType = json.getString("platformType").toLowerCase();
    try {
      return notificationQueryService.getInterOpMetrics(runAlias, platformType);
    } catch (IntegrationException e) {
      log.error("Failed to retrieve InterOp metrics", e);
      return JSONUtils.SimpleJSONError("Failed to retrieve InterOp metrics: " + e.getMessage());
    }
  }

  public JSONObject getInterOpMetricsForLane(HttpSession session, JSONObject json) {
    String runAlias = json.getString("runAlias");
    String platformType = json.getString("platformType").toLowerCase();
    int laneNum = json.getInt("lane");
    try {
      return notificationQueryService.getInterOpMetricsForLane(runAlias, platformType, laneNum);
    } catch (IntegrationException e) {
      log.error("Failed", e);
      return JSONUtils.SimpleJSONError("Failed to retrieve InterOp metrics: " + e.getMessage());
    }
  }

  public JSONObject updateRunProgress(HttpSession session, JSONObject json) {
    String runAlias = json.getString("runAlias");
    try {
      Run run = runService.getRunByAlias(runAlias);
      if (run != null && run.getHealth() == HealthType.Unknown) {
        String platformType = json.getString("platformType").toLowerCase();
        JSONObject response = notificationQueryService.getRunProgress(runAlias, platformType);
        if (response.has("progress")) {
          HealthType progress = HealthType.valueOf(response.getString("progress"));
          if (run.getHealth() != progress) {
            run.setHealth(progress);
            runService.update(run);
            return response;
          }
          return JSONUtils.SimpleJSONResponse("No run progress change necessary for run " + runAlias);
        }
        return JSONUtils.SimpleJSONResponse("No run progress available for run " + runAlias);
      }
      return JSONUtils.SimpleJSONResponse("Run already set to non-Unknown: " + runAlias);
    } catch (IntegrationException e) {
      log.error("Failed", e);
      return JSONUtils.SimpleJSONError("Failed to retrieve run progress: " + e.getMessage());
    } catch (IOException e) {
      log.error("Failed", e);
      return JSONUtils.SimpleJSONError("Failed to retrieve run progress: " + e.getMessage());
    }
  }
}

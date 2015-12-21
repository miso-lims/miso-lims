/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
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

package uk.ac.bbsrc.tgac.miso.notification.demo.service.mechanism;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Status;
import uk.ac.bbsrc.tgac.miso.core.data.impl.illumina.IlluminaRun;
import uk.ac.bbsrc.tgac.miso.core.data.impl.illumina.IlluminaStatus;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.exception.InterrogationException;
import uk.ac.bbsrc.tgac.miso.core.service.integration.mechanism.NotificationMessageConsumerMechanism;

/**
 * uk.ac.bbsrc.tgac.miso.core.service.integration.mechanism.impl
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 03/02/12
 * @since 0.1.5
 */
public class DemoIlluminaConsumerMechanism implements NotificationMessageConsumerMechanism<Message<Map<String, List<String>>>, Set<Run>> {
  protected static final Logger log = LoggerFactory.getLogger(DemoIlluminaConsumerMechanism.class);

  @Override
  public Set<Run> consume(Message<Map<String, List<String>>> message) throws InterrogationException {
    Map<String, List<String>> statuses = message.getPayload();
    Set<Run> output = new HashSet<Run>();
    for (String key : statuses.keySet()) {
      HealthType ht = HealthType.valueOf(key);
      JSONArray runs = (JSONArray) JSONArray.fromObject(statuses.get(key)).get(0);
      Map<String, Run> map = processRunJSON(ht, runs);
      for (Run r : map.values()) {
        output.add(r);
      }
    }
    return output;
  }

  private Map<String, Run> processRunJSON(HealthType ht, JSONArray runs) {
    Map<String, Run> updatedRuns = new HashMap<String, Run>();

    StringBuilder sb = new StringBuilder();

    for (JSONObject run : (Iterable<JSONObject>) runs) {
      String runName = run.getString("runName");
      sb.append("Processing " + runName);
      log.debug("Processing " + runName);
      Status is = new IlluminaStatus();
      is.setRunName(runName);

      Run r = new IlluminaRun();
      r.setPlatformRunId(0);
      r.setAlias(runName);
      r.setFilePath(runName);
      r.setDescription("Test Run Import");
      r.setPairedEnd(false);
      is.setHealth(ht);
      r.setStatus(is);

      updatedRuns.put(r.getAlias(), r);
      sb.append("...done\n");
    }

    log.info(sb.toString());
    return updatedRuns;
  }
}

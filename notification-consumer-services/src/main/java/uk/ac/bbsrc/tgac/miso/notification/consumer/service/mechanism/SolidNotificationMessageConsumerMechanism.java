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

package uk.ac.bbsrc.tgac.miso.notification.consumer.service.mechanism;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunUtils;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.exception.InterrogationException;
import uk.ac.bbsrc.tgac.miso.core.service.integration.mechanism.NotificationMessageConsumerMechanism;
import uk.ac.bbsrc.tgac.miso.service.ContainerService;
import uk.ac.bbsrc.tgac.miso.service.SequencerReferenceService;
import uk.ac.bbsrc.tgac.miso.service.impl.RunService;
import uk.ac.bbsrc.tgac.miso.tools.run.RunFolderConstants;

/**
 * uk.ac.bbsrc.tgac.miso.core.service.integration.mechanism.impl
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 03/02/12
 * @since 0.1.5
 */
public class SolidNotificationMessageConsumerMechanism
    implements NotificationMessageConsumerMechanism<Message<Map<String, List<String>>>, Set<Run>> {
  protected static final Logger log = LoggerFactory.getLogger(SolidNotificationMessageConsumerMechanism.class);

  @Autowired
  private RunService runService;

  @Autowired
  private SequencerReferenceService sequencerService;

  @Autowired
  private ContainerService containerService;

  public boolean attemptRunPopulation = true;

  public void setAttemptRunPopulation(boolean attemptRunPopulation) {
    this.attemptRunPopulation = attemptRunPopulation;
  }

  private final String runDirRegex = RunFolderConstants.SOLID_FOLDER_NAME_GROUP_CAPTURE_REGEX;
  private final Pattern p = Pattern.compile(runDirRegex);

  @Override
  public Set<Run> consume(Message<Map<String, List<String>>> message) throws InterrogationException {
    Map<String, List<String>> statuses = message.getPayload();
    Set<Run> output = new HashSet<>();
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
    Map<String, Run> updatedRuns = new HashMap<>();
    List<Run> runsToSave = new ArrayList<>();
    // 2011-01-25 15:37:27.093
    DateFormat logDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    DateFormat simpleLogDateFormat = new SimpleDateFormat("yyyyMMdd");
    Pattern simpleDateRegex = Pattern.compile("[0-9]{8}");

    StringBuilder sb = new StringBuilder();

    for (JSONObject run : (Iterable<JSONObject>) runs) {
      String runName = run.getString("runName");
      sb.append("Processing " + runName + "\n");
      log.debug("Processing " + runName);

      if (run.has("status")) {
        String xml = run.getString("status");
        Run is = RunUtils.createFromSolidXml(xml, t -> {
          try {
            return sequencerService.getByName(t);
          } catch (IOException e) {
            log.warn("No sequencer: " + t, e);
          }
          return null;
        });

        is.setHealth(ht);

        Run r = null;
        Matcher m = p.matcher(runName);
        if (m.matches()) {
          try {
            r = runService.getRunByAlias(runName);
          } catch (IOException ioe) {
            log.warn(
                "Cannot find run by this alias. This usually means the run hasn't been previously imported. If attemptRunPopulation is false, processing will not take place for this run!");
          }
        }

        try {
          if (attemptRunPopulation) {
            if (r == null) {
              log.debug("Saving new run and status: " + runName);
              r = is;
              if (run.has("fullPath")) {
                r.setFilePath(run.getString("fullPath"));
              }

              SequencerReference sr = null;
              if (run.has("sequencerName")) {
                sr = sequencerService.getByName(run.getString("sequencerName"));
                r.setSequencerReference(sr);
              }
              if (r.getSequencerReference() == null) {
                sr = sequencerService.getByName(m.group(1));
                r.setSequencerReference(sr);
              }

              if (r.getSequencerReference() != null) {
                if (run.has("startDate")) {
                  try {
                    log.debug("Updating start date:" + run.getString("startDate"));

                    Matcher m2 = simpleDateRegex.matcher(run.getString("startDate"));
                    if (m2.matches()) {
                      r.setStartDate(simpleLogDateFormat.parse(run.getString("startDate")));
                    } else {
                      r.setStartDate(logDateFormat.parse(run.getString("startDate")));
                    }
                  } catch (ParseException e) {
                    log.error("process run JSON start date", e);
                  }
                }

                if (run.has("completionDate")) {
                  try {
                    if (run.get("completionDate") != null && !run.getString("completionDate").equals("null")) {
                      log.debug("Updating completion date:" + run.getString("completionDate"));
                      r.setCompletionDate(logDateFormat.parse(run.getString("completionDate")));
                    } else {
                      r.setCompletionDate(null);
                    }
                  } catch (ParseException e) {
                    log.error("process run JSON completion date", e);
                  }
                }
              }
            } else {
              log.debug("Updating existing run and status: " + runName);

              r.setAlias(runName);

              if (r.getSequencerReference() == null) {
                SequencerReference sr = null;
                if (run.has("sequencerName")) {
                  sr = sequencerService.getByName(run.getString("sequencerName"));
                  r.setSequencerReference(sr);
                }
                if (r.getSequencerReference() == null) {
                  sr = sequencerService.getByName(m.group(1));
                  r.setSequencerReference(sr);
                }
              }
              if (r.getSequencerReference() != null) {
                if (run.has("startDate")) {
                  try {
                    log.debug("Updating start date:" + run.getString("startDate"));

                    Matcher m2 = simpleDateRegex.matcher(run.getString("startDate"));
                    if (m2.matches()) {
                      r.setStartDate(simpleLogDateFormat.parse(run.getString("startDate")));
                    } else {
                      r.setStartDate(logDateFormat.parse(run.getString("startDate")));
                    }
                  } catch (ParseException e) {
                    log.error("process run JSON start date", e);
                  }
                }

                if (run.has("completionDate")) {
                  try {
                    if (run.get("completionDate") != null && !run.getString("completionDate").equals("null")) {
                      log.debug("Updating completion date:" + run.getString("completionDate"));
                      r.setCompletionDate(logDateFormat.parse(run.getString("completionDate")));
                    } else {
                      r.setCompletionDate(logDateFormat.parse(run.getString("completionDate")));
                    }
                  } catch (ParseException e) {
                    log.error("process run JSON completion date", e);
                  }
                }

                // update path if changed
                if (run.has("fullPath") && !"".equals(run.getString("fullPath")) && r.getFilePath() != null
                    && !"".equals(r.getFilePath())) {
                  if (!run.getString("fullPath").equals(r.getFilePath())) {
                    log.debug("Updating run file path:" + r.getFilePath() + " -> " + run.getString("fullPath"));
                    r.setFilePath(run.getString("fullPath"));
                  }
                }

              }
            }

            if (r.getSequencerReference() != null) {
              List<SequencerPartitionContainer> fs = r.getSequencerPartitionContainers();
              if (fs.isEmpty()) {
                if (run.has("containerId") && !isStringEmptyOrNull(run.getString("containerId"))) {
                  Collection<SequencerPartitionContainer> pfs = containerService.listByBarcode(run.getString("containerId"));
                  if (!pfs.isEmpty()) {
                    if (pfs.size() == 1) {
                      SequencerPartitionContainer lf = new ArrayList<>(pfs).get(0);
                      if (lf.getSecurityProfile() != null && r.getSecurityProfile() == null) {
                        r.setSecurityProfile(lf.getSecurityProfile());
                      }
                      if (lf.getPlatform() == null && r.getSequencerReference().getPlatform() != null) {
                        lf.setPlatform(r.getSequencerReference().getPlatform());
                      }
                      r.addSequencerPartitionContainer(lf);
                    }
                  } else {
                    log.debug("No containers linked to run " + r.getId() + ": creating...");
                    SequencerPartitionContainer f = new SequencerPartitionContainerImpl();
                    f.setSecurityProfile(r.getSecurityProfile());
                    f.setIdentificationBarcode(run.getString("containerNum"));
                    if (f.getPlatform() == null && r.getSequencerReference().getPlatform() != null) {
                      f.setPlatform(r.getSequencerReference().getPlatform());
                    }
                    r.addSequencerPartitionContainer(f);
                  }
                }
              } else {
                SequencerPartitionContainer f = fs.iterator().next();
                log.debug("Got container " + f.getId());

                if (f.getSecurityProfile() == null) {
                  f.setSecurityProfile(r.getSecurityProfile());
                }

                if (f.getPlatform() == null && r.getSequencerReference().getPlatform() != null) {
                  f.setPlatform(r.getSequencerReference().getPlatform());
                }

                if (run.has("containerId") && !isStringEmptyOrNull(run.getString("containerId"))) {
                  f.setIdentificationBarcode(run.getString("containerId"));
                }

                long flowId = containerService.save(f).getId();
                f.setId(flowId);
              }

              updatedRuns.put(r.getAlias(), r);
              runsToSave.add(r);
            }
          }
        } catch (IOException e) {
          log.error("process run JSON", e);
        }
      }
    }

    try {
      if (runsToSave.size() > 0) {
        runService.saveRuns(runsToSave);
        log.info("Batch saved " + runsToSave.size() + " runs");
      }
    } catch (IOException e) {
      log.error("Couldn't save run batch", e);
    }

    return updatedRuns;
  }
}

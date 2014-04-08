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

package uk.ac.bbsrc.tgac.miso.notification.consumer.service.mechanism;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;
import org.springframework.util.Assert;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.solid.SolidRun;
import uk.ac.bbsrc.tgac.miso.core.data.impl.solid.SolidStatus;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.InterrogationException;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.service.integration.mechanism.NotificationMessageConsumerMechanism;
import uk.ac.bbsrc.tgac.miso.tools.run.RunFolderConstants;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * uk.ac.bbsrc.tgac.miso.core.service.integration.mechanism.impl
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 03/02/12
 * @since 0.1.5
 */
public class SolidNotificationMessageConsumerMechanism implements NotificationMessageConsumerMechanism<Message<Map<String, List<String>>>, Set<Run>> {
  protected static final Logger log = LoggerFactory.getLogger(SolidNotificationMessageConsumerMechanism.class);

  public boolean attemptRunPopulation = true;

  public void setAttemptRunPopulation(boolean attemptRunPopulation) {
    this.attemptRunPopulation = attemptRunPopulation;
  }

  private final String runDirRegex = RunFolderConstants.SOLID_FOLDER_NAME_GROUP_CAPTURE_REGEX;
  private final Pattern p = Pattern.compile(runDirRegex);

  @Override
  public Set<Run> consume(Message<Map<String, List<String>>> message) throws InterrogationException {
    RequestManager requestManager = message.getHeaders().get("handler", RequestManager.class);
    Assert.notNull(requestManager, "Cannot consume MISO notification messages without a RequestManager.");
    Map<String, List<String>> statuses = message.getPayload();
    Set<Run> output = new HashSet<Run>();
    for (String key : statuses.keySet()) {
      HealthType ht = HealthType.valueOf(key);
      JSONArray runs = (JSONArray) JSONArray.fromObject(statuses.get(key)).get(0);
      Map<String, Run> map = processRunJSON(ht, runs, requestManager);
      for (Run r : map.values()) {
        output.add(r);
      }
    }
    return output;
  }

  private Map<String, Run> processRunJSON(HealthType ht, JSONArray runs, RequestManager requestManager) {
    Map<String, Run> updatedRuns = new HashMap<String, Run>();
    List<Run> runsToSave = new ArrayList<Run>();
    //2011-01-25 15:37:27.093
    DateFormat logDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    DateFormat simpleLogDateFormat = new SimpleDateFormat("yyyyMMdd");
    Pattern simpleDateRegex = Pattern.compile("[0-9]{8}");

    StringBuilder sb = new StringBuilder();

    for (JSONObject run : (Iterable<JSONObject>)runs) {
      String runName = run.getString("runName");
      sb.append("Processing " + runName + "\n");
      log.debug("Processing " + runName);

      if (run.has("status")) {
        String xml = run.getString("status");
        Status is = new SolidStatus(xml);
        is.setHealth(ht);
        is.setRunName(runName);

        Run r = null;
        Matcher m = p.matcher(runName);
        if (m.matches()) {
          try {
            r = requestManager.getRunByAlias(runName);
          }
          catch(IOException ioe) {
            log.warn("Cannot find run by this alias. This usually means the run hasn't been previously imported. If attemptRunPopulation is false, processing will not take place for this run!");
          }
        }

        try {
          if (attemptRunPopulation) {
            if (r == null) {
              log.debug("Saving new run and status: " + is.getRunName());
              r = new SolidRun(xml);
              r.getStatus().setHealth(ht);
              if (run.has("fullPath")) {
                r.setFilePath(run.getString("fullPath"));
              }

              SequencerReference sr = null;
              if (run.has("sequencerName")) {
                sr = requestManager.getSequencerReferenceByName(run.getString("sequencerName"));
                r.getStatus().setInstrumentName(run.getString("sequencerName"));
                r.setSequencerReference(sr);
              }
              if (r.getSequencerReference() == null) {
                sr = requestManager.getSequencerReferenceByName(m.group(1));
                r.getStatus().setInstrumentName(m.group(1));
                r.setSequencerReference(sr);
              }
              if (r.getSequencerReference() == null) {
                sr = requestManager.getSequencerReferenceByName(r.getStatus().getInstrumentName());
                r.setSequencerReference(sr);
              }

              if (r.getSequencerReference() != null) {
                if (run.has("startDate")) {
                  try {
                    log.debug("Updating start date:" + run.getString("startDate"));

                    Matcher m2 = simpleDateRegex.matcher(run.getString("startDate"));
                    if (m2.matches()) {
                      r.getStatus().setStartDate(simpleLogDateFormat.parse(run.getString("startDate")));
                    }
                    else {
                      r.getStatus().setStartDate(logDateFormat.parse(run.getString("startDate")));
                    }
                  }
                  catch (ParseException e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                  }
                }

                if (run.has("completionDate")) {
                  try {
                    if (run.get("completionDate") != null && !run.getString("completionDate").equals("null")) {
                      log.debug("Updating completion date:" + run.getString("completionDate"));
                      r.getStatus().setCompletionDate(logDateFormat.parse(run.getString("completionDate")));
                    }
                    else {
                      r.getStatus().setCompletionDate(null);
                    }
                  }
                  catch (ParseException e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                  }
                }
              }
            }
            else {
              log.debug("Updating existing run and status: " + is.getRunName());

              r.setAlias(runName);
              r.setPlatformType(PlatformType.SOLID);

              if (r.getSequencerReference() == null) {
                SequencerReference sr = null;
                if (run.has("sequencerName")) {
                  sr = requestManager.getSequencerReferenceByName(run.getString("sequencerName"));
                  r.getStatus().setInstrumentName(run.getString("sequencerName"));
                  r.setSequencerReference(sr);
                }
                if (r.getSequencerReference() == null) {
                  sr = requestManager.getSequencerReferenceByName(m.group(1));
                  r.getStatus().setInstrumentName(m.group(1));
                  r.setSequencerReference(sr);
                }
                if (r.getSequencerReference() == null) {
                  sr = requestManager.getSequencerReferenceByName(r.getStatus().getInstrumentName());
                  r.setSequencerReference(sr);
                }
              }
              if (r.getSequencerReference() != null) {
                if (run.has("startDate")) {
                  try {
                    log.debug("Updating start date:" + run.getString("startDate"));

                    Matcher m2 = simpleDateRegex.matcher(run.getString("startDate"));
                    if (m2.matches()) {
                      r.getStatus().setStartDate(simpleLogDateFormat.parse(run.getString("startDate")));
                    }
                    else {
                      r.getStatus().setStartDate(logDateFormat.parse(run.getString("startDate")));
                    }
                  }
                  catch (ParseException e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                  }
                }

                if (run.has("completionDate")) {
                  try {
                    if (run.get("completionDate") != null && !run.getString("completionDate").equals("null")) {
                      log.debug("Updating completion date:" + run.getString("completionDate"));
                      r.getStatus().setCompletionDate(logDateFormat.parse(run.getString("completionDate")));
                    }
                    else {
                      r.getStatus().setCompletionDate(null);
                    }
                  }
                  catch (ParseException e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                  }
                }

                //update path if changed
                if (run.has("fullPath") && !"".equals(run.getString("fullPath")) && r.getFilePath() != null && !"".equals(r.getFilePath())) {
                  if (!run.getString("fullPath").equals(r.getFilePath())) {
                    log.debug("Updating run file path:" + r.getFilePath() + " -> " + run.getString("fullPath"));
                    r.setFilePath(run.getString("fullPath"));
                  }
                }

                // update status if run isn't completed or failed
                if (!r.getStatus().getHealth().equals(HealthType.Completed) && !r.getStatus().getHealth().equals(HealthType.Failed)) {
                  log.debug("Saving previously saved status: " + is.getRunName() + " (" + r.getStatus().getHealth().getKey() + " -> " + is.getHealth().getKey() + ")");
                  r.setStatus(is);
                }
              }
            }

            if (r.getSequencerReference() != null) {
              List<SequencerPartitionContainer<SequencerPoolPartition>> fs = ((SolidRun)r).getSequencerPartitionContainers();
              if (fs.isEmpty()) {
                if (run.has("containerId") && !"".equals(run.getString("containerId"))) {
                  Collection<SequencerPartitionContainer<SequencerPoolPartition>> pfs = requestManager.listSequencerPartitionContainersByBarcode(run.getString("containerId"));
                  if (!pfs.isEmpty()) {
                    if (pfs.size() == 1) {
                      SequencerPartitionContainer lf = new ArrayList<SequencerPartitionContainer<SequencerPoolPartition>>(pfs).get(0);
                      if (lf.getSecurityProfile() != null && r.getSecurityProfile() == null) {
                        r.setSecurityProfile(lf.getSecurityProfile());
                      }
                      if (lf.getPlatform() == null && r.getSequencerReference().getPlatform() != null) {
                        lf.setPlatform(r.getSequencerReference().getPlatform());
                      }
//                      else {
//                        lf.setPlatformType(PlatformType.SOLID);
//                      }
                      ((RunImpl)r).addSequencerPartitionContainer(lf);
                    }
                  }
                  else {
                    log.debug("No containers linked to run " + r.getId() + ": creating...");
                    SequencerPartitionContainer f = new SequencerPartitionContainerImpl();
                    f.setSecurityProfile(r.getSecurityProfile());
                    f.initEmptyPartitions();
                    f.setIdentificationBarcode(run.getString("containerNum"));
                    if (f.getPlatform() == null && r.getSequencerReference().getPlatform() != null) {
                      f.setPlatform(r.getSequencerReference().getPlatform());
                    }
//                    else {
//                      f.setPlatformType(PlatformType.SOLID);
//                    }
                    //f.setPaired(r.getPairedEnd());
                    ((RunImpl)r).addSequencerPartitionContainer(f);
                  }
                }
              }
              else {
                SequencerPartitionContainer f = fs.iterator().next();
                log.debug("Got container " + f.getId());

                if (f.getSecurityProfile() == null) {
                  f.setSecurityProfile(r.getSecurityProfile());
                }

                if (f.getPlatform() == null && r.getSequencerReference().getPlatform() != null) {
                  f.setPlatform(r.getSequencerReference().getPlatform());
                }
//                else {
//                  f.setPlatformType(PlatformType.SOLID);
//                }

                if (run.has("containerId") && !"".equals(run.getString("containerId"))) {
                  f.setIdentificationBarcode(run.getString("containerId"));
                }

                long flowId = requestManager.saveSequencerPartitionContainer(f);
                f.setId(flowId);
              }

              updatedRuns.put(r.getAlias(), r);
              runsToSave.add(r);
            }
          }
        }
        catch (IOException e) {
          log.error(e.getMessage());
          e.printStackTrace();
        }
      }
    }

    try {
      if (runsToSave.size() > 0) {
        int[] saved = requestManager.saveRuns(runsToSave);
        log.info("Batch saved " + saved.length + " / "+ runs.size() + " runs");
      }
    }
    catch (IOException e) {
      log.error("Couldn't save run batch: " + e.getMessage());
      e.printStackTrace();
    }

    return updatedRuns;
  }
}

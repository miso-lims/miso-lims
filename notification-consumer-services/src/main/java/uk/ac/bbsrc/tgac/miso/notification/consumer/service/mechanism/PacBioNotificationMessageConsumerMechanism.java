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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import org.springframework.integration.Message;
import org.springframework.util.Assert;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.Status;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.pacbio.PacBioRun;
import uk.ac.bbsrc.tgac.miso.core.data.impl.pacbio.PacBioStatus;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.InterrogationException;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.service.integration.mechanism.NotificationMessageConsumerMechanism;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationUtils;
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
public class PacBioNotificationMessageConsumerMechanism
    implements NotificationMessageConsumerMechanism<Message<Map<String, List<String>>>, Set<Run>> {
  protected static final Logger log = LoggerFactory.getLogger(PacBioNotificationMessageConsumerMechanism.class);

  public boolean attemptRunPopulation = true;

  public void setAttemptRunPopulation(boolean attemptRunPopulation) {
    this.attemptRunPopulation = attemptRunPopulation;
  }

  private final String runDirRegex = RunFolderConstants.PACBIO_FOLDER_NAME_GROUP_CAPTURE_REGEX;
  private final Pattern p = Pattern.compile(runDirRegex);

  @Override
  public Set<Run> consume(Message<Map<String, List<String>>> message) throws InterrogationException {
    RequestManager requestManager = message.getHeaders().get("handler", RequestManager.class);
    Assert.notNull(requestManager, "Cannot consume MISO notification messages without a RequestManager.");
    Map<String, List<String>> statuses = message.getPayload();
    Set<Run> output = new HashSet<>();
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
    Map<String, Run> updatedRuns = new HashMap<>();
    List<Run> runsToSave = new ArrayList<>();

    DateFormat gsLogDateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy");
    DateFormat startDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    for (JSONObject run : (Iterable<JSONObject>) runs) {
      String runName = run.getString("runName");
      log.info("Processing " + runName);

      String status = "";

      if (run.has("cells")) {
        JSONArray cells = run.getJSONArray("cells");
        for (JSONObject cell : (Iterable<JSONObject>) cells) {
          if (cell.has("cellStatus")) {
            try {
              String s = new String(IntegrationUtils.decompress(URLDecoder.decode(cell.getString("cellStatus"), "UTF-8").getBytes()));
              status += s + "\n\n";
            } catch (UnsupportedEncodingException e) {
              log.error("Cannot decode status xml", e);
            } catch (IOException e) {
              log.error("Cannot decompress and decode incoming status", e);
            }
          }
        }
      }

      if (!isStringEmptyOrNull(status)) {
        try {
          if (!status.startsWith("ERROR")) {
            Status is = new PacBioStatus(status);
            is.setHealth(ht);
            is.setRunName(runName);

            Run r = null;

            Matcher m = p.matcher(runName);
            if (m.matches()) {
              try {
                r = requestManager.getRunByAlias(runName);
              } catch (IOException ioe) {
                log.warn(
                    "Cannot find run by this alias. This usually means the run hasn't been previously imported. If attemptRunPopulation is false, processing will not take place for this run!");
              }
            }

            if (attemptRunPopulation) {
              if (r == null) {
                log.info("\\_ Saving new run and status: " + is.getRunName());
                r = new PacBioRun(status);
                r.setAlias(run.getString("runName"));
                r.setDescription(m.group(1));
                r.setPairedEnd(false);

                if (run.has("fullPath")) {
                  r.setFilePath(run.getString("fullPath"));
                }

                SequencerReference sr = null;
                if (run.has("sequencerName")) {
                  sr = requestManager.getSequencerReferenceByName(run.getString("sequencerName"));
                }

                if (sr != null) {
                  if (run.has("startDate") && !isStringEmptyOrNull(run.getString("startDate"))) {
                    try {
                      r.getStatus().setStartDate(startDateFormat.parse(run.getString("startDate")));
                    } catch (ParseException e) {
                      log.error("process run JSON start date", e);
                    }
                  }

                  if (run.has("completionDate") && !isStringEmptyOrNull(run.getString("completionDate"))) {
                    try {
                      r.getStatus().setCompletionDate(startDateFormat.parse(run.getString("completionDate")));
                    } catch (ParseException e) {
                      log.error("process run JSON completion date", e);
                    }
                  }

                  is.setInstrumentName(sr.getName());
                  r.setStatus(is);

                  r.setSequencerReference(sr);
                } else {
                  log.error("\\_ Cannot save " + is.getRunName() + ": no sequencer reference available.");
                }
              } else {
                log.info("\\_ Updating existing run and status: " + is.getRunName());

                r.setAlias(runName);

                r.setPlatformType(PlatformType.PACBIO);
                r.setDescription(m.group(1));
                r.setPairedEnd(false);

                if (r.getSequencerReference() == null) {
                  SequencerReference sr = null;
                  if (run.has("sequencerName")) {
                    sr = requestManager.getSequencerReferenceByName(run.getString("sequencerName"));
                  }

                  if (sr != null) {
                    r.getStatus().setInstrumentName(sr.getName());
                    r.setSequencerReference(sr);
                  }
                }

                if (r.getSequencerReference() != null) {
                  if (run.has("startDate") && !isStringEmptyOrNull(run.getString("startDate"))) {
                    try {
                      r.getStatus().setStartDate(startDateFormat.parse(run.getString("startDate")));
                    } catch (ParseException e) {
                      log.error("process run JSON start date", e);
                    }
                  }

                  if (run.has("completionDate") && !isStringEmptyOrNull(run.getString("completionDate"))) {
                    try {
                      r.getStatus().setCompletionDate(startDateFormat.parse(run.getString("completionDate")));
                    } catch (ParseException e) {
                      log.error("process run JSON completion date", e);
                    }
                  }

                  // update path if changed
                  if (run.has("fullPath") && !isStringEmptyOrNull(run.getString("fullPath")) && !isStringEmptyOrNull(r.getFilePath())) {
                    if (!run.getString("fullPath").equals(r.getFilePath())) {
                      log.info("Updating run file path:" + r.getFilePath() + " -> " + run.getString("fullPath"));
                      r.setFilePath(run.getString("fullPath"));
                    }
                  }

                  // update status if run isn't completed or failed
                  if (!r.getStatus().getHealth().equals(HealthType.Completed) && !r.getStatus().getHealth().equals(HealthType.Failed)) {
                    log.info("Saving previously saved status: " + is.getRunName() + " (" + r.getStatus().getHealth().getKey() + " -> "
                        + is.getHealth().getKey() + ")");
                    r.setStatus(is);
                  }
                }
              }

              if (r.getSequencerReference() != null) {
                List<SequencerPartitionContainer<SequencerPoolPartition>> fs = ((PacBioRun) r).getSequencerPartitionContainers();
                if (fs.isEmpty()) {
                  if (run.has("plateId") && !isStringEmptyOrNull(run.getString("plateId"))) {
                    Collection<SequencerPartitionContainer<SequencerPoolPartition>> pfs = requestManager
                        .listSequencerPartitionContainersByBarcode(run.getString("plateId"));
                    if (!pfs.isEmpty()) {
                      if (pfs.size() == 1) {
                        SequencerPartitionContainer<SequencerPoolPartition> lf = new ArrayList<>(
                            pfs).get(0);
                        if (lf.getSecurityProfile() != null && r.getSecurityProfile() == null) {
                          r.setSecurityProfile(lf.getSecurityProfile());
                        }
                        if (lf.getPlatform() == null && r.getSequencerReference().getPlatform() != null) {
                          lf.setPlatform(r.getSequencerReference().getPlatform());
                        }
                        JSONArray cells = run.getJSONArray("cells");
                        if (cells.size() > lf.getPartitions().size()) {
                          int numNewcells = cells.size() - lf.getPartitions().size();
                          lf.setPartitionLimit(cells.size());
                          for (int i = 0; i < numNewcells; i++) {
                            lf.addNewPartition();
                          }
                        }

                        ((RunImpl) r).addSequencerPartitionContainer(lf);
                      } else {
                        // more than one flowcell hit to this barcode
                        log.warn(r.getAlias()
                            + ":: More than one container has this barcode. Cannot automatically link to a pre-existing barcode.");
                      }
                    } else {
                      if (run.has("cells")) {
                        JSONArray cells = run.getJSONArray("cells");
                        SequencerPartitionContainer f = new SequencerPartitionContainerImpl();
                        f.setPartitionLimit(cells.size());
                        f.initEmptyPartitions();
                        if (run.has("plateId") && !isStringEmptyOrNull(run.getString("plateId"))) {
                          f.setIdentificationBarcode(run.getString("plateId"));
                        }
                        if (f.getPlatform() == null && r.getSequencerReference().getPlatform() != null) {
                          f.setPlatform(r.getSequencerReference().getPlatform());
                        }
                        f.setRun(r);
                        log.info("\\_ Created new container with " + f.getPartitions().size() + " partitions");
                        long flowId = requestManager.saveSequencerPartitionContainer(f);
                        f.setId(flowId);
                        ((RunImpl) r).addSequencerPartitionContainer(f);
                      }
                    }
                  }
                } else {
                  SequencerPartitionContainer f = fs.iterator().next();
                  f.setSecurityProfile(r.getSecurityProfile());
                  if (f.getPlatform() == null && r.getSequencerReference().getPlatform() != null) {
                    f.setPlatform(r.getSequencerReference().getPlatform());
                  }
                  if (isStringEmptyOrNull(f.getIdentificationBarcode())) {
                    if (run.has("plateId") && !isStringEmptyOrNull(run.getString("plateId"))) {
                      f.setIdentificationBarcode(run.getString("plateId"));
                      requestManager.saveSequencerPartitionContainer(f);
                    }
                  }
                  JSONArray cells = run.getJSONArray("cells");
                  if (cells.size() > f.getPartitions().size()) {
                    int numNewcells = cells.size() - f.getPartitions().size();
                    f.setPartitionLimit(cells.size());
                    for (int i = 0; i < numNewcells; i++) {
                      f.addNewPartition();
                    }
                  }
                }

                updatedRuns.put(r.getAlias(), r);
                runsToSave.add(r);
              }
            } else {
              log.warn("\\_ Run not saved. Saving status: " + is.getRunName());
              requestManager.saveStatus(is);
            }
          }
        } catch (IOException e) {
          log.error("process run JSON", e);
        }
      } else {
        log.error("No notification status available for " + runName);
      }
    }

    try {
      if (runsToSave.size() > 0) {
        int[] saved = requestManager.saveRuns(runsToSave);
        log.info("Batch saved " + saved.length + " / " + runs.size() + " runs");
      }
    } catch (IOException e) {
      log.error("Couldn't save run batch", e);
    }

    return updatedRuns;
  }
}

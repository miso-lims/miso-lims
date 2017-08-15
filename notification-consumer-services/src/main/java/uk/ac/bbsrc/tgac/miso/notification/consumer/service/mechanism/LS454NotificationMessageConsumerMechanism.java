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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import uk.ac.bbsrc.tgac.miso.core.data.LS454Run;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.exception.InterrogationException;
import uk.ac.bbsrc.tgac.miso.core.service.integration.mechanism.NotificationMessageConsumerMechanism;
import uk.ac.bbsrc.tgac.miso.core.util.SubmissionUtils;
import uk.ac.bbsrc.tgac.miso.core.util.UnicodeReader;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationUtils;
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
public class LS454NotificationMessageConsumerMechanism
    implements NotificationMessageConsumerMechanism<Message<Map<String, List<String>>>, Set<Run>> {
  protected static final Logger log = LoggerFactory.getLogger(LS454NotificationMessageConsumerMechanism.class);

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

  private final String runDirRegex = RunFolderConstants.LS454_FOLDER_CAPTURE_REGEX;
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

    DateFormat gsLogDateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy");
    DateFormat startDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    StringBuilder sb = new StringBuilder();

    for (JSONObject run : (Iterable<JSONObject>) runs) {
      String runName = run.getString("runName");
      sb.append("Processing " + runName + "\n");
      log.debug("Processing " + runName);

      if (run.has("status")) {
        String runLog = "";
        if (!isStringEmptyOrNull(run.getString("status"))) {
          try {
            runLog = new String(IntegrationUtils.decompress(URLDecoder.decode(run.getString("status"), "UTF-8").getBytes()));
          } catch (UnsupportedEncodingException e) {
            log.error("Cannot decode status runLog", e);
          } catch (IOException e) {
            log.error("Cannot decompress and decode incoming status", e);
          }
        }

        if (!runLog.startsWith("ERROR")) {
          LS454Run r = null;

          Matcher m = p.matcher(runName);
          if (m.matches()) {
            try {
              r = (LS454Run) runService.getRunByAlias(runName);

            } catch (IOException ioe) {
              log.warn(
                  "Cannot find run by this alias. This usually means the run hasn't been previously imported. If attemptRunPopulation is false, processing will not take place for this run!");
            }
          }

          try {
            if (attemptRunPopulation) {
              if (r == null) {
                log.debug("\\_ Saving new run and status: " + runName);

                r = new LS454Run();
                r.setAlias(run.getString("runName"));
                r.setDescription(m.group(3));
                // TODO check this properly
                r.setPairedEnd(false);

                if (run.has("fullPath")) {
                  r.setFilePath(run.getString("fullPath"));
                }


                SequencerReference sr = null;
                if (run.has("sequencerName")) {
                  sr = sequencerService.getByName(run.getString("sequencerName"));
                }
                if (sr == null) {
                  sr = sequencerService.getByName(m.group(2));
                }


                if (run.has("completionDate")) {
                  try {
                    r.setCompletionDate(gsLogDateFormat.parse(run.getString("completionDate")));
                  } catch (ParseException e) {
                    log.error("Cannot parse " + runName + " completion date", e);
                  }
                }

                if (sr != null) {
                  r.setSequencerReference(sr);
                  runsToSave.add(r);
                } else {
                  log.error("\\_ Cannot save " + runName + ": no sequencer reference available.");
                }
              } else {
                log.debug("\\_ Updating existing run and status: " + runName);

                r.setAlias(runName);

                r.setDescription(m.group(3));
                // TODO check this properly
                r.setPairedEnd(false);

                if (run.has("status")) {
                  if (r.getHealth() != HealthType.Failed && r.getHealth() != HealthType.Completed) {
                    r.setHealth(ht);
                  }
                }

                if (r.getSequencerReference() == null) {
                  SequencerReference sr = null;
                  if (run.has("sequencerName")) {
                    sr = sequencerService.getByName(run.getString("sequencerName"));
                  }
                  if (sr == null) {
                    sr = sequencerService.getByName(m.group(2));
                  }

                  if (sr != null) {
                    r.setSequencerReference(sr);
                  }
                }

                if (run.has("completionDate")) {
                  try {
                    r.setCompletionDate(gsLogDateFormat.parse(run.getString("completionDate")));
                  } catch (ParseException e) {
                    log.error("run JSON", e);
                  }
                }

                // update path if changed
                if (run.has("fullPath") && !isStringEmptyOrNull(run.getString("fullPath")) && !isStringEmptyOrNull(r.getFilePath())) {
                  if (!run.getString("fullPath").equals(r.getFilePath())) {
                    log.debug("Updating run file path:" + r.getFilePath() + " -> " + run.getString("fullPath"));
                    r.setFilePath(run.getString("fullPath"));
                  }
                }

              }

              if (r.getSequencerReference() != null) {
                if (run.has("runparams")) {
                  try {
                    Document paramsDoc = SubmissionUtils.emptyDocument();
                    SubmissionUtils.transform(new UnicodeReader(run.getString("runparams")), paramsDoc);

                    Element runInfo = (Element) paramsDoc.getElementsByTagName("run").item(0);
                    String runDesc = runInfo.getElementsByTagName("shortName").item(0).getTextContent();
                    r.setDescription(runDesc);

                    String cycles = runInfo.getElementsByTagName("numCycles").item(0).getTextContent();
                    r.setCycles(Integer.parseInt(cycles));

                    String startDateStr = runInfo.getElementsByTagName("date").item(0).getTextContent();
                    Date startDate = startDateFormat.parse(startDateStr);
                    if (!startDate.equals(r.getStartDate())) {
                      r.setStartDate(startDate);

                    }

                    List<SequencerPartitionContainer> fs = r.getSequencerPartitionContainers();

                    Element ptp = (Element) paramsDoc.getElementsByTagName("ptp").item(0);
                    String ptpId = ptp.getElementsByTagName("id").item(0).getTextContent();

                    if (fs.isEmpty()) {
                      if (ptp.getElementsByTagName("padLayout").getLength() > 0 && ptp.getElementsByTagName("padLayout").item(0) != null) {
                        int numPartitions = Integer.parseInt(ptp.getElementsByTagName("padLayout").item(0).getTextContent().split("_")[0]);
                        SequencerPartitionContainer f = new SequencerPartitionContainerImpl();
                        if (f.getPlatform() == null && r.getSequencerReference().getPlatform() != null) {
                          f.setPlatform(r.getSequencerReference().getPlatform());
                        }
                        f.setPartitionLimit(numPartitions);
                        f.setIdentificationBarcode(ptpId);

                        log.debug("\\_ Created new SequencerPartitionContainer with " + f.getPartitions().size() + " partitions");
                        r.addSequencerPartitionContainer(f);
                      }
                    } else {
                      SequencerPartitionContainer f = fs.iterator().next();
                      log.debug("\\_ Got SequencerPartitionContainer " + f.getId());
                      if (f.getPlatform() == null && r.getSequencerReference().getPlatform() != null) {
                        f.setPlatform(r.getSequencerReference().getPlatform());
                      }
                      if (isStringEmptyOrNull(f.getIdentificationBarcode())) {
                        f.setIdentificationBarcode(ptpId);
                        long flowId = containerService.save(f).getId();
                        f.setId(flowId);
                      }
                    }
                  } catch (ParserConfigurationException e) {
                    log.error("run JSON", e);
                  } catch (TransformerException e) {
                    log.error("run JSON", e);
                  } catch (ParseException e) {
                    log.error("run JSON", e);
                  }
                } else {
                  try {
                    String startDateStr = m.group(1);
                    DateFormat df = new SimpleDateFormat("yyyy'_'MM'_'dd'_'HH'_'mm'_'ss");
                    Date startDate = df.parse(startDateStr);
                    if (!startDate.equals(r.getStartDate())) {
                      r.setStartDate(startDate);

                    }
                  } catch (ParseException e) {
                    log.error("run JSON", e);
                  }
                }

                updatedRuns.put(r.getAlias(), r);
                runsToSave.add(r);
              }
            }
          } catch (IOException e) {
            log.error("run JSON", e);
          }
        } else {
          log.error("Error consuming run " + runName + ". Please check the gsRunProcessor.log file for this run.");
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

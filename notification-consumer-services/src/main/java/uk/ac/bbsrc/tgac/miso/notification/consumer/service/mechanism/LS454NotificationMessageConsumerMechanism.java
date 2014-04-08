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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ls454.LS454Run;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ls454.LS454Status;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.InterrogationException;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.service.integration.mechanism.NotificationMessageConsumerMechanism;
import uk.ac.bbsrc.tgac.miso.core.util.SubmissionUtils;
import uk.ac.bbsrc.tgac.miso.core.util.UnicodeReader;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationUtils;
import uk.ac.bbsrc.tgac.miso.tools.run.RunFolderConstants;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
public class LS454NotificationMessageConsumerMechanism implements NotificationMessageConsumerMechanism<Message<Map<String, List<String>>>, Set<Run>> {
  protected static final Logger log = LoggerFactory.getLogger(LS454NotificationMessageConsumerMechanism.class);

  public boolean attemptRunPopulation = true;

  public void setAttemptRunPopulation(boolean attemptRunPopulation) {
    this.attemptRunPopulation = attemptRunPopulation;
  }

  private final String runDirRegex = RunFolderConstants.LS454_FOLDER_CAPTURE_REGEX;
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

    DateFormat gsLogDateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy");
    DateFormat startDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    StringBuilder sb = new StringBuilder();

    for (JSONObject run : (Iterable<JSONObject>)runs) {
      String runName = run.getString("runName");
      sb.append("Processing " + runName + "\n");
      log.debug("Processing " + runName);

      if (run.has("status")) {
        String runLog = "";
        if (!"".equals(run.getString("status"))) {
          try {
            runLog = new String(IntegrationUtils.decompress(URLDecoder.decode(run.getString("status"), "UTF-8").getBytes()));
          }
          catch (UnsupportedEncodingException e) {
            log.error("Cannot decode status runLog: " + e.getMessage());
            e.printStackTrace();
          }
          catch (IOException e) {
            log.error("Cannot decompress and decode incoming status: " + e.getMessage());
            e.printStackTrace();
          }
        }

        if (!runLog.startsWith("ERROR")) {
          Status is = new LS454Status(runLog);
          is.setHealth(ht);
          is.setRunName(runName);

          Run r = null;
          Matcher m = p.matcher(runName);
          if (m.matches()) {
            try {
              is.setInstrumentName(m.group(2));
              r = requestManager.getRunByAlias(runName);
            }
            catch(IOException ioe) {
              log.warn("Cannot find run by this alias. This usually means the run hasn't been previously imported. If attemptRunPopulation is false, processing will not take place for this run!");
            }
          }

          try {
            if (attemptRunPopulation) {
              if (r == null) {
                log.debug("\\_ Saving new run and status: " + is.getRunName());
                r = new LS454Run();
                r.setAlias(run.getString("runName"));
                r.setDescription(m.group(3));
                //TODO check this properly
                r.setPairedEnd(false);

                if (run.has("fullPath")) {
                  r.setFilePath(run.getString("fullPath"));
                }

                is.setInstrumentName(m.group(2));
                r.setStatus(is);

                SequencerReference sr = null;
                if (run.has("sequencerName")) {
                  sr = requestManager.getSequencerReferenceByName(run.getString("sequencerName"));
                }
                if (sr == null) {
                  sr = requestManager.getSequencerReferenceByName(m.group(2));
                }
                if (sr == null) {
                  sr = requestManager.getSequencerReferenceByName(r.getStatus().getInstrumentName());
                }

                if (run.has("completionDate")) {
                  try {
                    is.setCompletionDate(gsLogDateFormat.parse(run.getString("completionDate")));
                  }
                  catch (ParseException e) {
                    log.error("Cannot parse "+runName+" completion date: " +e.getMessage());
                    e.printStackTrace();
                  }
                }

                if (sr != null) {
                  r.setSequencerReference(sr);
                  runsToSave.add(r);
                }
                else {
                  log.error("\\_ Cannot save " + is.getRunName() + ": no sequencer reference available.");
                }
              }
              else {
                log.debug("\\_ Updating existing run and status: " + is.getRunName());

                r.setAlias(runName);

                r.setPlatformType(PlatformType.LS454);
                r.setDescription(m.group(3));
                //TODO check this properly
                r.setPairedEnd(false);

                if (r.getStatus() != null && run.has("status")) {
                  if (!r.getStatus().getHealth().equals(HealthType.Failed) && !r.getStatus().getHealth().equals(HealthType.Completed)) {
                    r.getStatus().setHealth(ht);
                  }
                }
                else {
                  if (run.has("status")) {
                    r.setStatus(is);
                  }
                }

                r.getStatus().setInstrumentName(m.group(2));

                if (r.getSequencerReference() == null) {
                  SequencerReference sr = null;
                  if (run.has("sequencerName")) {
                    sr = requestManager.getSequencerReferenceByName(run.getString("sequencerName"));
                  }
                  if (sr == null) {
                    sr = requestManager.getSequencerReferenceByName(m.group(2));
                  }
                  if (sr == null) {
                    sr = requestManager.getSequencerReferenceByName(r.getStatus().getInstrumentName());
                  }

                  if (sr != null) {
                    r.setSequencerReference(sr);
                  }
                }

                if (run.has("completionDate")) {
                  try {
                    r.getStatus().setCompletionDate(gsLogDateFormat.parse(run.getString("completionDate")));
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

              if (r.getSequencerReference() != null) {
                if (run.has("runparams")) {
                  try {
                    Document paramsDoc = SubmissionUtils.emptyDocument();
                    SubmissionUtils.transform(new UnicodeReader(run.getString("runparams")), paramsDoc);

                    Element runInfo = (Element)paramsDoc.getElementsByTagName("run").item(0);
                    String runDesc = runInfo.getElementsByTagName("shortName").item(0).getTextContent();
                    r.setDescription(runDesc);

                    String cycles = runInfo.getElementsByTagName("numCycles").item(0).getTextContent();
                    r.setCycles(Integer.parseInt(cycles));

                    String startDateStr = runInfo.getElementsByTagName("date").item(0).getTextContent();
                    Date startDate = startDateFormat.parse(startDateStr);
                    if (!startDate.equals(r.getStatus().getStartDate())) {
                      r.getStatus().setStartDate(startDate);
                      requestManager.saveStatus(r.getStatus());
                    }

                    List<SequencerPartitionContainer<SequencerPoolPartition>> fs = ((LS454Run)r).getSequencerPartitionContainers();

                    Element ptp = (Element)paramsDoc.getElementsByTagName("ptp").item(0);
                    String ptpId = ptp.getElementsByTagName("id").item(0).getTextContent();

                    if (fs.isEmpty()) {
                      if (ptp.getElementsByTagName("padLayout").getLength() > 0 && ptp.getElementsByTagName("padLayout").item(0) != null) {
                        int numPartitions = Integer.parseInt(ptp.getElementsByTagName("padLayout").item(0).getTextContent().split("_")[0]);
                        SequencerPartitionContainer f = new SequencerPartitionContainerImpl();
                        if (f.getPlatform() == null && r.getSequencerReference().getPlatform() != null) {
                          f.setPlatform(r.getSequencerReference().getPlatform());
                        }
//                        else {
//                          f.setPlatformType(PlatformType.LS454);
//                        }
                        f.setPartitionLimit(numPartitions);
                        f.initEmptyPartitions();
                        f.setIdentificationBarcode(ptpId);

                        log.debug("\\_ Created new SequencerPartitionContainer with "+f.getPartitions().size()+" partitions");
                        ((RunImpl)r).addSequencerPartitionContainer(f);
                      }
                    }
                    else {
                      SequencerPartitionContainer f = fs.iterator().next();
                      log.debug("\\_ Got SequencerPartitionContainer " + f.getId());
                      if (f.getPlatform() == null && r.getSequencerReference().getPlatform() != null) {
                        f.setPlatform(r.getSequencerReference().getPlatform());
                      }
//                      else {
//                        f.setPlatformType(PlatformType.LS454);
//                      }
                      if (f.getIdentificationBarcode() == null || "".equals(f.getIdentificationBarcode())) {
                        f.setIdentificationBarcode(ptpId);
                        long flowId = requestManager.saveSequencerPartitionContainer(f);
                        f.setId(flowId);
                      }
                    }
                  }
                  catch (ParserConfigurationException e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                  }
                  catch (TransformerException e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                  }
                  catch (ParseException e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                  }
                }
                else {
                  try {
                    String startDateStr = m.group(1);
                    DateFormat df = new SimpleDateFormat("yyyy'_'MM'_'dd'_'HH'_'mm'_'ss");
                    Date startDate = df.parse(startDateStr);
                    if (!startDate.equals(r.getStatus().getStartDate())) {
                      r.getStatus().setStartDate(startDate);
                      requestManager.saveStatus(r.getStatus());
                    }
                  }
                  catch (ParseException e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                  }
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
        else {
          log.error("Error consuming run "+runName+". Please check the gsRunProcessor.log file for this run.");
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

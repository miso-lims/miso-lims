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

package uk.ac.bbsrc.tgac.miso.notification.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import nki.core.MetrixContainer;
import nki.decorators.MetrixContainerDecorator;
import nki.parsers.illumina.ExtractionMetrics;
import nki.parsers.xml.RunInfoHandler;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.tools.run.util.FileSetTransformer;

/**
 * uk.ac.bbsrc.tgac.miso.notification.util
 * <p/>
 * Transforms relevant Illumina metadata files into a Map to form the payload of a Message
 * 
 * @author Rob Davey
 * @date 10-Dec-2010
 * @since 0.1.6
 */
public class IlluminaTransformer implements FileSetTransformer<String, String, File> {
  static class ComputeInterOp extends RunTransform<Document, MetrixContainer> {

    @Override
    protected MetrixContainer convert(Document input, IlluminaRunMessage output) throws Exception {
      output.getSummary().setRunDirectory(output.getFullPath());

      // Metrix fails to set current cycle, and parsing ErrorMetricsOut.bin depends on this, so set it here
      String extractionMetricsPath = output.getFullPath() + "/InterOp/" + nki.constants.Constants.EXTRACTION_METRICS;
      ExtractionMetrics eim = new ExtractionMetrics(extractionMetricsPath, null);
      output.getSummary().setCurrentCycle(eim.getLastCycle());

      RunInfoHandler.parseAll(input, output.getSummary());
      return new MetrixContainer(output.getSummary(), false); // Note: this is very slow, but the data is cached at least
    }

  }

  private static final class CountReads extends RunSink<Document> {
    @Override
    public void process(Document input, IlluminaRunMessage output) throws Exception {
      if (output.getNumReads() == null || output.getNumReads() == 0) {
        output.setNumReads(input.getElementsByTagName("Read").getLength());
      }
    }
  }

  private static class LastCycleComplete extends RunSink<Integer> {

    @Override
    public void process(Integer input, IlluminaRunMessage output) throws Exception {
      if (output.getNumCycles() != null) {
        boolean ok = output.getNumCycles().equals(input);
        if (!ok) {
          output.setSeenLastCycle(false);
        } else if (output.hasSeenLastCycle() == null) {
          output.setSeenLastCycle(true);
        }
      }
    }
  }

  private static class WriteCompletionDate extends WriteNewestDateField {

    @Override
    protected Date getField(IlluminaRunMessage info) {
      return info.getCompletionDate();
    }

    @Override
    protected void setField(IlluminaRunMessage info, Date value) {
      info.setCompletionDate(value);
    }

  }

  private static final class WriteContainerId extends WriteCheckedField<String> {
    @Override
    protected String getField(IlluminaRunMessage info) {
      return info.getContainerId();
    }

    @Override
    protected void setField(IlluminaRunMessage info, String value) {
      info.setContainerId(value);
    }
  }

  private static final class WriteLaneCount extends WriteCheckedField<Integer> {

    @Override
    protected Integer getField(IlluminaRunMessage info) {
      return info.getLaneCount();
    }

    @Override
    protected void setField(IlluminaRunMessage info, Integer value) {
      info.setLaneCount(value);
    }
  }

  private static final class WriteMetrix extends WriteCheckedField<MetrixContainer> {
    @Override
    protected MetrixContainer getField(IlluminaRunMessage info) {
      return info.getMetrixContainer();
    }

    @Override
    protected void setField(IlluminaRunMessage info, MetrixContainer value) {
      info.setMetrixContainer(value);
    }
  }

  static class WriteMetrixJson extends RunSink<MetrixContainer> {

    @Override
    public void process(MetrixContainer input, IlluminaRunMessage output) throws Exception {
      output.setMetrixJson(new MetrixContainerDecorator(input, true).toJSON().toJSONString());
    }

  }

  private static class WriteNumCycles extends WriteCheckedField<Integer> {

    @Override
    protected Integer getField(IlluminaRunMessage info) {
      return info.getNumCycles();
    }

    @Override
    protected void setField(IlluminaRunMessage info, Integer value) {
      info.setNumCycles(value);
    }

  }

  private static final class WriteNumReads extends WriteCheckedField<Integer> {

    @Override
    protected Integer getField(IlluminaRunMessage info) {
      return info.getNumReads();
    }

    @Override
    protected void setField(IlluminaRunMessage info, Integer value) {
      info.setNumReads(value);
    }
  }

  private static final class WriteRunInfo extends WriteCheckedField<String> {

    @Override
    protected String getField(IlluminaRunMessage info) {
      return info.getRuninfo();
    }

    @Override
    protected void setField(IlluminaRunMessage info, String value) {
      info.setRuninfo(value);
    }
  }

  private static final class WriteRunName extends WriteCheckedField<String> {
    @Override
    protected String getField(IlluminaRunMessage info) {
      return info.getRunName();
    }

    @Override
    protected void setField(IlluminaRunMessage info, String value) {
      info.setRunName(value);
    }
  }

  private static final class WriteRunParams extends WriteCheckedField<String> {

    @Override
    protected String getField(IlluminaRunMessage info) {
      return info.getRunparams();
    }

    @Override
    protected void setField(IlluminaRunMessage info, String value) {
      info.setRunparams(value);
    }

  }

  private static final class WriteSequencerName extends WriteCheckedField<String> {
    @Override
    protected String getField(IlluminaRunMessage info) {
      return info.getSequencerName();
    }

    @Override
    protected void setField(IlluminaRunMessage info, String value) {
      info.setSequencerName(value);
    }
  }

  private static class WriteStartDate extends WriteCheckedField<Date> {

    @Override
    protected Date getField(IlluminaRunMessage info) {
      return info.getStartDate();
    }

    @Override
    protected void setField(IlluminaRunMessage info, Date value) {
      info.setStartDate(value);
    }

  }

  private static final class WriteStatus extends WriteCheckedField<String> {

    @Override
    protected String getField(IlluminaRunMessage info) {
      return info.getStatus();
    }

    @Override
    protected void setField(IlluminaRunMessage info, String value) {
      info.setStatus(value);
    }
  }

  public static final String JSON_COMPLETE_DATE = "completionDate";

  public static final String JSON_CONTAINER_ID = "containerId";

  public static final String JSON_FULL_PATH = "fullPath";

  public static final String JSON_LANE_COUNT = "laneCount";

  public static final String JSON_NUM_CYCLES = "numCycles";

  public static final String JSON_RUN_INFO = "runinfo";

  public static final String JSON_RUN_NAME = "runName";

  public static final String JSON_RUN_PARAMS = "runparams";

  public static final String JSON_SEQUENCER_NAME = "sequencerName";

  public static final String JSON_START_DATE = "startDate";

  public static final String JSON_STATUS = "status";

  private final static String lastDateEntryLogPattern = "(\\d{1,2}\\/\\d{1,2}\\/\\d{4},\\d{2}:\\d{2}:\\d{2})\\.\\d{3},\\d+,\\d+,\\d+,.*";

  protected static final Logger log = LoggerFactory.getLogger(IlluminaTransformer.class);

  private final static String logDateFormat = "MM'/'dd'/'yyyy','HH:mm:ss";

  private final static String runCompleteLogPattern = "(\\d{1,2}\\/\\d{1,2}\\/\\d{4},\\d{2}:\\d{2}:\\d{2})\\.\\d{3},\\d+,\\d+,\\d+,Proce[s||e]sing\\s+completed\\.\\s+Run\\s+has\\s+finished\\.";

  public static final String STATUS_COMPLETE = "Completed";

  public static final String STATUS_FAILED = "Failed";

  public static final String STATUS_RUNNING = "Running";

  public static final String STATUS_UNKNOWN = "Unknown";

  private static final RunSink<String> steps = new All<String>(makeRunParametersProcessor(), makeRunStatusProcessor(),
      makeRunInfoProcessor(), new CheckLoggedFailures(), new CheckLastCycle(), makeDates(), makeBasecallChecks());

  private static All<String> makeBasecallChecks() {
    FindFile netcopyComplete = new FindFile("/Basecalling_Netcopy_complete.txt", "/Basecalling_Netcopy_complete_SINGLEREAD.txt");
    new RunSink<InputStream>() {

      @Override
      public void process(InputStream input, IlluminaRunMessage output) throws Exception {
        output.setBaseCallComplete(true);
      }
    }.attachTo(netcopyComplete);
    FindFile runCompleted = new FindFile("/Run.completed");
    runCompleted.add(new RunSink<InputStream>() {

      @Override
      public void process(InputStream input, IlluminaRunMessage output) throws Exception {
        output.setHealth(HealthType.Completed);
      }
    });
    return new All<>(netcopyComplete, runCompleted, new CheckNumberedBasecallFiles());
  }

  /**
   * Attempts to find the sequencer run's start and end date, and add them to the run data
   */
  private static RunSink<String> makeDates() {
    All<String> all = new All<>();
    new DateInRunName().attachTo(all).add(new WriteStartDate());
    WriteCompletionDate writeCompletion = new WriteCompletionDate();
    RunSink<String> writeCompletionDate = new AsDate(logDateFormat).add(writeCompletion);
    RunTransform<String, String> onlyIfUnset = writeCompletion.onlyIfUnset();
    onlyIfUnset.attachTo(all);
    new MatchPatternInFile("/Data/RTALogs/Log.txt", runCompleteLogPattern, lastDateEntryLogPattern).attachTo(onlyIfUnset)
        .add(writeCompletionDate);
    new MatchPatternInFile("/Data/Log.txt", runCompleteLogPattern, lastDateEntryLogPattern).attachTo(onlyIfUnset).add(writeCompletionDate);
    new MatchPatternInFile("/Logs/CycleTimes.txt",
        "(\\d{1,2}\\/\\d{1,2}\\/\\d{4})\\s+(\\d{2}:\\d{2}:\\d{2})\\.\\d{3}\\s+[A-z0-9]+\\s+\\d+\\s+End\\s{1}Imaging").attachTo(onlyIfUnset)
            .add(writeCompletionDate);
    new MatchPatternInFile("/Events.log", "\\.*\\s+(\\d{1,2}\\/\\d{2}\\/\\d{4})\\s+(\\d{1,2}:\\d{2}:\\d{2}).\\d+.*").attachTo(onlyIfUnset)
        .add(writeCompletionDate);
    new MatchPatternInFile("/RTAComplete.txt", "\\.*(\\d{1,2}\\/\\d{1,2}\\/\\d{4}),(\\d{1,2}:\\d{1,2}:\\d{1,2}).\\d+.*")
        .attachTo(onlyIfUnset).add(writeCompletionDate);
    return all;
  }

  private static RunSink<String> makeRunInfoProcessor() {
    FindFile matchFiles = new FindFile("/RunInfo.xml");
    RunTransform<?, Document> readXmlDocument = new ParseXml().attachTo(matchFiles);
    new WriteXml().attachTo(readXmlDocument).add(new WriteRunInfo());
    new CountReads().attachTo(readXmlDocument);
    new RunSink<Document>() {

      @Override
      public void process(Document input, IlluminaRunMessage output) throws Exception {
        if (output.getNumCycles() != null && output.getNumCycles() != 0) return;
        int sum = 0;
        NodeList nodes = input.getElementsByTagName("Read");
        for (int i = 0; i < nodes.getLength(); i++) {
          Element e = (Element) nodes.item(i);
          if (e.hasAttribute("NumCycles")) {
            sum += Integer.parseInt(e.getAttribute("NumCycles"));
          }
        }
        output.setNumCycles(sum);
      }

    }.attachTo(readXmlDocument);
    new FindXmlElement("Instrument").attachTo(readXmlDocument).toText().add(new WriteSequencerName());
    new FindXmlElement("FlowcellId", "Flowcell").attachTo(readXmlDocument).toText().add(new WriteContainerId());
    new FindXmlElement("FlowcellLayout").attachTo(readXmlDocument)
        .add(new GetAttribute("LaneCount").add(new AsInteger().add(new WriteLaneCount())));
    new ComputeInterOp().attachTo(readXmlDocument).add(new WriteMetrix()).add(new WriteMetrixJson());
    return matchFiles;
  }

  private static RunSink<String> makeRunParametersProcessor() {
    FindFile matchFiles = new FindFile("/runParameters.xml", "/RunParameters.xml");
    RunTransform<?, Document> readXmlDocument = new ParseXml().attachTo(matchFiles);
    new WriteXml().attachTo(readXmlDocument).add(new WriteRunParams());
    new FindXmlElement("ScannerID").attachTo(readXmlDocument).toText().add(new WriteSequencerName());
    new FindXmlElement("Barcode").attachTo(readXmlDocument).toText().add(new WriteContainerId());
    new FindXmlElement("ReagentKits").attachTo(readXmlDocument).add(new CollectKits());
    return matchFiles;
  }

  private static RunSink<String> makeRunStatusProcessor() {
    FindFile matchFiles = new FindFile("/Data/Status.xml", "/Data/reports/Status.xml");
    RunTransform<?, Document> readXmlDocument = new ParseXml().attachTo(matchFiles);
    new WriteXml().attachTo(readXmlDocument).add(new WriteStatus());
    new FindXmlElement("RunName").attachTo(readXmlDocument).toText().add(new WriteRunName());
    new FindXmlElement("NumberOfReads").attachTo(readXmlDocument).toText().add(new AsInteger().add(new WriteNumReads()));
    new FindXmlElement("NumCycles", "NumberCycles").attachTo(readXmlDocument).toText().add(new AsInteger().add(new WriteNumCycles()));
    new FindXmlElement("ImgCycle").attachTo(readXmlDocument).toText().add(new AsInteger().add(new LastCycleComplete()));
    new FindXmlElement("ScoreCycle").attachTo(readXmlDocument).toText().add(new AsInteger().add(new LastCycleComplete()));
    new FindXmlElement("CallCycle").attachTo(readXmlDocument).toText().add(new AsInteger().add(new LastCycleComplete()));
    return matchFiles;
  }

  private final Map<String, IlluminaRunMessage> cache = new HashMap<>();

  public Map<String, String> transform(Message<Set<File>> message) {
    return transform(message.getPayload());
  }

  @Override
  public Map<String, String> transform(Set<File> files) {
    log.info("Processing " + files.size() + " Illumina run directories...");

    int count = 0;
    for (File rootFile : files) {
      count++;
      String countStr = "[#" + count + "/" + files.size() + "] ";
      String runName = rootFile.getName();

      if (!rootFile.isDirectory()) {
        log.error(runName + " :: Not a directory");
        continue;
      }
      if (!rootFile.canRead()) {
        log.error(runName + " :: Permission denied");
        continue;
      }
      if (cache.containsKey(rootFile.getName())) {
        IlluminaRunMessage message = cache.get(rootFile.getName());
        if (!message.getFullPath().equals(rootFile.getAbsolutePath())) {
          message.setFullPath(rootFile.getAbsolutePath());
        }
        if (message.getHealth() == HealthType.Completed) {
          continue;
        }
      }

      try {
        log.debug(countStr + "Processing run " + runName);
        IlluminaRunMessage message = new IlluminaRunMessage();
        message.setRunName(runName);
        message.setFullPath(rootFile.getAbsolutePath());
        steps.process(rootFile.getAbsolutePath(), message);
        message.setHealth(determineHealth(message));
        log.info("Health of " + message.getRunName() + " is " + message.getHealth());
        cache.put(message.getRunName(), message);
      } catch (Exception e) {
        log.error("Failed to process " + runName, e);
      }
    }

    HashMap<HealthType, List<IlluminaRunMessage>> results = new HashMap<>();
    for (HealthType t : HealthType.values()) {
      results.put(t, new ArrayList<IlluminaRunMessage>());
    }
    for (IlluminaRunMessage run : cache.values()) {
      results.get(run.getHealth()).add(run);
    }

    try {
      HashMap<String, String> stringifiedResults = new HashMap<>();
      ObjectMapper mapper = new ObjectMapper();
      for (java.util.Map.Entry<HealthType, List<IlluminaRunMessage>> entry : results.entrySet()) {
        stringifiedResults.put(entry.getKey().getKey(), mapper.writeValueAsString(entry.getValue()));
      }
      return stringifiedResults;
    } catch (IOException e) {
      log.error("Failed to serialise.", e);
      return Collections.emptyMap();
    }
  }

  private HealthType determineHealth(IlluminaRunMessage message) {
    if (message.getHealth() == HealthType.Failed) return HealthType.Failed;
    if (message.getHealth() == HealthType.Completed) return HealthType.Completed;
    boolean hasSeenLastCycle = message.hasSeenLastCycle() == null ? false : message.hasSeenLastCycle();
    if (!hasSeenLastCycle && !message.isNumberedBaseCallsComplete() && !message.isBaseCallComplete()) {
      return HealthType.Running;
    }
    if (hasSeenLastCycle) {
      return HealthType.Completed;
    }
    return HealthType.Unknown;
  }

  public IlluminaRunMessage transformInterOpOnly(File file) {
    String runName = file.getName();
    return cache.containsKey(runName) ? cache.get(runName) : null;
  }
}

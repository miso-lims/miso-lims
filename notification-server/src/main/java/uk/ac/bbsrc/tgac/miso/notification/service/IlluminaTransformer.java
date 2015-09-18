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
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import nki.core.MetrixContainer;
import nki.decorators.MetrixContainerDecorator;
import nki.objects.Summary;
import nki.parsers.illumina.ExtractionMetrics;
import nki.parsers.xml.RunInfoHandler;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.integration.Message;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import uk.ac.bbsrc.tgac.miso.core.util.SubmissionUtils;
import uk.ac.bbsrc.tgac.miso.notification.util.NotificationUtils;
import uk.ac.bbsrc.tgac.miso.notification.util.PossiblyGzippedFileUtils;
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
  protected static final Logger log = LoggerFactory.getLogger(IlluminaTransformer.class);
  
  public static final String JSON_RUN_NAME = "runName";
  public static final String JSON_FULL_PATH = "fullPath";
  public static final String JSON_RUN_INFO = "runinfo";
  public static final String JSON_RUN_PARAMS = "runparams";
  public static final String JSON_STATUS = "status";
  public static final String JSON_SEQUENCER_NAME = "sequencerName";
  public static final String JSON_CONTAINER_ID = "containerId";
  public static final String JSON_LANE_COUNT = "laneCount";
  public static final String JSON_NUM_CYCLES = "numCycles";
  public static final String JSON_START_DATE = "startDate";
  public static final String JSON_COMPLETE_DATE = "completionDate";
  
  public static final String STATUS_COMPLETE = "Completed";
  public static final String STATUS_RUNNING = "Running";
  public static final String STATUS_UNKNOWN = "Unknown";
  public static final String STATUS_FAILED = "Failed";
  
  private final String oldStatusPath = "/Data/Status.xml";
  private final String newStatusPath = "/Data/reports/Status.xml";
  private final String runInfoPath = "/RunInfo.xml";
  private final String runParametersPath = "/runParameters.xml";

  private final Map<String, String> finishedCache = new HashMap<>();

  private final Pattern runCompleteLogPattern = Pattern.compile(
      "(\\d{1,2}\\/\\d{1,2}\\/\\d{4},\\d{2}:\\d{2}:\\d{2})\\.\\d{3},\\d+,\\d+,\\d+,Proce[s||e]sing\\s+completed\\.\\s+Run\\s+has\\s+finished\\."
  );

  private final Pattern lastDateEntryLogPattern = Pattern.compile(
      "(\\d{1,2}\\/\\d{1,2}\\/\\d{4},\\d{2}:\\d{2}:\\d{2})\\.\\d{3},\\d+,\\d+,\\d+,.*"
  );

  private final DateFormat logDateFormat = new SimpleDateFormat("MM'/'dd'/'yyyy','HH:mm:ss");

  public Map<String, String> transform(Message<Set<File>> message) {
    return transform(message.getPayload());
  }
  
  @Override
  public Map<String, String> transform(Set<File> files) {
    log.info("Processing " + files.size() + " Illumina run directories...");

    int count = 0;

    //TODO modify this to use a JSONObject instead of a Map
    Map<String, JSONArray> map = new HashMap<>();

    map.put(STATUS_RUNNING, new JSONArray());
    map.put(STATUS_COMPLETE, new JSONArray());
    map.put(STATUS_UNKNOWN, new JSONArray());
    map.put(STATUS_FAILED, new JSONArray());

    for (File rootFile : files) {
      count++;
      String countStr = "[#" + count + "/" + files.size() + "] ";
      if (rootFile.isDirectory()) {
        if (rootFile.canRead()) {
          JSONObject run = new JSONObject();

          try {
            String runName = rootFile.getName();
            log.debug(countStr + "Processing run " + runName);

            if (!finishedCache.keySet().contains(runName)) {
              int numReads = 0;
              
              run.put(JSON_RUN_NAME, runName);
              run.put(JSON_FULL_PATH, rootFile.getCanonicalPath()); //follow symlinks!
              
              // Get xml files
              Document statusDoc = null;
              if (PossiblyGzippedFileUtils.checkExists(rootFile, oldStatusPath)) {
                statusDoc = PossiblyGzippedFileUtils.getXmlDocument(rootFile, oldStatusPath);
                if (statusDoc == null) {
                  run.put(JSON_STATUS, "<error><RunName>" + runName + "</RunName><ErrorMessage>Cannot read status file</ErrorMessage></error>");
                }
              }
              else if (PossiblyGzippedFileUtils.checkExists(rootFile, newStatusPath)) {
                statusDoc = PossiblyGzippedFileUtils.getXmlDocument(rootFile, newStatusPath);
                if (statusDoc == null) {
                  run.put(JSON_STATUS, "<error><RunName>" + runName + "</RunName><ErrorMessage>Cannot read status file</ErrorMessage></error>");
                }
              }
              Document runInfoDoc = PossiblyGzippedFileUtils.getXmlDocument(rootFile, runInfoPath);
              Document runParamDoc = PossiblyGzippedFileUtils.getXmlDocument(rootFile, runParametersPath);
              
              // Get main stuff from Status.xml
              if (statusDoc != null) {
                run.put(JSON_STATUS, SubmissionUtils.transform(statusDoc));
                if (statusDoc.getElementsByTagName("RunName").getLength() > 0) {
                  runName = statusDoc.getElementsByTagName("RunName").item(0).getTextContent();
                  run.put(JSON_RUN_NAME, runName);
                }
                
                if (statusDoc.getElementsByTagName("NumberOfReads").getLength() != 0) {
                  numReads = new Integer(statusDoc.getElementsByTagName("NumberOfReads").item(0).getTextContent());
                }
              }
              
              // Get main stuff from RunInfo.xml
              if (runInfoDoc != null) {
                run.put(JSON_RUN_INFO, SubmissionUtils.transform(runInfoDoc));
                checkRunInfo(runInfoDoc, run);
                if (numReads == 0) {
                  numReads = runInfoDoc.getElementsByTagName("Read").getLength();
                }
              }
              
              // Get main stuff from runParams.xml
              if (runParamDoc != null) {
                run.put(JSON_RUN_PARAMS, SubmissionUtils.transform(runParamDoc));
                checkRunParams(runParamDoc, run);
              }
              
              boolean lastCycleComplete = checkCycles(rootFile, run, statusDoc, runInfoDoc);
              checkDates(rootFile, run);
              String status = checkRunStatus(run, rootFile, numReads, lastCycleComplete);
              map.get(status).add(run);
            }
            else {
              log.info("Run already scanned. Getting cached version " + runName);

              //if a completed run has been moved (e.g. archived), update the new path
              JSONObject json = JSONObject.fromObject(finishedCache.get(runName));
              if (json.has("fullPath") && !rootFile.getCanonicalPath().equals(json.getString("fullPath"))) {
                log.info("Cached path changed. Updating " + runName);
                json.put("fullPath", rootFile.getCanonicalPath());
                finishedCache.put(runName, json.toString());
              }
              map.get(STATUS_COMPLETE).add(finishedCache.get(runName));
            }
          }
          catch (ParserConfigurationException e) {
            log.error("Error configuring parser: " + e.getMessage());
            e.printStackTrace();
          }
          catch (TransformerException e) {
            log.error("Error transforming XML: " + e.getMessage());
            e.printStackTrace();
          }
          catch (IOException e) {
            log.error("Error with file IO: " + e.getMessage());
            e.printStackTrace();
          }
        }
        else {
          log.error(rootFile.getName() + " :: Permission denied");
        }
      }
    }

    HashMap<String, String> smap = new HashMap<>();
    for (String key : map.keySet()) {
      smap.put(key, map.get(key).toString());

      if (STATUS_COMPLETE.equals(key)) {
        for (JSONObject run : (Iterable<JSONObject>)map.get(key)) {
          if (!finishedCache.keySet().contains(run.getString("runName"))) {
            log.info("Caching completed run " + run.getString("runName"));
            finishedCache.put(run.getString("runName"), run.toString());
          }
        }
      }
    }

    return smap;
  }
  
  /**
   * Reads a RunInfo document, looks for sequencer name, container ID, and lane count, and adds to the run any of these that are 
   * not already included
   * 
   * @param runInfoDoc the RunInfo.xml Document
   * @param run JSON representation of the sequencer run
   * @throws TransformerException
   * @throws IOException
   * @throws ParserConfigurationException
   */
  private void checkRunInfo(Document runInfoDoc, JSONObject run) {
    if (!run.has(JSON_SEQUENCER_NAME) && runInfoDoc.getElementsByTagName("Instrument").getLength() != 0) {
      run.put(JSON_SEQUENCER_NAME, runInfoDoc.getElementsByTagName("Instrument").item(0).getTextContent());
    }

    if (runInfoDoc.getElementsByTagName("FlowcellId").getLength() != 0) {
      run.put(JSON_CONTAINER_ID, runInfoDoc.getElementsByTagName("FlowcellId").item(0).getTextContent());
    }
    else if (runInfoDoc.getElementsByTagName("Flowcell").getLength() != 0) {
      run.put(JSON_CONTAINER_ID, runInfoDoc.getElementsByTagName("Flowcell").item(0).getTextContent());
    }

    if (runInfoDoc.getElementsByTagName("FlowcellLayout").getLength() != 0) {
      NamedNodeMap n = runInfoDoc.getElementsByTagName("FlowcellLayout").item(0).getAttributes();
      if (n.getLength() != 0) {
        Node attr = n.getNamedItem("LaneCount");
        if (attr != null) {
          run.put(JSON_LANE_COUNT, attr.getTextContent());
        }
      }
    }
  }
  
  /**
   * Reads the runParameters.xml document, looks for the sequencer name and container ID, and adds to the run any of these that are not 
   * already included
   * 
   * @param runParamDoc the runParameters.xml Document
   * @param run JSON representation of the sequencer run
   * @return true if runParameters.xml is missing, but runParameters.xml* is found, which indicates run failure; false otherwise
   * @throws TransformerException
   * @throws IOException
   * @throws ParserConfigurationException
   */
  private void checkRunParams(Document runParamDoc, JSONObject run) {
    if (!run.has(JSON_SEQUENCER_NAME) && runParamDoc.getElementsByTagName("ScannerID").getLength() != 0) {
      run.put(JSON_SEQUENCER_NAME, runParamDoc.getElementsByTagName("ScannerID").item(0).getTextContent());
    }

    if (!run.has(JSON_CONTAINER_ID) && runParamDoc.getElementsByTagName("Barcode").getLength() != 0) {
      run.put(JSON_CONTAINER_ID, runParamDoc.getElementsByTagName("Barcode").item(0).getTextContent());
    }
    
    run.put("kits", checkKits(runParamDoc));
  }
  
  /**
   * Looks for a file in the run directory that begins with "runParameters.xml" which may indicate a failure
   * 
   * @param rootFile run directory
   * @return true if any such files are found; false otherwise
   */
  private boolean checkRunParametersXFile(File rootFile) {
    FileFilter fileFilter = new WildcardFileFilter("runParameters.xml*");
    File[] filterFiles = rootFile.listFiles(fileFilter);
    if (rootFile.listFiles(fileFilter) != null && filterFiles.length > 0) {
      return true;
    }
    return false;
  }
  
  /**
   * Checks Status.xml and RunInfo.xml for the total number of cycles, and looks for evidence of the final cycle completing. Status.xml 
   * and several log files are examined. The number of cycles, if found, is added to the run data
   * 
   * @param rootFile run directory
   * @param run JSON representation of the sequencer run
   * @param statusDoc Status.xml Document (may be null)
   * @param runInfoDoc RunInfo.xml Document (may be null)
   * @return true if the method finds evidence of the final cycle completing; false otherwise
   * @throws FileNotFoundException
   * @throws IOException
   */
  private boolean checkCycles(File rootFile, JSONObject run, Document statusDoc, Document runInfoDoc) throws FileNotFoundException, IOException {
    String runName = run.getString(JSON_RUN_NAME);
    int numCycles = 0;
    
    if (statusDoc != null) {
      if (statusDoc.getElementsByTagName("NumCycles").getLength() != 0) {
        numCycles = new Integer(statusDoc.getElementsByTagName("NumCycles").item(0).getTextContent());
      }
      else if (statusDoc.getElementsByTagName("NumberCycles").getLength() != 0) {
        numCycles = new Integer(statusDoc.getElementsByTagName("NumberCycles").item(0).getTextContent());
      }
      if (numCycles > 0) {
        run.put(JSON_NUM_CYCLES, numCycles);
        if (statusDoc.getElementsByTagName("ImgCycle").getLength() != 0
            && statusDoc.getElementsByTagName("ScoreCycle").getLength() != 0
            && statusDoc.getElementsByTagName("CallCycle").getLength() != 0) {
          int imgCycle = new Integer(statusDoc.getElementsByTagName("ImgCycle").item(0).getTextContent());
          int scoreCycle = new Integer(statusDoc.getElementsByTagName("ScoreCycle").item(0).getTextContent());
          int callCycle = new Integer(statusDoc.getElementsByTagName("CallCycle").item(0).getTextContent());
          return numCycles == imgCycle && numCycles == scoreCycle && numCycles == callCycle;
        }
      }
    }
    
    if (numCycles == 0 && runInfoDoc != null) {
      NodeList nl = runInfoDoc.getElementsByTagName("Read");
      if (nl.getLength() > 0) {
        for (int i = 0; i < nl.getLength(); i++) {
          Element e = (Element) nl.item(i);
          if (!"".equals(e.getAttributeNS(null, "NumCycles"))) {
            numCycles += Integer.parseInt(e.getAttributeNS(null, "NumCycles"));
          }
        }
        run.put(JSON_NUM_CYCLES, numCycles);
      }
    }
    
    // Check for Post Run Step log
    File dir = new File(rootFile, "/Logs/");
    FileFilter fileFilter = new WildcardFileFilter("*Post Run Step.log*");
    File[] filterFiles = dir.listFiles(fileFilter);
    if (filterFiles != null && filterFiles.length > 0) {
      return true;
    }
    
    if (numCycles > 0) {
      // Check for last cycle log file
      if (PossiblyGzippedFileUtils.checkExists(rootFile, "/Logs/" + runName + "_Cycle" + numCycles + "_Log.00.log")) {
        return true;
      }
      
      // Check CycleTimes.txt for last cycle complete log message
      String cycleTimeLogPath = "/Logs/CycleTimes.txt";
      if (PossiblyGzippedFileUtils.checkExistsReadable(rootFile, cycleTimeLogPath)) {
        Pattern p = Pattern.compile(
          "(\\d{1,2}\\/\\d{1,2}\\/\\d{4})\\s+(\\d{2}:\\d{2}:\\d{2})\\.\\d{3}\\s+[A-z0-9]+\\s+" + numCycles + "\\s+End\\s{1}Imaging"
        );
        
        Matcher m = PossiblyGzippedFileUtils.tailGrep(rootFile, cycleTimeLogPath, p, 10);
        if (m != null && m.groupCount() > 0) {
          return true;
        }
      }
    }
    return false;
  }
  
  /**
   * Checks for existance the expected Basecalling_Netcopy_complete_X files for a completed run
   * 
   * @param rootFile run directory
   * @param numReads number of reads
   * @return true if the expected files exist; false otherwise
   */
  private boolean checkReadCompleteFiles(File rootFile, int numReads) {
    if (!PossiblyGzippedFileUtils.checkExists(rootFile, "/Basecalling_Netcopy_complete_SINGLEREAD.txt")) {
      if (numReads < 1) return false;
      for (int i = 1; i <= numReads; i++) {
        if (!PossiblyGzippedFileUtils.checkExists(rootFile, "/Basecalling_Netcopy_complete_Read" + (i) + ".txt")
        && !PossiblyGzippedFileUtils.checkExists(rootFile, "/Basecalling_Netcopy_complete_READ" + (i) + ".txt")) {
          log.debug(rootFile.getName() + " :: No Basecalling_Netcopy_complete_Read" + (i) + ".txt / Basecalling_Netcopy_complete_READ" + (i) + ".txt!");
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Attempts to find the sequencer run's start and end date, and add them to the run data
   * 
   * @param rootFile run directory
   * @param run JSON representation of the sequencer run
   * @throws IOException
   */
  private void checkDates(File rootFile, JSONObject run) throws IOException {
    String runName = run.getString("runName");

    String runDirRegex = "(\\d{6})_[A-z0-9]+_\\d+_[A-z0-9_\\+\\-]*";
    Matcher startMatcher = Pattern.compile(runDirRegex).matcher(runName);
    if (startMatcher.matches()) {
      log.debug(runName + " :: Got start date -> " + startMatcher.group(1));
      run.put(JSON_START_DATE, startMatcher.group(1));
    }
    
    final String cycleTimeLogPath = "/Logs/CycleTimes.txt";
    final String rtaLogPath = "/Data/RTALogs/Log.txt";
    final String rtaLog2Path = "/Data/Log.txt";
    final String eventsLogPath = "/Events.log";
    final String rtaCompletePath = "/RTAComplete.txt";
    
    String completed = null;

    // check RTA logs
    if (PossiblyGzippedFileUtils.checkExistsReadable(rootFile, rtaLogPath)) {
      Matcher m = PossiblyGzippedFileUtils.tailGrep(rootFile, rtaLogPath, runCompleteLogPattern, 10);
      if (m != null && m.groupCount() > 0) {
        completed = m.group(1);
        log.debug(runName + " :: Got RTALogs Log.txt completion date -> " + completed);
      }
    }
    else if (PossiblyGzippedFileUtils.checkExistsReadable(rootFile, rtaLog2Path)) {
      Matcher m = PossiblyGzippedFileUtils.tailGrep(rootFile, rtaLog2Path, runCompleteLogPattern, 10);
      if (m != null && m.groupCount() > 0) {
        completed = m.group(1);
        log.debug(runName + " :: Got Log.txt completion date -> " + completed);
      }
    }
    
    if (completed == null) {
    //attempt to get latest log file entry date
      if (PossiblyGzippedFileUtils.checkExistsReadable(rootFile, rtaLogPath)) {
        Matcher m = PossiblyGzippedFileUtils.tailGrep(rootFile, rtaLogPath, lastDateEntryLogPattern, 1);
        if (m != null && m.groupCount() > 0) {
          completed = m.group(1);
          log.debug(runName + " :: Got RTALogs Log.txt last entry date -> " + completed);
        }
      }
      else if (PossiblyGzippedFileUtils.checkExistsReadable(rootFile, rtaLog2Path)) {
        Matcher m = PossiblyGzippedFileUtils.tailGrep(rootFile, rtaLog2Path, lastDateEntryLogPattern, 1);
        if (m != null && m.groupCount() > 0) {
          completed = m.group(1);
          log.debug(runName + " :: Got Log.txt last entry date -> " + completed);
        }
      }
    }
    
    // Try CycleTimes.txt
    if (run.has(JSON_NUM_CYCLES) && PossiblyGzippedFileUtils.checkExistsReadable(rootFile, cycleTimeLogPath)) {
      int numCycles = run.getInt(JSON_NUM_CYCLES);
      Pattern p = Pattern.compile(
          "(\\d{1,2}\\/\\d{1,2}\\/\\d{4})\\s+(\\d{2}:\\d{2}:\\d{2})\\.\\d{3}\\s+[A-z0-9]+\\s+" + numCycles + "\\s+End\\s{1}Imaging"
      );

      Matcher m = PossiblyGzippedFileUtils.tailGrep(rootFile, cycleTimeLogPath, p, 10);
      if (m != null && m.groupCount() > 0) {
        String cycleDateStr = m.group(1) + "," + m.group(2);
        if (completed == null) {
          completed = cycleDateStr;
          log.debug(runName + " :: Got CycleTimes.txt last cycle date -> " + completed);
        }
        else { // check if this time is newer
          log.debug(runName + " :: Checking " + cycleDateStr + " vs. " + completed);
          try {
            Date cycleDate = logDateFormat.parse(cycleDateStr);
            Date cDate = logDateFormat.parse(completed);

            if (cycleDate.after(cDate)) {
              completed = cycleDateStr;
              log.debug(runName + " :: Cycletimes completion date is newer -> " + completed);
            }
          }
          catch (ParseException e) {
            log.debug(runName + " :: Oops. Can't parse dates. Falling back!");
          }
        }
      }
    }

    //still nothing? attempt with Events.log
    if (completed == null) {
      //attempt to get latest log file entry date
      if (PossiblyGzippedFileUtils.checkExistsReadable(rootFile, eventsLogPath)) {
        log.debug(runName + " :: Checking events log...");
        Pattern p = Pattern.compile(
            "\\.*\\s+(\\d{1,2}\\/\\d{2}\\/\\d{4})\\s+(\\d{1,2}:\\d{2}:\\d{2}).\\d+.*"
        );

        Matcher m = PossiblyGzippedFileUtils.tailGrep(rootFile, eventsLogPath, p, 50);
        if (m != null && m.groupCount() > 0) {
          completed = m.group(1) + "," + m.group(2);
          log.debug(runName + " :: Got last log event date -> " + completed);
        }
      }
    }

    // last ditch attempt with RTAComplete.txt
    if (completed == null) {
      if (PossiblyGzippedFileUtils.checkExistsReadable(rootFile, rtaCompletePath)) {
        log.debug(runName + " :: Last ditch attempt. Checking RTAComplete log...");
        Pattern p = Pattern.compile(
            "\\.*(\\d{1,2}\\/\\d{1,2}\\/\\d{4}),(\\d{1,2}:\\d{1,2}:\\d{1,2}).\\d+.*"
        );

        Matcher m = PossiblyGzippedFileUtils.tailGrep(rootFile, rtaCompletePath, p, 2);
        if (m != null && m.groupCount() > 0) {
          completed = m.group(1) + "," + m.group(2);
          log.debug(runName + " :: Got RTAComplete date -> " + completed);
        }
      }
    }
    
    if (completed == null) completed = "null";
    run.put(JSON_COMPLETE_DATE, completed);
  }

  /**
   * Checks files in the run directory's /Data/RTALogs/ subdirectory for a log message indicating run failure
   * 
   * @param rootFile run directory
   * @return true if a message is found that indicates run failure; false otherwise. A return value of false is not conclusive evidence 
   * that a run has completed or that it was successful
   * @throws IOException
   */
  private boolean checkLoggedFailures(File rootFile) throws IOException {
    File rtaLogDir = new File(rootFile, "/Data/RTALogs/");
    boolean failed = false;
    if (rtaLogDir.exists()) {
      Pattern p = Pattern.compile(".*(Application\\s{1}exited\\s{1}before\\s{1}completion).*");

      for (File f : rtaLogDir.listFiles(new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
          return (name.endsWith("Log_00.txt") || name.endsWith("Log_00.txt.gz") || name.equals("Log.txt") || name.equals("Log.txt.gz"));
        }
      })) {
        Matcher m = PossiblyGzippedFileUtils.tailGrep(f, p, 5);
        if (m != null && m.groupCount() > 0) {
          failed = true;
        }
      }
    }
    return failed;
  }
  
  private String checkRunStatus(JSONObject run, File rootFile, int numReads, boolean lastCycleComplete) throws IOException {
    final String runName = run.getString(JSON_RUN_NAME);
    
    boolean baseCompleteFileFound = PossiblyGzippedFileUtils.checkExists(rootFile, "/Basecalling_Netcopy_complete.txt");
    boolean baseCompleteReadFilesFound = checkReadCompleteFiles(rootFile, numReads);
    
    String returnStatus;
    
    if (baseCompleteReadFilesFound) {
      if (lastCycleComplete) {
        // ReadX files exist, BaseComplete undetermined, lastCycle evidence exists
        log.debug(runName + " :: All Basecalling_Netcopy_complete*.txt exist and last cycle evidence exists.");
        returnStatus = STATUS_COMPLETE;
      }
      else {
        if (baseCompleteFileFound) {
          // ReadX files exist, BaseComplete file exists, No lastCycle evidence
          log.debug(runName + " :: All Basecalling_Netcopy_complete_ReadX.txt exist and Basecalling_Netcopy_complete.txt exists but no evidence of last cycle completion.");
          returnStatus = STATUS_UNKNOWN;
        }
        else {
          // ReadX files exist, No BaseComplete file, No lastCycle evidence
          log.debug(runName + " :: All Basecalling_Netcopy_complete_ReadX.txt exist but Basecalling_Netcopy_complete.txt doesn't exist and no evidence of last cycle completion.");
          returnStatus = STATUS_UNKNOWN;
        }
      }
    }
    else { // Missing ReadX files
      if (PossiblyGzippedFileUtils.checkExists(rootFile, "/Run.completed")) {
        // Missing ReadX files, Run.completed exists
        log.debug(runName + " :: Basecalling_Netcopy_complete*.txt don't exist, but Run.completed exists.");
        returnStatus = STATUS_COMPLETE;
      }
      else {
        // Missing ReadX files, no Run.completed
        if (!baseCompleteFileFound && !lastCycleComplete) {
          // Missing ReadX files, no Run.completed, no BaseComplete file, no lastCycle evidence
          log.debug(runName + " :: Basecalling_Netcopy_complete*.txt don't exist and no evidence of last cycle completion.");
          returnStatus = STATUS_RUNNING;
        }
        else {
          // Missing ReadX files, no Run.completed, BaseComplete undetermined, lastCycle undetermined
          log.debug(runName + " :: Basecalling_Netcopy_complete*.txt don't exist.");
          returnStatus = STATUS_UNKNOWN;
        }
      }
    }
    
    boolean failed = checkLoggedFailures(rootFile) || (!run.has(JSON_RUN_PARAMS) && checkRunParametersXFile(rootFile));
    if (failed) {
      return STATUS_FAILED;
    }
    else {
      log.debug("Run is "+returnStatus);
      return returnStatus;
    }
  }

  private JSONArray checkKits(Document runParamDoc) {
    Set<String> rlist = new HashSet<>();
    NodeList reagents = runParamDoc.getElementsByTagName("ReagentKits");
    if (reagents.getLength() > 0) {
      Element rkit = (Element)reagents.item(0);
      NodeList kits = rkit.getElementsByTagName("ID");
      for (int i = 0; i < kits.getLength(); i++) {
        Element e = (Element) kits.item(i);
        String rs = e.getTextContent();
        for (String r : rs.split("[,;]")) {
          if (r != null && !"".equals(r)) rlist.add(r.trim());
        }
      }
    }
    return JSONArray.fromObject(rlist);
  }

  private JSONObject parseInterOp(File rootFile) throws IOException {
    Document runInfo = null;
    
    try {
      runInfo = PossiblyGzippedFileUtils.getXmlDocument(rootFile, "/RunInfo.xml");
    } catch (ParserConfigurationException | TransformerException e) {
      log.error("Error parsing file: " + e.getMessage());
      e.printStackTrace();
    }
    
    if (runInfo == null) {
      return null;
    }
    else {
      Summary sum = new Summary();
      sum.setRunDirectory(rootFile.getCanonicalPath());
      
      // Metrix fails to set current cycle, and parsing ErrorMetricsOut.bin depends on this, so set it here
      final String extractionMetricsPath = rootFile.getCanonicalPath() + "/InterOp/" + nki.constants.Constants.EXTRACTION_METRICS;
      ExtractionMetrics eim = new ExtractionMetrics(extractionMetricsPath, 0);
      sum.setCurrentCycle(eim.getLastCycle());
      
      RunInfoHandler.parseAll(runInfo, sum);
      MetrixContainer mc = new MetrixContainer(sum, false); // Note: this is very slow, but the data is cached at least
      return JSONObject.fromObject(new MetrixContainerDecorator(mc, true).toJSON().toJSONString());
    }
  }

  public JSONArray transformInterOpOnly(Set<File> files) {
    log.info("Processing InterOp files for " + files.size() + " Illumina run directories...");
    int count = 0;

    JSONArray map = new JSONArray();
    for (File rootFile : files) {
      count++;
      String countStr = "[#" + count + "/" + files.size() + "] ";
      log.info("Processing " + countStr + rootFile.getName());

      JSONObject run = new JSONObject();
      if (rootFile.isDirectory()) {
        if (rootFile.canRead()) {
          try {
            String runName = rootFile.getName();
            if (!finishedCache.keySet().contains(runName)) {
              //parse interop if completed cache doesn't hold this run
              JSONObject metrix = parseInterOp(rootFile);
              if (metrix != null) {
                run.put("metrix", metrix);
              }
              else {
                run.put("error", "Cannot provide metrics - parsing failed.");
              }
              run.put("runName", runName);
              map.add(run);
            }
            else {
              JSONObject cachedRun = JSONObject.fromObject(finishedCache.get(runName));
              if (!cachedRun.has("metrix")) {
                JSONObject metrix = parseInterOp(rootFile);
                if (metrix != null) {
                  run.put("metrix", metrix);
                  cachedRun.put("metrix", metrix);
                  finishedCache.put(runName, cachedRun.toString());
                }
                else {
                  run.put("error", "Cannot provide metrics - parsing failed.");
                }
              }
              else {
                run.put("metrix", cachedRun.get("metrix"));
              }
              run.put("runName", runName);
              map.add(run);
            }
          }
          catch (IOException e) {
            log.error("Error with file IO: " + e.getMessage());
            e.printStackTrace();
          }
        }
        else {
          log.error(rootFile.getName() + " :: Permission denied");
          run.put("runName", rootFile.getName());
          run.put("error", "Cannot read into run directory. Permission denied.");
          map.add(run);
        }
      }
    }

    return map;
  }

  public byte[] transformToJson(Set<File> files) {
    Map<String, String> smap = transform(files);
    JSONObject json = new JSONObject();
    for (String key : smap.keySet()) {
      json.put(key, JSONArray.fromObject(smap.get(key)));
    }
    return (json.toString() + "\r\n").getBytes();
  }

  public Message<Set<String>> runStatusFilesToStringSetMessage(Message<Set<File>> message) {
    Set<File> files = message.getPayload();
    Set<String> xmls = new HashSet<String>();
    for (File f : files) {
      try {
        xmls.add(SubmissionUtils.transform(f));
      }
      catch (TransformerException e) {
        //e.printStackTrace();
        log.error("Error transforming XML: " + e.getMessage());
      }
      catch (IOException e) {
        //e.printStackTrace();
        log.error("Error with file IO: " + e.getMessage());
      }
    }
    return NotificationUtils.buildSimpleMessage(xmls);
  }

  public Message<Set<String>> runCompletedFilesToStringSetMessage(Message<Set<File>> message) {
    Set<File> files = message.getPayload();
    Set<String> runNames = new HashSet<String>();
    String regex = ".*/([\\d]+_[A-z0-9]+_[\\d]+_[A-z0-9_]*)[/]{0,1}.*";
    Pattern p = Pattern.compile(regex);
    for (File f : files) {
      Matcher m = p.matcher(f.getAbsolutePath());
      if (m.matches()) {
        runNames.add(m.group(1));
      }
    }
    return NotificationUtils.buildSimpleMessage(runNames);
  }

  public Set<String> runStatusJobToStringSet(JobExecution exec) {
    Set<String> files = new HashSet<String>();
    for (Map.Entry<String, JobParameter> params : exec.getJobInstance().getJobParameters().getParameters().entrySet()) {
      File f = new File(params.getValue().toString());
      try {
        files.add(SubmissionUtils.transform(f));
      }
      catch (TransformerException e) {
        //e.printStackTrace();
        log.error("Error transforming XML: " + e.getMessage());
      }
      catch (IOException e) {
        //e.printStackTrace();
        log.error("Error with file IO: " + e.getMessage());
      }
    }
    return files;
  }

  public Set<String> runCompletedJobToStringSet(JobExecution exec) {
    Set<String> runNames = new HashSet<String>();
    for (Map.Entry<String, JobParameter> params : exec.getJobInstance().getJobParameters().getParameters().entrySet()) {
      runNames.add(params.getKey());
    }
    return runNames;
  }
}

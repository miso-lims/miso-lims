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

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;
import org.w3c.dom.Document;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import uk.ac.bbsrc.tgac.miso.core.service.integration.ws.solid.RunArray;
import uk.ac.bbsrc.tgac.miso.core.service.integration.ws.solid.SolidService;
import uk.ac.bbsrc.tgac.miso.core.service.integration.ws.solid.SolidServiceWrapper;
import uk.ac.bbsrc.tgac.miso.core.util.SubmissionUtils;
import uk.ac.bbsrc.tgac.miso.core.util.UnicodeReader;
import uk.ac.bbsrc.tgac.miso.integration.context.ApplicationContextProvider;
import uk.ac.bbsrc.tgac.miso.tools.run.util.FileSetTransformer;

/**
 * uk.ac.bbsrc.tgac.miso.notification.util
 * <p/>
 * Transforms relevant SOLiD web service responses into a Map to form the payload of a Message
 * 
 * @author Rob Davey
 * @date 08-Aug-2011
 * @since 0.0.3
 */
public class SolidTransformer implements FileSetTransformer<String, String, File> {
  protected static final Logger log = LoggerFactory.getLogger(SolidTransformer.class);

  public Map<String, String> transform(Message<Set<File>> message) {
    return transform(message.getPayload());
  }

  @Override
  public Map<String, String> transform(Set<File> files) {
    log.info("Processing " + files.size() + " SOLiD run directories...");

    HashMap<String, JSONArray> map = new HashMap<String, JSONArray>();
    map.put("Running", new JSONArray());
    map.put("Completed", new JSONArray());
    map.put("Unknown", new JSONArray());

    for (File rootFile : files) {
      if (rootFile.isDirectory()) {
        JSONObject run = new JSONObject();
        String runName = rootFile.getName();

        File statusFile = new File(rootFile, "/" + runName + ".xml");

        log.debug("SOLID: Processing " + runName);
        String runDirRegex = "([A-z0-9]+)_([0-9]{8})_(.*)";
        Matcher m = Pattern.compile(runDirRegex).matcher(runName);
        if (m.matches()) {
          String machineName = m.group(1);
          String startDate = m.group(2);
          try {
            SolidServiceWrapper solidServiceWrapper = ApplicationContextProvider.getApplicationContext().getBean(machineName,
                SolidServiceWrapper.class);

            SolidService solidService = solidServiceWrapper.getSolidService();
            RunArray ra = solidService.getSolidPort().getRun(runName, machineName);
            if (ra != null && !ra.getItem().isEmpty()) {
              try {
                String statusXml = ra.getItem().get(0).getXml();
                Document statusDoc = SubmissionUtils.emptyDocument();
                SubmissionUtils.transform(new UnicodeReader(statusXml), statusDoc);

                run.put("runName", runName);
                run.put("fullPath", rootFile.getAbsolutePath());
                run.put("status", statusXml);

                run.put("sequencerName", machineName);

                if (statusDoc.getElementsByTagName("flowcellNum").getLength() != 0
                    && statusDoc.getElementsByTagName("description").getLength() != 0) {
                  String id = statusDoc.getElementsByTagName("description").item(0).getTextContent() + "-"
                      + statusDoc.getElementsByTagName("flowcellNum").item(0).getTextContent();
                  run.put("containerId", id);
                }

                String dateStarted = statusDoc.getElementsByTagName("dateStarted").item(0).getTextContent();
                String dateCompleted = statusDoc.getElementsByTagName("dateCompleted").item(0).getTextContent();

                log.debug(runName + " :: Started -> " + dateStarted);
                run.put("startDate", dateStarted);

                if (!isStringEmptyOrNull(dateStarted) && isStringEmptyOrNull(dateCompleted)) {
                  log.debug(runName + " :: Running");
                  map.get("Running").add(run);
                } else if (!isStringEmptyOrNull(dateStarted) && !isStringEmptyOrNull(dateCompleted)) {
                  log.debug(runName + " :: Completed -> " + dateCompleted);
                  run.put("completionDate", dateCompleted);
                  map.get("Completed").add(run);
                } else {
                  log.debug(runName + " :: Unknown");
                  map.get("Unknown").add(run);
                }
              } catch (ParserConfigurationException e) {
                log.error("Error configuring parser", e);
              } catch (TransformerException e) {
                log.error("Error transforming XML", e);
              }
            }
          } catch (Exception e) {
            log.error("Error contacting SOLiD machine. Attempting to parse " + statusFile.getAbsolutePath(), e);
            run.put("runName", runName);

            if (statusFile.exists()) {
              if (statusFile.canRead()) {
                try {
                  Document statusDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
                  SubmissionUtils.transform(statusFile, statusDoc);

                  run.put("fullPath", rootFile.getAbsolutePath());
                  run.put("sequencerName", machineName);

                  if (statusDoc.getElementsByTagName("flowcellNum").getLength() != 0
                      && statusDoc.getElementsByTagName("description").getLength() != 0) {
                    String id = statusDoc.getElementsByTagName("description").item(0).getTextContent() + "-"
                        + statusDoc.getElementsByTagName("flowcellNum").item(0).getTextContent();
                    run.put("containerId", id);
                  }

                  String dateStarted = statusDoc.getElementsByTagName("dateStarted").item(0).getTextContent();
                  String dateCompleted = statusDoc.getElementsByTagName("dateCompleted").item(0).getTextContent();

                  log.debug(runName + " :: Started -> " + dateStarted);
                  run.put("startDate", dateStarted);

                  run.put("status", SubmissionUtils.transform(statusFile));

                  if (!isStringEmptyOrNull(dateStarted) && isStringEmptyOrNull(dateCompleted)) {
                    log.debug(runName + " :: Running");
                    map.get("Running").add(run);
                  } else if (!isStringEmptyOrNull(dateStarted) && !isStringEmptyOrNull(dateCompleted)) {
                    log.debug(runName + " :: Completed -> " + dateCompleted);
                    run.put("completionDate", dateCompleted);
                    map.get("Completed").add(run);
                  } else {
                    log.debug(runName + " :: Unknown");
                    run.put("completionDate", "null");
                    map.get("Unknown").add(run);
                  }
                } catch (ParserConfigurationException ee) {
                  log.error("Error configuring parser", ee);
                } catch (TransformerException ee) {
                  log.error("Error transforming XML", ee);
                } catch (IOException ee) {
                  log.error("Error with file IO", ee);
                }
              } else {
                log.debug(runName + " :: Cannot read status file. Minimal run information only.");
                run.put("fullPath", rootFile.getAbsolutePath());
                run.put("status", "<error><RunName>" + runName + "</RunName><ErrorMessage>Cannot read status file</ErrorMessage></error>");
                run.put("sequencerName", machineName);
                run.put("startDate", startDate);
                run.put("completionDate", "null");

                map.get("Unknown").add(run);
              }
            } else {
              log.debug(runName + " :: Status file doesn't exist. Minimal run information only.");
              run.put("fullPath", rootFile.getAbsolutePath());
              run.put("status", "<error><RunName>" + runName
                  + "</RunName><ErrorMessage>Error contacting SOLiD machine and status xml file doesn't exist</ErrorMessage></error>");
              run.put("sequencerName", machineName);
              run.put("startDate", startDate);
              run.put("completionDate", "null");

              map.get("Unknown").add(run);
            }
          }
        }
      }
    }

    HashMap<String, String> smap = new HashMap<String, String>();
    for (String key : map.keySet()) {
      smap.put(key, map.get(key).toString());
    }

    return smap;
  }
}

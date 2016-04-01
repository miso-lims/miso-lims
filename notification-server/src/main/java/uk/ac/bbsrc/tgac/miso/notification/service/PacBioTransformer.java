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
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import uk.ac.bbsrc.tgac.miso.core.exception.InterrogationException;
import uk.ac.bbsrc.tgac.miso.core.service.integration.ws.pacbio.PacBioService;
import uk.ac.bbsrc.tgac.miso.core.service.integration.ws.pacbio.PacBioServiceWrapper;
import uk.ac.bbsrc.tgac.miso.core.util.SubmissionUtils;
import uk.ac.bbsrc.tgac.miso.integration.context.ApplicationContextProvider;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationUtils;
import uk.ac.bbsrc.tgac.miso.tools.run.util.FileSetTransformer;

/**
 * uk.ac.bbsrc.tgac.miso.notification.util
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 10/04/12
 * @since 0.1.6
 */
public class PacBioTransformer implements FileSetTransformer<String, String, File> {
  protected static final Logger log = LoggerFactory.getLogger(PacBioTransformer.class);

  private final Pattern cellDirPattern = Pattern.compile("[A-Z]{1}[0-9]{2}_[0-9]{1}");

  private final DateFormat startDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
  private final Calendar cal = Calendar.getInstance();

  public Map<String, String> transform(Message<Set<File>> message) {
    return transform(message.getPayload());
  }

  @Override
  public Map<String, String> transform(Set<File> files) {
    log.info("Processing " + files.size() + " PacBio run directories...");

    HashMap<String, JSONArray> map = new HashMap<String, JSONArray>();
    map.put("Running", new JSONArray());
    map.put("Completed", new JSONArray());
    map.put("Failed", new JSONArray());
    map.put("Unknown", new JSONArray());

    for (File rootFile : files) {
      if (rootFile.isDirectory()) {
        if (rootFile.canRead()) {
          JSONObject run = new JSONObject();
          String runName = rootFile.getName();

          run.put("runName", runName);
          run.put("fullPath", rootFile.getAbsolutePath());

          run.put("cells", new JSONArray());

          // get cell directories
          for (File cell : rootFile.listFiles(new CellDirFilter())) {
            for (File metadata : cell.listFiles(new MetadataFilenameFilter())) {
              try {
                JSONObject cellObj = new JSONObject();
                String cellXml = SubmissionUtils.transform(metadata, true);
                Document statusDoc = SubmissionUtils.emptyDocument();
                SubmissionUtils.transform(metadata, statusDoc);

                if (!run.has("sequencerName")) {
                  run.put("sequencerName", statusDoc.getElementsByTagName("InstrumentName").item(0).getTextContent());
                  log.debug("Got sequencer name");
                }

                if (!run.has("plateId")) {
                  run.put("plateId", statusDoc.getElementsByTagName("PlateId").item(0).getTextContent());
                  log.debug("Got plate id");
                }

                if (!run.has("startDate")) {
                  run.put("startDate", statusDoc.getElementsByTagName("WhenStarted").item(0).getTextContent());
                  log.debug("Got start date");
                }

                if (!run.has("completionDate") && run.has("startDate")) {
                  try {
                    String sec = statusDoc.getElementsByTagName("DurationInSec").item(0).getTextContent();

                    Date start = startDateFormat.parse(run.getString("startDate"));
                    cal.setTime(start);
                    cal.add(Calendar.SECOND, Integer.parseInt(sec));

                    run.put("completionDate", startDateFormat.format(cal.getTime()));
                    log.debug("Got completion date");
                  } catch (ParseException e) {
                    log.warn("Cannot parse detected start date!");
                  }
                }

                cellObj.put("well", statusDoc.getElementsByTagName("WellName").item(0).getTextContent());
                cellObj.put("index", statusDoc.getElementsByTagName("CellIndex").item(0).getTextContent());
                cellObj.put("sample",
                    ((Element) statusDoc.getElementsByTagName("Sample").item(0)).getElementsByTagName("Name").item(0).getTextContent());
                String comp = URLEncoder.encode(new String(IntegrationUtils.compress(cellXml.getBytes())), "UTF-8");
                cellObj.put("cellStatus", comp);

                run.getJSONArray("cells").add(cellObj);
              } catch (ParserConfigurationException e) {
                log.error(runName + ":: Unable to process run", e);
              } catch (TransformerException e) {
                log.error(runName + ":: Unable to process run", e);
              } catch (IOException e) {
                log.error(runName + ":: Unable to process run", e);
              }
            }
          }

          try {
            PacBioServiceWrapper pacbioServiceWrapper = ApplicationContextProvider.getApplicationContext()
                .getBean(run.getString("sequencerName"), PacBioServiceWrapper.class);
            PacBioService pacbioService = pacbioServiceWrapper.getPacBioService();

            // note: PacBio webservice doesn't understand "+" as space in URL, must use "%20"
            String plateStatus = pacbioService.getPlateStatus(URLEncoder.encode(run.getString("plateId"), "UTF-8").replace("+", "%20"));
            if (plateStatus == null) {
              log.error(runName + " :: Could not retrieve plate status from PacBio webservice.");
            }
            else if ("Complete".equals(plateStatus)) {
              log.debug(runName + " :: Completed");
              if (!run.has("completionDate")) {
                run.put("completionDate", "");
              }
              map.get("Completed").add(run);
            } else if ("Running".equals(plateStatus) || "Failed".equals(plateStatus)) {
              log.debug(runName + " :: " + plateStatus);
              map.get(plateStatus).add(run);
            } else {
              if ("Aborted".equals(plateStatus)) {
                log.debug(runName + " :: Aborted");
                map.get("Failed").add(run);
              } else if ("Ready".equals(plateStatus)) {
                log.debug(runName + " :: Unknown");
                map.get("Unknown").add(run);
              }
            }
          } catch (InterrogationException e) {
            log.error("Attempting fall-back date resolution...", e);
          } catch (UnsupportedEncodingException e) {
            log.error("Cannot encode plateId to be URL friendly.", e);
          }
        } else {
          log.error(rootFile.getName() + " :: Permission denied");
        }
      }
    }

    HashMap<String, String> smap = new HashMap<String, String>();
    for (String key : map.keySet()) {
      smap.put(key, map.get(key).toString());
    }

    return smap;
  }

  private class CellDirFilter implements FileFilter {
    @Override
    public boolean accept(File pathname) {
      Matcher m = cellDirPattern.matcher(pathname.getName());
      return (pathname.isDirectory() && m.matches());
    }
  }

  private class MetadataFilenameFilter implements FilenameFilter {
    @Override
    public boolean accept(File dir, String name) {
      return (name.contains("metadata"));
    }
  }
}

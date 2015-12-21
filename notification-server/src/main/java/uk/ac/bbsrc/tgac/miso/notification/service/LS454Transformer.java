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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.SubmissionUtils;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationUtils;
import uk.ac.bbsrc.tgac.miso.tools.run.RunFolderConstants;
import uk.ac.bbsrc.tgac.miso.tools.run.util.FileSetTransformer;

/**
 * uk.ac.bbsrc.tgac.miso.notification.util
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 16/12/11
 * @since 0.1.4
 */
public class LS454Transformer implements FileSetTransformer<String, String, File> {
  protected static final Logger log = LoggerFactory.getLogger(LS454Transformer.class);

  private final Pattern runCompleteLogPattern = Pattern
      .compile("\\[([A-z]{3} [A-z]{3} \\d{2} \\d{2}:\\d{2}:\\d{2} \\d{4})\\].*Job complete.*");

  public Map<String, String> transform(Message<Set<File>> message) {
    return transform(message.getPayload());
  }

  @Override
  public Map<String, String> transform(Set<File> files) {
    log.info("Processing " + files.size() + " 454 run directories...");

    // TODO modify this to use a JSONObject instead of a Map
    HashMap<String, JSONArray> map = new HashMap<String, JSONArray>();

    map.put("Running", new JSONArray());
    map.put("Completed", new JSONArray());
    map.put("Unknown", new JSONArray());

    for (File rootFile : files) {
      if (rootFile.isDirectory()) {
        if (rootFile.canRead()) {
          JSONObject run = new JSONObject();
          run.put("status", "");

          // there might be more than one signalProcessing/imageProcessingOnly dir, so get them all
          List<File> imageDirs = new ArrayList<File>();
          List<File> signalDirs = new ArrayList<File>();
          for (File dir : rootFile.listFiles()) {
            if (dir.isDirectory()) {
              Matcher m1 = Pattern.compile(RunFolderConstants.LS454_SIGNAL_FOLDER_REGEX).matcher(dir.getName());
              if (m1.matches()) {
                signalDirs.add(dir);
              }

              Matcher m2 = Pattern.compile(RunFolderConstants.LS454_IMAGE_FOLDER_REGEX).matcher(dir.getName());
              if (m2.matches()) {
                imageDirs.add(dir);
              }
            }
          }

          // only parse the most recent dirs
          File recentImageDir = null;
          if (imageDirs.size() > 0) {
            Collections.sort(imageDirs);
            recentImageDir = imageDirs.get(imageDirs.size() - 1);
          }

          File recentProcessingDir = null;
          if (signalDirs.size() > 0) {
            Collections.sort(signalDirs);
            recentProcessingDir = signalDirs.get(signalDirs.size() - 1);
          }

          String runName = rootFile.getName();
          run.put("runName", runName);

          try {
            run.put("fullPath", rootFile.getCanonicalPath()); // follow symlinks!

            if (recentImageDir != null) {
              File paramsFile = new File(recentImageDir, "dataRunParams.xml");
              if (paramsFile.exists()) {
                try {
                  run.put("runparams", SubmissionUtils.transform(paramsFile));
                } catch (TransformerException e) {
                  log.warn(runName + " :: Not adding dataRunParams.xml - cannot read");
                }
              }
            } else {
              log.error("No signalProcessing/fullProcessingAmplicons folder detected. Cannot process run " + runName + ".");
            }
          } catch (IOException e) {
            log.error(recentImageDir.getAbsolutePath() + " :: Unable to read");
          }

          try {
            if (recentProcessingDir != null) {
              File runLogFile = new File(recentProcessingDir, "gsRunProcessor.log");
              if (runLogFile.exists()) {
                String runLog = LimsUtils.fileToString(runLogFile);

                String compstat = URLEncoder.encode(new String(IntegrationUtils.compress(runLog.getBytes())), "UTF-8");
                run.put("status", compstat);

                Matcher completeMatcher = runCompleteLogPattern.matcher(runLog);
                if (completeMatcher.find()) {
                  log.debug(runName + " :: Completed");
                  run.put("completionDate", completeMatcher.group(1));
                  map.get("Completed").add(run);
                } else {
                  log.debug(runName + " :: Running");
                  map.get("Running").add(run);
                }
              } else {
                log.debug(runName + " :: Unknown");
                map.get("Unknown").add(run);
              }
            } else {
              log.error("No imageProcessingOnly folder detected. Cannot process run " + runName + ".");
            }
          } catch (IOException e) {
            log.error(recentProcessingDir.getAbsolutePath() + " :: Unable to process runLog", e);
          }
        } else {
          log.error("Cannot read into run directory: " + rootFile.getAbsolutePath());
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

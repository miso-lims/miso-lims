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

package uk.ac.bbsrc.tgac.miso.notification.manager;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import uk.ac.bbsrc.tgac.miso.notification.service.IlluminaTransformer;
import uk.ac.bbsrc.tgac.miso.tools.run.RunFolderScanner;
import uk.ac.bbsrc.tgac.miso.tools.run.util.FileSetTransformer;

/**
 * uk.ac.bbsrc.tgac.miso.notification.manager
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 15/04/13
 * @since 0.2.0
 */
public class NotificationRequestManager {
  protected static final Logger log = LoggerFactory.getLogger(NotificationRequestManager.class);
  private ObjectMapper mapper = new ObjectMapper();

  private ClassPathXmlApplicationContext context;
  private Map<String, Set<File>> dataPaths;

  public NotificationRequestManager() {
  }

  public NotificationRequestManager(ClassPathXmlApplicationContext context, Map<String, Set<File>> dataPaths) {
    this.context = context;
    this.dataPaths = dataPaths;
  }

  public void setApplicationContext(ClassPathXmlApplicationContext context) {
    this.context = context;
  }

  public void setDataPaths(Map<String, Set<File>> dataPaths) {
    this.dataPaths = dataPaths;
  }

  public String queryRunProgress(JSONObject request) throws IllegalStateException, IllegalArgumentException {
    File folder = lookupRunAliasPath(request);
    if (folder != null) {
      String platformType = request.getString("platform").toLowerCase();
      Map<String, String> status = parseRunFolder(platformType, folder);
      if (status.isEmpty()) {
        return "{'response':'No runs found with alias " + request.getString("run") + "'}";
      }

      for (String s : status.keySet()) {
        if (!isStringEmptyOrNull(status.get(s))) {
          JSONArray runs = JSONArray.fromObject(status.get(s));
          if (!runs.isEmpty()) {
            return "{'progress':'" + s + "'}";
          }
        } else {
          return "{'response':'No runs found with status " + s + " with alias " + request.getString("run") + "'}";
        }
      }
    }

    return "";
  }

  public String queryRunStatus(JSONObject request) throws IllegalStateException, IllegalArgumentException {
    File folder = lookupRunAliasPath(request);
    if (folder != null) {
      String platformType = request.getString("platform").toLowerCase();
      Map<String, String> status = parseRunFolder(platformType, folder);
      for (String s : status.keySet()) {
        if (!isStringEmptyOrNull(status.get(s))) {
          log.debug("queryRunStatus: " + status.get(s));
          JSONArray runs = JSONArray.fromObject(status.get(s));
          if (!runs.isEmpty()) {
            JSONObject run = runs.getJSONObject(0);
            if (run.has("status")) {
              return run.getString("status");
            }
          }
        }
      }
    }

    return "";
  }

  public String queryRunInfo(JSONObject request) throws IllegalStateException, IllegalArgumentException {
    File folder = lookupRunAliasPath(request);
    if (folder != null) {
      String platformType = request.getString("platform").toLowerCase();
      Map<String, String> status = parseRunFolder(platformType, folder);
      for (String s : status.keySet()) {
        if (!isStringEmptyOrNull(status.get(s))) {
          log.debug("queryRunInfo: " + status.get(s));
          JSONArray runs = JSONArray.fromObject(status.get(s));
          if (!runs.isEmpty()) {
            JSONObject run = runs.getJSONObject(0);
            if (run.has("runinfo")) {
              return run.getString("runinfo");
            }
          }
        }
      }
    }

    return "";
  }

  public String queryRunParameters(JSONObject request) throws IllegalStateException, IllegalArgumentException {
    File folder = lookupRunAliasPath(request);
    if (folder != null) {
      String platformType = request.getString("platform").toLowerCase();
      Map<String, String> status = parseRunFolder(platformType, folder);
      for (String s : status.keySet()) {
        if (!isStringEmptyOrNull(status.get(s))) {
          log.debug("queryRunParameters: " + status.get(s));
          JSONArray runs = JSONArray.fromObject(status.get(s));
          if (!runs.isEmpty()) {
            JSONObject run = runs.getJSONObject(0);
            if (run.has("runparams")) {
              return run.getString("runparams");
            }
          }
        }
      }
    }

    return "";
  }

  public String queryInterOpMetrics(JSONObject request) throws IllegalStateException, IllegalArgumentException {
    File folder = lookupRunAliasPath(request);
    if (folder != null) {
      JSONArray runs = parseIlluminaInterOpFolder(folder);
      if (!runs.isEmpty()) {
        JSONObject run = runs.getJSONObject(0);
        if (run.has("error")) {
          return run.getString("error");
        }

        if (run.has("metrix")) {
          return run.getString("metrix");
        }
      }
    } else {
      return "{\"error\":\"Cannot find run folder " + request.getString("run").replaceAll("('|\")", "\\\\$1") + "\"}";
    }
    return "";
  }

  private File lookupRunAliasPath(JSONObject request) {
    if (context != null && dataPaths != null) {
      String platformType = request.getString("platform").toLowerCase();
      if (!isStringEmptyOrNull(platformType)) {
        String runAlias = request.getString("run");
        if (!isStringEmptyOrNull(runAlias)) {
          RunFolderScanner rfs = (RunFolderScanner) context.getBean(platformType + "StatusRecursiveScanner");
          if (rfs != null) {
            for (File dataPath : dataPaths.get(platformType)) {
              for (File runFolder : rfs.listFiles(dataPath)) {
                if (runAlias.equals(runFolder.getName())) {
                  return runFolder;
                }
              }
            }
          }
        }
      }
      return null;
    } else {
      throw new IllegalStateException("ApplicationContext and/or datapaths not set. Cannot action requests on notification system.");
    }
  }

  private Map<String, String> parseRunFolder(String platformType, File path) throws IllegalStateException, IllegalArgumentException {
    if (context != null && dataPaths != null) {
      if (!isStringEmptyOrNull(platformType)) {
        FileSetTransformer<String, String, File> fst = (FileSetTransformer<String, String, File>) context
            .getBean(platformType + "Transformer");
        Set<File> fs = new HashSet<>();
        fs.add(path);
        return fst.transform(fs);
      } else {
        throw new IllegalArgumentException("No platformType set. Cannot parse run folder.");
      }
    } else {
      throw new IllegalStateException("ApplicationContext and/or datapaths not set. Cannot action requests on notification system.");
    }
  }

  private JSONArray parseIlluminaInterOpFolder(File path) throws IllegalStateException {
    if (context != null && dataPaths != null) {
      IlluminaTransformer fst = (IlluminaTransformer) context.getBean("illuminaTransformer");
      if (fst != null) {
        Set<File> fs = new HashSet<>();
        fs.add(path);
        return fst.transformInterOpOnly(fs);
      } else {
        throw new IllegalStateException("No IlluminaTransformer available");
      }
    } else {
      throw new IllegalStateException("ApplicationContext and/or datapaths not set. Cannot action requests on notification system.");
    }
  }
}

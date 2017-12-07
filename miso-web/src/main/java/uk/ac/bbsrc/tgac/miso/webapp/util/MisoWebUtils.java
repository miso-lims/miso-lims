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

package uk.ac.bbsrc.tgac.miso.webapp.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * uk.ac.bbsrc.tgac.miso.webapp.util
 * <p/>
 * Utility class containing static methods for helping with tasks specific to the miso-web module
 * 
 * @author Rob Davey
 * @date 03-Sep-2010
 * @since 0.0.2
 */
public class MisoWebUtils {
  private static final Logger log = LoggerFactory.getLogger(MisoWebUtils.class);

  public static String generateErrorDivMessage(String errorMessage) {
    return "<div id='errordiv' class='flasherror'>" + errorMessage + "</div>";
  }

  public static String generateErrorDivMessage(String errorMessage, String exceptionMessage) {
    return "<div id='errordiv' class='flasherror'>" + errorMessage + "<br/><pre>" + exceptionMessage + "</pre></div>";
  }

  public static Map<String, String> checkStorageDirectories(String baseStoragePath) {
    Map<String, String> checks = new HashMap<>();
    if (baseStoragePath.endsWith("/")) {
      try {
        File misoDir = new File(baseStoragePath);
        if (LimsUtils.checkDirectory(misoDir, true)) {
          LimsUtils.checkDirectory(new File(baseStoragePath, "files"), true);
          LimsUtils.checkDirectory(new File(baseStoragePath, "files/submission"), true);
          LimsUtils.checkDirectory(new File(baseStoragePath, "log"), true);
          LimsUtils.checkDirectory(new File(baseStoragePath, "temp"), true);
          checks.put("ok", "All storage directories OK");
        } else {
          checks.put("error",
              "MISO storage directory seems to exist, but some other IO error occurred. Please check that this directory is writable.");
        }
      } catch (IOException e) {
        log.error("check storage directories", e);
        checks.put("error", "Cannot access one of the MISO storage directories: " + e.getMessage());
      }
    } else {
      checks.put("error", "MISO storage directory is defined, but must end with a trailing slash!");
    }
    return checks;
  }

  /**
   * Similar to checkDirectory, but for single files.
   * 
   * @param path of type File
   * @return boolean true if the file exists, false if not
   * @throws IOException when the file doesn't exist
   */
  private static boolean checkFile(File path) throws IOException {
    boolean storageOk = path.exists();
    if (!storageOk) {
      StringBuilder sb = new StringBuilder("The file [" + path.toString() + "] doesn't exist.");
      throw new IOException(sb.toString());
    } else {
      log.info("File (" + path + ") OK.");
    }
    return storageOk;
  }
  public static Map<String, String> checkCorePropertiesFiles(String baseStoragePath) {
    Map<String, String> checks = new HashMap<>();
    if (baseStoragePath.endsWith("/")) {
      try {
        checkFile(new File(baseStoragePath, "security.properties"));
        checkFile(new File(baseStoragePath, "submission.properties"));
        checks.put("ok", "All core properties files OK");
      } catch (IOException e) {
        log.error("core properties files check", e);
        checks.put("error", "Cannot access one of the MISO core properties files: " + e.getMessage());
      }
    }
    return checks;
  }
}

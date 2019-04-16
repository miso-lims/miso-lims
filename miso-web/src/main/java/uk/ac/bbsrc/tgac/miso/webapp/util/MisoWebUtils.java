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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.HandsontableSpreadsheet;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.SpreadSheetFormat;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.Spreadsheet;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.SpreadsheetRequest;

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

  public static Map<String, String> checkStorageDirectories(String baseStoragePath, String fileStoragePath) {
    Map<String, String> checks = new HashMap<>();
    if (baseStoragePath.endsWith("/")) {
      try {
        File misoDir = new File(baseStoragePath);
        if (LimsUtils.checkDirectory(misoDir, true)) {
          LimsUtils.checkDirectory(new File(baseStoragePath, "log"), true);
          LimsUtils.checkDirectory(new File(baseStoragePath, "temp"), true);
          if (LimsUtils.checkDirectory(new File(fileStoragePath), true)) {
            LimsUtils.checkDirectory(new File(fileStoragePath, "submission"), true);
            checks.put("ok", "All storage directories OK");
          } else {
            checks.put("error", "Error accessing MISO storage files directory.");
          }
        } else {
          checks.put("error", "Error accessing MISO storage base directory.");
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

  public static <T> HttpEntity<byte[]> generateSpreadsheet(SpreadsheetRequest request, WhineyFunction<Long, T> fetcher,
      Function<String, Spreadsheet<T>> formatLibrary,
      HttpServletResponse response) {
    Stream<T> input = request.getIds().stream().map(WhineyFunction.rethrow(fetcher));
    return generateSpreadsheet(request, input, formatLibrary, response);
  }

  public static <T> HttpEntity<byte[]> generateSpreadsheet(SpreadsheetRequest request, Stream<T> input,
      Function<String, Spreadsheet<T>> formatLibrary,
      HttpServletResponse response) {
    Spreadsheet<T> spreadsheet = formatLibrary.apply(request.getSheet());
    SpreadSheetFormat formatter = SpreadSheetFormat.valueOf(request.getFormat());
    HttpHeaders headers = makeHttpHeaders(spreadsheet, formatter, response);
    return new HttpEntity<>(formatter.generate(input, spreadsheet), headers);
  }

  public static HttpEntity<byte[]> generateSpreadsheet(List<String> headers, List<List<String>> data,
      SpreadSheetFormat formatter,
      HttpServletResponse response) {
    Spreadsheet<List<String>> spreadsheet = new HandsontableSpreadsheet(headers);
    HttpHeaders httpHeaders = makeHttpHeaders(spreadsheet, formatter, response);
    return new HttpEntity<>(formatter.generate(data.stream(), spreadsheet), httpHeaders);
  }

  private static <T> HttpHeaders makeHttpHeaders(Spreadsheet<T> spreadsheet, SpreadSheetFormat formatter, HttpServletResponse response) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(formatter.mediaType());
    response.setHeader("Content-Disposition",
        "attachment; filename=" + String.format("%s-%s.%s", spreadsheet.name(), DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(
            ZonedDateTime.now()), formatter.extension()));
    return headers;
  }

}

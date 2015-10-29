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

package uk.ac.bbsrc.tgac.miso.runstats.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.tools.run.util.FileSetTransformer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * uk.ac.bbsrc.tgac.miso.tools.run
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 26/10/11
 * @since 0.1.2
 */
public class StatisticsTransformer implements FileSetTransformer<String, Map<String, byte[]>, File> {
  protected static final Logger log = LoggerFactory.getLogger(StatisticsTransformer.class);

  private Pattern runDirPattern;

  public StatisticsTransformer(String runDirRegex) {
    runDirPattern = Pattern.compile(runDirRegex);
  }

  public Map<String, Map<String, byte[]>> transform(Message<Set<File>> message) {
    return transform(message.getPayload());
  }

  public Map<String, Map<String, byte[]>> transform(Set<File> files) {
    Map<String, Map<String, byte[]>> map = new HashMap<String, Map<String, byte[]>>();
    map.put("stats", new HashMap<String, byte[]>());

    for (File f : files) {
      try {
        if (f.exists()) {
          Matcher rm = runDirPattern.matcher(f.getAbsolutePath());
          if (rm.matches()) {
            String fileName = f.getName();
            String runName = rm.group(1);
            byte[] b = LimsUtils.inputStreamToByteArray(new FileInputStream(f));

            map.get("stats").put(runName + "-" + fileName, b);
          }
        }
      } catch (FileNotFoundException e) {
        log.error("Cannot process file", e);
      } catch (IOException e) {
        log.error("Error with file IO", e);
      }
    }

    log.debug("Transformed: " + map.toString());
    return map;
  }
}

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
import org.springframework.integration.file.DefaultDirectoryScanner;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * uk.ac.bbsrc.tgac.miso.notification.core
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 13-Dec-2010
 * @since 0.0.2
 */
public class StatisticsScanner extends DefaultDirectoryScanner {
  protected static final Logger log = LoggerFactory.getLogger(StatisticsScanner.class);

  private Pattern runDirPattern;
  private Pattern statsFilePattern;
  private String statsDir;

  public StatisticsScanner(String runDirRegex, String statsDir, String statsFileRegex) {
    this.statsDir = statsDir;
    runDirPattern = Pattern.compile(runDirRegex);
    statsFilePattern = Pattern.compile(runDirRegex + statsDir + statsFileRegex);
  }

  protected File[] listEligibleFiles(File directory) throws IllegalArgumentException {
    File[] rootFiles = directory.listFiles();
    List<File> files = new ArrayList<File>(rootFiles.length);
    for (File rootFile : rootFiles) {
      if (rootFile.isDirectory()) {
        Matcher rm = runDirPattern.matcher(rootFile.getAbsolutePath());
        if (rm.matches()) {
          File f = new File(rootFile.getAbsolutePath(), statsDir);
          if (f.exists()) {
            File[] statsFiles = f.listFiles();
            if (statsFiles != null) {
              for (File sf : statsFiles) {
                Matcher sm = statsFilePattern.matcher(sf.getAbsolutePath());
                if (sm.matches()) {
                  log.debug("Added " + sf.getAbsolutePath());
                  files.add(sf);
                }
              }
            }
          }
        } else {
          if (rm.find()) {
            files.addAll(Arrays.asList(listEligibleFiles(rootFile)));
          }
        }
      }
    }
    return files.toArray(new File[files.size()]);
  }
}

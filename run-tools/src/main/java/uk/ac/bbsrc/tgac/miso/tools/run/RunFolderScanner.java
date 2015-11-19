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

package uk.ac.bbsrc.tgac.miso.tools.run;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.integration.file.DefaultDirectoryScanner;

/**
 * uk.ac.bbsrc.tgac.miso.tools.run
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 26/10/11
 * @since 0.1.2
 */
public class RunFolderScanner extends DefaultDirectoryScanner {
  private static final Log log = LogFactory.getLog(RunFolderScanner.class);

  private Pattern runDirPattern;

  public RunFolderScanner(String runDirRegex) {
    runDirPattern = Pattern.compile(runDirRegex);
  }

  @Override
  protected File[] listEligibleFiles(File directory) throws IllegalArgumentException {
    File[] rootFiles = directory.listFiles();
    List<File> files = new ArrayList<File>(rootFiles.length);
    for (File rootFile : rootFiles) {
      if (rootFile.isDirectory()) {
        Matcher rm = runDirPattern.matcher(rootFile.getAbsolutePath());
        if (rm.matches()) {
          files.add(rootFile);
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

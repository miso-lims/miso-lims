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

package uk.ac.bbsrc.tgac.miso.analysis.process;

import java.io.File;
import java.util.Map;

import uk.ac.ebi.fgpt.conan.lsf.AbstractLSFProcess;
import uk.ac.ebi.fgpt.conan.model.ConanParameter;

/**
 * uk.ac.bbsrc.tgac.miso.analysis.tgac
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 14/10/11
 * @since 0.1.6
 */
public abstract class AbstractTgacLsfProcess extends AbstractLSFProcess {
  private String bsubPath = "/export/lsf/7.0/linux2.6-glibc2.3-x86_64/bin/bsub";

  @Override
  protected String getLSFOutputFilePath(Map<ConanParameter, String> parameters) throws IllegalArgumentException {
    final File parentDir = new File(System.getProperty("user.home"));

    File conanOutput = generateOutputDir(parentDir, ".conan", parameters);

    // lsf output file
    return new File(conanOutput, getName() + ".lsfoutput.txt").getAbsolutePath();
  }

  protected File generateOutputDir(File parentDir, String dirname, Map<ConanParameter, String> parameters) {
    File outputDir = new File(parentDir, dirname);
    for (ConanParameter parameter : parameters.keySet()) {
      if (parameter.getName().contains("Accession")) {
        outputDir = new File(new File(parentDir, dirname), parameters.get(parameter));
        break;
      }
    }
    return outputDir;
  }
}

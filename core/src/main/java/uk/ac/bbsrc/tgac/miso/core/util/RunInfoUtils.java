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

package uk.ac.bbsrc.tgac.miso.core.util;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to grab run info from a sequencer. Deprecated!
 * <p/>
 * Use SequencerInterrogationUtils instead.
 * 
 * @author Rob Davey
 * @since 0.0.2
 * @deprecated
 */
@Deprecated
public class RunInfoUtils {
  protected static final Logger log = LoggerFactory.getLogger(RunInfoUtils.class);

  /**
   * Check the status of an Illumina run
   * 
   * @param runName
   *          of type String
   * @return Map<String, String>
   * @throws IOException
   *           when
   */
  public static Map<String, String> checkIlluminaStatus(String runName) throws IOException {
    ProcessBuilder pb = new ProcessBuilder();
    pb.command("ssh", "davey@149.155.209.208", "./glob_illumina_status.pl", "-p", runName);
    Process process = pb.start();
    return LimsUtils.checkPipes(process);
  }

  /**
   * Check if an Illumina run is completed
   * 
   * @param runName
   *          of type String
   * @return Map<String, String>
   * @throws IOException
   *           when
   */
  public static Map<String, String> checkIlluminaCompleted(String runName) throws IOException {
    ProcessBuilder pb = new ProcessBuilder();
    pb.command("ssh", "davey@149.155.209.208", "./glob_illumina_complete.pl", "-p", runName);
    Process process = pb.start();
    return LimsUtils.checkPipes(process);
  }

  /**
   * Check the status of a 454 run
   * 
   * @param runName
   *          of type String
   * @return Map<String, String>
   * @throws IOException
   *           when
   */
  public static Map<String, String> check454Status(String runName) throws IOException {
    ProcessBuilder pb = new ProcessBuilder();
    pb.command("ssh", "davey@149.155.209.208", "./glob_454_status.pl", "-p", runName);
    Process process = pb.start();
    return LimsUtils.checkPipes(process);
  }

  /**
   * Check if a 454 run is completed
   * 
   * @param runName
   *          of type String
   * @return Map<String, String>
   * @throws IOException
   *           when
   */
  public static Map<String, String> check454Completed(String runName) throws IOException {
    ProcessBuilder pb = new ProcessBuilder();
    pb.command("ssh", "davey@149.155.209.208", "./glob_454_complete.pl", "-p", runName);
    Process process = pb.start();
    return LimsUtils.checkPipes(process);
  }

}

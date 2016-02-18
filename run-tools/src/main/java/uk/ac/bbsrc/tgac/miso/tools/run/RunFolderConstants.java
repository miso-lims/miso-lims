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

/**
 * uk.ac.bbsrc.tgac.miso.tools.run
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 26/10/11
 * @since 0.1.2
 */
public interface RunFolderConstants {
  String ILLUMINA_FOLDER_REGEX = ".*/[\\d]+_[A-z0-9\\-]+_[\\d]+_[A-z0-9_\\+\\-]*";
  String ILLUMINA_FOLDER_CAPTURE_REGEX = ".*/([\\d]+_[A-z0-9\\-]+_[\\d]+_[A-z0-9_\\+\\-]*)/.*";
  String ILLUMINA_FOLDER_NAME_GROUP_CAPTURE_REGEX = "[\\d]+_([A-z0-9\\-]+)_([\\d]+)_([A-z0-9_\\+\\-]*)";

  String SOLID_FOLDER_REGEX = ".*/[A-z0-9]+_[0-9]{8}_.*";
  String SOLID_FOLDER_CAPTURE_REGEX = ".*/([A-z0-9]+)_([0-9]{8})_(.*)/.*";
  String SOLID_FOLDER_NAME_GROUP_CAPTURE_REGEX = "([A-z0-9]+)_([0-9]{8})_(.*)";

  // R_2010_03_22_12_55_18_FLX03090505_Administrator_T3K1_2
  // R_2009_11_30_08_46_52_FLX02090498_Administrator_JR2JR4JR6JR7
  // R_2014_02_10_13_28_05_seq-454-3-JR07100262_Administrator_BRAF
  String LS454_FOLDER_REGEX = ".*/R_\\d{4}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_[A-z0-9\\+\\-]+_[A-z0-9]+_[A-z0-9\\+\\-_]+.*";
  String LS454_FOLDER_CAPTURE_REGEX = "R_(\\d{4}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\d{2})_([A-z0-9\\+\\-]+)_[A-z0-9]+_([A-z0-9\\+\\-_]*)";
  String LS454_SIGNAL_FOLDER_REGEX = "D_\\d{4}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_[A-z0-9\\+\\-]+_[signalProcessing|fullProcessingAmplicons].*";
  String LS454_IMAGE_FOLDER_REGEX = "D_\\d{4}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_[A-z0-9\\+\\-]+_imageProcessingOnly.*";

  String PACBIO_FOLDER_REGEX = ".*/\\d{8}_.*";
  String PACBIO_FOLDER_CAPTURE_REGEX = ".*/(\\d{8})_(.*)/.*";
  String PACBIO_FOLDER_NAME_GROUP_CAPTURE_REGEX = "(\\d{8})_(.*)";
}

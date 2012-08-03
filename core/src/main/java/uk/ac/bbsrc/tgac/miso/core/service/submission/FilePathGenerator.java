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

package uk.ac.bbsrc.tgac.miso.core.service.submission;

import uk.ac.bbsrc.tgac.miso.core.data.Dilution;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.exception.SubmissionException;

import java.io.File;
import java.util.Set;

/**
 * An interface that defines a TransferMethod object that can submit data files to a service defined by an endpoint
 *
 * @author Antony Colles
 * @since 0.1.6
 */
public interface FilePathGenerator {
  /**
   * Generates file Paths for sequence datafiles
   *
   //* @param dataFiles set of datafiles
   * @return Object response
   */
  public File generateFilePath(SequencerPoolPartition partition, Dilution libraryDilution) throws SubmissionException;

  public Set<File> generateFilePaths(SequencerPoolPartition partition) throws SubmissionException;
}
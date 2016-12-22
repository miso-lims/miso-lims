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

package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.util.List;

import com.eaglegenomics.simlims.core.Note;

import uk.ac.bbsrc.tgac.miso.core.data.KitComponent;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;

/**
 * Defines a DAO interface for storing Notes
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public interface NoteStore extends Store<Note> {
  /**
   * List all Notes related to a ProjectOverview given a ProjectOverview ID
   *
   * @param overviewId
   *          of type Long
   * @return List<Note>
   * @throws IOException
   *           when
   */
  List<Note> listByProjectOverview(Long overviewId) throws IOException;

  /**
   * List all Notes related to a Kit given a Kit ID
   *
   * @param kitId
   *          of type Long
   * @return List<Note>
   * @throws IOException
   *           when
   */
  List<Note> listByKit(Long kitId) throws IOException;

  /**
   * List all Notes related to a Sample given a Sample ID
   *
   * @param sampleId
   *          of type Long
   * @return List<Note>
   * @throws IOException
   *           when
   */
  List<Note> listBySample(Long sampleId) throws IOException;

  /**
   * List all Notes related to a Library given a Library ID
   *
   * @param libraryId
   *          of type Long
   * @return List<Note>
   * @throws IOException
   *           when
   */
  List<Note> listByLibrary(Long libraryId) throws IOException;

  /**
   * List all Notes related to a Run given a Run ID
   *
   * @param runId
   *          of type Long
   * @return List<Note>
   * @throws IOException
   *           when
   */
  List<Note> listByRun(Long runId) throws IOException;

  /**
   * Save a ProjectOverview Note
   *
   * @param overview
   *          of type ProjectOverview
   * @param note
   *          of type Note
   * @return long
   * @throws IOException
   *           when
   */
  long saveProjectOverviewNote(ProjectOverview overview, Note note) throws IOException;

  /**
   * Save a KitComponent Note
   *
   * @param kitComponent
   *          of type KitComponent
   * @param note
   *          of type Note
   * @return long
   * @throws IOException
   *           when
   */
  long saveKitNote(KitComponent kitComponent, Note note) throws IOException;

  /**
   * Save a Sample Note
   *
   * @param sample
   *          of type Sample
   * @param note
   *          of type Note
   * @return long
   * @throws IOException
   *           when
   */
  long saveSampleNote(Sample sample, Note note) throws IOException;

  /**
   * Save a Library Note
   *
   * @param library
   *          of type Library
   * @param note
   *          of type Note
   * @return long
   * @throws IOException
   *           when
   */
  long saveLibraryNote(Library library, Note note) throws IOException;

  /**
   * Save a Run Note
   *
   * @param run
   *          of type Run
   * @param note
   *          of type Note
   * @return long
   * @throws IOException
   *           when
   */
  long saveRunNote(Run run, Note note) throws IOException;

  /**
   * Remove note
   *
   * @param note
   *          Note
   * @return boolean true if removed successfully
   * @throws java.io.IOException
   *           when the object cannot be removed
   */
  public boolean remove(Note note) throws IOException;

  /**
   * List all Notes related to a Pool given a Pool ID
   *
   * @param poolId
   *          of type Long
   * @return List<Note>
   * @throws IOException
   *           when
   */
  List<Note> listByPool(Long poolId) throws IOException;

  /**
   * Save a Pool Note
   *
   * @param pool
   *          of type Pool
   * @param note
   *          of type Note
   * @return long
   * @throws IOException
   *           when
   */
  long savePoolNote(Pool pool, Note note) throws IOException;

}

/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK MISO project contacts: Robert Davey @
 * TGAC *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MISO. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;

/**
 * Defines a DAO interface for storing Experiments
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface ExperimentStore extends SaveDao<Experiment> {

  /**
   * List all Experiments that are part of a Study given a Study ID
   * 
   * @param studyId of type long
   * @return Collection<Experiment>
   * @throws IOException when
   */
  public Collection<Experiment> listByStudyId(long studyId) throws IOException;

  /**
   * List all persisted objects
   * 
   * @return Collection<Experiment>
   * @throws IOException when the objects cannot be retrieved
   */
  public Collection<Experiment> listAllWithLimit(long limit) throws IOException;

  public Collection<Experiment> listByLibrary(long id) throws IOException;

  public List<Experiment> listByRun(long runId) throws IOException;

  public long getUsage(Experiment experiment) throws IOException;

}

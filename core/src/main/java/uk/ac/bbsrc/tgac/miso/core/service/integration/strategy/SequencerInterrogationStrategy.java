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

package uk.ac.bbsrc.tgac.miso.core.service.integration.strategy;

import java.util.List;

import net.sf.json.JSONObject;
import net.sourceforge.fluxion.spi.Spi;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.Status;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.exception.InterrogationException;

/**
 * This interface defines the contract that an interrogation mechanism can implement to query an underlying interrogatable object
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Spi
public interface SequencerInterrogationStrategy {
  /**
   * List the status of all runs exposed by a SequencerReference
   * 
   * @param sr
   *          of type SequencerReference
   * @return List<Status>
   * @throws InterrogationException
   *           when
   */
  List<Status> listAllStatus(SequencerReference sr) throws InterrogationException;

  /**
   * List the status of all runs for a given sequencer exposed by a SequencerReference
   * 
   * @param sr
   *          of type SequencerReference
   * @param name
   *          of type String
   * @return List<Status>
   * @throws InterrogationException
   *           when
   */
  List<Status> listAllStatusBySequencerName(SequencerReference sr, String name) throws InterrogationException;

  /**
   * List all runs of a given HealthType exposed by a SequencerReference
   * 
   * @param sr
   *          of type SequencerReference
   * @param healthType
   *          of type HealthType
   * @return List<String>
   * @throws InterrogationException
   *           when
   */
  List<String> listRunsByHealthType(SequencerReference sr, HealthType healthType) throws InterrogationException;

  /**
   * List all completed runs exposed by a SequencerReference
   * 
   * @param sr
   *          of type SequencerReference
   * @return List<String>
   * @throws InterrogationException
   *           when
   */
  List<String> listAllCompleteRuns(SequencerReference sr) throws InterrogationException;

  /**
   * List all incomplete runs exposed by a SequencerReference
   * 
   * @param sr
   *          of type SequencerReference
   * @return List<String>
   * @throws InterrogationException
   *           when
   */
  List<String> listAllIncompleteRuns(SequencerReference sr) throws InterrogationException;

  /**
   * Get the status of a single run exposed by a SequencerReference
   * 
   * @param sr
   *          of type SequencerReference
   * @param runName
   *          of type String
   * @return Status
   * @throws InterrogationException
   *           when
   */
  Status getRunStatus(SequencerReference sr, String runName) throws InterrogationException;

  /**
   * Get general run information of a single run exposed by a SequencerReference
   * 
   * @param sr
   *          of type SequencerReference
   * @param runName
   *          of type String
   * @return JSONObject
   * @throws InterrogationException
   *           when
   */
  JSONObject getRunInformation(SequencerReference sr, String runName) throws InterrogationException;

  /**
   * Return true if this strategy is able to interrogate a given SequencerReference
   * 
   * @param sr
   *          of type SequencerReference
   * @return boolean
   */
  boolean isStrategyFor(SequencerReference sr);
}

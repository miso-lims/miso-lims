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

package uk.ac.bbsrc.tgac.miso.core.service.integration.strategy.interrogator;

import java.util.List;

import net.sf.json.JSONObject;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.Status;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.exception.InterrogationException;
import uk.ac.bbsrc.tgac.miso.core.service.integration.strategy.SequencerInterrogationStrategy;

/**
 * This class represents an entry point that couples a SequencerInterrogationStrategy to a SequencerReference, so that reference can be
 * interrogated by that strategy. In this way, each mechanism that can be employed by a strategy has to be wrapped up with a
 * SequencerReference.
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public class SequencerInterrogator {
  /** Field strategy */
  private SequencerInterrogationStrategy strategy;
  /** Field reference */
  private SequencerReference reference;

  /**
   * Creates a new SequencerInterrogator instance from a given SequencerInterrogationStrategy and SequencerReference
   * 
   * @param strategy
   *          of type SequencerInterrogationStrategy
   * @param reference
   *          of type SequencerReference
   */
  public SequencerInterrogator(SequencerInterrogationStrategy strategy, SequencerReference reference) {
    this.strategy = strategy;
    this.reference = reference;
  }

  /**
   * List the status of all runs exposed by the SequencerInterrogationStrategy supplied at construction
   * 
   * @return List<Status>
   * @throws InterrogationException
   *           when
   */
  public List<Status> listAllStatus() throws InterrogationException {
    return strategy.listAllStatus(reference);
  }

  /**
   * List the status of all runs on a given machine exposed by the SequencerInterrogationStrategy supplied at construction
   * 
   * @param name
   *          of type String
   * @return List<Status>
   * @throws InterrogationException
   *           when
   */
  public List<Status> listAllStatusBySequencerName(String name) throws InterrogationException {
    return strategy.listAllStatusBySequencerName(reference, name);
  }

  /**
   * List all runs of a given HealthType on a given machine exposed by the SequencerInterrogationStrategy supplied at construction
   * 
   * @param healthType
   *          of type HealthType
   * @return List<String>
   * @throws InterrogationException
   *           when
   */
  public List<String> listAllRunsByHealthType(HealthType healthType) throws InterrogationException {
    return strategy.listRunsByHealthType(reference, healthType);
  }

  /**
   * List all complete runs exposed by the SequencerInterrogationStrategy supplied at construction
   * 
   * @return List<String>
   * @throws InterrogationException
   *           when
   */
  public List<String> listAllCompleteRuns() throws InterrogationException {
    return strategy.listAllCompleteRuns(reference);
  }

  /**
   * List all imcomplete runs exposed by the SequencerInterrogationStrategy supplied at construction
   * 
   * @return List<String>
   * @throws InterrogationException
   *           when
   */
  public List<String> listAllIncompleteRuns() throws InterrogationException {
    return strategy.listAllIncompleteRuns(reference);
  }

  /**
   * Get the status of a single run exposed by the SequencerInterrogationStrategy supplied at construction
   * 
   * @param name
   *          of type String
   * @return Status
   * @throws InterrogationException
   *           when
   */
  public Status getRunStatus(String name) throws InterrogationException {
    return strategy.getRunStatus(reference, name);
  }

  /**
   * Get the information about a single run exposed by the SequencerInterrogationStrategy supplied at construction
   * 
   * @param name
   *          of type String
   * @return JSONObject
   * @throws InterrogationException
   *           when
   */
  public JSONObject getRunInformation(String name) throws InterrogationException {
    return strategy.getRunInformation(reference, name);
  }
}

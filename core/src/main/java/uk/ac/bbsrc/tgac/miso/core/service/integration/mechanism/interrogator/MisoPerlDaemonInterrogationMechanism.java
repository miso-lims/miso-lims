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

package uk.ac.bbsrc.tgac.miso.core.service.integration.mechanism.interrogator;

import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.exception.InterrogationException;
import uk.ac.bbsrc.tgac.miso.core.service.integration.contract.impl.MisoPerlDaemonQuery;
import uk.ac.bbsrc.tgac.miso.core.service.integration.contract.impl.MisoPerlDaemonResult;
import uk.ac.bbsrc.tgac.miso.core.service.integration.mechanism.InterrogationMechanism;
import uk.ac.bbsrc.tgac.miso.core.util.SequencerInterrogationUtils;

/**
 * A concrete interrogation mechanism that interrogates a queryable SequencerReference with a MisoPerlDaemonQuery that produces a
 * MisoPerlDaemonResult
 * 
 * @author Rob Davey
 * @date 07-Oct-2010
 * @since 0.0.2
 */
public class MisoPerlDaemonInterrogationMechanism
    implements InterrogationMechanism<MisoPerlDaemonQuery, SequencerReference, MisoPerlDaemonResult> {
  /**
   * Push a MisoPerlDaemonQuery to a queryable SequencerReference and return a MisoPerlDaemonResult
   * 
   * @param sr
   *          of type SequencerReference
   * @param query
   *          of type MisoPerlDaemonQuery
   * @return MisoPerlDaemonResult
   * @throws InterrogationException
   *           when the SequencerReference cannot be interrogated
   */
  @Override
  public MisoPerlDaemonResult doQuery(SequencerReference sr, MisoPerlDaemonQuery query) throws InterrogationException {
    String resultString = SequencerInterrogationUtils.querySocket(SequencerInterrogationUtils.prepareSocket(sr), query.generateQuery());
    return new MisoPerlDaemonResult(resultString);
  }
}

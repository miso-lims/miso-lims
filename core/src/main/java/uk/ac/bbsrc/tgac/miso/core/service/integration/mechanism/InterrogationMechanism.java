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

package uk.ac.bbsrc.tgac.miso.core.service.integration.mechanism;

import uk.ac.bbsrc.tgac.miso.core.exception.InterrogationException;
import uk.ac.bbsrc.tgac.miso.core.service.integration.contract.InterrogationQuery;
import uk.ac.bbsrc.tgac.miso.core.service.integration.contract.InterrogationResult;

/**
 * A simple interface to describe objects that can query an object O with an IntererogationQuery Q, and return an InterrogationResult R
 * 
 * @author Rob Davey
 * @date 07-Oct-2010
 * @since 0.0.2
 */
public interface InterrogationMechanism<Q extends InterrogationQuery, O, R extends InterrogationResult> {
  /**
   * Push a query to a queryable object and return a result
   * 
   * @param queryable
   *          of type O
   * @param query
   *          of type Q
   * @return R
   * @throws InterrogationException
   *           when
   */
  R doQuery(O queryable, Q query) throws InterrogationException;
}

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

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;

public interface SequencingContainerModelStore {
  SequencingContainerModel get(long id);

  /**
   * Attempt to find a SequencingContainerModel matching the supplied parameters
   * 
   * @param platform
   *          the platform to find a model for (required)
   * @param search
   *          the alias or identificationBarcode of the model to search for. If null, will search for a "fallback" model instead
   * @param partitionCount
   *          the number of partitions that the model must have (required)
   * @return an appropriate model if one is found; null otherwise
   */
  SequencingContainerModel find(InstrumentModel platform, String search, int partitionCount);

  List<SequencingContainerModel> list();

}

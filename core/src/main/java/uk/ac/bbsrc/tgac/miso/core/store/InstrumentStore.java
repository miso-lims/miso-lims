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

import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface InstrumentStore
    extends Store<Instrument>, PaginatedDataSource<Instrument> {

  /**
   * Get an Instrument by a given name
   * 
   * @param name
   * @return the Instrument
   * @throws IOException
   */
  Instrument getByName(String name) throws IOException;

  /**
   * Get the Instrument which was the pre-upgrade Instrument for the Instrument provided (by its id)
   * 
   * @param upgradedInstrumentId
   * @return the pre-upgrade Instrument if one exists; otherwise null
   * @throws IOException if there is more than one pre-upgrade Instrument for the provided Instrument, or there are any other IO errors
   */
  Instrument getByUpgradedInstrument(long upgradedInstrumentId) throws IOException;

}

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
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentPosition;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

/**
 * Defines a DAO interface for storing Platforms
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface InstrumentModelStore extends Store<InstrumentModel> {
  /**
   * Get an Instrument Model given a model alias
   * 
   * @param alias
   * @return Platform
   * @throws IOException
   */
  InstrumentModel getByAlias(String alias) throws IOException;

  /**
   * List all Instrument Models given a PlatformType
   * 
   * @param platformType
   * @return List<Platform>
   * @throws IOException
   */
  List<InstrumentModel> listByPlatformType(String platformType) throws IOException;

  /**
   * List all distinct Platform names
   * 
   * @return List<String>
   * @throws IOException
   *           when
   */
  List<PlatformType> listDistinctPlatformNames() throws IOException;

  InstrumentPosition getInstrumentPosition(long positionId) throws IOException;

  Set<PlatformType> listActivePlatformTypes() throws IOException;

}

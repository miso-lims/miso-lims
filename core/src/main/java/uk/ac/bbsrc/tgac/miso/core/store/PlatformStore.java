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

import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.PlatformPosition;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

/**
 * Defines a DAO interface for storing Platforms
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface PlatformStore extends Store<Platform> {
  /**
   * Get a Platform given a model name
   * 
   * @param model
   *          of type String
   * @return Platform
   * @throws IOException
   *           when
   */
  Platform getByModel(String model) throws IOException;

  /**
   * List all Platforms given a Platform manufacturer name
   * 
   * @param name
   *          of type String
   * @return List<Platform>
   * @throws IOException
   *           when
   */
  List<Platform> listByName(String name) throws IOException;

  /**
   * List all distinct Platform names
   * 
   * @return List<String>
   * @throws IOException
   *           when
   */
  List<PlatformType> listDistinctPlatformNames() throws IOException;

  PlatformPosition getPlatformPosition(long positionId) throws IOException;

  Set<PlatformType> listActivePlatformTypes() throws IOException;

}

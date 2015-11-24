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

package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.service.printing.MisoPrintService;

/**
 * Defines a DAO for storing {@link MisoPrintService} objects
 * 
 * @author Rob Davey
 * @date 01-Jul-2011
 * @since 0.0.3
 */
public interface PrintServiceStore extends Store<MisoPrintService> {
  /**
   * Get the MisoPrintService with the specified name
   * 
   * @param serviceName
   *          of type String
   * @return the MisoPrintService with the given name, or null if none exists
   * @throws IOException
   */
  MisoPrintService getByName(String serviceName) throws IOException;

  /**
   * List all {@link MisoPrintService} objects that are able to handle a given
   * {@link uk.ac.bbsrc.tgac.miso.core.service.printing.context.PrintContext}
   * 
   * @param contextName
   *          of type String
   * @return List<MisoPrintService>
   * @throws IOException
   *           when
   */
  List<MisoPrintService> listByContext(String contextName) throws IOException;
}

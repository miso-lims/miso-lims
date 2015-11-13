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
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCR;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeAware;

/**
 * Defines a DAO interface for storing {@link emPCR} objects
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface EmPCRStore extends Store<emPCR>, Remover<emPCR>, NamingSchemeAware<emPCR> {
  /**
   * List all EmPCRs by a parent {@link uk.ac.bbsrc.tgac.miso.core.data.Dilution} ID
   * 
   * @param dilutionId
   *          of type Long
   * @return Collection<emPCR>
   * @throws IOException
   *           when
   */
  public Collection<emPCR> listAllByDilutionId(long dilutionId) throws IOException;

  /**
   * List all EmPCRs by a parent {@link uk.ac.bbsrc.tgac.miso.core.data.Project} ID
   * 
   * @param projectId
   *          of type Long
   * @return Collection<emPCR>
   * @throws IOException
   *           when
   */
  public Collection<emPCR> listAllByProjectId(long projectId) throws IOException;
}

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

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.PrintJob;
import uk.ac.bbsrc.tgac.miso.core.service.printing.MisoPrintService;

/**
 * Defines a DAO interface for storing PrintJobs
 * 
 * @author Rob Davey
 * @date 01-Jul-2011
 * @since 0.0.3
 */
public interface PrintJobStore extends Store<PrintJob> {
  /**
   * List all {@link PrintJob} objects that belong to a given {@link User}
   * 
   * @param user
   *          of type User
   * @return List<PrintJob>
   * @throws IOException
   *           when
   */
  List<PrintJob> listByUser(User user) throws IOException;

  /**
   * List all {@link PrintJob} objects that were printed by a given {@link MisoPrintService}
   * 
   * @param service
   *          of type MisoPrintService
   * @return List<PrintJob>
   * @throws IOException
   *           when
   */
  List<PrintJob> listByPrintService(MisoPrintService service) throws IOException;
}

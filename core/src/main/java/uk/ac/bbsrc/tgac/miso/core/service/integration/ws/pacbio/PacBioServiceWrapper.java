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

package uk.ac.bbsrc.tgac.miso.core.service.integration.ws.pacbio;

import java.net.URI;

import uk.ac.bbsrc.tgac.miso.core.exception.InterrogationException;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * uk.ac.bbsrc.tgac.miso.core.service.integration.ws.solid
 * <p/>
 * Wraps up a PacBioService so that, following construction, any exceptions can be caught when getting the underlying PacBioService via
 * getPacBioService(). Without this class, applications would exit following the non-propagatable exception producing by a malfunctioning
 * Service (e.g. unresolvable REST URL)
 * 
 * @author Rob Davey
 * @date 11/04/12
 * @since 0.1.6
 */
public class PacBioServiceWrapper {
  private String machineName;
  private URI restLocation;
  private PacBioService pacbioService;

  public PacBioServiceWrapper(String machineName, URI restLocation) {
    this.machineName = machineName;
    this.restLocation = restLocation;

    if (LimsUtils.isUrlValid(restLocation)) {
      this.pacbioService = new PacBioService(restLocation);
    }
  }

  public PacBioService getPacBioService() throws InterrogationException {
    if (pacbioService != null) {
      return pacbioService;
    } else {
      throw new InterrogationException("Couldn't contact PacBio machine " + machineName + " at " + restLocation.toString());
    }
  }
}

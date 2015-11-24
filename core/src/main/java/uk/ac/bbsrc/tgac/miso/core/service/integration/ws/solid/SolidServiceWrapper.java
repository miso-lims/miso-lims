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

package uk.ac.bbsrc.tgac.miso.core.service.integration.ws.solid;

import java.net.URL;

import javax.xml.namespace.QName;

import uk.ac.bbsrc.tgac.miso.core.exception.InterrogationException;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * uk.ac.bbsrc.tgac.miso.core.service.integration.ws.solid
 * <p/>
 * Wraps up a SolidService so that, following construction, any exceptions can be caught when getting the underlying SolidService via
 * getSolidService(). Without this class, applications would exit following the non-propagatable exception producing by a malfunctioning
 * Service (e.g. unresolvable WSDL URL)
 * 
 * @author Rob Davey
 * @date 09/11/11
 * @since 0.1.3
 */
public class SolidServiceWrapper {
  private String machineName;
  private URL wsdlLocation;
  private SolidService solidService;

  public SolidServiceWrapper(String machineName, URL wsdlLocation) {
    QName qname = new QName("http://solid.aga.appliedbiosystems.com", "SolidService");
    this.machineName = machineName;
    this.wsdlLocation = wsdlLocation;

    if (LimsUtils.isUrlValid(wsdlLocation)) {
      this.solidService = new SolidService(wsdlLocation, qname);
    }
  }

  public SolidService getSolidService() throws InterrogationException {
    if (solidService != null) {
      return solidService;
    } else {
      throw new InterrogationException("Couldn't contact SOLiD machine " + machineName + " at " + wsdlLocation.toString());
    }
  }
}

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

package uk.ac.bbsrc.tgac.miso.core.service.integration.contract.impl;

import uk.ac.bbsrc.tgac.miso.core.service.integration.contract.InterrogationResult;

/**
 * A class that can parse responses made by the MISO Interrogation Daemon
 * 
 * @author Rob Davey
 * @date 07-Oct-2010
 * @since 0.0.2
 */
public class MisoPerlDaemonResult implements InterrogationResult<String> {
  private String resultString;

  /**
   * Creates a new MisoPerlDaemonResult instance.
   * 
   * @param resultString
   *          of type String
   */
  public MisoPerlDaemonResult(String resultString) {
    this.resultString = resultString;
  }

  /**
   * Parses the response given on construction and returns a result in the form of a simple String
   * 
   * @return String the parsed response
   */
  @Override
  public String parseResult() {
    return resultString;
  }
}

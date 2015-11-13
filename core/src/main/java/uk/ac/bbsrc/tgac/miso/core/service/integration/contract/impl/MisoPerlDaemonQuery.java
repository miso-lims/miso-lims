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

import uk.ac.bbsrc.tgac.miso.core.service.integration.contract.InterrogationQuery;

/**
 * A class that can build queries that can be consumed by the MISO Interrogation Daemon
 * 
 * @author Rob Davey
 * @date 07-Oct-2010
 * @since 0.0.2
 */
public class MisoPerlDaemonQuery implements InterrogationQuery<String> {
  String platform;
  String type;
  String runId;

  /**
   * Creates a new MisoPerlDaemonQuery instance with no run specified. This will produce lists of all runs that meet the query parameters.
   * 
   * @param platform
   *          of type String
   * @param type
   *          of type String
   */
  public MisoPerlDaemonQuery(String platform, String type) {
    this.platform = platform;
    this.type = type;
  }

  /**
   * Creates a new MisoPerlDaemonQuery instance with a run specified. This will produce information about the given run that meets the query
   * parameters.
   * 
   * @param platform
   *          of type String
   * @param runId
   *          of type String
   * @param type
   *          of type String
   */
  public MisoPerlDaemonQuery(String platform, String runId, String type) {
    this.platform = platform;
    this.type = type;
    this.runId = runId;
  }

  /**
   * Returns the query String that this query object represents
   * 
   * @return String
   */
  @Override
  public String generateQuery() {
    if (runId != null) {
      return "{'query':" + "{'run':" + "[" + "{'platform':'" + platform + "','type':'" + type + "','runId':'" + runId + "'}" + "]" + "}"
          + "}\n";
    } else {
      return "{'query':" + "{'run':" + "[" + "{'platform':'" + platform + "','type':'" + type + "'}" + "]" + "}" + "}\n";
    }
  }
}

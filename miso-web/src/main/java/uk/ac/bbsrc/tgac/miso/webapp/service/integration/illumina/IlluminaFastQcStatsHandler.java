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

package uk.ac.bbsrc.tgac.miso.webapp.service.integration.illumina;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.Message;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;

import java.util.Map;

/**
 * uk.ac.bbsrc.tgac.miso.webapp.service.integration.illumina
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 07-Mar-2011
 * @since version
 */
public class IlluminaFastQcStatsHandler {
  private Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  private RequestManager requestManager;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void parseStatsMessage(Message<Map<String, String>> message) {
    Map<String, String> stats = message.getPayload();
  }
}

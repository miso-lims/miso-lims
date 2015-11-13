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

package uk.ac.bbsrc.tgac.miso.webapp.service.integration.test;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;

import net.sf.json.JSONObject;

/**
 * uk.ac.bbsrc.tgac.miso.webapp.service
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 02-Dec-2010
 * @since version
 */
public class TestReceiverHandler {
  private static Logger logger = LoggerFactory.getLogger(TestReceiverHandler.class);

  public void handleMessage(Message<String> message) {
    logger.debug("At {} I received a message with payload {}", new String[] { new Date(message.getHeaders().getTimestamp()).toString(), });
  }

  public void handleJson(Message<String> message) {
    JSONObject json = JSONObject.fromObject(message.getPayload());
    logger.debug("At {} I received a message with payload {}",
        new String[] { new Date(message.getHeaders().getTimestamp()).toString(), json.getString("foo") });
  }
}

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

package uk.ac.bbsrc.tgac.miso.webapp.service.integration.ls454;

import org.springframework.messaging.Message;
import org.springframework.integration.annotation.Gateway;
import org.springframework.util.MultiValueMap;

/**
 * uk.ac.bbsrc.tgac.miso.webapp.service
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 16-Dec-2011
 * @since 0.1.4
 */
public interface Ls454StatusGateway {
  @Gateway(requestChannel = "ls454StatusChannel")
  void consume(Message<MultiValueMap<String, String>> message);
}

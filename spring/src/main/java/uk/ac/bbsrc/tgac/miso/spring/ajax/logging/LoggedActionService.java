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

package uk.ac.bbsrc.tgac.miso.spring.ajax.logging;

import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.ac.bbsrc.tgac.miso.core.logging.LoggedAction;

import javax.servlet.http.HttpSession;

/**
 * uk.ac.bbsrc.tgac.miso.spring.ajax.logging
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
@Ajaxified
public class LoggedActionService {

  protected static final Logger log = LoggerFactory.getLogger("limsFileLogger");

  @LoggedAction
  public JSONObject logAction(HttpSession session, JSONObject json) {
    //TODO - find a way to aspect this - can't get it to work, so resorting to bog standard logging :(
    log.info("AJAX ["+ SecurityContextHolder.getContext().getAuthentication().getName()+"] "+json.getString("action")+" [" + json.getString("objectType") +","+json.getString("objectId")+"]");
    return json;
  }
}

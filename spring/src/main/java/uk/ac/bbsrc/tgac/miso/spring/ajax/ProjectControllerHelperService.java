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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.spring.ajax;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;

import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;

/**
 * uk.ac.bbsrc.tgac.miso.spring.ajax
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
@Ajaxified
public class ProjectControllerHelperService {
  protected static final Logger log = LoggerFactory.getLogger(ProjectControllerHelperService.class);
  @Autowired
  private NamingScheme namingScheme;

  public void setNamingScheme(NamingScheme namingScheme) {
    this.namingScheme = namingScheme;
  }

  public JSONObject validateProjectShortName(HttpSession session, JSONObject json) {
    if (!json.has("shortName")) {
      return JSONUtils.SimpleJSONError("No shortName specified");
    } else {
      String shortName = json.getString("shortName");
      ValidationResult shortNameValidation = namingScheme.validateProjectShortName(shortName);
      if (shortNameValidation.isValid()) {
        log.info("Project shortName OK!");
        return JSONUtils.SimpleJSONResponse("OK");
      } else {
        log.error("Project shortName not valid: " + shortName);
        return JSONUtils.SimpleJSONError(shortNameValidation.getMessage());
      }
    }
  }
}

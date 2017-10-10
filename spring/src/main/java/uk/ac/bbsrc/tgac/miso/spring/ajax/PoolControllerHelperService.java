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

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.service.PoolService;

/**
 * uk.ac.bbsrc.tgac.miso.spring.ajax
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Ajaxified
public class PoolControllerHelperService {
  protected static final Logger log = LoggerFactory.getLogger(PoolControllerHelperService.class);
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private PoolService poolService;

  public JSONObject deletePoolNote(HttpSession session, JSONObject json) {
    Long poolId = json.getLong("poolId");
    Long noteId = json.getLong("noteId");

    try {
      Pool pool = poolService.get(poolId);
      poolService.deleteNote(pool, noteId);
      return JSONUtils.SimpleJSONResponse("OK");
    } catch (IOException e) {
      log.error("cannot remove note", e);
      return JSONUtils.SimpleJSONError("Cannot remove note: " + e.getMessage());
    }
  }

  public JSONObject addPoolNote(HttpSession session, JSONObject json) {
    Long poolId = json.getLong("poolId");
    String text = json.getString("text");

    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Pool pool = poolService.get(poolId);
      Note note = new Note();

      note.setInternalOnly(json.getString("internalOnly").equals("on"));
      note.setText(text);
      note.setOwner(user);
      note.setCreationDate(new Date());
      pool.getNotes().add(note);
      poolService.saveNote(pool, note);
      pool.setLastModifier(user);
      poolService.save(pool);
    } catch (IOException e) {
      log.error("add pool note", e);
      return JSONUtils.SimpleJSONError(e.getMessage());
    }

    return JSONUtils.SimpleJSONResponse("Note saved successfully");
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }
}

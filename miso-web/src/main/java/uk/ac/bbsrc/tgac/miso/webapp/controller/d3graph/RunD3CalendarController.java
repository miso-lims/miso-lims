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

package uk.ac.bbsrc.tgac.miso.webapp.controller.d3graph;

import java.io.IOException;
import java.util.Collection;

import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.webapp.controller.EditProjectController;

/**
 * Created by IntelliJ IDEA. User: thankia Date: 06/12/11 Time: 11:36 To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/d3graph/run")
@SessionAttributes("run")
public class RunD3CalendarController {
  protected static final Logger log = LoggerFactory.getLogger(EditProjectController.class);

  @Autowired
  private RequestManager requestManager;

  @RequestMapping(method = RequestMethod.GET)
  public @ResponseBody JSONArray graphd3Rest() throws IOException {
    try {
      Collection<Run> runs = requestManager.listAllRuns();
      JSONArray runsArray = new JSONArray();
      for (Run r : runs) {
        runsArray.add(JSONObject.fromObject(
            "{'ID':'" + r.getId() + "','Name':'" + r.getName() + "','Start':'" + (r.getStatus() != null ? r.getStatus().getStartDate() : "")
                + "','Stop':'" + (r.getStatus() != null ? r.getStatus().getCompletionDate() : "") + "','Instrument':'"
                + r.getSequencerReference().getId() + "','InstrumentName':'" + r.getSequencerReference().getPlatform().getInstrumentModel()
                + "','Health':'" + (r.getStatus() != null && r.getStatus().getHealth() != null ? r.getStatus().getHealth().getKey() : "")
                + "','Description':'" + r.getDescription() + "'}"));
      }
      return runsArray;
    } catch (InvalidOperationException e) {
      log.debug("Failed", e);
      JSONArray runsArray = new JSONArray();
      runsArray.add(JSONObject.fromObject("{'Error':" + e + "}"));
      return runsArray;
    }
  }
}

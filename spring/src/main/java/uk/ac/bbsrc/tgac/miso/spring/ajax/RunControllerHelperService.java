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

package uk.ac.bbsrc.tgac.miso.spring.ajax;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.illumina.IlluminaRun;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ls454.LS454Run;
import uk.ac.bbsrc.tgac.miso.core.data.impl.pacbio.PacBioRun;
import uk.ac.bbsrc.tgac.miso.core.data.impl.solid.SolidRun;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.event.manager.WatchManager;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.RunProcessingUtils;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * uk.ac.bbsrc.tgac.miso.spring.ajax
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
@Ajaxified
public class RunControllerHelperService {
  protected static final Logger log = LoggerFactory.getLogger(RunControllerHelperService.class);
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private RequestManager requestManager;
  @Autowired
  private WatchManager watchManager;
  @Autowired
  private DataObjectFactory dataObjectFactory;

  public void setWatchManager(WatchManager watchManager) {
    this.watchManager = watchManager;
  }

  public JSONObject changePlatformType(HttpSession session, JSONObject json) {
    String cId = json.getString("run_cId");
    Run run = (Run) session.getAttribute("run_" + cId);

    String newRuntype = json.getString("platformtype");

    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Long runId = AbstractRun.UNSAVED_ID;

      if (json.has("runId") && !json.getString("runId").equals("")) {
        //edit existing run
        Map<String, Object> responseMap = new HashMap<String, Object>();
        runId = Long.parseLong(json.getString("runId"));
        Run storedRun = requestManager.getRunById(runId);
        String storedPlatformType = storedRun.getPlatformType().getKey();

        PlatformType newPt = PlatformType.get(newRuntype);
        if (newPt != null) {
          log.info("STORED: " + newRuntype + " :: " + storedPlatformType);
          if (!newRuntype.equals(storedPlatformType)) {
            run = dataObjectFactory.getRunOfType(newPt, user);
            run.setId(storedRun.getId());
          }
          else {
            run = storedRun;
          }

          session.setAttribute("run_" + cId, run);
          responseMap.put("partitions", getPlatformRunOptions(run));
          StringBuilder srb = new StringBuilder();
          srb.append("<select name='sequencer' id='sequencerReference' onchange='Run.ui.populateRunOptions(this);'>");
          srb.append("<option value='0' selected='selected'>Please select...</option>");
          for (SequencerReference sr : requestManager.listSequencerReferencesByPlatformType(newPt)) {
            srb.append("<option value='" + sr.getId() + "'>" + sr.getName() + " (" + sr.getPlatform().getInstrumentModel() + ")</option>");
          }
          srb.append("</select>");
          responseMap.put("sequencers", srb.toString());
        }
        else {
          return JSONUtils.SimpleJSONError("Unrecognised PlatformType");
        }
        return JSONUtils.JSONObjectResponse(responseMap);
      }
      else {
        //new run
        Map<String, Object> responseMap = new HashMap<String, Object>();

        PlatformType newPt = PlatformType.get(newRuntype);
        if (newPt != null) {
          StringBuilder srb = new StringBuilder();
          srb.append("<select name='sequencer' id='sequencerReference' onchange='Run.ui.populateRunOptions(this);'>");
          srb.append("<option value='0' selected='selected'>Please select...</option>");
          for (SequencerReference sr : requestManager.listSequencerReferencesByPlatformType(newPt)) {
            srb.append("<option value='" + sr.getId() + "'>" + sr.getName() + " (" + sr.getPlatform().getInstrumentModel() + ")</option>");
          }
          srb.append("</select>");
          responseMap.put("sequencers", srb.toString());
        }
        else {
          return JSONUtils.SimpleJSONError("Unrecognised PlatformType");
        }

        return JSONUtils.JSONObjectResponse(responseMap);
      }
    }
    catch (IOException e) {
      log.debug("Failed to change PlatformType", e);
      return JSONUtils.SimpleJSONError("Failed to change PlatformType");
    }
  }

  public JSONObject populateRunOptions(HttpSession session, JSONObject json) {
    Long sequencerReferenceId = json.getLong("sequencerReference");
    String cId = json.getString("run_cId");
    try {
      SequencerReference sr = requestManager.getSequencerReferenceById(sequencerReferenceId);
      PlatformType pt = sr.getPlatform().getPlatformType();
      Map<String, Object> responseMap = new HashMap<String, Object>();
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());

      Run run = dataObjectFactory.getRunOfType(pt, user);
      run.setSequencerReference(sr);

      session.setAttribute("run_" + cId, run);
      responseMap.put("partitions", getPlatformRunOptions(run));

      return JSONUtils.JSONObjectResponse(responseMap);
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Failed to get Run options");
    }
  }

  public JSONObject changeContainer(HttpSession session, JSONObject json) {
    if (json.has("platform")) {
      String platform = json.getString("platform");
      PlatformType pt = PlatformType.get(platform);
      if (pt != null) {
        if (pt.equals(PlatformType.ILLUMINA)) {
          return changeIlluminaContainer(session, json);
        }
        else if (pt.equals(PlatformType.LS454)) {
          return changeLS454Container(session, json);
        }
        else if (pt.equals(PlatformType.SOLID)) {
          return changeSolidContainer(session, json);
        }
        else if (pt.equals(PlatformType.IONTORRENT)) {
          return null;
        }
        else if (pt.equals(PlatformType.PACBIO)) {
          return changePacBioContainer(session, json);
        }
        else {
          return JSONUtils.SimpleJSONError("Unrecognised platform type: " + platform);
        }
      }
    }
    return JSONUtils.SimpleJSONError("No platform specified");
  }

  public JSONObject changeChamber(HttpSession session, JSONObject json) {
    if (json.has("platform")) {
      String platform = json.getString("platform");
      PlatformType pt = PlatformType.get(platform);
      if (pt != null) {
        if (pt.equals(PlatformType.LS454)) {
          return changeLS454Chamber(session, json);
        }
        else if (pt.equals(PlatformType.SOLID)) {
          return changeSolidChamber(session, json);
        }
        else if (pt.equals(PlatformType.PACBIO)) {
          return changePacBioChamber(session, json);
        }
        else {
          return JSONUtils.SimpleJSONError("Unrecognised platform type: " + platform);
        }
      }
    }
    return JSONUtils.SimpleJSONError("No platform specified");
  }

  public JSONObject changeIlluminaContainer(HttpSession session, JSONObject json) {
    StringBuilder b = new StringBuilder();
    IlluminaRun run = (IlluminaRun) session.getAttribute("run_" + json.getString("run_cId"));
    run.getSequencerPartitionContainers().clear();

    if ("Illumina MiSeq".equals(run.getSequencerReference().getPlatform().getInstrumentModel())) {
      b.append("<h2>Container 1</h2>");
      b.append("<table class='in'>");
      b.append("<tr><td>ID:</td><td><button onclick='Run.container.lookupContainer(this, 0);' type='button' class='right-button ui-state-default ui-corner-all'>Lookup</button><div style='overflow:hidden'><input type='text' id='sequencerPartitionContainers[0].identificationBarcode' name='sequencerPartitionContainers[0].identificationBarcode'/></div></td></tr>");
      b.append("<tr><td>Location:</td><td><input type='text' id='sequencerPartitionContainers[0].locationBarcode' name='sequencerPartitionContainers[0].locationBarcode'/></td></tr>");
      b.append("<tr><td>Paired:</td><td><input type='checkbox' id='sequencerPartitionContainers[0].paired' name='sequencerPartitionContainers[0].paired' value='false'/></td></tr>");
      b.append("</table>");
      b.append("<div id='partitionErrorDiv'> </div>");
      b.append("<div id='partitionDiv'>");
      b.append("<table class='in'>");
      b.append("<th>Lane No.</th>");
      b.append("<th>Pool</th>");

      b.append("<tr><td>1 </td><td width='90%'><div id='p_div_0-0' class='elementListDroppableDiv'><ul class='runPartitionDroppable' bind='sequencerPartitionContainers[0].partitions[0].pool' partition='0_0'></ul></div></td></tr>");
      b.append("</table>");
      b.append("</div>");

      SequencerPartitionContainer<SequencerPoolPartition> f = dataObjectFactory.getSequencerPartitionContainer();
      f.setPartitionLimit(1);
      f.initEmptyPartitions();
      run.addSequencerPartitionContainer(f);
    }
    else if ("Illumina HiSeq 2500".equals(run.getSequencerReference().getPlatform().getInstrumentModel())) {
      b.append("<h2>Container 1</h2>");
      b.append("<table class='in'>");
      b.append("<tr><td>ID:</td><td><button onclick='Run.container.lookupContainer(this, 0);' type='button' class='right-button ui-state-default ui-corner-all'>Lookup</button><div style='overflow:hidden'><input type='text' id='sequencerPartitionContainers[0].identificationBarcode' name='sequencerPartitionContainers[0].identificationBarcode'/></div></td></tr>");
      b.append("<tr><td>Location:</td><td><input type='text' id='sequencerPartitionContainers[0].locationBarcode' name='sequencerPartitionContainers[0].locationBarcode'/></td></tr>");
      b.append("<tr><td>Paired:</td><td><input type='checkbox' id='sequencerPartitionContainers[0].paired' name='sequencerPartitionContainers[0].paired' value='false'/></td></tr>");
      b.append("</table>");
      b.append("<div id='partitionErrorDiv'> </div>");
      b.append("<div id='partitionDiv'>");
      b.append("<input id='lane2' name='container0Select' onchange='Run.ui.changeIlluminaLane(this, 0);' type='radio' value='2'/>2 ");
      b.append("<input id='lane8' name='container0Select' onchange='Run.ui.changeIlluminaLane(this, 0);' type='radio' value='8'/>8 ");
      b.append("<div id='containerdiv0'> </div>");

      SequencerPartitionContainer<SequencerPoolPartition> f = dataObjectFactory.getSequencerPartitionContainer();
      run.addSequencerPartitionContainer(f);
    }
    else {
      int numContainers = json.getInt("numContainers");
      run.getSequencerPartitionContainers().clear();
      for (int i = 0; i < numContainers; i++) {
        b.append("<h2>Container " + (i + 1) + "</h2>");
        b.append("<table class='in'>");
        b.append("<tr><td>ID:</td><td><button onclick='Run.container.lookupContainer(this, " + i + ");' type='button' class='right-button ui-state-default ui-corner-all'>Lookup</button><div style='overflow:hidden'><input type='text' id='sequencerPartitionContainers[" + i + "].identificationBarcode' name='sequencerPartitionContainers[" + i + "].identificationBarcode'/></div></td></tr>");
        b.append("<tr><td>Location:</td><td><input type='text' id='sequencerPartitionContainers[" + i + "].locationBarcode' name='sequencerPartitionContainers[" + i + "].locationBarcode'/></td></tr>");
        b.append("<tr><td>Paired:</td><td><input type='checkbox' id='sequencerPartitionContainers[" + i + "].paired' name='sequencerPartitionContainers[" + i + "].paired' value='false'/></td></tr>");
        b.append("</table>");
        b.append("<div id='partitionErrorDiv'> </div>");
        b.append("<div id='partitionDiv'>");
        b.append("<table class='in'>");
        b.append("<th>Lane No.</th>");
        b.append("<th>Pool</th>");

        b.append("<tr><td>1 </td><td width='90%'><div id='p_div_" + i + "-0' class='elementListDroppableDiv'><ul class='runPartitionDroppable' bind='sequencerPartitionContainers[" + i + "].partitions[0].pool' partition='" + i + "_0'></ul></div></td></tr>");
        b.append("<tr><td>2 </td><td width='90%'><div id='p_div_" + i + "-1' class='elementListDroppableDiv'><ul class='runPartitionDroppable' bind='sequencerPartitionContainers[" + i + "].partitions[1].pool' partition='" + i + "_1'></ul></div></td></tr>");
        b.append("<tr><td>3 </td><td width='90%'><div id='p_div_" + i + "-2' class='elementListDroppableDiv'><ul class='runPartitionDroppable' bind='sequencerPartitionContainers[" + i + "].partitions[2].pool' partition='" + i + "_2'></ul></div></td></tr>");
        b.append("<tr><td>4 </td><td width='90%'><div id='p_div_" + i + "-3' class='elementListDroppableDiv'><ul class='runPartitionDroppable' bind='sequencerPartitionContainers[" + i + "].partitions[3].pool' partition='" + i + "_3'></ul></div></td></tr>");
        b.append("<tr><td>5 </td><td width='90%'><div id='p_div_" + i + "-4' class='elementListDroppableDiv'><ul class='runPartitionDroppable' bind='sequencerPartitionContainers[" + i + "].partitions[4].pool' partition='" + i + "_4'></ul></div></td></tr>");
        b.append("<tr><td>6 </td><td width='90%'><div id='p_div_" + i + "-5' class='elementListDroppableDiv'><ul class='runPartitionDroppable' bind='sequencerPartitionContainers[" + i + "].partitions[5].pool' partition='" + i + "_5'></ul></div></td></tr>");
        b.append("<tr><td>7 </td><td width='90%'><div id='p_div_" + i + "-6' class='elementListDroppableDiv'><ul class='runPartitionDroppable' bind='sequencerPartitionContainers[" + i + "].partitions[6].pool' partition='" + i + "_6'></ul></div></td></tr>");
        b.append("<tr><td>8 </td><td width='90%'><div id='p_div_" + i + "-7' class='elementListDroppableDiv'><ul class='runPartitionDroppable' bind='sequencerPartitionContainers[" + i + "].partitions[7].pool' partition='" + i + "_7'></ul></div></td></tr>");
        b.append("</table>");
        b.append("</div>");

        SequencerPartitionContainer<SequencerPoolPartition> f = dataObjectFactory.getSequencerPartitionContainer();
        f.initEmptyPartitions();
        run.addSequencerPartitionContainer(f);
      }
    }
    return JSONUtils.SimpleJSONResponse(b.toString());
  }

  public JSONObject changeIlluminaLane(HttpSession session, JSONObject json) {
    int numLanes = json.getInt("numLanes");
    int container = json.getInt("container");
    StringBuilder b = new StringBuilder();
    b.append("<table class='in'>");
    b.append("<th>Lane No.</th>");
    b.append("<th>Pool</th>");

    IlluminaRun run = (IlluminaRun) session.getAttribute("run_" + json.getString("run_cId"));
    SequencerPartitionContainer<SequencerPoolPartition> f = run.getSequencerPartitionContainers().get(container);
    f.setPartitionLimit(numLanes);
    f.initEmptyPartitions();

    for (int i = 0; i < numLanes; i++) {
      b.append("<tr><td>" + (i + 1) + "</td>");
      b.append("<td width='90%'><div id='p_div_" + container + "-" + i + "' class='elementListDroppableDiv'><ul class='runPartitionDroppable' bind='sequencerPartitionContainers[" + container + "].partitions[" + i + "].pool' partition='" + container + "_" + i + "'></ul></div></td>");
      b.append("</tr>");
    }
    b.append("</table>");

    return JSONUtils.SimpleJSONResponse(b.toString());
  }

  public JSONObject changeLS454Container(HttpSession session, JSONObject json) {
    StringBuilder b = new StringBuilder();
    int numContainers = json.getInt("numContainers");
    LS454Run run = (LS454Run) session.getAttribute("run_" + json.getString("run_cId"));
    run.getSequencerPartitionContainers().clear();

    for (int i = 0; i < numContainers; i++) {
      b.append("<h2>Container " + (i + 1) + "</h2>");
      b.append("<table class='in'>");
      b.append("<tr><td>ID:</td><td><button onclick='Run.container.lookupContainer(this, " + i + ");' type='button' class='right-button ui-state-default ui-corner-all'>Lookup</button><div style='overflow:hidden'><input type='text' id='sequencerPartitionContainers[" + i + "].identificationBarcode' name='sequencerPartitionContainers[" + i + "].identificationBarcode'/></div></td></tr>");
      b.append("<tr><td>Location:</td><td><input type='text' id='sequencerPartitionContainers[" + i + "].locationBarcode' name='sequencerPartitionContainers[" + i + "].locationBarcode'/></td></tr>");
      b.append("<tr><td>Paired:</td><td><input type='checkbox' id='sequencerPartitionContainers[" + i + "].paired' name='sequencerPartitionContainers[" + i + "].paired'/></td></tr>");
      b.append("</table>");
      b.append("<div id='partitionErrorDiv'> </div>");
      b.append("<div id='partitionDiv'>");
      b.append("<input id='chamber1' name='container" + i + "Select' onchange='Run.ui.changeLS454Chamber(this, " + i + ");' type='radio' value='1'/>1 ");
      b.append("<input id='chamber2' name='container" + i + "Select' onchange='Run.ui.changeLS454Chamber(this, " + i + ");' type='radio' value='2'/>2 ");
      b.append("<input id='chamber4' name='container" + i + "Select' onchange='Run.ui.changeLS454Chamber(this, " + i + ");' type='radio' value='4'/>4 ");
      b.append("<input id='chamber8' name='container" + i + "Select' onchange='Run.ui.changeLS454Chamber(this, " + i + ");' type='radio' value='8'/>8 ");
      b.append("<input id='chamber16' name='container" + i + "Select' onchange='Run.ui.changeLS454Chamber(this, " + i + ");' type='radio' value='16'/>16<br/>");
      b.append("<div id='containerdiv" + i + "'> </div>");
      b.append("</div>");

      SequencerPartitionContainer<SequencerPoolPartition> f = dataObjectFactory.getSequencerPartitionContainer();
      run.addSequencerPartitionContainer(f);
    }
    return JSONUtils.SimpleJSONResponse(b.toString());
  }

  public JSONObject changeLS454Chamber(HttpSession session, JSONObject json) {
    int numChambers = json.getInt("numChambers");
    int container = json.getInt("container");
    StringBuilder b = new StringBuilder();
    b.append("<table class='in'>");
    b.append("<th>Chamber No.</th>");
    b.append("<th>Pool</th>");

    LS454Run run = (LS454Run) session.getAttribute("run_" + json.getString("run_cId"));
    SequencerPartitionContainer<SequencerPoolPartition> f = run.getSequencerPartitionContainers().get(container);
    f.setPartitionLimit(numChambers);
    f.initEmptyPartitions();

    for (int i = 0; i < numChambers; i++) {
      b.append("<tr><td>" + (i + 1) + "</td>");
      b.append("<td width='90%'><div id='p_div_" + container + "-" + i + "' class='elementListDroppableDiv'><ul class='runPartitionDroppable' bind='sequencerPartitionContainers[" + container + "].partitions[" + i + "].pool' partition='" + container + "_" + i + "'></ul></div></td>");
      b.append("</tr>");
    }
    b.append("</table>");

    return JSONUtils.SimpleJSONResponse(b.toString());
  }

  public JSONObject changeSolidContainer(HttpSession session, JSONObject json) {
    int numContainers = json.getInt("numContainers");
    StringBuilder b = new StringBuilder();
    SolidRun run = (SolidRun) session.getAttribute("run_" + json.getString("run_cId"));
    run.getSequencerPartitionContainers().clear();

    for (int i = 0; i < numContainers; i++) {
      b.append("<h2>Container " + (i + 1) + "</h2>");
      b.append("<table class='in'>");
      b.append("<tr><td>ID:</td><td><button onclick='Run.container.lookupContainer(this, " + i + ");' type='button' class='right-button ui-state-default ui-corner-all'>Lookup</button><div style='overflow:hidden'><input type='text' id='sequencerPartitionContainers[" + i + "].identificationBarcode' name='sequencerPartitionContainers[" + i + "].identificationBarcode'/></div></td></tr>");
      b.append("<tr><td>Location:</td><td><input type='text' id='sequencerPartitionContainers[" + i + "].locationBarcode' name='sequencerPartitionContainers[" + i + "].locationBarcode'/></td></tr>");
      b.append("<tr><td>Paired:</td><td><input type='checkbox' id='sequencerPartitionContainers[" + i + "].paired' name='sequencerPartitionContainers[" + i + "].paired'/></td></tr>");
      b.append("</table>");
      b.append("<div id='partitionErrorDiv'> </div>");
      b.append("<div id='partitionDiv'>");
      if ("AB SOLiD 5500xl".equals(run.getSequencerReference().getPlatform().getInstrumentModel())) {
        b.append("<table class='in'>");
        b.append("<th>Chamber No.</th>");
        b.append("<th>Pool</th>");

        b.append("<tr><td>1 </td><td width='90%'><div id='p_div_" + i + "-0' class='elementListDroppableDiv'><ul class='runPartitionDroppable' bind='sequencerPartitionContainers[" + i + "].partitions[0].pool' partition='" + i + "_0'></ul></div></td></tr>");
        b.append("<tr><td>2 </td><td width='90%'><div id='p_div_" + i + "-1' class='elementListDroppableDiv'><ul class='runPartitionDroppable' bind='sequencerPartitionContainers[" + i + "].partitions[1].pool' partition='" + i + "_1'></ul></div></td></tr>");
        b.append("<tr><td>3 </td><td width='90%'><div id='p_div_" + i + "-2' class='elementListDroppableDiv'><ul class='runPartitionDroppable' bind='sequencerPartitionContainers[" + i + "].partitions[2].pool' partition='" + i + "_2'></ul></div></td></tr>");
        b.append("<tr><td>4 </td><td width='90%'><div id='p_div_" + i + "-3' class='elementListDroppableDiv'><ul class='runPartitionDroppable' bind='sequencerPartitionContainers[" + i + "].partitions[3].pool' partition='" + i + "_3'></ul></div></td></tr>");
        b.append("<tr><td>5 </td><td width='90%'><div id='p_div_" + i + "-4' class='elementListDroppableDiv'><ul class='runPartitionDroppable' bind='sequencerPartitionContainers[" + i + "].partitions[4].pool' partition='" + i + "_4'></ul></div></td></tr>");
        b.append("<tr><td>6 </td><td width='90%'><div id='p_div_" + i + "-5' class='elementListDroppableDiv'><ul class='runPartitionDroppable' bind='sequencerPartitionContainers[" + i + "].partitions[5].pool' partition='" + i + "_5'></ul></div></td></tr>");
        b.append("</table>");
      }
      else {
        b.append("<input id='chamber1' name='container" + i + "Select' onchange='Run.ui.changeSolidChamber(this, " + i + ");' type='radio' value='1'/>1 ");
        b.append("<input id='chamber4' name='container" + i + "Select' onchange='Run.ui.changeSolidChamber(this, " + i + ");' type='radio' value='4'/>4 ");
        b.append("<input id='chamber8' name='container" + i + "Select' onchange='Run.ui.changeSolidChamber(this, " + i + ");' type='radio' value='8'/>8 ");
        b.append("<input id='chamber16' name='container" + i + "Select' onchange='Run.ui.changeSolidChamber(this, " + i + ");' type='radio' value='16'/>16<br/>");
      }
      b.append("<div id='containerdiv" + i + "'> </div>");
      b.append("</div>");
      SequencerPartitionContainer<SequencerPoolPartition> f = dataObjectFactory.getSequencerPartitionContainer();
      run.addSequencerPartitionContainer(f);
    }
    return JSONUtils.SimpleJSONResponse(b.toString());
  }

  public JSONObject changeSolidChamber(HttpSession session, JSONObject json) {
    int numChambers = json.getInt("numChambers");
    int container = json.getInt("container");

    SolidRun run = (SolidRun) session.getAttribute("run_" + json.getString("run_cId"));
    SequencerPartitionContainer<SequencerPoolPartition> f = run.getSequencerPartitionContainers().get(container);
    f.setPartitionLimit(numChambers);
    f.initEmptyPartitions();

    StringBuilder b = new StringBuilder();
    b.append("<table class='in'>");
    b.append("<th>Chamber No.</th>");
    b.append("<th>Pool</th>");
    for (int i = 0; i < numChambers; i++) {
      b.append("<tr><td>" + (i + 1) + "</td>");
      b.append("<td width='90%'><div id='p_div_" + container + "-" + i + "' class='elementListDroppableDiv'><ul class='runPartitionDroppable' bind='sequencerPartitionContainers[" + container + "].partitions[" + i + "].pool'  partition='" + container + "_" + i + "'></ul></div></td>");
      b.append("</tr>");
    }
    b.append("</table>");
    return JSONUtils.SimpleJSONResponse(b.toString());
  }

  public JSONObject changePacBioContainer(HttpSession session, JSONObject json) {
    int numContainers = json.getInt("numContainers");
    StringBuilder b = new StringBuilder();
    PacBioRun run = (PacBioRun) session.getAttribute("run_" + json.getString("run_cId"));
    run.getSequencerPartitionContainers().clear();

    for (int i = 0; i < numContainers; i++) {
      b.append("<h2>Container " + (i + 1) + "</h2>");
      b.append("<table class='in'>");
      b.append("<tr><td>ID:</td><td><button onclick='Run.container.lookupContainer(this, " + i + ");' type='button' class='right-button ui-state-default ui-corner-all'>Lookup</button><div style='overflow:hidden'><input type='text' id='sequencerPartitionContainers[" + i + "].identificationBarcode' name='sequencerPartitionContainers[" + i + "].identificationBarcode'/></div></td></tr>");
      b.append("<tr><td>Location:</td><td><input type='text' id='sequencerPartitionContainers[" + i + "].locationBarcode' name='sequencerPartitionContainers[" + i + "].locationBarcode'/></td></tr>");
      b.append("<tr><td>Paired:</td><td><input type='checkbox' id='sequencerPartitionContainers[" + i + "].paired' name='sequencerPartitionContainers[" + i + "].paired'/></td></tr>");
      b.append("</table>");
      b.append("<div id='partitionErrorDiv'> </div>");
      b.append("<div id='partitionDiv'>");

      b.append("<input id='chamber1' name='container" + i + "Select' onchange='Run.ui.changePacBioChamber(this, " + i + ");' type='radio' value='1'/>1 ");
      b.append("<input id='chamber2' name='container" + i + "Select' onchange='Run.ui.changePacBioChamber(this, " + i + ");' type='radio' value='2'/>2 ");
      b.append("<input id='chamber3' name='container" + i + "Select' onchange='Run.ui.changePacBioChamber(this, " + i + ");' type='radio' value='3'/>3 ");
      b.append("<input id='chamber4' name='container" + i + "Select' onchange='Run.ui.changePacBioChamber(this, " + i + ");' type='radio' value='4'/>4 ");
      b.append("<input id='chamber5' name='container" + i + "Select' onchange='Run.ui.changePacBioChamber(this, " + i + ");' type='radio' value='5'/>5 ");
      b.append("<input id='chamber6' name='container" + i + "Select' onchange='Run.ui.changePacBioChamber(this, " + i + ");' type='radio' value='6'/>6 ");
      b.append("<input id='chamber7' name='container" + i + "Select' onchange='Run.ui.changePacBioChamber(this, " + i + ");' type='radio' value='7'/>7 ");
      b.append("<input id='chamber8' name='container" + i + "Select' onchange='Run.ui.changePacBioChamber(this, " + i + ");' type='radio' value='8'/>8<br/>");

      b.append("<div id='containerdiv" + i + "'> </div>");
      b.append("</div>");
      SequencerPartitionContainer<SequencerPoolPartition> f = dataObjectFactory.getSequencerPartitionContainer();
      run.addSequencerPartitionContainer(f);
    }
    return JSONUtils.SimpleJSONResponse(b.toString());
  }

  public JSONObject changePacBioChamber(HttpSession session, JSONObject json) {
    int numChambers = json.getInt("numChambers");
    int container = json.getInt("container");

    PacBioRun run = (PacBioRun) session.getAttribute("run_" + json.getString("run_cId"));
    SequencerPartitionContainer<SequencerPoolPartition> f = run.getSequencerPartitionContainers().get(container);
    f.setPartitionLimit(numChambers);
    f.initEmptyPartitions();

    StringBuilder b = new StringBuilder();
    b.append("<table class='in'>");
    b.append("<th>Chamber No.</th>");
    b.append("<th>Pool</th>");
    for (int i = 0; i < numChambers; i++) {
      b.append("<tr><td>" + (i + 1) + "</td>");
      b.append("<td width='90%'><div id='p_div_" + container + "-" + i + "' class='elementListDroppableDiv'><ul class='runPartitionDroppable' bind='sequencerPartitionContainers[" + container + "].partitions[" + i + "].pool'  partition='" + container + "_" + i + "'></ul></div></td>");
      b.append("</tr>");
    }
    b.append("</table>");

    return JSONUtils.SimpleJSONResponse(b.toString());
  }

  private String getPlatformSelect(Run run) throws IOException {
    StringBuilder b = new StringBuilder();
    Collection<Platform> platforms = requestManager.listAllPlatforms();
    b.append("<select name='platform' id='platform' onchange='setContainerCount(this);'>");
    for (Platform p : platforms) {
      if (p.getPlatformType().equals(run.getPlatformType())) {
        b.append("<option value='" + p.getPlatformId() + "' platform='" + p.getPlatformType().getKey() + "'");
        b.append(">" + p.getPlatformType().getKey() + " - " + p.getInstrumentModel() + "</option>");
      }
    }
    b.append("</select>");
    return b.toString();
  }

  public JSONObject getRunQCUsers(HttpSession session, JSONObject json) {
    try {
      Collection<String> users = new HashSet<String>();
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      users.add(user.getFullName());

      StringBuilder sb = new StringBuilder();
      for (String name : users) {
        sb.append("<option value='" + name + "'>" + name + "</option>");
      }
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("qcUserOptions", sb.toString());
      map.put("runId", json.getString("runId"));
      return JSONUtils.JSONObjectResponse(map);
    }
    catch (IOException e) {
      e.printStackTrace();
      log.error("Failed to get available users for this Run QC: ", e);
      return JSONUtils.SimpleJSONError("Failed to get available users for this Run QC: " + e.getMessage());
    }
  }

  public JSONObject getRunQCTypes(HttpSession session, JSONObject json) {
    try {
      StringBuilder sb = new StringBuilder();
      Collection<QcType> types = requestManager.listAllRunQcTypes();
      for (QcType s : types) {
        sb.append("<option value='" + s.getQcTypeId() + "'>" + s.getName() + "</option>");
      }
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("types", sb.toString());
      return JSONUtils.JSONObjectResponse(map);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return JSONUtils.SimpleJSONError("Cannot list all Run QC Types");
  }

  public JSONObject getRunQCProcessSelection(HttpSession session, JSONObject json) {
    try {
      StringBuilder sb = new StringBuilder();
      if (json.has("runId") && !json.get("runId").equals("")) {
        Long runId = Long.parseLong(json.getString("runId"));
        Run r = requestManager.getRunById(runId);

        for (SequencerPartitionContainer<SequencerPoolPartition> f : ((RunImpl) r).getSequencerPartitionContainers()) {
          sb.append("<table class='containerSummary'><tr>");
          for (Partition p : f.getPartitions()) {
            sb.append("<td onclick='Run.qc.toggleProcessPartition(this);' runId='" + r.getId() + "' containerId='" + f.getId() + "' partitionNumber='" + p.getPartitionNumber() + "' id='" + r.getId() + "_" + f.getId() + "_" + p.getPartitionNumber() + "' class='smallbox'>" + p.getPartitionNumber() + "</td>");
          }
          sb.append("</tr></table>");
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("processSelection", sb.toString());
        return JSONUtils.JSONObjectResponse(map);
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return JSONUtils.SimpleJSONError("Cannot list all Run QC Process Selection");
  }

  public JSONObject addRunQC(HttpSession session, JSONObject json) {
    try {
      for (Object key : json.keySet()) {
        if (json.get(key) == null || json.get(key).equals("")) {
          String k = (String) key;
          return JSONUtils.SimpleJSONError("Please enter a value for '" + k + "'");
        }
      }
      if (json.has("runId") && !json.get("runId").equals("")) {
        Long runId = Long.parseLong(json.getString("runId"));
        Run run = requestManager.getRunById(runId);
        /*
        if (json.has("qcPassed") && json.getString("qcPassed").equals("true")) {
          sample.setQcPassed(true);
        }
        */

        List<String> processSelections = new ArrayList<String>();
        List<Partition> partitionSelections = new ArrayList<Partition>();
        JSONArray a = JSONArray.fromObject(json.getString("processSelection"));
        for (JSONObject s : (Iterable<JSONObject>) a) {
          String id = s.getString("id");
          processSelections.add(id);

          long containerId = Long.parseLong(id.split("_")[1]);
          long partitionNumber = Long.parseLong(id.split("_")[2]);
          SequencerPartitionContainer<SequencerPoolPartition> f = requestManager.getSequencerPartitionContainerById(containerId);
          for (Partition p : f.getPartitions()) {
            if (p.getPartitionNumber() == partitionNumber) {
              p.setSequencerPartitionContainer(f);
              partitionSelections.add(p);
            }
          }
        }

        RunQC newQc = dataObjectFactory.getRunQC();
        newQc.setQcCreator(json.getString("qcCreator"));
        newQc.setQcDate(new SimpleDateFormat("dd/MM/yyyy").parse(json.getString("qcDate")));
        newQc.setQcType(requestManager.getRunQcTypeById(json.getLong("qcType")));
        newQc.setInformation(json.getString("information"));
        newQc.setDoNotProcess(json.getBoolean("doNotProcess"));
        newQc.setPartitionSelections(partitionSelections);
        newQc.setRun(run);
        run.addQc(newQc);

        StringBuilder sb = new StringBuilder();
        sb.append("<tr><th>QCed By</th><th>QC Date</th><th>Method</th><th>Process Selection</th><th>Information</th><th>Do Not Process</th></tr>");
        for (RunQC qc : run.getRunQCs()) {
          sb.append("<tr>");
          sb.append("<td>" + qc.getQcCreator() + "</td>");
          sb.append("<td>" + qc.getQcDate() + "</td>");
          sb.append("<td>" + qc.getQcType().getName() + "</td>");
          sb.append("<td>");

          for (SequencerPartitionContainer<SequencerPoolPartition> f : ((RunImpl) run).getSequencerPartitionContainers()) {
            sb.append("<table class='containerSummary'><tr>");
            for (Partition p : f.getPartitions()) {
              if (processSelections.contains(run.getId() + "_" + f.getId() + "_" + p.getPartitionNumber())) {
                sb.append("<td runId='" + run.getId() + "' containerId='" + f.getId() + "' partitionId='" + p.getId() + "' id='" + qc.getId() + "_" + run.getId() + "_" + f.getId() + "_" + p.getPartitionNumber() + "' class='smallbox partitionOccupied'>" + p.getPartitionNumber() + "</td>");
              }
              else {
                sb.append("<td runId='" + run.getId() + "' containerId='" + f.getId() + "' partitionId='" + p.getId() + "' id='" + qc.getId() + "_" + run.getId() + "_" + f.getId() + "_" + p.getPartitionNumber() + "' class='smallbox'>" + p.getPartitionNumber() + "</td>");
              }
            }
            sb.append("</tr></table>");
          }
          sb.append("</td>");
          sb.append("<td>" + qc.getInformation() + "</td>");
          sb.append("<td>" + qc.getDoNotProcess() + "</td>");
          sb.append("</tr>");
        }

        requestManager.saveRunQC(newQc);

        return JSONUtils.SimpleJSONResponse(sb.toString());
      }
    }
    catch (Exception e) {
      log.error("Failed to add Run QC to this run: ", e);
      return JSONUtils.SimpleJSONError("Failed to add Run QC to this run: " + e.getMessage());
    }
    return JSONUtils.SimpleJSONError("Cannot add RunQC");
  }

  //TODO - FIX THIS!
  /*
  public JSONObject previewRunImport(HttpSession session, JSONObject json) {
    StringBuffer sb = new StringBuffer();

    String runPath = json.getString("runPath");
    String platformType = json.getString("platformType");
    String experimentId = json.getString("experimentId");

    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Experiment experiment = requestManager.getExperimentById(Long.parseLong(experimentId));
      PlatformType rt = PlatformType.get(platformType);

      if (rt.equals(PlatformType.ILLUMINA)) {
        //Run run = ((TgacDataObjectFactory) DataObjectFactory.getDataObjectFactory(DataObjectFactory.TGAC)).getRunOfType(rt, experiment, user);
        Run run = dataObjectFactory.getRunOfType(rt, user);
        run.setPlatformType(rt);
        //match something like 100216_N73018_0002_desc
        String regex = "([\\d]+)_(N[0-9]{5})_([\\d]{4})_(.*)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(runPath); // get a matcher object

        if (m.matches()) {
          Map<String, String> s = RunInfoUtils.checkIlluminaStatus(runPath);
          if (s.get("error") == null) {
            try {
              StatusImpl status = new StatusImpl(s.get("ok"));
              Map<String, String> complete = RunInfoUtils.checkIlluminaCompleted(runPath);
              if (complete.get("error") == null) {
                status.setHealth(HealthType.Completed);
              }

              Document statusDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(status.parseStatusFile())));
              String runName = statusDoc.getElementsByTagName("RunName").item(0).getTextContent();
              String per = statusDoc.getElementsByTagName("IsPairedEndRun").item(0).getTextContent();
              String cyc = statusDoc.getElementsByTagName("NumCycles").item(0).getTextContent();

              run.setName(runName);
              run.setPairedEnd(Boolean.parseBoolean(per));
              run.setCycles(Integer.parseInt(cyc));

              run.setPlatformRunId(Integer.parseInt(m.group(3)));
              run.setDescription(m.group(4));
              run.setFilePath(runPath);

              run.setStatus(status);

              boolean stored = false;
              for (Run r : requestManager.listAllRuns()) {
                if (r.equals(run)) {
                  stored = true;
                  run.setRunId(r.getRunId());
                }
              }

              session.setAttribute("experiment", experiment);
              session.setAttribute("run", run);

              StringBuilder b = new StringBuilder();
              String health = run.getStatus().getHealth().getKey();
              b.append("<div id='runpreview' class='" + health + "'>");

              b.append("<h1 class='h_" + health + "'>" + run.getStatus().getHealth().getKey() + "</h1><br/>(" + run.getStatus().getPath() + ")<br/>");
              b.append("Name: <b>" + run.getName() + "</b><br/>");
              b.append("Description: <b>" + run.getDescription() + "</b><br/>");
              b.append("Paired End: <b>" + run.getPairedEnd() + "</b><br/>");
              b.append("Cycles: <b>" + run.getCycles() + "</b><br/><br/>");

              if (stored) {
                b.append("<br/>This run has already been added to the system: <a href='/miso/run/" + run.getRunId() + "/experiment/" + experimentId + "'>" + run.getName() + "</a>");
              }
              else {
                b.append("<form method='POST'>");
                b.append("<input type='hidden' name='experiment' id='experiment' value='" + experimentId + "'");
                b.append("<input type='submit' value='Import'/>");
                b.append("</form>");
              }

              b.append("</div>");
              return JSONUtils.SimpleJSONResponse(b.toString());
            }
            catch (ParserConfigurationException e) {
              //throw new MalformedRunException("Could not set up status parser", e);
              return JSONUtils.SimpleJSONError("Could not set up status parser: " + e.getMessage());
            }
            catch (SAXException e) {
              //throw new MalformedRunException("Could not parse status document", e);
              return JSONUtils.SimpleJSONError("Could not parse status document: " + e.getMessage());
            }
            catch (StatusException e) {
              //throw new MalformedRunException("Could not populate Status object", e);
              return JSONUtils.SimpleJSONError("Could not populate Status object: " + e.getMessage());
            }
          }
          else {
            //throw new MalformedRunException("Run directory \"" + runPath + "\" could not be found.");
            return JSONUtils.SimpleJSONError("Run directory \"" + runPath + "\" could not be found.");
          }
        }
        else {
          //throw new MalformedRunException("Run directory \"" + runPath + "\" does not look right.");
          return JSONUtils.SimpleJSONError("Run directory \"" + runPath + "\" does not look right.");
        }
      }
      else if (rt.equals(PlatformType.LS454)) {
        //Run run = ((TgacDataObjectFactory) DataObjectFactory.getDataObjectFactory(DataObjectFactory.TGAC)).getRunOfType(rt, experiment, user);
        Run run = dataObjectFactory.getRunOfType(rt, user);
        run.setPlatformType(rt);
        //match something like R_2009_11_20_08_52_18_FLX02090498_Administrator_JR1JR2JR3JR4
        String regex = "R_(\\d{4}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\d{2})_([A-Z0-9]+)_([A-Za-z]+)_(.*)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(runPath); // get a matcher object

        if (m.matches()) {
          Map<String, String> s = RunInfoUtils.check454Status(runPath);
          if (s.get("error") == null) {
            try {
              StatusImpl status = new StatusImpl(s.get("ok"));
              Map<String, String> complete = RunInfoUtils.check454Completed(runPath);
              if (complete.get("error") == null) {
                status.setHealth(HealthType.Completed);
              }

              Document statusDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(status.parseStatusFile())));

              //String per = statusDoc.getElementsByTagName("IsPairedEndRun").item(0).getTextContent();
              //run.setPairedEnd(Boolean.parseBoolean(per));

              NodeList nodes = statusDoc.getElementsByTagName("id");
              for (int i = 0; i < nodes.getLength(); i++) {
                Node n = nodes.item(i);
                if (n.getParentNode() != null) {
                  if (n.getParentNode().getNodeName().equals("run")) {
                    run.setName(n.getTextContent());
                  }
                  else if (n.getParentNode().getNodeName().equals("ptp")) {
                    run.setPlatformRunId(Integer.parseInt(n.getTextContent()));
                  }
                }
              }

              run.setPairedEnd(false);

              String cyc = statusDoc.getElementsByTagName("numCycles").item(0).getTextContent();
              run.setCycles(Integer.parseInt(cyc));

              String desc = statusDoc.getElementsByTagName("shortName").item(0).getTextContent();
              run.setDescription(desc);

              run.setFilePath(runPath);

              run.setStatus(status);

              boolean stored = false;
              for (Run r : requestManager.listAllRuns()) {
                if (r.equals(run)) {
                  stored = true;
                  run.setRunId(r.getRunId());
                }
              }

              session.setAttribute("experiment", experiment);
              session.setAttribute("run", run);

              StringBuilder b = new StringBuilder();
              String health = run.getStatus().getHealth().getKey();
              b.append("<div id='runpreview' class='" + health + "'");

              b.append("<h1 class='h_" + health + "'>" + health + "</h1><br/>(" + run.getStatus().getPath() + ")<br/>");
              b.append("Name: <b>" + run.getName() + "</b><br/>");
              b.append("Description: <b>" + run.getDescription() + "</b><br/>");
              //b.append("Paired End: <b>" + run.getPairedEnd() + "</b><br/>");
              b.append("Cycles: <b>" + run.getCycles() + "</b><br/><br/>");

              if (stored) {
                b.append("<br/>This run has already been added to the system: <a href='/miso/run/" + run.getRunId() + "/experiment/" + experimentId + "'>" + run.getName() + "</a>");
              }
              else {
                b.append("<form method='POST'>");
                b.append("<input type='hidden' name='experiment' id='experiment' value='" + experimentId + "'");
                b.append("<input type='submit' value='Import'/>");
                b.append("</form>");
              }

              b.append("</div>");
              return JSONUtils.SimpleJSONResponse(b.toString());
            }
            catch (ParserConfigurationException e) {
              //throw new MalformedRunException("Could not set up status parser", e);
              return JSONUtils.SimpleJSONError("Could not set up status parser: " + e.getMessage());
            }
            catch (SAXException e) {
              //throw new MalformedRunException("Could not parse status document", e);
              return JSONUtils.SimpleJSONError("Could not parse status document: " + e.getMessage());
            }
            catch (StatusException e) {
              //throw new MalformedRunException("Could not populate Status object", e);
              return JSONUtils.SimpleJSONError("Could not populate Status object: " + e.getMessage());
            }
          }
          else {
            //throw new MalformedRunException("Run directory \"" + runPath + "\" could not be found.");
            return JSONUtils.SimpleJSONError("Run directory \"" + runPath + "\" could not be found.");
          }
        }
        else {
          //throw new MalformedRunException("Run directory \"" + runPath + "\" does not look right.");
          return JSONUtils.SimpleJSONError("Run directory \"" + runPath + "\" does not look right.");
        }

      }
      else if (rt.equals(PlatformType.SOLID)) {
        return JSONUtils.SimpleJSONError("Unsupported import type at present.");
      }
      else {
        return JSONUtils.SimpleJSONError("Unrecognised run type.");
      }
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Could not retrieve essential objects for run import.");
    }
  }
  */

  private String getPlatformRunOptions(Run run) throws IOException {
    StringBuilder b = new StringBuilder();
    b.append("<span id='containerspan'>Containers: ");
    for (int i = 0; i < run.getSequencerReference().getPlatform().getNumContainers(); i++) {
      b.append("<input id='container" + (i + 1) + "' name='containerselect' ");
      b.append("onchange='Run.container.changeContainer(" +
               (i + 1) + "," +
               "\"" + run.getSequencerReference().getPlatform().getPlatformType().getKey() + "\"," +
               run.getSequencerReference().getId() + ");' type='radio' value='" + (i + 1) + "'/>" + (i + 1));
    }
    b.append("</span><br/>");
    b.append("<div id='containerdiv' class='note ui-corner-all'> </div>");
    return b.toString();
  }

  public JSONObject lookupContainer(HttpSession session, JSONObject json) {
    if (json.has("barcode") && !"".equals(json.getString("barcode")) &&
        json.has("containerNum")) {
      try {
        String barcode = json.getString("barcode");
        long containerNum = json.getLong("containerNum");
        Collection<SequencerPartitionContainer<SequencerPoolPartition>> fs = requestManager.listSequencerPartitionContainersByBarcode(barcode);
        if (!fs.isEmpty()) {
          JSONObject confirm = new JSONObject();
          StringBuilder sb = new StringBuilder();
          if (fs.size() == 1) {
            //replace container div
            SequencerPartitionContainer<SequencerPoolPartition> f = new ArrayList<SequencerPartitionContainer<SequencerPoolPartition>>(fs).get(0);
            sb.append("<table class='in'>");
            sb.append("<th>Partition No.</th>");
            sb.append("<th>Pool</th>");
            for (SequencerPoolPartition p : f.getPartitions()) {
              sb.append("<tr>");
              sb.append("<td>" + p.getPartitionNumber() + "</td>");
              sb.append("<td width='90%'>");
              if (p.getPool() != null) {
                confirm.put(p.getPartitionNumber(), p.getPool().getName());

                sb.append("<ul partition='" + (p.getPartitionNumber() - 1) + "' bind='sequencerPartitionContainers[" + containerNum + "].partitions[" + (p.getPartitionNumber() - 1) + "].pool' class='runPartitionDroppable'>");
                sb.append("<div class='dashboard'>");
                sb.append(p.getPool().getName());
                sb.append("(" + LimsUtils.getDateAsString(p.getPool().getCreationDate()) + ")<br/>");
                sb.append("<span style='font-size:8pt'>");
                if (!p.getPool().getExperiments().isEmpty()) {
                  sb.append("<i>");
                  for (Experiment e : p.getPool().getExperiments()) {
                    sb.append(e.getStudy().getProject().getAlias() + " (" + e.getName() + ": " + p.getPool().getDilutions().size() + " dilutions)<br/>");
                  }
                  sb.append("</i>");
                  sb.append("<input type='hidden' name='sequencerPartitionContainers[" + containerNum + "].partitions[" + (p.getPartitionNumber() - 1) + "].pool' id='pId" + (p.getPartitionNumber() - 1) + "' value='" + p.getPool().getId() + "'/>");
                }
                else {
                  sb.append("<i>No experiment linked to this pool</i>");
                }
                sb.append("</span>");
                sb.append("</div>");
                sb.append("</ul>");
              }
              else {
                confirm.put(p.getPartitionNumber(), "empty");

                sb.append("<div id='p_div_" + (p.getPartitionNumber() - 1) + "' class='elementListDroppableDiv'>");
                sb.append("<ul class='runPartitionDroppable' bind='sequencerPartitionContainers[" + containerNum + "].partitions[" + (p.getPartitionNumber() - 1) + "].pool' partition='" + (p.getPartitionNumber() - 1) + "' ondblclick='Run.container.populatePartition(this);'></ul>");
                sb.append("</div>");
              }
              sb.append("</td>");
              sb.append("</tr>");
            }
            sb.append("</table>");
            Map<String, Object> responseMap = new HashMap<String, Object>();
            responseMap.put("html", sb.toString());
            responseMap.put("barcode", f.getIdentificationBarcode());
            responseMap.put("verify", confirm);
            return JSONUtils.JSONObjectResponse(responseMap);
          }
          else {
            //choose container
            return JSONUtils.JSONObjectResponse("html", "");
          }
        }
        else {
          return JSONUtils.JSONObjectResponse("err", "No containers with this barcode.");
        }
      }
      catch (IOException e) {
        e.printStackTrace();
        return JSONUtils.JSONObjectResponse("err", "Unable to lookup barcode.");
      }
    }
    else {
      return JSONUtils.SimpleJSONError("Please supply a barcode to lookup.");
    }
  }

  public JSONObject generateIlluminaDemultiplexCSV(HttpSession session, JSONObject json) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    IlluminaRun r = (IlluminaRun) requestManager.getRunById(json.getLong("runId"));
    SequencerPartitionContainer<SequencerPoolPartition> f = requestManager.getSequencerPartitionContainerById(json.getLong("containerId"));
    if (r != null && f != null) {
      String casavaVersion = "1.8.2";
      if (json.has("casavaVersion") && !"".equals(json.getString("casavaVersion"))) {
        casavaVersion = json.getString("casavaVersion");
      }

      String sheet = RunProcessingUtils.buildIlluminaDemultiplexCSV(r, f, casavaVersion, user.getFullName());
      return JSONUtils.SimpleJSONResponse(sheet);
    }
    return JSONUtils.SimpleJSONError("No run or container found with that ID.");
  }

  public JSONObject addRunNote(HttpSession session, JSONObject json) {
    Long runId = json.getLong("runId");
    String internalOnly = json.getString("internalOnly");
    String text = json.getString("text");

    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Run run = requestManager.getRunById(runId);
      Note note = new Note();

      internalOnly = internalOnly.equals("on") ? "true" : "false";

      note.setInternalOnly(Boolean.parseBoolean(internalOnly));
      note.setText(text);
      note.setOwner(user);
      note.setCreationDate(new Date());
      run.getNotes().add(note);
      requestManager.saveRunNote(run, note);
      requestManager.saveRun(run);
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError(e.getMessage());
    }

    return JSONUtils.SimpleJSONResponse("Note saved successfully");
  }

  public JSONObject watchRun(HttpSession session, JSONObject json) {
    Long runId = json.getLong("runId");
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Run run = requestManager.getRunById(runId);
      if (!run.getWatchers().contains(user)) {
        //run.addWatcher(user);
        watchManager.watch(run, user);
        requestManager.saveRun(run);
      }
      return JSONUtils.SimpleJSONResponse("OK");
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return JSONUtils.SimpleJSONError("Unable to watch run");
  }

  public JSONObject unwatchRun(HttpSession session, JSONObject json) {
    Long runId = json.getLong("runId");
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Run run = requestManager.getRunById(runId);
      if (run.getWatchers().contains(user)) {
        //run.removeWatcher(user);
        watchManager.unwatch(run, user);
        requestManager.saveRun(run);
      }
      return JSONUtils.SimpleJSONResponse("OK");
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return JSONUtils.SimpleJSONError("Unable to unwatch run");
  }

  public JSONObject getPoolByBarcode(HttpSession session, JSONObject json) {
    String barcode = json.getString("barcode");
    int container = json.getInt("container");
    int partition = json.getInt("partition");

    RunImpl r = (RunImpl) session.getAttribute("run_" + json.getString("run_cId"));

    try {
      Pool<? extends Poolable> p = null;

      if (barcode != null && !"".equals(barcode)) {
        if (LimsUtils.isBase64String(barcode)) {
          //Base64-encoded string, most likely a barcode image beeped in. decode and search
          barcode = new String(Base64.decodeBase64(barcode));
        }
      }

      if (json.has("platform") && !"".equals(json.getString("platform"))) {
        PlatformType pt = PlatformType.get(json.getString("platform"));
        if (pt != null) {
          p = requestManager.getPoolByBarcode(barcode, pt);
        }
        else {
          p = requestManager.getPoolByBarcode(barcode);
        }
      }
      else {
        if (r.getPlatformType() != null) {
          p = requestManager.getPoolByBarcode(barcode, r.getPlatformType());
        }
        else {
          p = requestManager.getPoolByBarcode(barcode);
        }
      }
      List<SequencerPartitionContainer> fs = new ArrayList<SequencerPartitionContainer>(r.getSequencerPartitionContainers());
      if (!fs.isEmpty()) {
        SequencerPartitionContainer f = fs.get(container);
        //if (f.getPlatformType().equals(p.getPlatformType())) {
        if (f.getPlatform().getPlatformType().equals(p.getPlatformType())) {
          return JSONUtils.JSONObjectResponse("html", poolHtml(p, container, partition));
        }
        else {
          return JSONUtils.JSONObjectResponse("err", "Error: pool platform does not match container platform");
        }
      }
      return JSONUtils.JSONObjectResponse("err", "Error: cannot get containers from this run");
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.JSONObjectResponse("err", "Error: no such pool");
    }
  }

  public JSONObject checkPoolExperiment(HttpSession session, JSONObject json) {
    try {
      String partition = json.getString("partition");
      Long poolId = json.getLong("poolId");
      Pool<? extends Poolable> p = requestManager.getPoolById(poolId);
      StringBuilder sb = new StringBuilder();

      if (p.getExperiments().size() == 0) {
        Set<Project> pooledProjects = new HashSet<Project>();
        Collection<? extends Poolable> ds = p.getPoolableElements();
        for (Poolable d : ds) {
          if (d instanceof Dilution) {
            pooledProjects.add(((Dilution)d).getLibrary().getSample().getProject());
          }
          else if (d instanceof Plate) {
            Plate plate = (Plate)d;
            if (!plate.getElements().isEmpty()) {
              if (plate.getElementType().equals(Library.class)) {
                Library l = (Library)plate.getElements().get(0);
                pooledProjects.add(l.getSample().getProject());
              }
            }
          }
        }

        sb.append("<div style='float:left; clear:both'>");
        for (Project project : pooledProjects) {
          sb.append("<div id='studySelectDiv" + partition + "_" + project.getProjectId() + "'>");
          sb.append(project.getAlias() + ": <select name='poolStudies" + partition + "_" + project.getProjectId() + "' id='poolStudies" + partition + "_" + project.getProjectId() + "'>");
          Collection<Study> studies = requestManager.listAllStudiesByProjectId(project.getProjectId());
          if (studies.isEmpty()) {
            //throw new Exception("No studies available on project " + project.getName() + ". At least one study must be available for each project associated with this Pool.");
            return JSONUtils.SimpleJSONError("No studies available on project " + project.getName() + ". At least one study must be available for each project associated with this Pool.");
          }
          else {
            for (Study s : studies) {
              sb.append("<option value='" + s.getId() + "'>" + s.getAlias() + " (" + s.getName() + " - " + s.getStudyType() + ")</option>");
            }
          }
          sb.append("</select>");
          sb.append("<input id='studySelectButton-" + partition + "_" + p.getId() + "' type='button' onclick=\"Run.container.selectStudy('" + partition + "', " + p.getId() + "," + project.getProjectId() + ");\" class=\"ui-state-default ui-corner-all\" value='Select Study'/>");
          sb.append("</div><br/>");
        }
        sb.append("</div>");
      }

      return JSONUtils.JSONObjectResponse("html", sb.toString());
    }
    catch (Exception e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError(e.getMessage());
    }
  }

  private String poolHtml(Pool<? extends Poolable> p, int container, int partition) {
    StringBuilder b = new StringBuilder();
    try {
      b.append("<div style='position:relative' onMouseOver='this.className=\"dashboardhighlight\"' onMouseOut='this.className=\"dashboard\"' class='dashboard'>");
      b.append("<div style=\"float:left\"><b>" + p.getName() + " (" + LimsUtils.getDateAsString(p.getCreationDate()) + ")</b><br/>");

      Collection<? extends Dilution> ds = p.getDilutions();
      for (Dilution d : ds) {
        b.append("<span>" + d.getName() + " (" + d.getLibrary().getSample().getProject().getAlias() + ")</span><br/>");
      }

      b.append("<br/><i>");
      Collection<Experiment> exprs = p.getExperiments();
      for (Experiment e : exprs) {
        b.append("<span>" + e.getStudy().getProject().getAlias() + "(" + e.getName() + ": " + p.getDilutions().size() + " dilutions)</span><br/>");
      }
      b.append("</i>");

      if (p.getExperiments().size() == 0) {
        Set<Project> pooledProjects = new HashSet<Project>();
        for (Dilution d : ds) {
          pooledProjects.add(d.getLibrary().getSample().getProject());
        }

        b.append("<div style='float:left; clear:both'>");
        for (Project project : pooledProjects) {
          b.append("<div id='studySelectDiv" + partition + "_" + project.getProjectId() + "'>");
          b.append(project.getAlias() + ": <select name='poolStudies" + partition + "_" + project.getProjectId() + "' id='poolStudies" + partition + "_" + project.getProjectId() + "'>");
          Collection<Study> studies = requestManager.listAllStudiesByProjectId(project.getProjectId());
          if (studies.isEmpty()) {
            throw new Exception("No studies available on project " + project.getName() + ". At least one study must be available for each project associated with this Pool. Double click to add a different pool");
          }
          else {
            for (Study s : studies) {
              b.append("<option value='" + s.getId() + "'>" + s.getAlias() + " (" + s.getName() + " - " + s.getStudyType() + ")</option>");
            }
          }
          b.append("</select>");
          b.append("<input type='button' onclick=\"Run.container.selectStudy('" + partition + "', " + p.getId() + "," + project.getProjectId() + ");\" class=\"ui-state-default ui-corner-all\" value='Select Study'/>");
          b.append("</div><br/>");
        }
      }
      b.append("</div>");
      b.append("<input type='hidden' name='sequencerPartitionContainers[" + container + "].partitions[" + partition + "].pool' id='pId" + p.getId() + "' value='" + p.getId() + "'/></div>");
      b.append("<div style='position: absolute; bottom: 0; right: 0; font-size: 24px; font-weight: bold; color:#BBBBBB'>" + p.getPlatformType().getKey() + "</div>");
      b.append("<span style='position: absolute; top: 0; right: 0;' onclick='Run.pool.confirmPoolRemove(this);' class='float-right ui-icon ui-icon-circle-close'></span>");
      b.append("</div>");
    }
    catch (IOException e) {
      e.printStackTrace();
      return "Cannot get studies for pool: " + e.getMessage();
    }
    catch (Exception e) {
      e.printStackTrace();
      return "Cannot get studies for pool: " + e.getMessage();
    }

    return b.toString();
  }

  public JSONObject deleteRun(HttpSession session, JSONObject json) {
    User user;
    try {
      user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Error getting currently logged in user.");
    }

    if (user != null && user.isAdmin()) {
      if (json.has("runId")) {
        Long runId = json.getLong("runId");
        try {
          requestManager.deleteRun(requestManager.getRunById(runId));
          return JSONUtils.SimpleJSONResponse("Run deleted");
        }
        catch (IOException e) {
          e.printStackTrace();
          return JSONUtils.SimpleJSONError("Cannot delete run: " + e.getMessage());
        }
      }
      else {
        return JSONUtils.SimpleJSONError("No run specified to delete.");
      }
    }
    else {
      return JSONUtils.SimpleJSONError("Only admins can delete objects.");
    }
  }

  public JSONObject listRunsDataTable(HttpSession session, JSONObject json) {
    try {
      JSONObject j = new JSONObject();
      JSONArray jsonArray = new JSONArray();
      for (Run run : requestManager.listAllRuns()) {
        jsonArray.add("['" + run.getName() + "','" +
                      run.getAlias() + "','" +
                      (run.getStatus() != null && run.getStatus().getHealth() != null ? run.getStatus().getHealth().getKey() : "") + "','" +
                      (run.getStatus() != null && run.getStatus().getStartDate() != null ? LimsUtils.getDateAsString(run.getStatus().getStartDate()) : "") + "','" +
                      (run.getStatus() != null && run.getStatus().getCompletionDate() != null ? LimsUtils.getDateAsString(run.getStatus().getCompletionDate()) : "") + "','" +
                      (run.getPlatformType() != null ? run.getPlatformType().getKey() : "") + "','" +
                      "<a href=\"/miso/run/" + run.getId() + "\"><span class=\"ui-icon ui-icon-pencil\"></span></a>" + "']");

      }
      j.put("runsArray", jsonArray);
      return j;
    }
    catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
  }
}

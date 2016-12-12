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

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AutoPopulatingList;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractSequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.Dilution;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedExperimentException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;

/**
 * Created by IntelliJ IDEA. User: davey Date: 25-May-2010 Time: 16:39:52
 */
@Ajaxified
public class ContainerControllerHelperService {
  protected static final Logger log = LoggerFactory.getLogger(ContainerControllerHelperService.class);
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private RequestManager requestManager;
  @Autowired
  private DataObjectFactory dataObjectFactory;

  public JSONObject getPlatformTypes(HttpSession session, JSONObject json) throws IOException {
    StringBuilder b = new StringBuilder();
    for (String p : PlatformType.platformTypeNames(requestManager.listActivePlatformTypes())) {
      b.append("<input type='radio' name='platformTypes' id='platformTypes" + p + "' value='" + p
          + "' onchange='Container.ui.changeContainerPlatformType(this);'/>");
      b.append("<label for='platformTypes" + p + "'>" + p + "</label>");
    }
    return JSONUtils.JSONObjectResponse("html", b.toString());
  }

  public JSONObject changePlatformType(HttpSession session, JSONObject json) {
    String newContainerType = json.getString("platformtype");
    PlatformType pt = PlatformType.get(newContainerType);
    try {

      Map<String, Object> responseMap = new HashMap<>();
      if (pt != null) {
        StringBuilder srb = new StringBuilder();
        srb.append("<select name='sequencer' id='sequencerReference' onchange='Container.ui.populateContainerOptions(this);'>");
        srb.append("<option value='0' selected='selected'>Please select...</option>");
        for (SequencerReference sr : requestManager.listSequencerReferencesByPlatformType(pt)) {
          if (sr.isActive()) {
            srb.append("<option value='" + sr.getId() + "'>" + sr.getName() + " (" + sr.getPlatform().getInstrumentModel() + ")</option>");
          }
        }
        srb.append("</select>");

        responseMap.put("sequencers", srb.toString());
      } else {
        return JSONUtils.SimpleJSONError("Unrecognised PlatformType");
      }
      return JSONUtils.JSONObjectResponse(responseMap);
    } catch (IOException e) {
      log.debug("Failed to change PlatformType", e);
      return JSONUtils.SimpleJSONError("Failed to change PlatformType");
    }
  }

  public JSONObject populateContainerOptions(HttpSession session, JSONObject json) {
    Long sequencerReferenceId = json.getLong("sequencerReference");
    try {
      SequencerPartitionContainer<SequencerPoolPartition> lf = (SequencerPartitionContainer<SequencerPoolPartition>) session
          .getAttribute("container_" + json.getString("container_cId"));

      if (lf.getPlatform() == null) {
        if (lf.getId() == AbstractSequencerPartitionContainer.UNSAVED_ID) {
          SequencerReference sr = requestManager.getSequencerReferenceById(sequencerReferenceId);
          Map<String, Object> responseMap = new HashMap<>();
          responseMap.put("partitions", getContainerOptions(sr));
          responseMap.put("platformId", sr.getPlatform().getId());
          return JSONUtils.JSONObjectResponse(responseMap);
        } else {
          SequencerReference sr = requestManager.getSequencerReferenceById(sequencerReferenceId);
          lf.setPlatform(sr.getPlatform());
          Map<String, Object> responseMap = new HashMap<>();
          responseMap.put("platformId", sr.getPlatform().getId());
          return JSONUtils.JSONObjectResponse(responseMap);
        }
      } else {
        Map<String, Object> responseMap = new HashMap<>();
        SequencerReference sr = requestManager.getSequencerReferenceById(sequencerReferenceId);
        responseMap.put("platformId", sr.getPlatform().getId());
        return JSONUtils.JSONObjectResponse(responseMap);
      }
    } catch (IOException e) {
      log.error("failed to get container options", e);
      return JSONUtils.SimpleJSONError("Failed to get Container options");
    }
  }

  private String getContainerOptions(SequencerReference sr) throws IOException {
    StringBuilder b = new StringBuilder();
    b.append("<span id='containerspan'>" + sr.getPlatform().getPlatformType().getContainerName() + "s: ");
    PlatformType pt = sr.getPlatform().getPlatformType();
    for (int i = 0; i < sr.getPlatform().getNumContainers(); i++) {
      b.append("<input id='container" + (i + 1) + "' name='containerselect' onchange='Container.ui.changeContainer("
          + sr.getPlatform().getNumContainers() + ", \"" + pt.getKey() + "\", " + sr.getId() + ");' type='radio' value='" + (i + 1) + "'/>"
          + (i + 1));
    }
    b.append("</span><br/>");
    b.append("<div id='containerdiv' class='note ui-corner-all'> </div>");
    return b.toString();
  }

  public JSONObject changeContainer(HttpSession session, JSONObject json) {
    if (json.has("platform")) {
      String platform = json.getString("platform");
      PlatformType pt = PlatformType.get(platform);
      if (pt != null) {
        if (pt.equals(PlatformType.ILLUMINA)) {
          return changeIlluminaContainer(session, json);
        } else if (pt.equals(PlatformType.LS454)) {
          return changeLS454Container(session, json);
        } else if (pt.equals(PlatformType.SOLID)) {
          return changeSolidContainer(session, json);
        } else if (pt.equals(PlatformType.PACBIO)) {
          return changePacBioContainer(session, json);
        } else {
          return JSONUtils.SimpleJSONError("Unsupported platform type: " + platform);
        }
      }
    }
    return JSONUtils.SimpleJSONError("No platform specified");
  }

  public String containerInfoHtml(PlatformType platformType) {
    StringBuilder sb = new StringBuilder();
    sb.append("<h2>" + platformType.getContainerName() + "</h2>");
    sb.append("<table class='in'>");
    sb.append(
        "<tr><td>Serial Number:</td><td><button onclick='Container.lookupContainer(this);' type='button' class='right-button ui-state-default ui-corner-all'>Lookup</button><div style='overflow:hidden'><input type='text' id='identificationBarcode' name='identificationBarcode'/><input type='hidden' value='on' name='_identificationBarcode'></div></td></tr>");
    sb.append(
        "<tr><td>Location:</td><td><input type='text' id='locationBarcode' name='locationBarcode'/><input type='hidden' value='on' name='_locationBarcode'></td></tr>");
    sb.append(
        "<tr><td>Validation:</td><td><input type='text' id='validationBarcode' name='validationBarcode'/><input type='hidden' value='on' name='_validationBarcode'></td></tr>");
    sb.append(
        "<tr><td>Paired End:</td><td><input type='checkbox' id='paired' name='paired' value='false'/><input type='hidden' value='on' name='_paired'></td></tr>");
    sb.append("</table>");
    sb.append("<div id='partitionErrorDiv'> </div>");
    sb.append("<div id='partitionDiv'>");
    return sb.toString();
  }

  public JSONObject changeIlluminaContainer(HttpSession session, JSONObject json) {
    long seqRefId = json.getLong("sequencerReferenceId");
    StringBuilder b = new StringBuilder();
    b.append(containerInfoHtml(PlatformType.ILLUMINA));

    try {
      SequencerReference sr = requestManager.getSequencerReferenceById(seqRefId);
      String instrumentModel = sr.getPlatform().getInstrumentModel();
      if ("Illumina MiSeq".equals(instrumentModel) || "Illumina NextSeq 500".equals(instrumentModel)) {
        b.append("<i class='italicInfo'>Click in a " + PlatformType.get("Illumina").getPartitionName()
            + " box to beep/type in barcodes, or double click a pool on the right to sequentially add pools to the "
            + PlatformType.get("Illumina").getContainerName() + "</i>");
        b.append("<table class='in'>");
        b.append("<th>" + PlatformType.get("Illumina").getPartitionName() + " No.</th>");
        b.append("<th>Pool</th>");

        b.append(
            "<tr><td>1 </td><td width='90%'><div id='p_div-0'><ul class='runPartitionDroppable' bind='partitions[0].pool' partition='0' ondblclick='Container.partition.populatePartition(this);'></ul></div></td></tr>");
        b.append("</table>");
        b.append("</div>");

        SequencerPartitionContainer<SequencerPoolPartition> lf = (SequencerPartitionContainer<SequencerPoolPartition>) session
            .getAttribute("container_" + json.getString("container_cId"));
        lf.setPlatform(sr.getPlatform());
        lf.setPartitionLimit(1);
        lf.initEmptyPartitions();
      } else if ("Illumina HiSeq 2500".equals(sr.getPlatform().getInstrumentModel())) {
        SequencerPartitionContainer<SequencerPoolPartition> lf = (SequencerPartitionContainer<SequencerPoolPartition>) session
            .getAttribute("container_" + json.getString("container_cId"));
        lf.setPlatform(sr.getPlatform());

        b.append("Number of " + PlatformType.ILLUMINA.getPartitionName() + "s:");
        b.append(
            "<input id='lane2' name='container0Select' onchange='Container.ui.changeContainerIlluminaLane(this, 0);' type='radio' value='2'/>2 ");
        b.append(
            "<input id='lane8' name='container0Select' onchange='Container.ui.changeContainerIlluminaLane(this, 0);' type='radio' value='8'/>8 ");
        b.append("<div id='containerdiv0'> </div>");
      } else {
        b.append("<i class='italicInfo'>Click in a " + PlatformType.get("Illumina").getPartitionName()
            + " box to beep/type in barcodes, or double click a pool on the right to sequentially add pools to the "
            + PlatformType.get("Illumina").getContainerName() + "</i>");
        b.append("<table class='in'>");
        b.append("<th>" + PlatformType.get("Illumina").getPartitionName() + " No.</th>");
        b.append("<th>Pool</th>");

        b.append(
            "<tr><td>1 </td><td width='90%'><div id='p_div-0'><ul class='runPartitionDroppable' bind='partitions[0].pool' partition='0' ondblclick='Container.partition.populatePartition(this);'></ul></div></td></tr>");
        b.append(
            "<tr><td>2 </td><td width='90%'><div id='p_div-1'><ul class='runPartitionDroppable' bind='partitions[1].pool' partition='1' ondblclick='Container.partition.populatePartition(this);'></ul></div></td></tr>");
        b.append(
            "<tr><td>3 </td><td width='90%'><div id='p_div-2'><ul class='runPartitionDroppable' bind='partitions[2].pool' partition='2' ondblclick='Container.partition.populatePartition(this);'></ul></div></td></tr>");
        b.append(
            "<tr><td>4 </td><td width='90%'><div id='p_div-3'><ul class='runPartitionDroppable' bind='partitions[3].pool' partition='3' ondblclick='Container.partition.populatePartition(this);'></ul></div></td></tr>");
        b.append(
            "<tr><td>5 </td><td width='90%'><div id='p_div-4'><ul class='runPartitionDroppable' bind='partitions[4].pool' partition='4' ondblclick='Container.partition.populatePartition(this);'></ul></div></td></tr>");
        b.append(
            "<tr><td>6 </td><td width='90%'><div id='p_div-5'><ul class='runPartitionDroppable' bind='partitions[5].pool' partition='5' ondblclick='Container.partition.populatePartition(this);'></ul></div></td></tr>");
        b.append(
            "<tr><td>7 </td><td width='90%'><div id='p_div-6'><ul class='runPartitionDroppable' bind='partitions[6].pool' partition='6' ondblclick='Container.partition.populatePartition(this);'></ul></div></td></tr>");
        b.append(
            "<tr><td>8 </td><td width='90%'><div id='p_div-7'><ul class='runPartitionDroppable' bind='partitions[7].pool' partition='7' ondblclick='Container.partition.populatePartition(this);'></ul></div></td></tr>");
        b.append("</table>");
        b.append("</div>");

        SequencerPartitionContainer<SequencerPoolPartition> lf = (SequencerPartitionContainer<SequencerPoolPartition>) session
            .getAttribute("container_" + json.getString("container_cId"));
        lf.setPlatform(sr.getPlatform());
        lf.setPartitionLimit(8);
        lf.initEmptyPartitions();
      }
      b.append("<div id='containerdiv0'> </div>");
      b.append("</div>");
      return JSONUtils.SimpleJSONResponse(b.toString());
    } catch (IOException e) {
      log.error("no sequencer reference defined", e);
      return JSONUtils.SimpleJSONError("No sequencer reference defined");
    }
  }

  public JSONObject changeLS454Container(HttpSession session, JSONObject json) {
    StringBuilder b = new StringBuilder();
    b.append(containerInfoHtml(PlatformType.LS454));

    b.append("Number of " + PlatformType.LS454.getPartitionName() + "s:");
    b.append(
        "<input id='chamber1' name='container0Select' onchange='Container.ui.changeContainerLS454Chamber(this, 0);' type='radio' value='1'/>1 ");
    b.append(
        "<input id='chamber2' name='container0Select' onchange='Container.ui.changeContainerLS454Chamber(this, 0);' type='radio' value='2'/>2 ");
    b.append(
        "<input id='chamber4' name='container0Select' onchange='Container.ui.changeContainerLS454Chamber(this, 0);' type='radio' value='4'/>4 ");
    b.append(
        "<input id='chamber8' name='container0Select' onchange='Container.ui.changeContainerLS454Chamber(this, 0);' type='radio' value='8'/>8 ");
    b.append(
        "<input id='chamber16' name='container0Select' onchange='Container.ui.changeContainerLS454Chamber(this, 0);' type='radio' value='16'/>16<br/>");
    b.append("<div id='containerdiv0'> </div>");
    b.append("</div>");
    return JSONUtils.SimpleJSONResponse(b.toString());
  }

  public JSONObject changeSolidContainer(HttpSession session, JSONObject json) {
    long seqRefId = json.getLong("sequencerReferenceId");
    StringBuilder b = new StringBuilder();
    try {
      SequencerReference sr = requestManager.getSequencerReferenceById(seqRefId);
      b.append(containerInfoHtml(PlatformType.SOLID));
      if ("AB SOLiD 5500xl".equals(sr.getPlatform().getInstrumentModel())) {
        b.append("<table class='in'>");
        b.append("<th>" + PlatformType.SOLID.getPartitionName() + " No.</th>");
        b.append("<th>Pool</th>");

        b.append(
            "<tr><td>1 </td><td width='90%'><div id='p_div_0-0' class='barcodeEntryDiv'><ul class='runPartitionDroppable' bind='partitions[0].pool' partition='0' ondblclick='Container.partition.populatePartition(this);'></ul></div></td></tr>");
        b.append(
            "<tr><td>2 </td><td width='90%'><div id='p_div_0-1' class='barcodeEntryDiv'><ul class='runPartitionDroppable' bind='partitions[1].pool' partition='1' ondblclick='Container.partition.populatePartition(this);'></ul></div></td></tr>");
        b.append(
            "<tr><td>3 </td><td width='90%'><div id='p_div_0-2' class='barcodeEntryDiv'><ul class='runPartitionDroppable' bind='partitions[2].pool' partition='2' ondblclick='Container.partition.populatePartition(this);'></ul></div></td></tr>");
        b.append(
            "<tr><td>4 </td><td width='90%'><div id='p_div_0-3' class='barcodeEntryDiv'><ul class='runPartitionDroppable' bind='partitions[3].pool' partition='3' ondblclick='Container.partition.populatePartition(this);'></ul></div></td></tr>");
        b.append(
            "<tr><td>5 </td><td width='90%'><div id='p_div_0-4' class='barcodeEntryDiv'><ul class='runPartitionDroppable' bind='partitions[4].pool' partition='4' ondblclick='Container.partition.populatePartition(this);'></ul></div></td></tr>");
        b.append(
            "<tr><td>6 </td><td width='90%'><div id='p_div_0-5' class='barcodeEntryDiv'><ul class='runPartitionDroppable' bind='partitions[5].pool' partition='5' ondblclick='Container.partition.populatePartition(this);'></ul></div></td></tr>");
        b.append("</table>");

        SequencerPartitionContainer<SequencerPoolPartition> lf = (SequencerPartitionContainer<SequencerPoolPartition>) session
            .getAttribute("container_" + json.getString("container_cId"));
        lf.setPartitionLimit(6);
        lf.initEmptyPartitions();
        session.setAttribute("container_" + json.getString("container_cId"), lf);
      } else {

        b.append("Number of " + PlatformType.SOLID.getPartitionName() + "s:");
        b.append(
            "<input id='chamber1' name='container0Select' onchange='Container.ui.changeContainerSolidChamber(this, 0);' type='radio' value='1'/>1 ");
        b.append(
            "<input id='chamber4' name='container0Select' onchange='Container.ui.changeContainerSolidChamber(this, 0);' type='radio' value='4'/>4 ");
        b.append(
            "<input id='chamber8' name='container0Select' onchange='Container.ui.changeContainerSolidChamber(this, 0);' type='radio' value='8'/>8 ");
        b.append(
            "<input id='chamber16' name='container0Select' onchange='Container.ui.changeContainerSolidChamber(this, 0);' type='radio' value='16'/>16<br/>");
      }
      b.append("<div id='containerdiv0'> </div>");
      b.append("</div>");
      return JSONUtils.SimpleJSONResponse(b.toString());
    } catch (IOException e) {
      log.error("no sequencer reference defined", e);
      return JSONUtils.SimpleJSONError("No sequencer reference defined");
    }
  }

  public JSONObject changePacBioContainer(HttpSession session, JSONObject json) {
    StringBuilder b = new StringBuilder();
    b.append(containerInfoHtml(PlatformType.PACBIO));
    b.append("Number of " + PlatformType.PACBIO.getPartitionName() + "s:");
    b.append(
        "<input id='chamber1' name='container0Select' onchange='Container.ui.changeContainerPacBioChamber(this, 0);' type='radio' value='1'/>1 ");
    b.append(
        "<input id='chamber2' name='container0Select' onchange='Container.ui.changeContainerPacBioChamber(this, 0);' type='radio' value='2'/>2 ");
    b.append(
        "<input id='chamber3' name='container0Select' onchange='Container.ui.changeContainerPacBioChamber(this, 0);' type='radio' value='3'/>3 ");
    b.append(
        "<input id='chamber4' name='container0Select' onchange='Container.ui.changeContainerPacBioChamber(this, 0);' type='radio' value='4'/>4 ");
    b.append(
        "<input id='chamber5' name='container0Select' onchange='Container.ui.changeContainerPacBioChamber(this, 0);' type='radio' value='5'/>5 ");
    b.append(
        "<input id='chamber6' name='container0Select' onchange='Container.ui.changeContainerPacBioChamber(this, 0);' type='radio' value='6'/>6 ");
    b.append(
        "<input id='chamber7' name='container0Select' onchange='Container.ui.changeContainerPacBioChamber(this, 0);' type='radio' value='7'/>7 ");
    b.append(
        "<input id='chamber8' name='container0Select' onchange='Container.ui.changeContainerPacBioChamber(this, 0);' type='radio' value='8'/>8<br/>");

    b.append("<div id='containerdiv0'> </div>");
    b.append("</div>");
    return JSONUtils.SimpleJSONResponse(b.toString());
  }

  public JSONObject changePartition(HttpSession session, JSONObject json) {
    if (json.has("platform")) {
      String platform = json.getString("platform");
      PlatformType pt = PlatformType.get(platform);
      if (pt != null) {
        if (pt.equals(PlatformType.ILLUMINA)) {
          return changePlatformTypePartition(PlatformType.ILLUMINA, session, json);
        } else if (pt.equals(PlatformType.LS454)) {
          return changePlatformTypePartition(PlatformType.LS454, session, json);
        } else if (pt.equals(PlatformType.SOLID)) {
          return changePlatformTypePartition(PlatformType.SOLID, session, json);
        } else if (pt.equals(PlatformType.PACBIO)) {
          return changePlatformTypePartition(PlatformType.PACBIO, session, json);
        } else {
          return JSONUtils.SimpleJSONError("Unrecognised platform type: " + platform);
        }
      }
    }
    return JSONUtils.SimpleJSONError("No platform specified");
  }

  public JSONObject changePlatformTypePartition(PlatformType platformType, HttpSession session, JSONObject json) {
    String partitionsLabel;
    if (platformType.equals(PlatformType.ILLUMINA)) {
      partitionsLabel = "numLanes";
    } else if (platformType.equals(PlatformType.LS454) || platformType.equals(PlatformType.SOLID)
        || platformType.equals(PlatformType.PACBIO)) {
      partitionsLabel = "numChambers";
    } else {
      return JSONUtils.SimpleJSONError("Unknown platform; cannot derive label.");
    }
    int numPartitions = json.getInt(partitionsLabel);
    int container = json.getInt("container");

    StringBuilder b = new StringBuilder();
    b.append("<i class='italicInfo'>Click in a " + platformType.getPartitionName()
        + " box to beep/type in barcodes, or double click a pool on the right to sequentially add pools to the "
        + platformType.getContainerName() + "</i>");
    b.append("<table class='in'>");
    b.append("<th>" + platformType.getPartitionName() + " No.</th>");
    b.append("<th>Pool</th>");

    SequencerPartitionContainer<SequencerPoolPartition> lf = (SequencerPartitionContainer<SequencerPoolPartition>) session
        .getAttribute("container_" + json.getString("container_cId"));
    lf.setPartitionLimit(numPartitions);
    lf.initEmptyPartitions();
    session.setAttribute("container_" + json.getString("container_cId"), lf);

    for (int i = 0; i < numPartitions; i++) {
      b.append("<tr><td>" + (i + 1) + "</td>");
      b.append("<td width='90%'><div id='p_div_" + container + "-" + i
          + "' class='elementListDroppableDiv'><ul class='runPartitionDroppable' bind='partitions[" + i + "].pool' partition='" + i
          + "' ondblclick='Container.partition.populatePartition(this);'>");
      b.append("</ul></div></td>");
      b.append("</tr>");
    }
    b.append("</table>");

    return JSONUtils.SimpleJSONResponse(b.toString());
  }

  public JSONObject getPoolByBarcode(HttpSession session, JSONObject json) {
    String barcode = json.getString("barcode");
    int partition = json.getInt("partition");

    try {
      Pool p = requestManager.getPoolByBarcode(barcode);
      // Base64-encoded string, most likely a barcode image beeped in. decode and search
      if (p == null) {
        p = requestManager.getPoolByBarcode(new String(Base64.decodeBase64(barcode)));
      }
      // if pool still can't be found, return error
      if (p == null) {
        return JSONUtils.SimpleJSONError("Cannot find a pool with barcode " + barcode);
      }
      SequencerPartitionContainer<SequencerPoolPartition> lf = (SequencerPartitionContainer<SequencerPoolPartition>) session
          .getAttribute("container_" + json.getString("container_cId"));
      if (lf.getPlatform().getPlatformType().equals(p.getPlatformType())) {
        return JSONUtils.JSONObjectResponse("html", poolHtml(p, partition));
      } else {
        return JSONUtils.JSONObjectResponse("err", "Error: pool platform does not match container platform");
      }
    } catch (IOException e) {
      log.error("no such pool", e);
      return JSONUtils.JSONObjectResponse("err", "Error: no such pool");
    }
  }

  public JSONObject checkPoolExperiment(HttpSession session, JSONObject json) {
    try {
      String partition = json.getString("partition");
      Long poolId = json.getLong("poolId");
      Pool p = requestManager.getPoolById(poolId);
      StringBuilder sb = new StringBuilder();

      Set<Project> pooledProjects = new HashSet<>();

      if (p.getExperiments().size() != 0) {
        // check if each poolable has been in a study for this pool already
        Collection<Dilution> ds = p.getPoolableElements();
        for (Dilution d : ds) {
          pooledProjects.add(d.getLibrary().getSample().getProject());
        }

        for (Experiment poolExp : p.getExperiments()) {
          Project expProject = poolExp.getStudy().getProject();
          if (pooledProjects.contains(expProject)) {
            pooledProjects.remove(expProject);
          }
        }
      } else {
        Collection<Dilution> ds = p.getPoolableElements();
        for (Dilution d : ds) {
          pooledProjects.add(d.getLibrary().getSample().getProject());
        }
      }
      sb.append("<div style='float:left; clear:both'>");
      for (Project project : pooledProjects) {
        sb.append("<div id='studySelectDiv" + partition + "_" + project.getProjectId() + "'>");
        sb.append(project.getAlias() + ": <select name='poolStudies" + partition + "_" + project.getProjectId() + "' id='poolStudies"
            + partition + "_" + project.getProjectId() + "'>");
        Collection<Study> studies = requestManager.listAllStudiesByProjectId(project.getProjectId());
        if (studies.isEmpty()) {
          return JSONUtils.SimpleJSONError("No studies available on project " + project.getName()
              + ". At least one study must be available for each project associated with this Pool.");
        } else {
          for (Study s : studies) {
            sb.append("<option value='" + s.getId() + "'>" + s.getName() + " - " + s.getStudyType() + "</option>");
          }
        }
        sb.append("</select>");
        sb.append("<input id='studySelectButton-" + partition + "_" + p.getId()
            + "' type='button' onclick=\"Container.partition.selectContainerStudy('" + partition + "', " + p.getId() + ","
            + project.getProjectId() + ");\" class=\"ui-state-default ui-corner-all\" value='Select Study'/>");
        sb.append("</div><br/>");
      }
      sb.append("</div>");

      return JSONUtils.JSONObjectResponse("html", sb.toString());
    } catch (Exception e) {
      log.error("check pool experiment", e);
      return JSONUtils.SimpleJSONError(e.getMessage());
    }
  }

  private String poolHtml(Pool p, int partition) {
    StringBuilder b = new StringBuilder();
    try {
      b.append(
          "<div style='position:relative' onMouseOver='this.className=\"dashboardhighlight\"' onMouseOut='this.className=\"dashboard\"' class='dashboard'>");
      if (isStringEmptyOrNull(p.getAlias())) {
        b.append("<div style=\"float:left\"><b>" + p.getName() + " : " + p.getCreationDate() + "</b><br/>");
      } else {
        b.append("<div style=\"float:left\"><b>" + p.getName() + " (" + p.getAlias() + ") : " + p.getCreationDate() + "</b><br/>");
      }

      Collection<Dilution> ds = p.getPoolableElements();
      Set<Project> pooledProjects = new HashSet<>();
      for (Dilution d : ds) {
        pooledProjects.add(d.getLibrary().getSample().getProject());
        b.append("<span>" + d.getName() + " (" + d.getLibrary().getSample().getProject().getAlias() + ") : "
            + d.getConcentration() + " " + d.getUnits() + "</span><br/>");
      }

      b.append("<br/><i>");
      Collection<Experiment> exprs = p.getExperiments();
      for (Experiment e : exprs) {
        b.append("<span>" + e.getStudy().getProject().getAlias() + "(" + e.getName() + ": " + p.getPoolableElements().size()
            + " dilutions)</span><br/>");
      }
      b.append("</i>");

      if (p.getExperiments().size() == 0) {
        b.append("<div style='float:left; clear:both'>");
        for (Project project : pooledProjects) {
          b.append("<div id='studySelectDiv" + partition + "_" + project.getProjectId() + "'>");
          b.append(project.getAlias() + ": <select name='poolStudies" + partition + "_" + project.getProjectId() + "' id='poolStudies"
              + partition + "_" + project.getProjectId() + "'>");
          Collection<Study> studies = requestManager.listAllStudiesByProjectId(project.getProjectId());
          if (studies.isEmpty()) {
            throw new Exception("No studies available on project " + project.getName()
                + ". At least one study must be available for each project associated with this Pool.");
          } else {
            for (Study s : studies) {
              b.append("<option value='" + s.getId() + "'>" + s.getAlias() + " (" + s.getName() + " - " + s.getStudyType() + ")</option>");
            }
          }
          b.append("</select>");
          b.append("<input id='studySelectButton-" + partition + "_" + p.getId()
              + "' type='button' onclick=\"Container.partition.selectContainerStudy('" + partition + "', " + p.getId() + ","
              + project.getProjectId() + ");\" class=\"ui-state-default ui-corner-all\" value='Select Study'/>");
          b.append("</div><br/>");
        }
        b.append("</div>");
      }

      b.append(
          "<input type='hidden' name='partitions[" + partition + "].pool' id='pId" + p.getId() + "' value='" + p.getId() + "'/></div>");
      b.append("<div style='position: absolute; bottom: 0; right: 0; font-size: 24px; font-weight: bold; color:#BBBBBB'>"
          + p.getPlatformType().getKey() + "</div>");
      b.append(
          "<span style='position: absolute; top: 0; right: 0;' onclick='Container.pool.confirmPoolRemove(this);' class='float-right ui-icon ui-icon-circle-close'></span>");
      b.append("</div>");
    } catch (Exception e) {
      log.error("cannot get studies for pool", e);
      return "Cannot get studies for pool: " + e.getMessage();
    }

    return b.toString();
  }

  public JSONObject selectStudyForPool(HttpSession session, JSONObject json) {
    try {
      Long poolId = json.getLong("poolId");
      Pool p = requestManager.getPoolById(poolId);
      if (p == null) {
        throw new Exception("Could not retrieve pool: " + poolId);
      }

      Long studyId = json.getLong("studyId");
      Study s = requestManager.getStudyById(studyId);
      if (s == null) {
        throw new Exception("Could not retrieve study: " + studyId);
      }

      Long platformId = json.getLong("platformId");
      Platform platform = requestManager.getPlatformById(platformId);
      if (platform == null) {
        throw new Exception("Could not retrieve Platform:" + platformId);
      }

      StringBuilder sb = new StringBuilder();

      Experiment e = dataObjectFactory.getExperiment();
      e.setAlias("EXP_AUTOGEN_" + s.getName() + "_" + s.getStudyType() + "_" + (s.getExperiments().size() + 1));
      e.setTitle(s.getProject().getName() + " " + platform.getPlatformType().getKey() + " " + s.getStudyType() + " experiment (Auto-gen)");
      e.setDescription(s.getProject().getAlias());
      e.setPlatform(platform);
      e.setStudy(s);
      e.setSecurityProfile(s.getSecurityProfile());

      try {
        e.setLastModifier(securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName()));
        p.addExperiment(e);
        requestManager.saveExperiment(e);
      } catch (MalformedExperimentException e1) {
        log.error("failed to save experiment", e1);
        return JSONUtils.SimpleJSONError("Failed to save experiment: " + e1.getMessage());
      }

      sb.append("<i>");
      sb.append(
          "<span>" + s.getProject().getAlias() + " (" + e.getName() + ": " + p.getPoolableElements().size() + " dilutions)</span><br/>");
      sb.append("</i>");

      return JSONUtils.JSONObjectResponse("html", sb.toString());
    } catch (Exception e) {
      log.error("select study for pool", e);
      return JSONUtils.SimpleJSONError("Failed " + e.getMessage());
    }
  }

  public JSONObject lookupContainer(HttpSession session, JSONObject json) {
    if (json.has("barcode") && !isStringEmptyOrNull(json.getString("barcode"))) {
      try {
        String barcode = json.getString("barcode");
        Collection<SequencerPartitionContainer<SequencerPoolPartition>> fs = requestManager
            .listSequencerPartitionContainersByBarcode(barcode);
        if (!fs.isEmpty()) {
          JSONObject confirm = new JSONObject();
          StringBuilder sb = new StringBuilder();
          if (fs.size() == 1) {
            // replace container div
            SequencerPartitionContainer<SequencerPoolPartition> f = new ArrayList<>(fs).get(0);
            sb.append("<table class='in'>");
            sb.append("<th>Lane No.</th>");
            sb.append("<th>Pool</th>");

            if (f.getPartitions().isEmpty()) {
              // something went wrong previously. a saved container shouldn't have empty partitions - recreate
              json.put("platform", f.getPlatform().getPlatformType().getKey());

              session.setAttribute("container_" + json.getString("container_cId"), f);

              // reset container
              changeContainer(session, json);
            } else if (f.getPartitions() == null) {
              // something went wrong previously. a saved container shouldn't have null partition set - recreate
              f.setPartitions(new AutoPopulatingList<SequencerPoolPartition>(PartitionImpl.class));
              json.put("platform", f.getPlatform().getPlatformType().getKey());

              session.setAttribute("container_" + json.getString("container_cId"), f);

              // reset container
              changeContainer(session, json);
            }

            for (SequencerPoolPartition p : f.getPartitions()) {
              sb.append("<tr>");
              sb.append("<td>" + p.getPartitionNumber() + "</td>");
              sb.append("<td width='90%'>");
              if (p.getPool() != null) {
                confirm.put(p.getPartitionNumber(), p.getPool().getName());

                sb.append("<ul partition='" + (p.getPartitionNumber() - 1) + "' bind='partitions[" + (p.getPartitionNumber() - 1)
                    + "].pool' class='runPartitionDroppable'>");
                sb.append("<div class='dashboard'>");
                sb.append(p.getPool().getName());
                sb.append("(" + p.getPool().getCreationDate() + ")<br/>");
                sb.append("<span style='font-size:8pt'>");
                if (!p.getPool().getExperiments().isEmpty()) {
                  sb.append("<i>");
                  for (Experiment e : p.getPool().getExperiments()) {
                    sb.append(e.getStudy().getProject().getAlias() + " (" + e.getName() + ": " + p.getPool().getPoolableElements().size()
                        + " dilutions)<br/>");
                  }
                  sb.append("</i>");
                  sb.append("<input type='hidden' name='partitions[" + (p.getPartitionNumber() - 1) + "].pool' id='pId"
                      + (p.getPartitionNumber() - 1) + "' value='" + p.getPool().getId() + "'/>");
                } else {
                  sb.append("<i>No experiment linked to this pool</i>");
                }
                sb.append("</span>");
                sb.append("</div>");
                sb.append("</ul>");
              } else {
                confirm.put(p.getPartitionNumber(), "Empty");

                sb.append("<div id='p_div-" + (p.getPartitionNumber() - 1) + "' class='elementListDroppableDiv'>");
                sb.append("<ul class='runPartitionDroppable' bind='partitions[" + (p.getPartitionNumber() - 1) + "].pool' partition='"
                    + (p.getPartitionNumber() - 1) + "' ondblclick='Container.partition.populatePartition(this);'></ul>");
                sb.append("</div>");
              }
              sb.append("</td>");
              sb.append("</tr>");
            }
            sb.append("</table>");

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("html", sb.toString());
            responseMap.put("barcode", f.getIdentificationBarcode());
            responseMap.put("containerId", f.getId());
            responseMap.put("verify", confirm);
            return JSONUtils.JSONObjectResponse(responseMap);
          } else {
            // choose container
            return JSONUtils.SimpleJSONError("Multiple containers found with serial number " + barcode);
          }
        } else {
          return JSONUtils.JSONObjectResponse("error", "No containers with this serial number.");
        }
      } catch (IOException e) {
        log.error("unable to lookup serial number", e);
        return JSONUtils.JSONObjectResponse("error", "Unable to lookup serial number.");
      }
    } else {
      return JSONUtils.SimpleJSONError("Please supply a serial number to lookup.");
    }
  }

  public JSONObject listSequencePartitionContainersDataTable(HttpSession session, JSONObject json) {
    try {
      JSONObject j = new JSONObject();
      JSONArray jsonArray = new JSONArray();
      for (SequencerPartitionContainer<SequencerPoolPartition> spc : requestManager.listAllSequencerPartitionContainers()) {
        String run = "";
        String sequencer = "";
        if (spc.getRun() != null) {
          run = TableHelper.hyperLinkify("/miso/run/" + spc.getRun().getId(), spc.getRun().getAlias());
          if (spc.getRun().getSequencerReference() != null) {
            sequencer = TableHelper.hyperLinkify("/miso/sequencer/" + spc.getRun().getSequencerReference().getId(),
                spc.getRun().getSequencerReference().getPlatform().getNameAndModel());
          }
        }
        String identificationBarcode = (isStringEmptyOrNull(spc.getIdentificationBarcode()) ? "Unknown Barcode"
            : spc.getIdentificationBarcode());

        JSONArray inner = new JSONArray();
        inner.add(TableHelper.hyperLinkify("/miso/container/" + spc.getId(), identificationBarcode));
        inner.add(
            spc.getPlatform() != null && spc.getPlatform().getPlatformType() != null ? spc.getPlatform().getPlatformType().getKey() : "");
        inner.add(run);
        inner.add(sequencer);

        jsonArray.add(inner);
      }
      j.put("array", jsonArray);
      return j;
    } catch (IOException e) {
      log.error("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONObject getContainerLastRun(HttpSession session, JSONObject json) throws IOException {
    if (json.has("containerId")) {
      Long containerId = json.getLong("containerId");
      Run run = requestManager.getLatestRunBySequencerPartitionContainerId(containerId);
      if (run != null && run.getSequencerReference() != null) {
        return JSONUtils.SimpleJSONResponse(run.getSequencerReference().getName());
      } else {
        return JSONUtils.SimpleJSONResponse("");
      }
    } else {
      return JSONUtils.SimpleJSONError("No containerId specified");
    }
  }

  public JSONObject removePoolFromPartition(HttpSession session, JSONObject json) {
    if (json.has("container_cId") && json.has("partitionNum")) {
      Integer partitionNum = json.getInt("partitionNum");

      SequencerPartitionContainer<SequencerPoolPartition> lf = (SequencerPartitionContainer<SequencerPoolPartition>) session
          .getAttribute("container_" + json.getString("container_cId"));
      SequencerPoolPartition spp = lf.getPartitionAt(partitionNum);
      spp.setPool(null);
      session.setAttribute("container_" + json.getString("container_cId"), lf);

      return JSONUtils.SimpleJSONResponse("OK");
    } else {
      return JSONUtils.SimpleJSONError("No partitionId specified");
    }
  }

  public JSONObject checkContainer(HttpSession session, JSONObject json) {
    try {
      if (json.has("containerId")) {
        Long containerId = json.getLong("containerId");
        SequencerPartitionContainer container = requestManager.getSequencerPartitionContainerById(containerId);

        if (container.getRun() != null && "Completed".equals(container.getRun().getStatus().getHealth().getKey())) {
          return JSONUtils.SimpleJSONResponse("yes");
        } else {
          return JSONUtils.SimpleJSONResponse("no");
        }

      } else {
        return JSONUtils.SimpleJSONError("No Sequencing Container specified");
      }
    } catch (IOException e) {
      log.error("error getting currently logged in user", e);
      return JSONUtils.SimpleJSONError("Error getting currently logged in user.");
    }
  }

  public JSONObject deleteContainer(HttpSession session, JSONObject json) {
    User user;
    try {
      user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    } catch (IOException e) {
      log.error("error getting currently logged in user", e);
      return JSONUtils.SimpleJSONError("Error getting currently logged in user.");
    }

    if (user != null && user.isAdmin()) {
      if (json.has("containerId")) {
        Long containerId = json.getLong("containerId");
        try {
          SequencerPartitionContainer container = requestManager.getSequencerPartitionContainerById(containerId);
          requestManager.deleteContainer(container);
          return JSONUtils.SimpleJSONResponse("Sequencing Container deleted");
        } catch (IOException e) {
          log.error("cannot delete sequencing container", e);
          return JSONUtils.SimpleJSONError("Cannot delete Sequencing Container: " + e.getMessage());
        }
      } else {
        return JSONUtils.SimpleJSONError("No Sequencing Container specified to delete.");
      }
    } else {
      return JSONUtils.SimpleJSONError("Only admins can delete objects.");
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

  public JSONObject isSerialNumberUnique(HttpSession session, JSONObject json) {
    if (!json.has("serialNumber") || (json.has("serialNumber") && isStringEmptyOrNull(json.getString("serialNumber")))) {
      return JSONUtils.SimpleJSONError("Please supply a serial number to lookup.");
    }

    String serialNumber = json.getString("serialNumber"); // Want to know if a container with this serial number already exists.
    String containerId = json.getString("containerId"); // Id of the container the serial number will be applied to. Might be null.
    Map<String, Object> responseMap = new HashMap<>();
    try {
      Collection<SequencerPartitionContainer<SequencerPoolPartition>> containers = requestManager
          .listSequencerPartitionContainersByBarcode(serialNumber);
      if (containers.isEmpty()) {
        responseMap.put("isSerialNumberUnique", true);
      } else {
        SequencerPartitionContainer<SequencerPoolPartition> container = new ArrayList<>(containers).get(0);

        if (containerId != null && !containerId.equals("null") && Long.valueOf(containerId).longValue() == container.getId()) {
          // The serial number is unique. Lookup returned the container being edited.
          responseMap.put("isSerialNumberUnique", true);
        } else {
          responseMap.put("isSerialNumberUnique", false);
        }
      }
      return JSONUtils.JSONObjectResponse(responseMap);
    } catch (IOException e) {
      String err = String.format("Unable to lookup serial number %s.", serialNumber);
      log.error(err, e);
      responseMap.put("error", err);
      responseMap.put("isSerialNumberUnique", false); // Uniqueness is unknown. Enter the error state to be safe.
      return JSONUtils.JSONObjectResponse(responseMap);
    }

  }
}

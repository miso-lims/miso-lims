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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.google.common.collect.Sets;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractRun;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunQC;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunQCImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.RunProcessingUtils;
import uk.ac.bbsrc.tgac.miso.service.PoolService;
import uk.ac.bbsrc.tgac.miso.service.StudyService;

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
  private PoolService poolService;
  @Autowired
  private MisoFilesManager misoFileManager;
  @Autowired
  private StudyService studyService;

  public void setMisoFileManager(MisoFilesManager misoFileManager) {
    this.misoFileManager = misoFileManager;
  }

  public JSONObject changePlatformType(HttpSession session, JSONObject json) {
    String cId = json.getString("run_cId");
    Run run = (Run) session.getAttribute("run_" + cId);

    String newRuntype = json.getString("platformtype");

    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Long runId = AbstractRun.UNSAVED_ID;

      if (json.has("runId") && !isStringEmptyOrNull(json.getString("runId"))) {
        // edit existing run
        Map<String, Object> responseMap = new HashMap<>();
        runId = Long.parseLong(json.getString("runId"));
        Run storedRun = requestManager.getRunById(runId);
        String storedPlatformType = storedRun.getPlatformType().getKey();

        PlatformType newPt = PlatformType.get(newRuntype);
        if (newPt != null) {
          log.info("STORED: " + newRuntype + " :: " + storedPlatformType);
          if (!newRuntype.equals(storedPlatformType)) {
            run = new RunImpl(user);
            run.setPlatformType(newPt);
            run.setId(storedRun.getId());
          } else {
            run = storedRun;
          }

          session.setAttribute("run_" + cId, run);
          StringBuilder srb = new StringBuilder();
          srb.append("<select name='sequencer' id='sequencerReference' onchange='Run.ui.populateRunOptions(this);'>");
          srb.append("<option value='0' selected='selected'>Please select...</option>");
          for (SequencerReference sr : requestManager.listSequencerReferencesByPlatformType(newPt)) {
            if (sr.isActive()) {
              srb.append(
                  "<option value='" + sr.getId() + "'>" + sr.getName() + " (" + sr.getPlatform().getInstrumentModel() + ")</option>");
            }
          }
          srb.append("</select>");
          responseMap.put("sequencers", srb.toString());
        } else {
          return JSONUtils.SimpleJSONError("Unrecognised PlatformType");
        }
        return JSONUtils.JSONObjectResponse(responseMap);
      } else {
        // new run
        Map<String, Object> responseMap = new HashMap<>();

        PlatformType newPt = PlatformType.get(newRuntype);
        if (newPt != null) {
          StringBuilder srb = new StringBuilder();
          srb.append("<select name='sequencer' id='sequencerReference' onchange='Run.ui.populateRunOptions(this);'>");
          srb.append("<option value='0' selected='selected'>Please select...</option>");
          for (SequencerReference sr : requestManager.listSequencerReferencesByPlatformType(newPt)) {
            if (sr.isActive()) {
              srb.append(
                  "<option value='" + sr.getId() + "'>" + sr.getName() + " (" + sr.getPlatform().getInstrumentModel() + ")</option>");
            }
          }
          srb.append("</select>");
          responseMap.put("sequencers", srb.toString());
        } else {
          return JSONUtils.SimpleJSONError("Unrecognised PlatformType");
        }

        return JSONUtils.JSONObjectResponse(responseMap);
      }
    } catch (IOException e) {
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
      Map<String, Object> responseMap = new HashMap<>();
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());

      Run run = new RunImpl(user);
      run.setPlatformType(pt);
      run.setSequencerReference(sr);

      session.setAttribute("run_" + cId, run);

      return JSONUtils.JSONObjectResponse(responseMap);
    } catch (IOException e) {
      log.error("failed to get run options", e);
      return JSONUtils.SimpleJSONError("Failed to get Run options");
    }
  }

  public String containerInfoHtml(PlatformType platformType) {
    StringBuilder sb = new StringBuilder();
    sb.append("<table class='in'>");
    sb.append(
        "<tr><td>Serial Number:</td><td><button onclick='Run.lookupContainer(this);' type='button' class='right-button ui-state-default ui-corner-all'>Lookup</button><div style='overflow:hidden'>"
            + "<input type='text' id='identificationBarcode' name='identificationBarcode'/><input type='hidden' value='on' name='_identificationBarcode'></div></td></tr>");
    sb.append(
        "<tr><td>Location:</td><td><input type='text' id='locationBarcode' name='locationBarcode'/><input type='hidden' value='on' name='_locationBarcode'></td></tr>");
    sb.append(
        "<tr><td>Validation:</td><td><input type='text' id='validationBarcode' name='validationBarcode'/><input type='hidden' value='on' name='_validationBarcode'></td></tr>");
    sb.append("</table>");
    sb.append("<div id='partitionErrorDiv'> </div>");
    sb.append("<div id='partitionDiv'>");
    return sb.toString();
  }

  public JSONObject getRunQCUsers(HttpSession session, JSONObject json) {
    try {
      Collection<String> users = new HashSet<>();
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      users.add(user.getFullName());

      StringBuilder sb = new StringBuilder();
      for (String name : users) {
        sb.append("<option value='" + name + "'>" + name + "</option>");
      }
      Map<String, Object> map = new HashMap<>();
      map.put("qcUserOptions", sb.toString());
      map.put("runId", json.getString("runId"));
      return JSONUtils.JSONObjectResponse(map);
    } catch (IOException e) {
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
      Map<String, Object> map = new HashMap<>();
      map.put("types", sb.toString());
      return JSONUtils.JSONObjectResponse(map);
    } catch (IOException e) {
      log.error("cannot list all run QC types", e);
    }
    return JSONUtils.SimpleJSONError("Cannot list all Run QC Types");
  }

  public JSONObject getRunQCProcessSelection(HttpSession session, JSONObject json) {
    try {
      StringBuilder sb = new StringBuilder();
      if (json.has("runId") && !isStringEmptyOrNull(json.getString("runId"))) {
        Long runId = Long.parseLong(json.getString("runId"));
        Run r = requestManager.getRunById(runId);

        for (SequencerPartitionContainer f : ((RunImpl) r).getSequencerPartitionContainers()) {
          sb.append("<table class='containerSummary'><tr>");
          for (Partition p : f.getPartitions()) {
            sb.append("<td onclick='Run.qc.toggleProcessPartition(this);' runId='" + r.getId() + "' containerId='" + f.getId()
                + "' partitionNumber='" + p.getPartitionNumber() + "' id='" + r.getId() + "_" + f.getId() + "_" + p.getPartitionNumber()
                + "' class='smallbox'>" + p.getPartitionNumber() + "</td>");
          }
          sb.append("</tr></table>");
        }

        Map<String, Object> map = new HashMap<>();
        map.put("processSelection", sb.toString());
        return JSONUtils.JSONObjectResponse(map);
      }
    } catch (IOException e) {
      log.error("get run QC process selection", e);
    }
    return JSONUtils.SimpleJSONError("Cannot list all Run QC Process Selection");
  }

  public JSONObject addRunQC(HttpSession session, JSONObject json) {
    try {
      for (Object k : json.keySet()) {
        String key = (String) k;
        if (json.get(key) == null || isStringEmptyOrNull(json.getString(key))) {
          return JSONUtils.SimpleJSONError("Please enter a value for '" + key + "'");
        }
      }
      if (json.has("runId") && !isStringEmptyOrNull(json.getString("runId"))) {
        Long runId = Long.parseLong(json.getString("runId"));
        Run run = requestManager.getRunById(runId);

        List<String> processSelections = new ArrayList<>();
        List<Partition> partitionSelections = new ArrayList<>();
        JSONArray a = JSONArray.fromObject(json.getString("processSelection"));
        for (JSONObject s : (Iterable<JSONObject>) a) {
          String id = s.getString("id");
          processSelections.add(id);

          long containerId = Long.parseLong(id.split("_")[1]);
          long partitionNumber = Long.parseLong(id.split("_")[2]);
          SequencerPartitionContainer f = requestManager.getSequencerPartitionContainerById(containerId);
          for (Partition p : f.getPartitions()) {
            if (p.getPartitionNumber() == partitionNumber) {
              p.setSequencerPartitionContainer(f);
              partitionSelections.add(p);
            }
          }
        }

        RunQC newQc = new RunQCImpl();
        newQc.setQcCreator(json.getString("qcCreator"));
        newQc.setQcDate(new SimpleDateFormat("dd/MM/yyyy").parse(json.getString("qcDate")));
        newQc.setQcType(requestManager.getRunQcTypeById(json.getLong("qcType")));
        newQc.setInformation(json.getString("information"));
        newQc.setDoNotProcess(json.getBoolean("doNotProcess"));
        newQc.setPartitionSelections(partitionSelections);
        newQc.setRun(run);
        run.addQc(newQc);

        StringBuilder sb = new StringBuilder();
        sb.append(
            "<tr><th>QCed By</th><th>QC Date</th><th>Method</th><th>Process Selection</th><th>Information</th><th>Do Not Process</th></tr>");
        for (RunQC qc : run.getRunQCs()) {
          sb.append("<tr>");
          sb.append("<td>" + qc.getQcCreator() + "</td>");
          sb.append("<td>" + qc.getQcDate() + "</td>");
          sb.append("<td>" + qc.getQcType().getName() + "</td>");
          sb.append("<td>");

          for (SequencerPartitionContainer f : ((RunImpl) run).getSequencerPartitionContainers()) {
            sb.append("<table class='containerSummary'><tr>");
            for (Partition p : f.getPartitions()) {
              if (processSelections.contains(run.getId() + "_" + f.getId() + "_" + p.getPartitionNumber())) {
                sb.append("<td runId='" + run.getId() + "' containerId='" + f.getId() + "' partitionId='" + p.getId() + "' id='"
                    + qc.getId() + "_" + run.getId() + "_" + f.getId() + "_" + p.getPartitionNumber()
                    + "' class='smallbox partitionOccupied'>" + p.getPartitionNumber() + "</td>");
              } else {
                sb.append("<td runId='" + run.getId() + "' containerId='" + f.getId() + "' partitionId='" + p.getId() + "' id='"
                    + qc.getId() + "_" + run.getId() + "_" + f.getId() + "_" + p.getPartitionNumber() + "' class='smallbox'>"
                    + p.getPartitionNumber() + "</td>");
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
    } catch (Exception e) {
      log.error("Failed to add Run QC to this run: ", e);
      return JSONUtils.SimpleJSONError("Failed to add Run QC to this run: " + e.getMessage());
    }
    return JSONUtils.SimpleJSONError("Cannot add RunQC");
  }

  public JSONObject addContainerByBarcode(HttpSession session, JSONObject json) {
    long runId = json.getLong("runId");
    if (!json.has("barcode") || isStringEmptyOrNull(json.getString("barcode"))) {
      return JSONUtils.SimpleJSONError("Please supply a barcode to lookup.");
    }
    try {
      Run run = requestManager.getRunById(runId);
      String barcode = json.getString("barcode");
      Collection<SequencerPartitionContainer> containers = requestManager
          .listSequencerPartitionContainersByBarcode(barcode);
      if (containers.isEmpty()) {
        return JSONUtils.SimpleJSONError("No containers with this barcode.");
      }
      if (containers.size() > 1) {
        return JSONUtils.SimpleJSONError("Multiple containers with this barcode.");
      }
      SequencerPartitionContainer container = containers.iterator().next();
      if (container.getPlatform().getId() != run.getSequencerReference().getPlatform().getId()) {
        return JSONUtils.SimpleJSONError(String.format("This container is meant for %s but this run came from %s.",
            container.getPlatform().getNameAndModel(), run.getSequencerReference().getPlatform().getNameAndModel()));
      }
      run.addSequencerPartitionContainer(container);
      requestManager.saveRun(run);
      return JSONUtils.SimpleJSONResponse("Success!");
    } catch (IOException e) {
      log.error("unable to lookup barcode", e);
      return JSONUtils.SimpleJSONError("Unable to lookup barcode.");
    }
  }

  public JSONObject generateIlluminaDemultiplexCSV(HttpSession session, JSONObject json) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    Run r = requestManager.getRunById(json.getLong("runId"));
    SequencerPartitionContainer f = requestManager.getSequencerPartitionContainerById(json.getLong("containerId"));
    if (r != null && f != null) {
      String casavaVersion = "1.8.2";
      if (json.has("casavaVersion") && !isStringEmptyOrNull(json.getString("casavaVersion"))) {
        casavaVersion = json.getString("casavaVersion");
      }

      String sheet = RunProcessingUtils.buildIlluminaDemultiplexCSV(r, f, casavaVersion, user.getFullName());

      File out = misoFileManager.getNewFile(Run.class, r.getAlias(), "samplesheet-" + LimsUtils.getSimpleCurrentDate() + ".csv");
      LimsUtils.stringToFile(sheet, out);
      log.debug("SampleSheet for " + r.getAlias() + " written to " + out.getAbsolutePath());

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
      run.setLastModifier(user);
      requestManager.saveRun(run);
    } catch (IOException e) {
      log.error("add run note", e);
      return JSONUtils.SimpleJSONError(e.getMessage());
    }

    return JSONUtils.SimpleJSONResponse("Note saved successfully");
  }

  public JSONObject deleteRunNote(HttpSession session, JSONObject json) {
    Long runId = json.getLong("runId");
    Long noteId = json.getLong("noteId");

    try {
      Run run = requestManager.getRunById(runId);
      requestManager.deleteRunNote(run, noteId);
      return JSONUtils.SimpleJSONResponse("OK");
    } catch (IOException e) {
      log.error("delete run note", e);
      return JSONUtils.SimpleJSONError("Cannot remove note: " + e.getMessage());
    }
  }

  public JSONObject watchRun(HttpSession session, JSONObject json) {
    Long runId = json.getLong("runId");
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Run run = requestManager.getRunById(runId);
      if (!run.getWatchers().contains(user)) {
        requestManager.addRunWatcher(run, user);
      }
      return JSONUtils.SimpleJSONResponse("OK");
    } catch (IOException e) {
      log.error("watch run", e);
    }
    return JSONUtils.SimpleJSONError("Unable to watch run");
  }

  public JSONObject unwatchRun(HttpSession session, JSONObject json) {
    Long runId = json.getLong("runId");
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Run run = requestManager.getRunById(runId);
      if (run.getWatchers().contains(user)) {
        requestManager.removeRunWatcher(run, user);
      }
      return JSONUtils.SimpleJSONResponse("OK");
    } catch (IOException e) {
      log.error("unwatch run", e);
    }
    return JSONUtils.SimpleJSONError("Unable to unwatch run");
  }

  public JSONObject getPoolByBarcode(HttpSession session, JSONObject json) {
    String barcode = json.getString("barcode");
    int container = json.getInt("container");
    int partition = json.getInt("partition");

    RunImpl r = (RunImpl) session.getAttribute("run_" + json.getString("run_cId"));

    try {
      PlatformType pt = json.has("platform") && !isStringEmptyOrNull(json.getString("platform"))
          ? PlatformType.get(json.getString("platform")) : r.getPlatformType();

      Pool p = poolService.getPoolByBarcode(barcode);
      // Base64-encoded string, most likely a barcode image beeped in. decode and search
      if (p == null) {
        p = poolService.getPoolByBarcode(new String(Base64.decodeBase64(barcode)));
      }
      // if pool still can't be found, return error
      if (p == null) {
        return JSONUtils.SimpleJSONError("Cannot find a pool with barcode " + barcode);
      }
      if (p.getPlatformType() != pt) {
        return JSONUtils.SimpleJSONError("Pool with that barcode is " + p.getPlatformType().getKey() + " and not " + pt.getKey());
      }
      List<SequencerPartitionContainer> fs = new ArrayList<>(r.getSequencerPartitionContainers());
      if (!fs.isEmpty()) {
        SequencerPartitionContainer f = fs.get(container);
        if (f.getPlatform().getPlatformType().equals(p.getPlatformType())) {
          return JSONUtils.JSONObjectResponse("html", poolHtml(p, container, partition));
        } else {
          return JSONUtils.JSONObjectResponse("err", "Error: pool platform does not match container platform");
        }
      }
      return JSONUtils.JSONObjectResponse("err", "Error: cannot get containers from this run");
    } catch (IOException e) {
      log.error("no such pool", e);
      return JSONUtils.JSONObjectResponse("err", "Error: no such pool");
    }
  }

  public JSONObject checkPoolExperiment(HttpSession session, JSONObject json) {
    try {
      String partition = json.getString("partition");
      Long poolId = json.getLong("poolId");
      Pool p = poolService.getPoolById(poolId);
      StringBuilder sb = new StringBuilder();
      sb.append("<div style='float:left; clear:both'>");

      Set<Long> skipProjects = Sets.newHashSet();

      for (Experiment poolExp : p.getExperiments()) {
        skipProjects.add(poolExp.getStudy().getProject().getId());
      }

      for (PoolableElementView d : p.getPoolableElementViews()) {
        if (!skipProjects.contains(d.getProjectId())) {
          skipProjects.add(d.getProjectId());
          sb.append("<div id='studySelectDiv" + partition + "_" + d.getProjectId() + "'>");
          sb.append((isStringEmptyOrNull(d.getProjectShortName()) ? d.getProjectAlias() : d.getProjectShortName()));
          sb.append(": <select name='poolStudies" + partition + "_" + d.getProjectId() + "' id='poolStudies"
              + partition + "_" + d.getProjectId() + "'>");
          Collection<Study> studies = studyService.listByProjectId(d.getProjectId());
          if (studies.isEmpty()) {
            return JSONUtils.SimpleJSONError("No studies available on project " + d.getProjectId()
                + ". At least one study must be available for each project associated with this Pool.");
          } else {
            for (Study s : studies) {
              sb.append("<option value='" + s.getId() + "'>" + s.getName() + " - " + s.getAlias() + " (" + s.getStudyType().getName()
                  + ")</option>");
            }
          }
          sb.append("</select>");
          sb.append("<input id='studySelectButton-" + partition + "_" + p.getId() + "' type='button' onclick=\"Run.container.selectStudy('"
              + partition + "', " + p.getId() + "," + d.getProjectId()
              + ");\" class=\"ui-state-default ui-corner-all\" value='Select Study'/>");
          sb.append("</div><br/>");
        }
      }

      sb.append("</div>");

      return JSONUtils.JSONObjectResponse("html", sb.toString());
    } catch (Exception e) {
      log.error("check pool experiment", e);
      return JSONUtils.SimpleJSONError(e.getMessage());
    }
  }

  private String poolHtml(Pool p, int container, int partition) {
    StringBuilder b = new StringBuilder();
    try {
      b.append(
          "<div style='position:relative' onMouseOver='this.className=\"dashboardhighlight\"' onMouseOut='this.className=\"dashboard\"' class='dashboard'>");
      b.append("<div style=\"float:left\"><b>" + p.getName() + " (" + LimsUtils.getDateAsString(p.getCreationDate()) + ")</b><br/>");

      Collection<PoolableElementView> ds = p.getPoolableElementViews();
      for (PoolableElementView d : ds) {
        b.append("<span>" + d.getDilutionName() + " (" + d.getProjectAlias() + ")</span><br/>");
      }

      b.append("<br/><i>");
      Collection<Experiment> exprs = p.getExperiments();
      for (Experiment e : exprs) {
        b.append("<span>" + e.getStudy().getProject().getAlias() + "(" + e.getName() + ": " + p.getPoolableElementViews().size()
            + " dilutions)</span><br/>");
      }
      b.append("</i>");

      if (p.getExperiments().size() == 0) {
        b.append("<div style='float:left; clear:both'>");
        Set<Long> pooledProjects = new HashSet<>();

        for (PoolableElementView d : ds) {
          if (!pooledProjects.contains(d.getProjectId())) {
            pooledProjects.add(d.getProjectId());
            b.append("<div id='studySelectDiv" + partition + "_" + d.getProjectId() + "'>");
            b.append(d.getProjectAlias() + ": <select name='poolStudies" + partition + "_" + d.getProjectId() + "' id='poolStudies"
                + partition + "_" + d.getProjectId() + "'>");
            Collection<Study> studies = studyService.listByProjectId(d.getProjectId());
            if (studies.isEmpty()) {
              throw new Exception(
                  "No studies available on project "
                      + d.getProjectId()
                      + ". At least one study must be available for each project associated with this Pool. Double click to add a different pool");
            } else {
              for (Study s : studies) {
                b.append("<option value='" + s.getId() + "'>" + s.getAlias() + " (" + s.getName() + " - " + s.getStudyType().getName()
                    + ")</option>");
              }
            }
            b.append("</select>");
            b.append("<input type='button' onclick=\"Run.container.selectStudy('" + partition + "', " + p.getId() + ","
                + d.getProjectId() + ");\" class=\"ui-state-default ui-corner-all\" value='Select Study'/>");
            b.append("</div><br/>");
          }
        }
      }
      b.append("</div>");
      b.append("<input type='hidden' name='sequencerPartitionContainers[" + container + "].partitions[" + partition + "].pool' id='pId"
          + p.getId() + "' value='" + p.getId() + "'/></div>");
      b.append("<div style='position: absolute; bottom: 0; right: 0; font-size: 24px; font-weight: bold; color:#BBBBBB'>"
          + p.getPlatformType().getKey() + "</div>");
      b.append(
          "<span style='position: absolute; top: 0; right: 0;' onclick='Run.pool.confirmPoolRemove(this);' class='float-right ui-icon ui-icon-circle-close'></span>");
      b.append("</div>");
    } catch (Exception e) {
      log.error("pool html", e);
      return "Cannot get studies for pool: " + e.getMessage();
    }

    return b.toString();
  }

  public JSONObject deleteRun(HttpSession session, JSONObject json) {
    User user;
    try {
      user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    } catch (IOException e) {
      log.error("delete run", e);
      return JSONUtils.SimpleJSONError("Error getting currently logged in user.");
    }

    if (user != null && user.isAdmin()) {
      if (json.has("runId")) {
        Long runId = json.getLong("runId");
        try {
          requestManager.deleteRun(requestManager.getRunById(runId));
          return JSONUtils.SimpleJSONResponse("Run deleted");
        } catch (IOException e) {
          log.error("delete run", e);
          return JSONUtils.SimpleJSONError("Cannot delete run: " + e.getMessage());
        }
      } else {
        return JSONUtils.SimpleJSONError("No run specified to delete.");
      }
    } else {
      return JSONUtils.SimpleJSONError("Only admins can delete objects.");
    }
  }

  public JSONObject listRunsDataTable(HttpSession session, JSONObject json) {
    try {
      JSONObject j = new JSONObject();
      JSONArray jsonArray = new JSONArray();
      for (Run run : requestManager.listAllRuns()) {
        JSONArray inner = new JSONArray();
        inner.add(TableHelper.hyperLinkify("/miso/run/" + run.getId(), run.getName()));
        inner.add(TableHelper.hyperLinkify("/miso/run/" + run.getId(), run.getAlias()));
        inner.add((run.getStatus() != null && run.getStatus().getHealth() != null ? run.getStatus().getHealth().getKey() : ""));
        inner.add((run.getStatus() != null && run.getStatus().getStartDate() != null
            ? LimsUtils.getDateAsString(run.getStatus().getStartDate()) : ""));
        inner.add((run.getStatus() != null && run.getStatus().getCompletionDate() != null
            ? LimsUtils.getDateAsString(run.getStatus().getCompletionDate()) : ""));
        inner.add((run.getPlatformType() != null ? run.getPlatformType().getKey() : ""));

        jsonArray.add(inner);
      }
      j.put("runsArray", jsonArray);
      return j;
    } catch (IOException e) {
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

  public JSONObject deleteRunContainer(HttpSession session, JSONObject json) {
    long runId = json.getLong("runId");
    long containerId = json.getLong("containerId");

    try {
      Run run = requestManager.getRunById(runId);
      Iterator<SequencerPartitionContainer> it = run.getSequencerPartitionContainers().iterator();
      while (it.hasNext()) {
        SequencerPartitionContainer spc = it.next();
        if (spc.getId() == containerId) {
          it.remove();
        }
      }
      requestManager.saveRun(run);
      return JSONUtils.SimpleJSONResponse("OK");
    } catch (IOException e) {
      log.error("delete run container", e);
      return JSONUtils.SimpleJSONError("Cannot remove container: " + e.getMessage());
    }
  }

}

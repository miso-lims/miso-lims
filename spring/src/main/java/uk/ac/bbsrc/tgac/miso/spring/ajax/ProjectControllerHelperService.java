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
//import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.json.JSONArray;
import org.codehaus.jackson.map.ObjectMapper;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.ac.bbsrc.tgac.miso.core.data.impl.*;
import uk.ac.bbsrc.tgac.miso.core.event.manager.WatchManager;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoPrintException;
import uk.ac.bbsrc.tgac.miso.core.manager.IssueTrackerManager;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.manager.PrintManager;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.service.printing.MisoPrintService;
import uk.ac.bbsrc.tgac.miso.core.service.printing.context.PrintContext;
import uk.ac.bbsrc.tgac.miso.core.util.FormUtils;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
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
public class ProjectControllerHelperService {
  protected static final Logger log = LoggerFactory.getLogger(ProjectControllerHelperService.class);
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private RequestManager requestManager;
  @Autowired
  private IssueTrackerManager issueTrackerManager;
  @Autowired
  private PrintManager<MisoPrintService, Queue<?>> printManager;
  @Autowired
  private MisoFilesManager misoFileManager;
  @Autowired
  private WatchManager watchManager;

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setIssueTrackerManager(IssueTrackerManager issueTrackerManager) {
    this.issueTrackerManager = issueTrackerManager;
  }

  public void setMisoFileManager(MisoFilesManager misoFileManager) {
    this.misoFileManager = misoFileManager;
  }

  public void setPrintManager(PrintManager<MisoPrintService, Queue<?>> printManager) {
    this.printManager = printManager;
  }

  public void setWatchManager(WatchManager watchManager) {
    this.watchManager = watchManager;
  }

  public JSONObject addProjectOverview(HttpSession session, JSONObject json) {
    Long projectId = json.getLong("projectId");
    String principalInvestigator = (String) json.get("principalInvestigator");
    Integer numProposedSamples = json.getInt("numProposedSamples");

    try {
      Project project = requestManager.getProjectById(projectId);
      ProjectOverview overview = new ProjectOverview();
      overview.setNumProposedSamples(numProposedSamples);
      overview.setPrincipalInvestigator(principalInvestigator);
      overview.setProject(project);
      overview.setLocked(false);
      project.getOverviews().add(overview);
      requestManager.saveProjectOverview(overview);
      requestManager.saveProject(project);
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError(e.getMessage());
    }

    return JSONUtils.SimpleJSONResponse("ok");
  }

  public JSONObject addProjectOverviewNote(HttpSession session, JSONObject json) {
    Long overviewId = json.getLong("overviewId");
    String internalOnly = json.getString("internalOnly");
    String text = json.getString("text");

    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      ProjectOverview overview = requestManager.getProjectOverviewById(overviewId);
      Project project = overview.getProject();

      Note note = new Note();

      internalOnly = internalOnly.equals("on") ? "true" : "false";

      note.setInternalOnly(Boolean.parseBoolean(internalOnly));
      note.setText(text);
      note.setOwner(user);
      note.setCreationDate(new Date());
      overview.getNotes().add(note);
      requestManager.saveProjectOverviewNote(overview, note);
      requestManager.saveProject(project);
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError(e.getMessage());
    }

    return JSONUtils.SimpleJSONResponse("ok");
  }

  public JSONObject unlockProjectOverview(HttpSession session, JSONObject json) {
    Long overviewId = json.getLong("overviewId");
    try {
      ProjectOverview overview = requestManager.getProjectOverviewById(overviewId);
      overview.setLocked(false);
      requestManager.saveProjectOverview(overview);
      requestManager.saveProject(overview.getProject());
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError(e.getMessage());
    }
    return JSONUtils.SimpleJSONResponse("ok");
  }

  public JSONObject lockProjectOverview(HttpSession session, JSONObject json) {
    Long overviewId = json.getLong("overviewId");
    try {
      ProjectOverview overview = requestManager.getProjectOverviewById(overviewId);
      overview.setLocked(true);
      requestManager.saveProjectOverview(overview);
      requestManager.saveProject(overview.getProject());
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError(e.getMessage());
    }
    return JSONUtils.SimpleJSONResponse("ok");
  }

  public JSONObject previewIssues(HttpSession session, JSONObject json) {
    if (issueTrackerManager != null) {
      List<JSONObject> issueList = new ArrayList<JSONObject>();
      List<String> errorList = new ArrayList<String>();
      JSONArray issues = JSONArray.fromObject(json.getString("issues"));
      for (JSONObject issueKey : (Iterable<JSONObject>) issues) {
        JSONObject issue = null;
        try {
          issue = issueTrackerManager.getIssue(issueKey.getString("key"));
          if (issue != null) {
            issueList.add(issue);
          }
          else {
            errorList.add(issueKey.getString("key"));
          }
        }
        catch (IOException e) {
          e.printStackTrace();
          errorList.add(issueKey.getString("key"));
        }
      }

      JSONObject j = new JSONObject();
      j.put("validIssues", JSONArray.fromObject(issueList));
      j.put("invalidIssues", JSONArray.fromObject(errorList));
      return j;
    }
    else {
      return JSONUtils.SimpleJSONError("No issue tracker manager available.");
    }
  }

  public JSONObject getIssues(HttpSession session, JSONObject json) {
    if (issueTrackerManager != null) {
      Long projectId = json.getLong("projectId");
      try {
        Project project = requestManager.getProjectById(projectId);
        JSONObject j = new JSONObject();
        if (project != null) {
          List<JSONObject> issueList = new ArrayList<JSONObject>();

          if (project.getIssueKeys() != null) {
            for (String issueKey : project.getIssueKeys()) {
              JSONObject issue = issueTrackerManager.getIssue(issueKey);
              if (issue != null) {
                issueList.add(issue);
              }
            }
            j.put("issues", JSONArray.fromObject(issueList));
          }
        }
        return j;
      }
      catch (IOException e) {
        e.printStackTrace();
        return JSONUtils.SimpleJSONError(e.getMessage());
      }
    }
    else {
      return JSONUtils.SimpleJSONError("No issue tracker manager available.");
    }
  }

  public JSONObject listProjectTrafficLight(HttpSession session, JSONObject json) {
    try {
      JSONObject j = new JSONObject();
      for (Project project : requestManager.listAllProjects()) {
        j.put(project.getProjectId(), checkOverviews(project.getProjectId()));
      }
      return j;
    }
    catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONObject checkOverviewByProjectId(HttpSession session, JSONObject json) {
    try {
      JSONObject j = new JSONObject();
      Long projectId = json.getLong("projectId");
      j.put("response", checkOverviews(projectId));
      return j;
    }
    catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  private String checkOverviews(Long projectId) throws IOException {
    StringBuilder sb = new StringBuilder();
    Collection<ProjectOverview> overviews = requestManager.listAllOverviewsByProjectId(projectId);
    if (overviews.size() > 0) {
      sb.append("<table class=\"overviewSummary\">");
      for (ProjectOverview overview : overviews) {
        sb.append("<tr>");
        sb.append("<td class=\"smallbox" + colorOverviewState(overview.getAllSampleQcPassed()) + "\">SQ</td>");
        sb.append("<td class=\"smallbox" + colorOverviewState(overview.getLibraryPreparationComplete()) + "\">L</td>");
        sb.append("<td class=\"smallbox" + colorOverviewState(overview.getAllLibrariesQcPassed()) + "\">LQ</td>");
        sb.append("<td class=\"smallbox" + colorOverviewState(overview.getAllPoolsConstructed()) + "\">P</td>");
        sb.append("<td class=\"smallbox" + colorOverviewState(overview.getAllRunsCompleted()) + "\">R</td>");
        sb.append("<td class=\"smallbox" + colorOverviewState(overview.getPrimaryAnalysisCompleted()) + "\">A</td>");
        sb.append("</tr>");
      }
      sb.append("</table>");
    }
    else {
      sb.append("No Overview");
    }
    return sb.toString();
  }

  public JSONObject listProjectsDataTable(HttpSession session, JSONObject json) {
    try {
      JSONObject j = new JSONObject();
      JSONArray jsonArray = new JSONArray();
      for (Project project : requestManager.listAllProjects()) {
        jsonArray.add("['" + project.getName() + "','" +
                      project.getAlias() + "','" +
                      project.getDescription() + "','" +
                      project.getProgress().getKey() + "','" +
//                      checkOverviews(project.getProjectId()) + "','" +
                      project.getProjectId() + "','" +
                      "<a href=\"/miso/project/" + project.getId() + "\"><span class=\"ui-icon ui-icon-pencil\"></span></a>" + "']");

      }
      j.put("projectsArray", jsonArray);
      return j;
    }
    catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  private String colorOverviewState(Boolean bool) {
    if (bool) {
      return " partitionOccupied";
    }
    else {
      return "";
    }
  }

  private String checkSamples(Long projectId) throws IOException {
    Collection<Sample> samples = requestManager.listAllSamplesByProjectId(projectId);
    if (samples.size() > 0) {
      int pass = 0;
      for (Sample s : samples) {
        if (s.getQcPassed() != null) {
          if (s.getQcPassed()) {
            pass++;
          }
        }
      }
      if (pass == samples.size()) {
        return "green";
      }
      else {
        return "yellow";
      }
    }
    else {
      return "gray";
    }
  }

  private String checkLibraries(Long projectId) throws IOException {
    int pass = 0;
    Collection<Library> libs = requestManager.listAllLibrariesByProjectId(projectId);
    if (libs.size() > 0) {
      for (Library l : libs) {
        if (l.getLibraryQCs().size() > 0) {
          pass += l.getLibraryQCs().size();
        }
      }
      if (pass >= libs.size()) {
        return "green";
      }
      else {
        return "yellow";
      }
    }
    else {
      return "gray";
    }
  }

  private String checkRuns(Long projectId) throws IOException {
    int pass = 0;
    Collection<Run> runs = requestManager.listAllRunsByProjectId(projectId);
    if (runs.size() > 0) {
      for (Run run : runs) {
        if (run.getStatus() != null && run.getStatus().getHealth() != null && run.getStatus().getHealth().getKey().equals("Completed")) {
          pass++;
        }
      }

      if (pass == runs.size()) {
        return "green";
      }
      else {
        return "yellow";
      }
    }
    else {
      return "gray";
    }
  }


  public JSONObject editProjectTrafficLight(HttpSession session, JSONObject json) {
    Long projectId = json.getLong("projectId");
    StringBuffer b = new StringBuffer();
    try {
      String trafSample = checkSamples(projectId);
      String trafLib = checkLibraries(projectId);
      String trafRun = checkRuns(projectId);

      b.append("<div id=\"projectstatustrafficlight\">\n" +
               "        <table class=\"traf\">\n" +
               "<thead><tr><th width=\"33%\">Samples</th><th width=\"33%\">Libraries</th><th width=\"33%\">Runs</th></tr></thead>" +
               "            <tbody>\n" +
               "            <tr>\n" +
               "                <td width=\"33%\"><img src=\"/styles/images/status/" + trafSample + ".png\"/></td>\n" +
               "                <td width=\"33%\"><img src=\"/styles/images/status/" + trafLib + ".png\"/></td>\n" +
               "                <td width=\"33%\"><img src=\"/styles/images/status/" + trafRun + ".png\"/></td>\n" +
               "            </tr>\n" +
               "            </tbody>\n" +
               "        </table>\n" +
               "</div>");

      return JSONUtils.JSONObjectResponse("html", b.toString());
    }
    catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONObject printAllSampleBarcodes(HttpSession session, JSONObject json) {
    Long projectId = json.getLong("projectId");
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());

      String serviceName = null;
      if (json.has("serviceName")) {
        serviceName = json.getString("serviceName");
      }

      MisoPrintService<File, Barcodable, PrintContext<File>> mps = null;
      if (serviceName == null) {
        Collection<MisoPrintService> services = printManager.listPrintServicesByBarcodeableClass(Sample.class);
        if (services.size() == 1) {
          mps = services.iterator().next();
        }
        else {
          return JSONUtils.SimpleJSONError("No serviceName specified, but more than one available service able to print this barcode type.");
        }
      }
      else {
        mps = printManager.getPrintService(serviceName);
      }

      Queue<File> thingsToPrint = new LinkedList<File>();
      Collection<Sample> samples = requestManager.listAllSamplesByProjectId(projectId);
      for (Sample sample : samples) {
        //autosave the barcode if none has been previously generated
        if (sample.getIdentificationBarcode() == null || "".equals(sample.getIdentificationBarcode())) {
          requestManager.saveSample(sample);
        }
        File f = mps.getLabelFor(sample);
        if (f != null) thingsToPrint.add(f);
      }
      PrintJob pj = printManager.print(thingsToPrint, mps.getName(), user);
      return JSONUtils.SimpleJSONResponse("Job " + pj.getJobId() + " : Barcodes printed.");
    }
    catch (MisoPrintException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Failed to print barcodes: " + e.getMessage());
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Failed to print barcodes: " + e.getMessage());
    }
  }

  public JSONObject printSelectedSampleBarcodes(HttpSession session, JSONObject json) {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      JSONArray ss = JSONArray.fromObject(json.getString("samples"));

      String serviceName = null;
      if (json.has("serviceName")) {
        serviceName = json.getString("serviceName");
      }

      MisoPrintService<File, Barcodable, PrintContext<File>> mps = null;
      if (serviceName == null) {
        Collection<MisoPrintService> services = printManager.listPrintServicesByBarcodeableClass(Sample.class);
        if (services.size() == 1) {
          mps = services.iterator().next();
        }
        else {
          return JSONUtils.SimpleJSONError("No serviceName specified, but more than one available service able to print this barcode type.");
        }
      }
      else {
        mps = printManager.getPrintService(serviceName);
      }

      Queue<File> thingsToPrint = new LinkedList<File>();
      for (JSONObject p : (Iterable<JSONObject>) ss) {
        Long sampleId = p.getLong("sampleId");
        Sample sample = requestManager.getSampleById(sampleId);
        if (sample.getIdentificationBarcode() == null || "".equals(sample.getIdentificationBarcode())) {
          requestManager.saveSample(sample);
        }
        File f = mps.getLabelFor(sample);
        if (f != null) thingsToPrint.add(f);
      }
      PrintJob pj = printManager.print(thingsToPrint, mps.getName(), user);
      return JSONUtils.SimpleJSONResponse("Job " + pj.getJobId() + " : Barcodes printed.");
    }
    catch (MisoPrintException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Failed to print barcodes: " + e.getMessage());
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Failed to print barcodes: " + e.getMessage());
    }
  }

  public JSONObject printAllLibraryBarcodes(HttpSession session, JSONObject json) {
    Long projectId = json.getLong("projectId");
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());

      String serviceName = null;
      if (json.has("serviceName")) {
        serviceName = json.getString("serviceName");
      }

      MisoPrintService<File, Barcodable, PrintContext<File>> mps = null;
      if (serviceName == null) {
        Collection<MisoPrintService> services = printManager.listPrintServicesByBarcodeableClass(Library.class);
        if (services.size() == 1) {
          mps = services.iterator().next();
        }
        else {
          return JSONUtils.SimpleJSONError("No serviceName specified, but more than one available service able to print this barcode type.");
        }
      }
      else {
        mps = printManager.getPrintService(serviceName);
      }

      Queue<File> thingsToPrint = new LinkedList<File>();
      Collection<Library> libraries = requestManager.listAllLibrariesByProjectId(projectId);
      for (Library library : libraries) {
        //autosave the barcode if none has been previously generated
        if (library.getIdentificationBarcode() == null || "".equals(library.getIdentificationBarcode())) {
          requestManager.saveLibrary(library);
        }
        File f = mps.getLabelFor(library);
        if (f != null) thingsToPrint.add(f);
      }
      PrintJob pj = printManager.print(thingsToPrint, mps.getName(), user);
      return JSONUtils.SimpleJSONResponse("Job " + pj.getJobId() + " : Barcodes printed.");
    }
    catch (MisoPrintException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Failed to print barcodes: " + e.getMessage());
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Failed to print barcodes: " + e.getMessage());
    }
  }

  public JSONObject printSelectedLibraryBarcodes(HttpSession session, JSONObject json) {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      JSONArray ss = JSONArray.fromObject(json.getString("libraries"));

      String serviceName = null;
      if (json.has("serviceName")) {
        serviceName = json.getString("serviceName");
      }

      MisoPrintService<File, Barcodable, PrintContext<File>> mps = null;
      if (serviceName == null) {
        Collection<MisoPrintService> services = printManager.listPrintServicesByBarcodeableClass(Library.class);
        if (services.size() == 1) {
          mps = services.iterator().next();
        }
        else {
          return JSONUtils.SimpleJSONError("No serviceName specified, but more than one available service able to print this barcode type.");
        }
      }
      else {
        mps = printManager.getPrintService(serviceName);
      }

      Queue<File> thingsToPrint = new LinkedList<File>();
      for (JSONObject p : (Iterable<JSONObject>) ss) {
        Long libraryId = p.getLong("libraryId");
        Library library = requestManager.getLibraryById(libraryId);
        //autosave the barcode if none has been previously generated
        if (library.getIdentificationBarcode() == null || "".equals(library.getIdentificationBarcode())) {
          requestManager.saveLibrary(library);
        }
        File f = mps.getLabelFor(library);
        if (f != null) thingsToPrint.add(f);
      }
      PrintJob pj = printManager.print(thingsToPrint, mps.getName(), user);
      return JSONUtils.SimpleJSONResponse("Job " + pj.getJobId() + " : Barcodes printed.");
    }
    catch (MisoPrintException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Failed to print barcodes: " + e.getMessage());
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Failed to print barcodes: " + e.getMessage());
    }
  }

  public JSONObject printAllLibraryDilutionBarcodes(HttpSession session, JSONObject json) {
    Long projectId = json.getLong("projectId");
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());

      String serviceName = null;
      if (json.has("serviceName")) {
        serviceName = json.getString("serviceName");
      }

      MisoPrintService<File, Barcodable, PrintContext<File>> mps = null;
      if (serviceName == null) {
        Collection<MisoPrintService> services = printManager.listPrintServicesByBarcodeableClass(Dilution.class);
        if (services.size() == 1) {
          mps = services.iterator().next();
        }
        else {
          return JSONUtils.SimpleJSONError("No serviceName specified, but more than one available service able to print this barcode type.");
        }
      }
      else {
        mps = printManager.getPrintService(serviceName);
      }

      Queue<File> thingsToPrint = new LinkedList<File>();
      Collection<LibraryDilution> libraryDilutions = requestManager.listAllLibraryDilutionsByProjectId(projectId);
      for (LibraryDilution libraryDilution : libraryDilutions) {
        //autosave the barcode if none has been previously generated
        if (libraryDilution.getIdentificationBarcode() == null || "".equals(libraryDilution.getIdentificationBarcode())) {
          requestManager.saveLibraryDilution(libraryDilution);
        }
        File f = mps.getLabelFor(libraryDilution);
        if (f != null) thingsToPrint.add(f);
      }
      PrintJob pj = printManager.print(thingsToPrint, mps.getName(), user);
      return JSONUtils.SimpleJSONResponse("Job " + pj.getJobId() + " : Barcodes printed.");
    }
    catch (MisoPrintException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Failed to print barcodes: " + e.getMessage());
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Failed to print barcodes: " + e.getMessage());
    }
  }

  public JSONObject printSelectedLibraryDilutionBarcodes(HttpSession session, JSONObject json) {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      JSONArray ss = JSONArray.fromObject(json.getString("dilutions"));

      String serviceName = null;
      if (json.has("serviceName")) {
        serviceName = json.getString("serviceName");
      }

      MisoPrintService<File, Barcodable, PrintContext<File>> mps = null;
      if (serviceName == null) {
        Collection<MisoPrintService> services = printManager.listPrintServicesByBarcodeableClass(Dilution.class);
        if (services.size() == 1) {
          mps = services.iterator().next();
        }
        else {
          return JSONUtils.SimpleJSONError("No serviceName specified, but more than one available service able to print this barcode type.");
        }
      }
      else {
        mps = printManager.getPrintService(serviceName);
      }

      Queue<File> thingsToPrint = new LinkedList<File>();
      for (JSONObject p : (Iterable<JSONObject>) ss) {
        Long dilutionId = p.getLong("dilutionId");
        LibraryDilution libraryDilution = requestManager.getLibraryDilutionById(dilutionId);
        //autosave the barcode if none has been previously generated
        if (libraryDilution.getIdentificationBarcode() == null || "".equals(libraryDilution.getIdentificationBarcode())) {
          requestManager.saveLibraryDilution(libraryDilution);
        }
        File f = mps.getLabelFor(libraryDilution);
        if (f != null) thingsToPrint.add(f);
      }
      PrintJob pj = printManager.print(thingsToPrint, mps.getName(), user);
      return JSONUtils.SimpleJSONResponse("Job " + pj.getJobId() + " : Barcodes printed.");
    }
    catch (MisoPrintException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Failed to print barcodes: " + e.getMessage());
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Failed to print barcodes: " + e.getMessage());
    }
  }

  public JSONObject generateSampleDeliveryForm(HttpSession session, JSONObject json) {
    Boolean plate = false;
    if ("yes".equals(json.getString("plate"))){
        plate = true;
    }
    Long projectId = json.getLong("projectId");
    List<Sample> samples = new ArrayList<Sample>();
    if (json.has("samples")) {
      try {
        JSONArray a = JSONArray.fromObject(json.get("samples"));
        for (JSONObject j : (Iterable<JSONObject>) a) {
          samples.add(requestManager.getSampleById(j.getLong("sampleId")));
        }
        File f = misoFileManager.getNewFile(
                Project.class,
                projectId.toString(),
                "SampleInformationForm-" + LimsUtils.getCurrentDateAsString() + ".odt");

        FormUtils.createSampleDeliveryForm(samples, f, plate);
        return JSONUtils.SimpleJSONResponse("" + f.getName().hashCode());
      }
      catch (Exception e) {
        e.printStackTrace();
        return JSONUtils.SimpleJSONError("Failed to create sample delivery form: " + e.getMessage());
      }
    }
    else {
      return JSONUtils.SimpleJSONError("No samples selected to generate delivery form");
    }
  }

  public JSONObject downloadBulkSampleInputForm(HttpSession session, JSONObject json) {
    if (json.has("projectId") && json.has("documentFormat")) {
      Long projectId = json.getLong("projectId");
      String documentFormat = json.getString("documentFormat");
      try {
        File f = misoFileManager.getNewFile(
                Project.class,
                projectId.toString(),
                "BulkInputForm-" + LimsUtils.getCurrentDateAsString() + "." + documentFormat);
        FormUtils.createSampleInputSpreadsheet(requestManager.getProjectById(projectId).getSamples(), f);
        return JSONUtils.SimpleJSONResponse("" + f.getName().hashCode());
      }
      catch (Exception e) {
        e.printStackTrace();
        return JSONUtils.SimpleJSONError("Failed to get bulk input form: " + e.getMessage());
      }
    }
    else {
      return JSONUtils.SimpleJSONError("Missing project ID or document format supplied.");
    }
  }

  public JSONObject visualiseBulkSampleInputForm(HttpSession session, JSONObject json) {
    JSONObject samplelist = (JSONObject) session.getAttribute("bulksamples");
    if (samplelist == null) {
      JSONObject error = (JSONObject) session.getAttribute("bulkerror");
      if (error != null) {
        return JSONUtils.SimpleJSONError("Failed to get bulk input sheet from session: " + error.getString("bulkerror"));
      }
      return JSONUtils.SimpleJSONError("Failed to get bulk input sheet from session.");
    }

    JSONArray samples = samplelist.getJSONArray("bulksamples");
    if (samples != null) {
      StringBuilder sb = new StringBuilder();
      sb.append("<div style='width: 100%;'>");
      ObjectMapper mapper = new ObjectMapper();
      for (JSONObject sam : (Iterable<JSONObject>) samples) {
        Sample s = null;
        try {
          s = mapper.readValue(sam.toString(), SampleImpl.class);
          sb.append("<a class=\"dashboardresult\" href=\"/miso/sample/" + s.getId() + "\"><div onMouseOver=\"this.className=&#39dashboardhighlight&#39\" onMouseOut=\"this.className=&#39dashboard&#39\" class=\"dashboard\">");
          sb.append("Name: <b>" + s.getName() + "</b><br/>");
          sb.append("Alias: <b>" + s.getAlias() + "</b><br/>");

          Set<Pool> pools = new HashSet<Pool>();

          for (Library l : s.getLibraries()) {
            if (!l.getLibraryDilutions().isEmpty()) {
              for (Dilution ld : l.getLibraryDilutions()) {
                if (!ld.getPools().isEmpty()) {
                  pools.addAll(ld.getPools());
                }
              }
            }
          }

          if (!pools.isEmpty()) {
            sb.append("Pools: <ul>");
            for (Pool p : pools) {
              sb.append("<li>")
                .append(p.getName())
                .append(" (")
                .append(p.getAlias())
                .append(") - ")
                .append(p.getDilutions().size())
                .append(" dilutions<li>");
            }
            sb.append("</ul><br/>");
          }

          sb.append("</div></a>");
        }
        catch (IOException e) {
          e.printStackTrace();
        }
        finally {
          sb.append("</div>");
          session.removeAttribute("bulksamples");
        }
      }

      return JSONUtils.SimpleJSONResponse(sb.toString());
    }
    else {
      return JSONUtils.SimpleJSONError("Failed to get bulk input sheet from session.");
    }
  }

  public JSONObject downloadPlateInputForm(HttpSession session, JSONObject json) {
    if (json.has("projectId") && json.has("documentFormat")) {
      Long projectId = json.getLong("projectId");
      String documentFormat = json.getString("documentFormat");
      try {
        File f = misoFileManager.getNewFile(
                Project.class,
                projectId.toString(),
                "PlateInputForm-" + LimsUtils.getCurrentDateAsString() + "." + documentFormat);
        //TODO select a single sample to base sheet on?
        FormUtils.createPlateInputSpreadsheet(f);
        return JSONUtils.SimpleJSONResponse("" + f.getName().hashCode());
      }
      catch (Exception e) {
        e.printStackTrace();
        return JSONUtils.SimpleJSONError("Failed to get plate input form: " + e.getMessage());
      }
    }
    else {
      return JSONUtils.SimpleJSONError("Missing project ID or document format supplied.");
    }
  }

  public JSONObject watchOverview(HttpSession session, JSONObject json) {
    Long overviewId = json.getLong("overviewId");
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      ProjectOverview overview = requestManager.getProjectOverviewById(overviewId);
      if (!overview.getWatchers().contains(user)) {
        watchManager.watch(overview, user);
        watchManager.watch(overview.getProject(), user);
        requestManager.saveProjectOverview(overview);
        requestManager.saveProject(overview.getProject());
      }
      return JSONUtils.SimpleJSONResponse("OK");
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return JSONUtils.SimpleJSONError("Unable to watch/unwatch overview");
  }

  public JSONObject unwatchOverview(HttpSession session, JSONObject json) {
    Long overviewId = json.getLong("overviewId");
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      ProjectOverview overview = requestManager.getProjectOverviewById(overviewId);
      if (!overview.getProject().getSecurityProfile().getOwner().equals(user)) {
        if (overview.getWatchers().contains(user)) {
          watchManager.unwatch(overview, user);
          watchManager.unwatch(overview.getProject(), user);
          requestManager.saveProjectOverview(overview);
          requestManager.saveProject(overview.getProject());
        }
        return JSONUtils.SimpleJSONResponse("OK");
      }
      else {
        return JSONUtils.SimpleJSONError("Cannot unwatch an entity of which you are the owner.");
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return JSONUtils.SimpleJSONError("Unable to watch/unwatch overview");
  }

  public JSONObject listWatchOverview(HttpSession session, JSONObject json) {
    Long overviewId = json.getLong("overviewId");
    StringBuilder sb = new StringBuilder();
    JSONObject j = new JSONObject();
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      ProjectOverview overview = requestManager.getProjectOverviewById(overviewId);
      sb.append("<ul class='bullets' style='margin-left: -30px;'>");
      for (User theUser : overview.getWatchers()) {
        sb.append("<li>");
        sb.append(theUser.getFullName());
        sb.append("</li>");
      }
      sb.append("</ul>");
      j.put("watchers", sb.toString());
      return j;
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return JSONUtils.SimpleJSONError("Unable to list watchers");
  }

  public JSONObject listSamplesByProject(HttpSession session, JSONObject json) {
    Long projectId = json.getLong("projectId");

    try {
      JSONObject j = new JSONObject();
      JSONArray jsonArray = new JSONArray();
      for (Sample sample : requestManager.listAllSamplesByProjectId(projectId)) {
        jsonArray.add("{'id':'" + sample.getId() + "'," +
                      "'name':'" + sample.getName() + "'," +
                      "'alias':'"+sample.getAlias() + "'," +
                      "'type':'"+sample.getSampleType() + "'," +
                      "'description':'"+sample.getDescription() + "'}");
      }
      j.put("array", jsonArray);
      return j;
    }
    catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONObject addSampleGroup(HttpSession session, JSONObject json) {
    Long overviewId = json.getLong("overviewId");
    try {
      ProjectOverview overview = requestManager.getProjectOverviewById(overviewId);

      Set<Sample> samples = new HashSet<>();
      if (json.has("samples")) {
        JSONArray a = JSONArray.fromObject(json.get("samples"));
        for (JSONObject j : (Iterable<JSONObject>) a) {
          if (j.has("sampleId")) {
            samples.add(requestManager.getSampleById(j.getLong("sampleId")));
          }
          else {
            return JSONUtils.SimpleJSONError("Unable to add Sample Group: invalid sample set JSON has missing sampleId");
          }
        }
      }

      EntityGroup<ProjectOverview, Sample> osg = new EntityGroupImpl<>();
      osg.setEntities(samples);
      osg.setParent(overview);
      overview.setSampleGroup(osg);

      requestManager.saveProjectOverview(overview);
      requestManager.saveProject(overview.getProject());

      return JSONUtils.SimpleJSONResponse("OK");
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Unable to add Sample Group: " + e.getMessage());
    }
  }

  public JSONObject addSamplesToGroup(HttpSession session, JSONObject json) {
    Long overviewId = json.getLong("overviewId");
    Long groupId = json.getLong("groupId");
    try {
      ProjectOverview overview = requestManager.getProjectOverviewById(overviewId);
      EntityGroup<ProjectOverview, Sample> osg = overview.getSampleGroup();

      if (osg != null && groupId != null && osg.getId() == groupId.longValue()) {
        if (json.has("samples")) {
          JSONArray a = JSONArray.fromObject(json.get("samples"));
          for (JSONObject j : (Iterable<JSONObject>) a) {
            if (j.has("sampleId")) {
              Sample s = requestManager.getSampleById(j.getLong("sampleId"));
              if (osg.getEntities().contains(s)) {
                log.error("Sample group already contains " + s.getName());
              }
              else {
                osg.addEntity(s);
              }
            }
            else {
              return JSONUtils.SimpleJSONError("Unable to add Sample Group: invalid sample set JSON has missing sampleId");
            }
          }
        }
      }

      requestManager.saveProjectOverview(overview);
      requestManager.saveProject(overview.getProject());

      return JSONUtils.SimpleJSONResponse("OK");
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Unable to add Sample Group: " + e.getMessage());
    }
  }
}
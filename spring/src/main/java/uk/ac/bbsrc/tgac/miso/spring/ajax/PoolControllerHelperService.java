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

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.opensymphony.util.FileUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedDilutionException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedExperimentException;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoPrintException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.factory.barcode.BarcodeFactory;
import uk.ac.bbsrc.tgac.miso.core.factory.barcode.MisoJscriptFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.FilesManager;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.manager.PrintManager;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.service.printing.MisoPrintService;
import uk.ac.bbsrc.tgac.miso.core.service.printing.context.PrintContext;

import javax.imageio.ImageIO;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.servlet.http.HttpSession;
import java.awt.image.RenderedImage;
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
public class PoolControllerHelperService {
  protected static final Logger log = LoggerFactory.getLogger(PoolControllerHelperService.class);
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private RequestManager requestManager;
  @Autowired
  private DataObjectFactory dataObjectFactory;
  @Autowired
  private MisoFilesManager misoFileManager;
  @Autowired
  private BarcodeFactory barcodeFactory;
  @Autowired
  private PrintManager<MisoPrintService, Queue<?>> printManager;

  private String processLibraryDilutions(Set<String> codes) throws IOException {
    StringBuilder sb = new StringBuilder();
    sb.append("<div id='dilslist' class='checklist' style='width: 100%;'>");
    for (String s : codes) {
      LibraryDilution ed = requestManager.getLibraryDilutionByBarcode(s);
      if (ed != null) {
        sb.append("<span>");
        sb.append("<input type='checkbox' value='" + s + "' name='importdilslist' id='importdilslist_" + ed.getName() + "'/>");
        sb.append("<label for='importdilslist_" + ed.getName() + "'>" + ed.getName() + " (" + s + ")</label>");
        sb.append("</span>");
      }
    }
    sb.append("</div>");
    sb.append("<a onclick='checkAll(\"importdilslist\"); return false;' href='javascript:void(0);'>All</a> " +
              "/ <a onclick='uncheckAll(\"importdilslist\"); return false;' href='javascript:void(0);'>None</a>");
    sb.append("<br/><button type='submit' class='br-button ui-state-default ui-corner-all'>Use</button>");
    return sb.toString();
  }

  private String processEmPcrDilutions(Set<String> codes) throws IOException {
    StringBuilder sb = new StringBuilder();
    sb.append("<div id='dilslist' class='checklist' style='width: 100%;'>");
    for (String s : codes) {
      emPCRDilution ed = requestManager.getEmPcrDilutionByBarcode(s);
      if (ed != null) {
        sb.append("<span>");
        sb.append("<input type='checkbox' value='" + s + "' name='importdilslist' id='importdilslist_" + ed.getName() + "'/>");
        sb.append("<label for='importdilslist_" + ed.getName() + "'>" + ed.getName() + " (" + s + ")</label>");
        sb.append("</span>");
      }
    }
    sb.append("</div>");
    sb.append("<a onclick='checkAll(\"importdilslist\"); return false;' href='javascript:void(0);'>All</a> " +
              "/ <a onclick='uncheckAll(\"importdilslist\"); return false;' href='javascript:void(0);'>None</a>");
    sb.append("<br/><button type='submit' class='br-button ui-state-default ui-corner-all'>Use</button>");
    return sb.toString();
  }

  public JSONObject selectLibraryDilutionsByBarcodeList(HttpSession session, JSONObject json) {
    try {
      if (json.has("barcodes")) {
        String barcodes = json.getString("barcodes");
        String[] codes = barcodes.split("\n");

        // make sure there are no duplicates and order the strings
        // by putitng the codes in a treeset        
        TreeSet<String> hcodes = new TreeSet<String>();
        hcodes.addAll(Arrays.asList(codes));

        StringBuilder sb = new StringBuilder();
        sb.append("<form action='/miso/pool/illumina/import' method='post'>");
        sb.append("<div style='width: 100%;'>");
        sb.append(processLibraryDilutions(hcodes));
        sb.append("</form>");
        sb.append("</div>");
        session.removeAttribute("barcodes");

        return JSONUtils.SimpleJSONResponse(sb.toString());
      }
    }
    catch (Exception e) {
      log.debug("Failed to generate barcode selection: ", e);
      return JSONUtils.SimpleJSONError("Failed to generate barcode selection");
    }
    return JSONUtils.SimpleJSONError("Cannot select barcodes");
  }

  public JSONObject selectLibraryDilutionsByBarcodeFile(HttpSession session, JSONObject json) {
    try {
      JSONObject barcodes = (JSONObject) session.getAttribute("barcodes");
      log.debug(barcodes.toString());
      if (barcodes.has("barcodes")) {
        JSONArray a = barcodes.getJSONArray("barcodes");

        // make sure there are no duplicates and order the strings
        // by putitng the codes in a treeset
        TreeSet<String> hcodes = new TreeSet<String>();
        hcodes.addAll(Arrays.asList((String[]) a.toArray(new String[0])));

        StringBuilder sb = new StringBuilder();
        sb.append("<form action='/miso/pool/illumina/import' method='post'>");
        sb.append("<div style='width: 100%;'>");
        sb.append(processLibraryDilutions(hcodes));
        sb.append("</form>");
        sb.append("</div>");
        session.removeAttribute("barcodes");

        return JSONUtils.SimpleJSONResponse(sb.toString());
      }
    }
    catch (Exception e) {
      log.debug("Failed to generate barcode selection: ", e);
      return JSONUtils.SimpleJSONError("Failed to generate barcode selection");
    }
    return JSONUtils.SimpleJSONError("Cannot select barcodes");
  }

  public JSONObject select454EmPCRDilutionsByBarcodeList(HttpSession session, JSONObject json) {
    try {
      if (json.has("barcodes")) {
        String barcodes = json.getString("barcodes");
        String[] codes = barcodes.split("\n");

        // make sure there are no duplicates and order the strings
        // by putitng the codes in a treeset
        TreeSet<String> hcodes = new TreeSet<String>();
        hcodes.addAll(Arrays.asList(codes));

        StringBuilder sb = new StringBuilder();
        sb.append("<div style='width: 100%;'>");
        sb.append("<form action='/miso/pool/ls454/import' method='post'>");
        sb.append(processEmPcrDilutions(hcodes));
        sb.append("</form>");
        sb.append("</div>");

        session.removeAttribute("barcodes");

        return JSONUtils.SimpleJSONResponse(sb.toString());
      }
    }
    catch (Exception e) {
      log.debug("Failed to generate barcode selection: ", e);
      return JSONUtils.SimpleJSONError("Failed to generate barcode selection");
    }
    return JSONUtils.SimpleJSONError("Cannot select barcodes");
  }

  public JSONObject select454EmPCRDilutionsByBarcodeFile(HttpSession session, JSONObject json) {
    try {
      JSONObject barcodes = (JSONObject) session.getAttribute("barcodes");
      log.debug(barcodes.toString());
      if (barcodes.has("barcodes")) {
        JSONArray a = barcodes.getJSONArray("barcodes");

        // make sure there are no duplicates and order the strings
        // by putitng the codes in a treeset
        TreeSet<String> hcodes = new TreeSet<String>();
        hcodes.addAll(Arrays.asList((String[]) a.toArray(new String[0])));

        StringBuilder sb = new StringBuilder();
        sb.append("<div style='width: 100%;'>");
        sb.append("<form action='/miso/pool/ls454/import' method='post'>");
        sb.append(processEmPcrDilutions(hcodes));
        sb.append("</form>");
        sb.append("</div>");

        session.removeAttribute("barcodes");

        return JSONUtils.SimpleJSONResponse(sb.toString());
      }
    }
    catch (Exception e) {
      log.debug("Failed to generate barcode selection: ", e);
      return JSONUtils.SimpleJSONError("Failed to generate barcode selection");
    }
    return JSONUtils.SimpleJSONError("Cannot select barcodes");
  }

  public JSONObject selectSolidEmPCRDilutionsByBarcodeList(HttpSession session, JSONObject json) {
    try {
      if (json.has("barcodes")) {
        String barcodes = json.getString("barcodes");
        String[] codes = barcodes.split("\n");

        // make sure there are no duplicates and order the strings
        // by putitng the codes in a treeset
        TreeSet<String> hcodes = new TreeSet<String>();
        hcodes.addAll(Arrays.asList(codes));

        StringBuilder sb = new StringBuilder();
        sb.append("<div style='width: 100%;'>");
        sb.append("<form action='/miso/pool/solid/import' method='post'>");
        sb.append(processEmPcrDilutions(hcodes));
        sb.append("</form>");
        sb.append("</div>");

        session.removeAttribute("barcodes");

        return JSONUtils.SimpleJSONResponse(sb.toString());
      }
    }
    catch (Exception e) {
      log.debug("Failed to generate barcode selection: ", e);
      return JSONUtils.SimpleJSONError("Failed to generate barcode selection");
    }
    return JSONUtils.SimpleJSONError("Cannot select barcodes");
  }

  public JSONObject selectSolidEmPCRDilutionsByBarcodeFile(HttpSession session, JSONObject json) {
    try {
      JSONObject barcodes = (JSONObject) session.getAttribute("barcodes");
      log.debug(barcodes.toString());
      if (barcodes.has("barcodes")) {
        JSONArray a = barcodes.getJSONArray("barcodes");

        // make sure there are no duplicates and order the strings
        // by putitng the codes in a treeset
        TreeSet<String> hcodes = new TreeSet<String>();
        hcodes.addAll(Arrays.asList((String[]) a.toArray(new String[0])));

        StringBuilder sb = new StringBuilder();
        sb.append("<div style='width: 100%;'>");
        sb.append("<form action='/miso/pool/solid/import' method='post'>");
        sb.append(processEmPcrDilutions(hcodes));
        sb.append("</form>");
        sb.append("</div>");

        session.removeAttribute("barcodes");

        return JSONUtils.SimpleJSONResponse(sb.toString());
      }
    }
    catch (Exception e) {
      log.debug("Failed to generate barcode selection: ", e);
      return JSONUtils.SimpleJSONError("Failed to generate barcode selection");
    }
    return JSONUtils.SimpleJSONError("Cannot select barcodes");
  }

  public JSONObject getPoolBarcode(HttpSession session, JSONObject json) {
    Long poolId = json.getLong("poolId");
    File temploc = new File(session.getServletContext().getRealPath("/") + "temp/");
    try {
      Pool pool = requestManager.getPoolById(poolId);
      barcodeFactory.setPointPixels(1.5f);
      barcodeFactory.setBitmapResolution(600);
      RenderedImage bi = barcodeFactory.generateSquareDataMatrix(pool, 400);
      if (bi != null) {
        File tempimage = misoFileManager.generateTemporaryFile("barcode-", ".png", temploc);
        if (ImageIO.write(bi, "png", tempimage)) {
          return JSONUtils.JSONObjectResponse("img", tempimage.getName());
        }
        return JSONUtils.SimpleJSONError("Writing temp image file failed.");
      }
      else {
        return JSONUtils.SimpleJSONError("Pool has no parseable barcode");
      }
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError(e.getMessage() + ": Cannot seem to access " + temploc.getAbsolutePath());
    }
  }

  public JSONObject printPoolBarcodes(HttpSession session, JSONObject json) {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      JSONArray ss = JSONArray.fromObject(json.getString("pools"));

      String serviceName = null;
      if (json.has("serviceName")) { serviceName = json.getString("serviceName"); }

      MisoPrintService<File, PrintContext<File>> mps = null;
      if (serviceName == null) {
        Collection<MisoPrintService> services = printManager.listPrintServicesByBarcodeableClass(Pool.class);
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
        try {
          Long poolId = p.getLong("poolId");
          Pool pool = requestManager.getPoolById(poolId);

          File f = mps.getLabelFor(pool);
          if (f!=null) thingsToPrint.add(f);
        }
        catch (IOException e) {
          e.printStackTrace();
          return JSONUtils.SimpleJSONError("Error printing pool barcode: " + e.getMessage());
        }
      }

      PrintJob pj = printManager.print(thingsToPrint, mps.getName(), user);
      return JSONUtils.SimpleJSONResponse("Job " + pj.getJobId() + " : Barcodes printed.");
    }
    catch (MisoPrintException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("No printer of that name available: " + e.getMessage());
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Cannot print barcodes: " + e.getMessage());
    }
  }

  public JSONObject poolSearchExperiments(HttpSession session, JSONObject json) {
    String searchStr = json.getString("str");
    String platformType = json.getString("platform").toUpperCase();
    try {
      if (searchStr.length() > 1) {
        String str = searchStr.toLowerCase();
        StringBuilder b = new StringBuilder();
        List<Experiment> experiments = new ArrayList<Experiment>(requestManager.listAllExperiments());
        int numMatches = 0;
        for (Experiment e : experiments) {
          if (e.getPlatform().getPlatformType().equals(PlatformType.valueOf(platformType))) {
            String expName = e.getName() == null ? null : e.getName();
            if (expName != null &&
                (expName.toLowerCase().equals(str) || expName.toLowerCase().contains(str)) ||
                (e.getStudy().getAlias().toLowerCase().contains(str) || e.getStudy().getName().toLowerCase().contains(str)) ||
                (e.getStudy().getProject().getAlias().toLowerCase().contains(str) || e.getStudy().getProject().getName().toLowerCase().contains(str))) {
              b.append("<div onmouseover=\"this.className='autocompleteboxhighlight'\" onmouseout=\"this.className='autocompletebox'\" class=\"autocompletebox\"" +
                       " onclick=\"poolSearchSelectExperiment('" + e.getExperimentId() + "', '" + e.getName() + "')\">" +
                       "<b>Experiment:</b> " + expName + "<br/>" +
                       "<b>Description:</b> " + e.getDescription() + "<br/>" +
                       "<b>Project:</b> " + e.getStudy().getProject().getAlias() + "<br/>" +
                       "</div>");
              numMatches++;
            }
          }
        }
        if (numMatches == 0) {
          return JSONUtils.JSONObjectResponse("html", "No matches");
        }
        else {
          return JSONUtils.JSONObjectResponse("html", "<div class=\"autocomplete\"><ul>" + b.toString() + "</ul></div>");
        }
      }
      else {
        return JSONUtils.JSONObjectResponse("html", "Need a longer search pattern ...");
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError(e.getMessage());
    }
  }

  public JSONObject poolSearchLibraryDilution(HttpSession session, JSONObject json) {
    String searchStr = json.getString("str");
    String platformType = json.getString("platform").toUpperCase();
    try {
      if (searchStr.length() > 1) {
        String str = searchStr.toLowerCase();
        StringBuilder b = new StringBuilder();
        List<LibraryDilution> dilutions = new ArrayList<LibraryDilution>(requestManager.listAllLibraryDilutionsByPlatform(PlatformType.valueOf(platformType)));
        int numMatches = 0;
        for (LibraryDilution d : dilutions) {
          String dilName = d.getName() == null ? null : d.getName();
          if (dilName != null &&
              (dilName.toLowerCase().equals(str) || dilName.toLowerCase().contains(str)) ||
              (d.getLibrary().getAlias().toLowerCase().contains(str) || d.getLibrary().getName().toLowerCase().contains(str)) ||
              (d.getLibrary().getSample().getAlias().toLowerCase().contains(str) || d.getLibrary().getSample().getName().toLowerCase().contains(str))) {
            b.append("<div onmouseover=\"this.className='autocompleteboxhighlight'\" onmouseout=\"this.className='autocompletebox'\" class=\"autocompletebox\"" +
                     " onclick=\"poolSearchSelectLibraryDilution('" + d.getDilutionId() + "', '" + d.getName() + "')\">" +
                     "<b>Dilution: " + dilName + "</b><br/>" +
                     "<b>Library: " + d.getLibrary().getAlias() + "</b><br/>" +
                     "<b>Sample: " + d.getLibrary().getSample().getAlias() + "</b><br/>" +
                     "</div>");
            numMatches++;
          }
        }
        if (numMatches == 0) {
          return JSONUtils.JSONObjectResponse("html", "No matches");
        }
        else {
          return JSONUtils.JSONObjectResponse("html", "<div class=\"autocomplete\"><ul>" + b.toString() + "</ul></div>");
        }
      }
      else {
        return JSONUtils.JSONObjectResponse("html", "Need a longer search pattern ...");
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError(e.getMessage());
    }
  }

  public JSONObject poolSearchEmPcrDilution(HttpSession session, JSONObject json) {
    String searchStr = json.getString("str");
    String platformType = json.getString("platform").toUpperCase();
    try {
      if (searchStr.length() > 1) {
        String str = searchStr.toLowerCase();
        StringBuilder b = new StringBuilder();
        List<emPCRDilution> dilutions = new ArrayList<emPCRDilution>(requestManager.listAllEmPcrDilutionsByPlatform(PlatformType.valueOf(platformType)));
        int numMatches = 0;
        for (emPCRDilution d : dilutions) {
          String dilName = d.getName() == null ? null : d.getName();
          if (dilName != null &&
              (dilName.toLowerCase().equals(str) || dilName.toLowerCase().contains(str)) ||
              (d.getLibrary().getAlias().toLowerCase().contains(str) || d.getLibrary().getName().toLowerCase().contains(str)) ||
              (d.getLibrary().getSample().getAlias().toLowerCase().contains(str) || d.getLibrary().getSample().getName().toLowerCase().contains(str))) {
            b.append("<div onmouseover=\"this.className='autocompleteboxhighlight'\" onmouseout=\"this.className='autocompletebox'\" class=\"autocompletebox\"" +
                     " onclick=\"poolSearchSelectEmPcrDilution('" + d.getDilutionId() + "', '" + d.getName() + "')\">" +
                     "<b>Dilution: " + dilName + "</b><br/>" +
                     "<b>Library: " + d.getLibrary().getAlias() + "</b><br/>" +
                     "<b>Sample: " + d.getLibrary().getSample().getAlias() + "</b><br/>" +
                     "</div>");
            numMatches++;
          }
        }
        if (numMatches == 0) {
          return JSONUtils.JSONObjectResponse("html", "No matches");
        }
        else {
          return JSONUtils.JSONObjectResponse("html", "<div class=\"autocomplete\"><ul>" + b.toString() + "</ul></div>");
        }
      }
      else {
        return JSONUtils.JSONObjectResponse("html", "Need a longer search pattern ...");
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError(e.getMessage());
    }
  }

  public JSONObject checkPoolExperiment(HttpSession session, JSONObject json) {
    try {
      String partition = json.getString("partition");
      Long poolId = json.getLong("poolId");
      Pool p = requestManager.getPoolById(poolId);
      StringBuilder sb = new StringBuilder();

      if (p.getExperiments().size() == 0) {
        Set<Project> pooledProjects = new HashSet<Project>();
        Collection<Dilution> dils = p.getDilutions();
        for (Dilution d : dils) {
          pooledProjects.add(d.getLibrary().getSample().getProject());
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
              sb.append("<option value='" + s.getStudyId() + "'>" + s.getAlias() + " (" + s.getName() + " - " + s.getStudyType() + ")</option>");
            }
          }
          sb.append("</select>");
          sb.append("<input id='studySelectButton-"+partition+"_"+p.getPoolId()+"' type='button' onclick=\"selectStudy('" + partition + "', " + p.getPoolId() + "," + project.getProjectId() + ");\" class=\"ui-state-default ui-corner-all\" value='Select Study'/>");
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

  public JSONObject selectStudyForPool(HttpSession session, JSONObject json) {
    try {
      Long poolId = json.getLong("poolId");
      Pool p = requestManager.getPoolById(poolId);

      Long studyId = json.getLong("studyId");
      Study s = requestManager.getStudyById(studyId);

      if (json.has("runId") && json.get("runId") != null) {
        Long runId = json.getLong("runId");
        Run r = requestManager.getRunById(runId);
        if (r != null) {
          StringBuilder sb = new StringBuilder();

          Experiment e = dataObjectFactory.getExperiment();
          e.setAlias("EXP_AUTOGEN_" + s.getName() + "_" + s.getStudyType() + "_" + (s.getExperiments().size() + 1));
          e.setTitle(s.getProject().getName() + " " + r.getPlatformType().getKey() + " " + s.getStudyType() + " experiment (Auto-gen)");
          e.setDescription(s.getProject().getAlias());
          e.setPlatform(r.getSequencerReference().getPlatform());
          e.setStudy(s);
          e.setSecurityProfile(s.getSecurityProfile());

          try {
            p.addExperiment(e);
            requestManager.saveExperiment(e);
          }
          catch (MalformedExperimentException e1) {
            e1.printStackTrace();
          }

          sb.append("<i>");
          sb.append("<span>" + s.getProject().getAlias() + " (" + e.getName() + ": " + p.getDilutions().size() + " dilutions)</span><br/>");
          sb.append("</i>");

          return JSONUtils.JSONObjectResponse("html", sb.toString());
        }
        else {
          return JSONUtils.SimpleJSONError("Could not find run with ID " + runId);
        }
      }
      else {
        return JSONUtils.SimpleJSONError("Could not resolve Run ID. Please ensure the run is saved before adding Pools");
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError(e.getMessage());
    }
  }

  public JSONObject listPoolAverageInsertSizes(HttpSession session, JSONObject json) {
    try {
      JSONObject j = new JSONObject();
      for (Pool<? extends Poolable> pool: requestManager.listAllPools()) {
        StringBuilder b = new StringBuilder();
        Collection<? extends Dilution> dls = pool.getDilutions();
        if (dls.size() > 0) {
          int sum = 0;
          int count = 0;
          for (Dilution ld : dls) {
            List<LibraryQC> libraryQCs = new ArrayList<LibraryQC>(requestManager.listAllLibraryQCsByLibraryId(ld.getLibrary().getLibraryId()));
            if (libraryQCs.size() > 0) {
              //List<LibraryQC> libraryQCsList = new ArrayList<LibraryQC>(libraryQCs);
              //Collections.sort(libraryQCsList);
              LibraryQC libraryQC = libraryQCs.get(libraryQCs.size() - 1);
              sum += libraryQC.getInsertSize();
              count++;
            }
          }
          if (count > 0) {
            b.append(Math.round(sum / count)+" bp");
          }
        }
        else {
          b.append("No QC");
        }
        j.put(pool.getPoolId(), b.toString());
      }
      return j;
    }
    catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError(e.getMessage());
    }
  }

  public JSONObject deletePool(HttpSession session, JSONObject json) {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      if (user.isAdmin()) {
        if (json.has("poolId")) {
          Long poolId = json.getLong("poolId");
          requestManager.deletePool(requestManager.getPoolById(poolId));
          return JSONUtils.SimpleJSONResponse("Pool deleted");
        }
        else {
          return JSONUtils.SimpleJSONError("No pool specified to delete.");
        }
      }
      else {
        return JSONUtils.SimpleJSONError("Only admins can delete objects.");
      }
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Error getting currently logged in user.");
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

  public void setBarcodeFactory(BarcodeFactory barcodeFactory) {
    this.barcodeFactory = barcodeFactory;
  }

  public void setMisoFileManager(MisoFilesManager misoFileManager) {
    this.misoFileManager = misoFileManager;
  }

  public void setPrintManager(PrintManager<MisoPrintService, Queue<?>> printManager) {
    this.printManager = printManager;
  }
}
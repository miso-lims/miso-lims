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
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import org.apache.commons.codec.binary.Base64;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedExperimentException;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoPrintException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.factory.barcode.BarcodeFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.manager.PrintManager;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.service.printing.MisoPrintService;
import uk.ac.bbsrc.tgac.miso.core.service.printing.context.PrintContext;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import java.awt.image.RenderedImage;
import java.io.File;
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

  public JSONObject getPoolQcTypes(HttpSession session, JSONObject json) {
    try {
      StringBuilder sb = new StringBuilder();
      Collection<QcType> types = requestManager.listAllPoolQcTypes();
      for (QcType s : types) {
        sb.append("<option units='"+s.getUnits()+"' value='" + s.getQcTypeId() + "'>"+s.getName()+"</option>");
      }
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("types", sb.toString());
      return JSONUtils.JSONObjectResponse(map);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return JSONUtils.SimpleJSONError("Cannot list all Pool QC Types");
  }

  public JSONObject addPoolQC(HttpSession session, JSONObject json) {
    try {
      for (Object key : json.keySet()) {
        if (json.get(key) == null || json.get(key).equals("")) {
          String k = (String)key;
          return JSONUtils.SimpleJSONError("Please enter a value for '" +k+ "'");
        }
      }
      if (json.has("poolId") && !json.get("poolId").equals("")) {
        Long poolId = Long.parseLong(json.getString("poolId"));
        Pool<? extends Poolable> pool = requestManager.getPoolById(poolId);
        PoolQC newQc = dataObjectFactory.getPoolQC();
        if (json.has("qcPassed") && json.getString("qcPassed").equals("true")) {
          pool.setQcPassed(true);
        }
        newQc.setQcCreator(json.getString("qcCreator"));
        newQc.setQcDate(new SimpleDateFormat("dd/MM/yyyy").parse(json.getString("qcDate")));
        newQc.setQcType(requestManager.getPoolQcTypeById(json.getLong("qcType")));
        newQc.setResults(Double.parseDouble(json.getString("results")));
        pool.addQc(newQc);
        requestManager.savePoolQC(newQc);

        StringBuilder sb = new StringBuilder();
        sb.append("<tr><th>QCed By</th><th>QC Date</th><th>Method</th><th>Results</th></tr>");
        for (PoolQC qc : pool.getPoolQCs()) {
          sb.append("<tr>");
          sb.append("<td>"+qc.getQcCreator()+"</td>");
          sb.append("<td>"+qc.getQcDate()+"</td>");
          sb.append("<td>"+qc.getQcType().getName()+"</td>");
          sb.append("<td>"+qc.getResults()+" "+ qc.getQcType().getUnits() +"</td>");
          sb.append("</tr>");
        }
        return JSONUtils.SimpleJSONResponse(sb.toString());
      }
      else {
        return JSONUtils.SimpleJSONError("Cannot detect parent pool ID. Cannot add PoolQC");
      }
    }
    catch (Exception e) {
      log.error("Failed to add Pool QC to this Pool: ", e);
      return JSONUtils.SimpleJSONError("Failed to add Pool QC to this Pool: " + e.getMessage());
    }
  }

  public JSONObject bulkAddPoolQCs(HttpSession session, JSONObject json) {
    try {
      JSONArray qcs = JSONArray.fromObject(json.getString("qcs"));
      //validate
      boolean ok = true;
      for (JSONObject qc : (Iterable<JSONObject>)qcs) {
        String qcPassed = qc.getString("qcPassed");
        String qcType = qc.getString("qcType");
        String results = qc.getString("results");
        String qcCreator = qc.getString("qcCreator");
        String qcDate = qc.getString("qcDate");

        if (qcPassed == null || qcPassed.equals("") ||
            qcType == null || qcType.equals("") ||
            results == null || results.equals("") ||
            qcCreator == null || qcCreator.equals("") ||
            qcDate == null || qcDate.equals("")) {
          ok = false;
        }
      }

      //persist
      if (ok) {
        Map<String, Object> map = new HashMap<String, Object>();
        JSONArray a = new JSONArray();
        for (JSONObject qc : (Iterable<JSONObject>)qcs) {
          JSONObject j = addPoolQC(session, qc);
          j.put("poolId", qc.getString("poolId"));
          a.add(j);
        }
        map.put("saved", a);
        return JSONUtils.JSONObjectResponse(map);
      }
      else {
        log.error("Failed to add Pool QC to this Pool: one of the required fields of the selected QCs is missing or invalid");
        return JSONUtils.SimpleJSONError("Failed to add Pool QC to this Pool: one of the required fields of the selected QCs is missing or invalid");
      }
    }
    catch (Exception e) {
      log.error("Failed to add Pool QC to this Pool: ", e);
      return JSONUtils.SimpleJSONError("Failed to add Pool QC to this Pool: " + e.getMessage());
    }
  }

  public JSONObject changePoolQCRow(HttpSession session, JSONObject json) {
    try {
      JSONObject response = new JSONObject();
      Long qcId = Long.parseLong(json.getString("qcId"));
      PoolQC poolQc = requestManager.getPoolQCById(qcId);
      response.put("results", "<input type='text' id='" + qcId + "' value='" + poolQc.getResults() + "'/>");
      response.put("edit", "<a href='javascript:void(0);' onclick='Pool.qc.editPoolQC(\"" + qcId + "\");'>Save</a>");
      return response;
    }
    catch (Exception e) {
      log.error("Failed to display Pool QC of this sample: ", e);
      return JSONUtils.SimpleJSONError("Failed to display Pool QC of this sample: " + e.getMessage());
    }
  }

  public JSONObject editPoolQC(HttpSession session, JSONObject json) {
    try {
      if (json.has("qcId") && !json.get("qcId").equals("")) {
        Long qcId = Long.parseLong(json.getString("qcId"));
        PoolQC poolQc = requestManager.getPoolQCById(qcId);
        poolQc.setResults(Double.parseDouble(json.getString("result")));
        requestManager.savePoolQC(poolQc);
        return JSONUtils.SimpleJSONResponse("OK");
      }
    }
    catch (Exception e) {
      log.error("Failed to add Pool QC to this sample: ", e);
      return JSONUtils.SimpleJSONError("Failed to add Pool QC to this sample: " + e.getMessage());
    }
    return JSONUtils.SimpleJSONError("Cannot add PoolQC");
  }

  private String processDilutions(Set<String> codes) throws IOException {
    StringBuilder sb = new StringBuilder();
    sb.append("<div id='dilslist' class='checklist' style='width: 100%;'>");
    for (String s : codes) {
      if (LimsUtils.isBase64String(s)) {
        //Base64-encoded string, most likely a barcode image beeped in. decode and search
        s = new String(Base64.decodeBase64(s));
      }
      Dilution ed = requestManager.getDilutionByBarcode(s);
      if (ed != null) {
        sb.append("<span>");
        sb.append("<input type='checkbox' value='" + s + "' name='importdilslist' id='importdilslist_" + ed.getName() + "'/>");
        sb.append("<label for='importdilslist_" + ed.getName() + "'>" + ed.getName() + " (" + s + ")</label>");
        sb.append("</span>");
      }
    }
    sb.append("</div>");
    sb.append("<a onclick='Utils.ui.checkAll(\"importdilslist\"); return false;' href='javascript:void(0);'>All</a> " +
              "/ <a onclick='Utils.ui.uncheckAll(\"importdilslist\"); return false;' href='javascript:void(0);'>None</a>");
    sb.append("<br/><button type='submit' class='br-button ui-state-default ui-corner-all'>Use</button>");
    return sb.toString();
  }

  public JSONObject selectDilutionsByBarcodeFile(HttpSession session, JSONObject json) {
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
        sb.append("<form action='/miso/pool/import' method='post'>");
        sb.append("<div style='width: 100%;'>");
        sb.append(processDilutions(hcodes));
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

  public JSONObject selectDilutionsByBarcodeList(HttpSession session, JSONObject json) {
    try {
      if (json.has("barcodes")) {
        String barcodes = json.getString("barcodes");
        String[] codes = barcodes.split("\n");

        // make sure there are no duplicates and order the strings
        // by putitng the codes in a treeset        
        TreeSet<String> hcodes = new TreeSet<String>();
        hcodes.addAll(Arrays.asList(codes));

        StringBuilder sb = new StringBuilder();
        sb.append("<form action='/miso/pool/import' method='post'>");
        sb.append("<div style='width: 100%;'>");
        sb.append(processDilutions(hcodes));
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

      RenderedImage bi = null;

      if (json.has("barcodeGenerator")) {
        BarcodeDimension dim = new BarcodeDimension(100, 100);
        if (json.has("dimensionWidth") && json.has("dimensionHeight")) {
          dim = new BarcodeDimension(json.getDouble("dimensionWidth"), json.getDouble("dimensionHeight"));
        }
        BarcodeGenerator bg = BarcodeFactory.lookupGenerator(json.getString("barcodeGenerator"));
        if (bg != null) {
          bi = barcodeFactory.generateBarcode(pool, bg, dim);
        }
        else {
          return JSONUtils.SimpleJSONError("'" + json.getString("barcodeGenerator") + "' is not a valid barcode generator type");
        }
      }
      else {
        bi = barcodeFactory.generateSquareDataMatrix(pool, 400);
      }

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
      if (json.has("serviceName")) {
        serviceName = json.getString("serviceName");
      }

      MisoPrintService<File, Barcodable, PrintContext<File>> mps = null;
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
          if (f != null) thingsToPrint.add(f);
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
                       " onclick=\"Pool.search.poolSearchSelectExperiment('" + e.getId() + "', '" + e.getName() + "')\">" +
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
      for (Pool<? extends Poolable> pool : requestManager.listAllPools()) {
        StringBuilder b = new StringBuilder();
        Collection<? extends Dilution> dls = pool.getDilutions();
        if (dls.size() > 0) {
          int sum = 0;
          int count = 0;
          for (Dilution ld : dls) {
            List<LibraryQC> libraryQCs = new ArrayList<LibraryQC>(requestManager.listAllLibraryQCsByLibraryId(ld.getLibrary().getId()));
            if (libraryQCs.size() > 0) {
              //List<LibraryQC> libraryQCsList = new ArrayList<LibraryQC>(libraryQCs);
              //Collections.sort(libraryQCsList);
              LibraryQC libraryQC = libraryQCs.get(libraryQCs.size() - 1);
              sum += libraryQC.getInsertSize();
              count++;
            }
          }
          if (count > 0) {
            b.append(Math.round(sum / count) + " bp");
          }
        }
        else {
          b.append("No QC");
        }
        j.put(pool.getId(), b.toString());
      }
      return j;
    }
    catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError(e.getMessage());
    }
  }

  public JSONObject checkAverageInsertSizeByPoolId(HttpSession session, JSONObject json) {
    try {
      JSONObject j = new JSONObject();
      Long poolId = json.getLong("poolId");
      Pool pool = requestManager.getPoolById(poolId);
      StringBuilder b = new StringBuilder();
      Collection<? extends Dilution> dls = pool.getDilutions();
      if (dls.size() > 0) {
        int sum = 0;
        int count = 0;
        for (Dilution ld : dls) {
          List<LibraryQC> libraryQCs = new ArrayList<LibraryQC>(requestManager.listAllLibraryQCsByLibraryId(ld.getLibrary().getId()));
          if (libraryQCs.size() > 0) {
            LibraryQC libraryQC = libraryQCs.get(libraryQCs.size() - 1);
            sum += libraryQC.getInsertSize();
            count++;
          }
        }
        if (count > 0) {
          b.append(Math.round(sum / count) + " bp");
        }
      }
      else {
        b.append("No QC");
      }
      j.put("response", b.toString());
      return j;
    }
    catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError(e.getMessage());
    }
  }

  public JSONObject checkConcentrationByPoolId(HttpSession session, JSONObject json) {
    try {
      JSONObject j = new JSONObject();
      Long poolId = json.getLong("poolId");
      Pool pool = requestManager.getPoolById(poolId);
      double concentration = pool.getConcentration();
      j.put("response", concentration);
      return j;
    }
    catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError(e.getMessage());
    }
  }

  public JSONObject checkInfoByPoolId(HttpSession session, JSONObject json) {
    try {
      JSONObject j = new JSONObject();
      Long poolId = json.getLong("poolId");
      Pool<? extends Poolable> pool = requestManager.getPoolById(poolId);
      StringBuilder info = new StringBuilder();
      if (pool.getDilutions().size() > 0) {
        info.append("<ul class=\"shorterbullets\">");
        Collection<? extends Dilution> dls = pool.getDilutions();
        for (Dilution dilution : dls) {
          info.append("<li><b>" + dilution.getName() + "</b>");
          info.append("<br/><small><u><a href='/miso/project/"+dilution.getLibrary().getSample().getProject().getId()+"'>" + dilution.getLibrary().getSample().getProject().getAlias() + "</a></u>");
          info.append("<br/><a href='/miso/library/"+dilution.getLibrary().getId()+"'>" + dilution.getLibrary().getAlias() + " (" + dilution.getLibrary().getName() + ")</a>");
          info.append("<br/><a href='/miso/sample/"+dilution.getLibrary().getSample().getId()+"'>" + dilution.getLibrary().getSample().getDescription() + " (" + dilution.getLibrary().getSample().getName() + ")</a></small>");
          info.append("</li>");
        }
        info.append("</ul>");
      }
      else if (pool.getPoolableElements().size() > 0) {
        info.append("<ul class=\"shorterbullets\">");
        Collection<? extends Poolable> ds = pool.getPoolableElements();
        for (Poolable p : ds) {
          if (p instanceof Dilution) {
            Dilution dilution = (Dilution)p;
            info.append("<li><b>" + dilution.getName() + "</b>");
            info.append("<br/><small><u><a href='/miso/project/"+dilution.getLibrary().getSample().getProject().getId()+"'>" + dilution.getLibrary().getSample().getProject().getAlias() + "</a></u>");
            info.append("<br/><a href='/miso/library/"+dilution.getLibrary().getId()+"'>" + dilution.getLibrary().getAlias() + " (" + dilution.getLibrary().getName() + ")</a>");
            info.append("<br/><a href='/miso/sample/"+dilution.getLibrary().getSample().getId()+"'>" + dilution.getLibrary().getSample().getDescription() + " (" + dilution.getLibrary().getSample().getName() + ")</a></small>");
            info.append("</li>");
          }
          else if (p instanceof Plate) {
            Plate<LinkedList<Plateable>, Plateable> plate = (Plate<LinkedList<Plateable>, Plateable>)p;
            info.append("<li><b><a href='/miso/plate/"+plate.getId()+"'>" + plate.getName() + "</b> ["+plate.getSize()+"-well]</a>");
            if (!plate.getElements().isEmpty()) {
              info.append("<br/><small><u>"+plate.getSize()+"-well "+plate.getElementType().getSimpleName()+" plate</u>");
              Plateable element = plate.getElements().getFirst();
              if (element instanceof Library) {
                Library l = (Library)element;
                info.append("<br/><small><u><a href='/miso/project/"+l.getSample().getProject().getId()+"'>" + l.getSample().getProject().getAlias() + " ("+l.getSample().getProject().getName()+")</a></u>");
                info.append("<br/>Platform: " + l.getPlatformName());
                info.append("<br/>Type: " + l.getLibraryType().getDescription());
                info.append("<br/>Selection: " + l.getLibrarySelectionType().getName());
                info.append("<br/>Strategy: " + l.getLibraryStrategyType().getName());
              }
              else if (element instanceof Dilution) {
                Dilution l = (Dilution)element;
                info.append("<br/><small><u><a href='/miso/project/"+l.getLibrary().getSample().getProject().getId()+"'>" + l.getLibrary().getSample().getProject().getAlias() + " ("+l.getLibrary().getSample().getProject().getName()+")</a></u>");
                info.append("<br/>Platform: " + l.getLibrary().getPlatformName());
                info.append("<br/>Type: " + l.getLibrary().getLibraryType().getDescription());
                info.append("<br/>Selection: " + l.getLibrary().getLibrarySelectionType().getName());
                info.append("<br/>Strategy: " + l.getLibrary().getLibraryStrategyType().getName());
              }
              else if (element instanceof Sample) {
                Sample l = (Sample)element;
                info.append("<br/><small><u><a href='/miso/project/"+l.getProject().getId()+"'>" + l.getProject().getAlias() + " ("+l.getProject().getName()+")</a></u>");
              }
            }
            info.append("</li>");
          }
          else {
            info.append("<li><b>" + p.getName() + "</b>");
          }
        }
        info.append("</ul>");
      }
      else {
        info.append("No pooled elements");
      }
      j.put("response", info.toString());
      return j;
    }
    catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError(e.getMessage());
    }
  }

  public JSONObject deletePool(HttpSession session, JSONObject json) {
    User user;
    try {
      user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Error getting currently logged in user.");
    }

    if (user != null && user.isAdmin()) {
      if (json.has("poolId")) {
        Long poolId = json.getLong("poolId");
        try {
          requestManager.deletePool(requestManager.getPoolById(poolId));
          return JSONUtils.SimpleJSONResponse("Pool deleted");
        }
        catch (IOException e) {
          e.printStackTrace();
          return JSONUtils.SimpleJSONError("Cannot delete pool: " + e.getMessage());
        }
      }
      else {
        return JSONUtils.SimpleJSONError("No pool specified to delete.");
      }
    }
    else {
      return JSONUtils.SimpleJSONError("Only logged-in admins can delete objects.");
    }
  }

  public JSONObject listPoolsDataTable(HttpSession session, JSONObject json) {
    if (json.has("platform") && !"".equals(json.getString("platform"))) {
      try {
        String platform = json.getString("platform");
        JSONObject j = new JSONObject();
        JSONArray arr = new JSONArray();
        for (Pool pool : requestManager.listAllPoolsByPlatform(PlatformType.get(platform))) {
          JSONArray pout = new JSONArray();
          pout.add(pool.getName());
          pout.add(pool.getAlias() != null ? pool.getAlias() : "");
          pout.add(pool.getCreationDate() != null ? pool.getCreationDate().toString() : "");
          pout.add(pool.getId());
          pout.add(pool.getId());
          pout.add(pool.getId());
          pout.add("<a href=\"/miso/pool/" + pool.getId() + "\"><span class=\"ui-icon ui-icon-pencil\"></span></a>");
          arr.add(pout);
        }
        j.put("pools", arr);
        return j;
      }
      catch (IOException e) {
        log.debug("Failed", e);
        return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
      }
    }
    else {
      return JSONUtils.SimpleJSONError("No platform specified");
    }
  }

  public JSONObject getPoolableElementInfo(HttpSession session, JSONObject json) {
    if (json.has("poolId") && json.has("elementId")) {
      try {
        Long poolId = json.getLong("poolId");
        Long elementId = json.getLong("elementId");
        Pool<? extends Poolable> pool = requestManager.getPoolById(poolId);
        StringBuilder info = new StringBuilder();
        for (Poolable p : pool.getPoolableElements()) {
          if (p.getId() == elementId) {
            if (p instanceof Plate) {
              Plate<LinkedList<Plateable>, Plateable> plate = (Plate)p;
              for (Plateable plateable : plate.getElements()) {
                if (plateable instanceof Plate) {
                  info.append("<b>Internal Plate:</b> <a href='/miso/plate/"+plate.getId()+"'>"+plate.getName()+"</a><br/>");
                }
                else if (p instanceof Library) {
                  Library library = (Library)plateable;
                  info.append("<b>Library:</b> <a href='/miso/library/"+library.getId()+"'>"+library.getAlias()+"("+library.getName()+")</a><br/>");
                }
              }
            }
            else if (p instanceof Dilution) {
              Dilution dilution = (Dilution)p;
              if (dilution instanceof emPCRDilution) {
                info.append("<b>emPCR:</b> <a href='/miso/empcr/"+((emPCRDilution)dilution).getEmPCR().getId()+"'>"+((emPCRDilution)dilution).getEmPCR().getName()+"<br/>");
              }
              info.append("<b>Library:</b> <a href='/miso/library/"+dilution.getLibrary().getId()+"'>"+dilution.getLibrary().getAlias()+"("+dilution.getLibrary().getName()+")</a><br/>");
              info.append("<b>Sample:</b> <a href='/miso/sample/"+dilution.getLibrary().getSample().getId()+"'>"+dilution.getLibrary().getSample().getAlias()+"("+dilution.getLibrary().getSample().getName()+")</a><br/>");
              if (pool.getPoolableElements().size() > 1) {
                Map<Integer, TagBarcode> barcodes = dilution.getLibrary().getTagBarcodes();
                if (!barcodes.isEmpty()) {
                  String out = "<b>Barcodes:</b></br>";
                  for (Integer key : barcodes.keySet()) {
                    TagBarcode tb = barcodes.get(key);
                    if (tb != null) {
                      out += key + ":" + tb.getName() + " (" + tb.getSequence() + ")<br/>";
                      out += "<span class='counter'><img src='/styles/images/status/green.png' border='0'></span>";
                    }
                    else {
                      out += "Error retrieving barcode ["+key+"] for library "+dilution.getLibrary().getName()+". Please check libraries for this pool.";
                      out += "<span class='counter'><img src='/styles/images/status/red.png' border='0'></span>";
                      break;
                    }
                  }
                  info.append(out);
                }
                else {
                  info.append("<b>Barcode:</b>");
                  info.append("<b>Library:</b> <a href='/miso/library/"+dilution.getLibrary().getId()+"'>Choose tag barcode</a>");
                  info.append("<span class='counter'><img src='/styles/images/status/red.png' border='0'></span>");
                }
              }
            }
            else {
              info.append("Unrecognised poolable element: " + p.getClass().getSimpleName());
            }
            break;
          }
        }
        return JSONUtils.JSONObjectResponse("info", info.toString());
      }
      catch (IOException e) {
        log.debug("Failed", e);
        return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
      }
    }
    else {
      return JSONUtils.SimpleJSONError("No pool or element ID specified");
    }
  }

  public JSONObject createElementSelectDataTable(HttpSession session, JSONObject json) {
    if (json.has("platform") && !"".equals(json.getString("platform"))) {
      try {
        String platform = json.getString("platform");
        JSONObject j = new JSONObject();
        JSONArray arr = new JSONArray();
        for (LibraryDilution libraryDilution : requestManager.listAllLibraryDilutionsByPlatform((PlatformType.get(platform)))) {
          JSONArray pout = new JSONArray();
          pout.add(libraryDilution.getName());
          pout.add(libraryDilution.getLibrary().getName() + "-" + libraryDilution.getLibrary().getAlias());
          pout.add(libraryDilution.getLibrary().getSample().getName() + "-" + libraryDilution.getLibrary().getSample().getAlias());
          pout.add(libraryDilution.getLibrary().getSample().getProject().getName() + "-" + libraryDilution.getLibrary().getSample().getProject().getAlias());
          pout.add("<div style='cursor:pointer;' onmousedown=\"Pool.search.poolSearchSelectElement('" + libraryDilution.getId() + "', '"
                   + libraryDilution.getName() + "')\"><span class=\"ui-icon ui-icon-plusthick\"></span></div>");
          arr.add(pout);
        }
        j.put("poolelements", arr);
        return j;
      }
      catch (IOException e) {
        log.debug("Failed", e);
        return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
      }
    }
    else {
      return JSONUtils.SimpleJSONError("No platform specified");
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
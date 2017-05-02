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
import static uk.ac.bbsrc.tgac.miso.spring.ControllerHelperServiceUtils.getBarcodeFileLocation;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;

import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.Dilution;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ExperimentImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolQCImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedExperimentException;
import uk.ac.bbsrc.tgac.miso.core.factory.barcode.BarcodeFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.service.ExperimentService;
import uk.ac.bbsrc.tgac.miso.service.LibraryDilutionService;
import uk.ac.bbsrc.tgac.miso.service.PoolService;
import uk.ac.bbsrc.tgac.miso.service.PoolableElementViewService;
import uk.ac.bbsrc.tgac.miso.service.PrinterService;
import uk.ac.bbsrc.tgac.miso.service.StudyService;
import uk.ac.bbsrc.tgac.miso.spring.ControllerHelperServiceUtils;
import uk.ac.bbsrc.tgac.miso.spring.ControllerHelperServiceUtils.BarcodePrintAssister;

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
  private PoolService poolService;
  @Autowired
  private MisoFilesManager misoFileManager;
  @Autowired
  private PrinterService printerService;
  @Autowired
  private ExperimentService experimentService;
  @Autowired
  private LibraryDilutionService dilutionService;
  @Autowired
  private PoolableElementViewService poolableElementViewService;
  @Autowired
  private StudyService studyService;

  public JSONObject getPoolQcTypes(HttpSession session, JSONObject json) {
    try {
      StringBuilder sb = new StringBuilder();
      Collection<QcType> types = requestManager.listAllPoolQcTypes();
      for (QcType s : types) {
        sb.append("<option units='" + s.getUnits() + "' value='" + s.getQcTypeId() + "'>" + s.getName() + "</option>");
      }
      Map<String, Object> map = new HashMap<>();
      map.put("types", sb.toString());
      return JSONUtils.JSONObjectResponse(map);
    } catch (IOException e) {
      log.error("get pool qc types", e);
    }
    return JSONUtils.SimpleJSONError("Cannot list all Pool QC Types");
  }

  public JSONObject addPoolQC(HttpSession session, JSONObject json) {
    try {
      for (Object k : json.keySet()) {
        String key = (String) k;
        if (isStringEmptyOrNull(json.getString(key))) {
          return JSONUtils.SimpleJSONError("Please enter a value for '" + key + "'");
        }
      }
      if (json.has("poolId") && !isStringEmptyOrNull(json.getString("poolId"))) {
        Long poolId = Long.parseLong(json.getString("poolId"));
        Pool pool = poolService.getPoolById(poolId);
        PoolQC newQc = new PoolQCImpl();
        if (json.has("qcPassed") && json.getString("qcPassed").equals("true")) {
          pool.setQcPassed(true);
        }
        newQc.setQcCreator(json.getString("qcCreator"));
        newQc.setQcDate(new SimpleDateFormat("dd/MM/yyyy").parse(json.getString("qcDate")));
        newQc.setQcType(requestManager.getPoolQcTypeById(json.getLong("qcType")));
        newQc.setResults(Double.parseDouble(json.getString("results")));
        pool.addQc(newQc);
        poolService.savePoolQC(newQc);

        StringBuilder sb = new StringBuilder();
        sb.append("<tr><th>QCed By</th><th>QC Date</th><th>Method</th><th>Results</th></tr>");
        for (PoolQC qc : pool.getPoolQCs()) {
          sb.append("<tr>");
          sb.append("<td>" + qc.getQcCreator() + "</td>");
          sb.append("<td>" + qc.getQcDate() + "</td>");
          sb.append("<td>" + qc.getQcType().getName() + "</td>");
          sb.append("<td>" + qc.getResults() + " " + qc.getQcType().getUnits() + "</td>");
          sb.append("</tr>");
        }
        return JSONUtils.SimpleJSONResponse(sb.toString());
      } else {
        return JSONUtils.SimpleJSONError("Cannot detect parent pool ID. Cannot add PoolQC");
      }
    } catch (Exception e) {
      log.error("Failed to add Pool QC to this Pool: ", e);
      return JSONUtils.SimpleJSONError("Failed to add Pool QC to this Pool: " + e.getMessage());
    }
  }

  public JSONObject bulkAddPoolQCs(HttpSession session, JSONObject json) {
    try {
      JSONArray qcs = JSONArray.fromObject(json.getString("qcs"));
      // validate
      boolean ok = true;
      for (JSONObject qc : (Iterable<JSONObject>) qcs) {
        String qcPassed = qc.getString("qcPassed");
        String qcType = qc.getString("qcType");
        String results = qc.getString("results");
        String qcCreator = qc.getString("qcCreator");
        String qcDate = qc.getString("qcDate");

        if (isStringEmptyOrNull(qcPassed) || isStringEmptyOrNull(qcType) || isStringEmptyOrNull(results) || isStringEmptyOrNull(qcCreator)
            || isStringEmptyOrNull(qcDate)) {
          ok = false;
        }
      }

      // persist
      if (ok) {
        Map<String, Object> map = new HashMap<>();
        JSONArray a = new JSONArray();
        for (JSONObject qc : (Iterable<JSONObject>) qcs) {
          JSONObject j = addPoolQC(session, qc);
          j.put("poolId", qc.getString("poolId"));
          a.add(j);
        }
        map.put("saved", a);
        return JSONUtils.JSONObjectResponse(map);
      } else {
        log.error("Failed to add Pool QC to this Pool: one of the required fields of the selected QCs is missing or invalid");
        return JSONUtils
            .SimpleJSONError("Failed to add Pool QC to this Pool: one of the required fields of the selected QCs is missing or invalid");
      }
    } catch (Exception e) {
      log.error("Failed to add Pool QC to this Pool: ", e);
      return JSONUtils.SimpleJSONError("Failed to add Pool QC to this Pool: " + e.getMessage());
    }
  }

  public JSONObject changePoolQCRow(HttpSession session, JSONObject json) {
    try {
      JSONObject response = new JSONObject();
      Long qcId = Long.parseLong(json.getString("qcId"));
      PoolQC poolQc = poolService.getPoolQCById(qcId);
      response.put("results", "<input type='text' id='" + qcId + "' value='" + poolQc.getResults() + "'/>");
      response.put("edit", "<a href='javascript:void(0);' onclick='Pool.qc.editPoolQC(\"" + qcId + "\");'>Save</a>");
      return response;
    } catch (Exception e) {
      log.error("Failed to display Pool QC of this sample: ", e);
      return JSONUtils.SimpleJSONError("Failed to display Pool QC of this sample: " + e.getMessage());
    }
  }

  public JSONObject editPoolQC(HttpSession session, JSONObject json) {
    try {
      if (json.has("qcId") && !isStringEmptyOrNull(json.getString("qcId"))) {
        Long qcId = Long.parseLong(json.getString("qcId"));
        PoolQC poolQc = poolService.getPoolQCById(qcId);
        poolQc.setResults(Double.parseDouble(json.getString("result")));
        poolService.savePoolQC(poolQc);
        return JSONUtils.SimpleJSONResponse("OK");
      }
    } catch (Exception e) {
      log.error("Failed to add Pool QC to this sample: ", e);
      return JSONUtils.SimpleJSONError("Failed to add Pool QC to this sample: " + e.getMessage());
    }
    return JSONUtils.SimpleJSONError("Cannot add PoolQC");
  }

  private String processDilutions(Set<String> codes) throws IOException {
    StringBuilder sb = new StringBuilder();
    sb.append("<div id='dilslist' class='checklist' style='width: 100%;'>");
    for (String s : codes) {
      Dilution ed = dilutionService.getByBarcode(s);
      // Base64-encoded string, most likely a barcode image beeped in. decode and search
      if (ed == null) {
        ed = dilutionService.getByBarcode(new String(Base64.decodeBase64(s)));
      }
      if (ed != null) {
        sb.append("<span>");
        sb.append("<input type='checkbox' value='" + s + "' name='importdilslist' id='importdilslist_" + ed.getName() + "'/>");
        sb.append("<label for='importdilslist_" + ed.getName() + "'>" + ed.getName() + " (" + s + ")</label>");
        sb.append("</span>");
      }
    }
    sb.append("</div>");
    sb.append("<a onclick='Utils.ui.checkAll(\"importdilslist\"); return false;' href='javascript:void(0);'>All</a> "
        + "/ <a onclick='Utils.ui.uncheckAll(\"importdilslist\"); return false;' href='javascript:void(0);'>None</a>");
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
        TreeSet<String> hcodes = new TreeSet<>();
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
    } catch (Exception e) {
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
        TreeSet<String> hcodes = new TreeSet<>();
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
    } catch (Exception e) {
      log.debug("Failed to generate barcode selection: ", e);
      return JSONUtils.SimpleJSONError("Failed to generate barcode selection");
    }
    return JSONUtils.SimpleJSONError("Cannot select barcodes");
  }

  public JSONObject getPoolBarcode(HttpSession session, JSONObject json) {
    Long poolId = json.getLong("poolId");
    File temploc = getBarcodeFileLocation(session);
    try {
      Pool pool = poolService.getPoolById(poolId);
      BarcodeFactory barcodeFactory = new BarcodeFactory();
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
        } else {
          return JSONUtils.SimpleJSONError("'" + json.getString("barcodeGenerator") + "' is not a valid barcode generator type");
        }
      } else {
        bi = barcodeFactory.generateSquareDataMatrix(pool, 400);
      }

      if (bi != null) {
        File tempimage = misoFileManager.generateTemporaryFile("barcode-", ".png", temploc);
        if (ImageIO.write(bi, "png", tempimage)) {
          return JSONUtils.JSONObjectResponse("img", tempimage.getName());
        }
        return JSONUtils.SimpleJSONError("Writing temp image file failed.");
      } else {
        return JSONUtils.SimpleJSONError("Pool has no parseable barcode");
      }
    } catch (IOException e) {
      log.error("cannot access " + temploc.getAbsolutePath(), e);
      return JSONUtils.SimpleJSONError(e.getMessage() + ": Cannot seem to access " + temploc.getAbsolutePath());
    }
  }

  public JSONObject printPoolBarcodes(HttpSession session, JSONObject json) {
    return ControllerHelperServiceUtils.printBarcodes(printerService, json, new BarcodePrintAssister<Pool>() {

      @Override
      public Pool fetch(long id) throws IOException {
        return poolService.getPoolById(id);
      }

      @Override
      public void store(Pool item) throws IOException {
        poolService.savePool(item);
      }

      @Override
      public String getGroupName() {
        return "pools";
      }

      @Override
      public String getIdName() {
        return "poolId";
      }

      @Override
      public Iterable<Pool> fetchAll(long projectId) throws IOException {
        return Collections.emptyList();
      }
    });
  }

  public JSONObject changePoolIdBarcode(HttpSession session, JSONObject json) {
    Long poolId = json.getLong("poolId");
    String idBarcode = json.getString("identificationBarcode");

    try {
      if (isStringEmptyOrNull(idBarcode)) {
        // if the user accidentally deletes a barcode, the changelogs will have a record of the original barcode
        idBarcode = null;
      } else {
        List<Boxable> previouslyBarcodedItems = new ArrayList<>(requestManager.getBoxablesFromBarcodeList(Arrays.asList(idBarcode)));
        if (!previouslyBarcodedItems.isEmpty()
            && !(previouslyBarcodedItems.size() == 1 && previouslyBarcodedItems.get(0).getId() == poolId)) {
          Boxable previouslyBarcodedItem = previouslyBarcodedItems.get(0);
          String error = String.format(
              "Could not change pool identification barcode to '%s'. This barcode is already in use by an item with the name '%s' and the alias '%s'.",
              idBarcode, previouslyBarcodedItem.getName(), previouslyBarcodedItem.getAlias());
          log.debug(error);
          return JSONUtils.SimpleJSONError(error);
        }
      }
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Pool pool = poolService.getPoolById(poolId);
      pool.setIdentificationBarcode(idBarcode);
      pool.setLastModifier(user);
      poolService.savePool(pool);
    } catch (IOException e) {
      log.debug("Could not change Pool identificationBarcode: " + e.getMessage());
      return JSONUtils.SimpleJSONError(e.getMessage());
    }

    return JSONUtils.SimpleJSONResponse("New identification barcode successfully assigned.");
  }

  public JSONObject poolSearchExperiments(HttpSession session, JSONObject json) {
    String searchStr = json.getString("str");
    String platformType = json.getString("platform").toUpperCase();
    try {
      if (searchStr.length() > 1) {
        String str = searchStr.toLowerCase();
        StringBuilder b = new StringBuilder();
        List<Experiment> experiments = new ArrayList<>(experimentService.listAll());
        int numMatches = 0;
        for (Experiment e : experiments) {
          if (e.getPlatform().getPlatformType().equals(PlatformType.valueOf(platformType))) {
            String expName = e.getName() == null ? null : e.getName();
            if (expName != null && (expName.toLowerCase().equals(str) || expName.toLowerCase().contains(str))
                || (e.getStudy().getAlias().toLowerCase().contains(str) || e.getStudy().getName().toLowerCase().contains(str))
                || (e.getStudy().getProject().getAlias().toLowerCase().contains(str)
                    || e.getStudy().getProject().getName().toLowerCase().contains(str))) {
              b.append(
                  "<div onmouseover=\"this.className='autocompleteboxhighlight'\" onmouseout=\"this.className='autocompletebox'\" class=\"autocompletebox\""
                      + " onclick=\"Pool.search.poolSearchSelectExperiment('" + e.getId() + "', '" + e.getName() + "')\">"
                      + "<b>Experiment:</b> " + expName + "<br/>" + "<b>Description:</b> " + e.getDescription() + "<br/>"
                      + "<b>Project:</b> " + e.getStudy().getProject().getAlias() + "<br/>" + "</div>");
              numMatches++;
            }
          }
        }
        if (numMatches == 0) {
          return JSONUtils.JSONObjectResponse("html", "No matches");
        } else {
          return JSONUtils.JSONObjectResponse("html", "<div class=\"autocomplete\"><ul>" + b.toString() + "</ul></div>");
        }
      } else {
        return JSONUtils.JSONObjectResponse("html", "Need a longer search pattern ...");
      }
    } catch (Exception e) {
      log.error("pool search experiments", e);
      return JSONUtils.SimpleJSONError(e.getMessage());
    }
  }

  public JSONObject selectStudyForPool(HttpSession session, JSONObject json) {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Long poolId = json.getLong("poolId");
      Pool p = poolService.getPoolById(poolId);

      Long studyId = json.getLong("studyId");
      Study s = studyService.get(studyId);

      if (json.has("runId") && json.get("runId") != null) {
        Long runId = json.getLong("runId");
        Run r = requestManager.getRunById(runId);
        if (r != null) {
          StringBuilder sb = new StringBuilder();

          Experiment e = new ExperimentImpl();
          e.setAlias("EXP_AUTOGEN_" + s.getName() + "_" + s.getStudyType().getName() + "_" + (s.getExperiments().size() + 1));
          e.setTitle(
              s.getProject().getName() + " " + r.getPlatformType().getKey() + " " + s.getStudyType().getName() + " experiment (Auto-gen)");
          e.setDescription(s.getProject().getAlias());
          e.setPlatform(r.getSequencerReference().getPlatform());
          e.setStudy(s);
          e.setSecurityProfile(s.getSecurityProfile());

          try {
            p.addExperiment(e);
            e.setLastModifier(user);
            experimentService.save(e);
          } catch (MalformedExperimentException e1) {
            log.error("save experiment", e1);
          }

          sb.append("<i>");
          sb.append("<span>" + s.getProject().getAlias() + " (" + e.getName() + ": " + p.getPoolableElementViews().size()
              + " dilutions)</span><br/>");
          sb.append("</i>");

          return JSONUtils.JSONObjectResponse("html", sb.toString());
        } else {
          return JSONUtils.SimpleJSONError("Could not find run with ID " + runId);
        }
      } else {
        return JSONUtils.SimpleJSONError("Could not resolve Run ID. Please ensure the run is saved before adding Pools");
      }
    } catch (Exception e) {
      log.error("select study for pool", e);
      return JSONUtils.SimpleJSONError(e.getMessage());
    }
  }

  public JSONObject listPoolAverageInsertSizes(HttpSession session, JSONObject json) {
    try {
      JSONObject j = new JSONObject();
      for (Pool pool : poolService.listAllPools()) {

        StringBuilder b = new StringBuilder();
        Integer avg = calculateAverageInsertSize(pool);
        if (avg != null) {
          b.append(avg + " bp");
        }
      }
      return j;
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError(e.getMessage());
    }
  }

  public JSONObject checkAverageInsertSizeByPoolId(HttpSession session, JSONObject json) {
    try {
      JSONObject j = new JSONObject();
      Long poolId = json.getLong("poolId");
      Pool pool = poolService.getPoolById(poolId);
      StringBuilder b = new StringBuilder();
      Integer avg = calculateAverageInsertSize(pool);
      if (avg != null) {
        b.append(avg + " bp");
      }
      j.put("response", b.toString());
      return j;
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError(e.getMessage());
    }
  }

  private Integer calculateAverageInsertSize(Pool pool) {
    Collection<PoolableElementView> dls = pool.getPoolableElementViews();
    if (dls.isEmpty()) return null;
    long sum = 0;
    for (PoolableElementView ld : dls) {
      if (ld.getLibraryDnaSize() != null) {
        sum += ld.getLibraryDnaSize();
      }
    }
    return Math.round(sum / dls.size());
  }

  public JSONObject checkConcentrationByPoolId(HttpSession session, JSONObject json) {
    try {
      JSONObject j = new JSONObject();
      Long poolId = json.getLong("poolId");
      Pool pool = poolService.getPoolById(poolId);
      double concentration = pool.getConcentration();
      j.put("response", concentration);
      return j;
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError(e.getMessage());
    }
  }

  public JSONObject deletePool(HttpSession session, JSONObject json) {
    User user;
    try {
      user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    } catch (IOException e) {
      log.error("error getting currently logged in user", e);
      return JSONUtils.SimpleJSONError("Error getting currently logged in user.");
    }

    if (user != null && user.isAdmin()) {
      if (json.has("poolId")) {
        Long poolId = json.getLong("poolId");
        try {
          poolService.deletePool(poolService.getPoolById(poolId));
          return JSONUtils.SimpleJSONResponse("Pool deleted");
        } catch (IOException e) {
          log.error("cannot delete pool", e);
          return JSONUtils.SimpleJSONError("Cannot delete pool: " + e.getMessage());
        }
      } else {
        return JSONUtils.SimpleJSONError("No pool specified to delete.");
      }
    } else {
      return JSONUtils.SimpleJSONError("Only logged-in admins can delete objects.");
    }
  }

  public JSONObject deletePoolNote(HttpSession session, JSONObject json) {
    Long poolId = json.getLong("poolId");
    Long noteId = json.getLong("noteId");

    try {
      Pool pool = poolService.getPoolById(poolId);
      poolService.deletePoolNote(pool, noteId);
      return JSONUtils.SimpleJSONResponse("OK");
    } catch (IOException e) {
      log.error("cannot remove note", e);
      return JSONUtils.SimpleJSONError("Cannot remove note: " + e.getMessage());
    }
  }

  public JSONObject addPoolNote(HttpSession session, JSONObject json) {
    Long poolId = json.getLong("poolId");
    String internalOnly = json.getString("internalOnly");
    String text = json.getString("text");

    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Pool pool = poolService.getPoolById(poolId);
      Note note = new Note();

      internalOnly = internalOnly.equals("on") ? "true" : "false";

      note.setInternalOnly(Boolean.parseBoolean(internalOnly));
      note.setText(text);
      note.setOwner(user);
      note.setCreationDate(new Date());
      pool.getNotes().add(note);
      poolService.savePoolNote(pool, note);
      pool.setLastModifier(user);
      poolService.savePool(pool);
    } catch (IOException e) {
      log.error("add pool note", e);
      return JSONUtils.SimpleJSONError(e.getMessage());
    }

    return JSONUtils.SimpleJSONResponse("Note saved successfully");
  }

  public JSONObject addDilutions(HttpSession session, JSONObject json) {
    Long poolId = json.getLong("poolId");
    JSONArray dilutionIds = json.getJSONArray("dilutionIds");
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Pool pool = poolService.getPoolById(poolId);
      if (!pool.userCanWrite(user)) {
        return JSONUtils.SimpleJSONError("Not authorized to modify pool.");
      }
      for (int i = 0; i < dilutionIds.size(); i++) {
        PoolableElementView target = poolableElementViewService.get(dilutionIds.getLong(i));
        if (target == null) {
          return JSONUtils.SimpleJSONError("No such element.");
        }
        pool.getPoolableElementViews().add(target);
      }
      pool.setLastModifier(user);
      poolService.savePool(pool);
      return JSONUtils.SimpleJSONResponse("Pool modified.");
    } catch (IOException e) {
      log.error("Add poolable element", e);
      return JSONUtils.SimpleJSONError(e.getMessage());
    }
  }

  public JSONObject removeDilutions(HttpSession session, JSONObject json) {
    Long poolId = json.getLong("poolId");
    JSONArray dilutionIds = json.getJSONArray("dilutionIds");
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Pool pool = poolService.getPoolById(poolId);
      if (!pool.userCanWrite(user)) {
        return JSONUtils.SimpleJSONError("Not authorized to modify pool.");
      }
      Set<Long> deadIds = new HashSet<>();
      for (int i = 0; i < dilutionIds.size(); i++) {
        deadIds.add(dilutionIds.getLong(i));
      }
      List<PoolableElementView> deadDilutions = new ArrayList<>();
      for (PoolableElementView element : pool.getPoolableElementViews()) {
        if (deadIds.contains(element.getDilutionId())) {
          deadDilutions.add(element);
        }
      }
      if (deadDilutions.size() > 0) {
        pool.getPoolableElementViews().removeAll(deadDilutions);
        pool.setLastModifier(user);
        poolService.savePool(pool);
      }
      return JSONUtils.SimpleJSONResponse("Pool modified.");
    } catch (IOException e) {
      log.error("Remove poolable element", e);
      return JSONUtils.SimpleJSONError(e.getMessage());
    }
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setMisoFileManager(MisoFilesManager misoFileManager) {
    this.misoFileManager = misoFileManager;
  }

  public void setPrinterService(PrinterService printerService) {
    this.printerService = printerService;
  }

  public void setExperimentService(ExperimentService experimentService) {
    this.experimentService = experimentService;
  }

  public void setDilutionService(LibraryDilutionService dilutionService) {
    this.dilutionService = dilutionService;
  }
}

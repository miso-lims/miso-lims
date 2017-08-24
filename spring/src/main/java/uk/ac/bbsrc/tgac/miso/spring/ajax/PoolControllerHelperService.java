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

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;
import static uk.ac.bbsrc.tgac.miso.spring.ControllerHelperServiceUtils.getBarcodeFileLocation;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ExperimentImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedExperimentException;
import uk.ac.bbsrc.tgac.miso.core.factory.barcode.BarcodeFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.service.BoxService;
import uk.ac.bbsrc.tgac.miso.service.ExperimentService;
import uk.ac.bbsrc.tgac.miso.service.LibraryDilutionService;
import uk.ac.bbsrc.tgac.miso.service.PoolService;
import uk.ac.bbsrc.tgac.miso.service.StudyService;
import uk.ac.bbsrc.tgac.miso.service.impl.RunService;

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
  @Autowired
  private MisoFilesManager misoFileManager;
  @Autowired
  private ExperimentService experimentService;
  @Autowired
  private LibraryDilutionService dilutionService;
  @Autowired
  private RunService runService;
  @Autowired
  private StudyService studyService;
  @Autowired
  private BoxService boxService;

  private String processDilutions(Set<String> codes) throws IOException {
    StringBuilder sb = new StringBuilder();
    sb.append("<div id='dilslist' class='checklist' style='width: 100%;'>");
    for (String s : codes) {
      LibraryDilution ed = dilutionService.getByBarcode(s);
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
      Pool pool = poolService.get(poolId);
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

  public JSONObject changePoolIdBarcode(HttpSession session, JSONObject json) {
    Long poolId = json.getLong("poolId");
    String idBarcode = json.getString("identificationBarcode");

    try {
      if (isStringEmptyOrNull(idBarcode)) {
        // if the user accidentally deletes a barcode, the changelogs will have a record of the original barcode
        idBarcode = null;
      } else {
        List<BoxableView> previouslyBarcodedItems = new ArrayList<>(boxService.getViewsFromBarcodeList(Arrays.asList(idBarcode)));
        if (!previouslyBarcodedItems.isEmpty() && (
            previouslyBarcodedItems.size() != 1
                || previouslyBarcodedItems.get(0).getId().getTargetType() != Boxable.EntityType.POOL
                || previouslyBarcodedItems.get(0).getId().getTargetId() != poolId)) {
          BoxableView previouslyBarcodedItem = previouslyBarcodedItems.get(0);
          String error = String.format(
              "Could not change pool identification barcode to '%s'. This barcode is already in use by an item with the name '%s' and the alias '%s'.",
              idBarcode, previouslyBarcodedItem.getName(), previouslyBarcodedItem.getAlias());
          log.debug(error);
          return JSONUtils.SimpleJSONError(error);
        }
      }
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Pool pool = poolService.get(poolId);
      pool.setIdentificationBarcode(idBarcode);
      pool.setLastModifier(user);
      poolService.save(pool);
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
      Pool p = poolService.get(poolId);

      Long studyId = json.getLong("studyId");
      Study s = studyService.get(studyId);

      if (json.has("runId") && json.get("runId") != null) {
        Long runId = json.getLong("runId");
        Run r = runService.get(runId);
        if (r != null) {
          StringBuilder sb = new StringBuilder();

          Experiment e = new ExperimentImpl();
          e.setAlias("EXP_AUTOGEN_" + s.getName() + "_" + s.getStudyType().getName() + "_" + (s.getExperiments().size() + 1));

              e.setTitle(s.getProject().getName() + " " + r.getSequencerReference().getPlatform().getPlatformType().getKey() + " "
                  + s.getStudyType() + " experiment (Auto-gen)");
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
          poolService.delete(poolService.get(poolId));
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
    String internalOnly = json.getString("internalOnly");
    String text = json.getString("text");

    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Pool pool = poolService.get(poolId);
      Note note = new Note();

      internalOnly = internalOnly.equals("on") ? "true" : "false";

      note.setInternalOnly(Boolean.parseBoolean(internalOnly));
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

  public void setMisoFileManager(MisoFilesManager misoFileManager) {
    this.misoFileManager = misoFileManager;
  }

  public void setExperimentService(ExperimentService experimentService) {
    this.experimentService = experimentService;
  }

  public void setDilutionService(LibraryDilutionService dilutionService) {
    this.dilutionService = dilutionService;
  }

  public void setRunService(RunService runService) {
    this.runService = runService;
  }
}

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;

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
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryQCImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.factory.barcode.BarcodeFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.service.BoxService;
import uk.ac.bbsrc.tgac.miso.service.LibraryDilutionService;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.service.PrinterService;
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
public class LibraryControllerHelperService {
  public static final class LibraryDilutionBarcodeAssister implements BarcodePrintAssister<LibraryDilution> {
    private final LibraryDilutionService dilutionService;

    public LibraryDilutionBarcodeAssister(LibraryDilutionService dilutionService) {
      super();
      this.dilutionService = dilutionService;
    }

    @Override
    public LibraryDilution fetch(long id) throws IOException {
      return dilutionService.get(id);
    }

    @Override
    public void store(LibraryDilution item) throws IOException {
      dilutionService.update(item);
    }

    @Override
    public String getGroupName() {
      return "dilutions";
    }

    @Override
    public String getIdName() {
      return "dilutionId";
    }

    @Override
    public Iterable<LibraryDilution> fetchAll(long projectId) throws IOException {
      return dilutionService.list(0, 0, false, "id", PaginationFilter.project(projectId));
    }
  }

  public static final class LibraryBarcodeAssister implements BarcodePrintAssister<Library> {
    private final LibraryService libraryService;

    public LibraryBarcodeAssister(LibraryService libraryService) {
      super();
      this.libraryService = libraryService;
    }

    @Override
    public Library fetch(long id) throws IOException {
      return libraryService.get(id);
    }

    @Override
    public void store(Library library) throws IOException {
      libraryService.update(library);
    }

    @Override
    public String getGroupName() {
      return "libraries";
    }

    @Override
    public String getIdName() {
      return "libraryId";
    }

    @Override
    public Iterable<Library> fetchAll(long projectId) throws IOException {
      return libraryService.listByProjectId(projectId);
    }
  }

  protected static final Logger log = LoggerFactory.getLogger(LibraryControllerHelperService.class);
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private MisoFilesManager misoFileManager;
  @Autowired
  private PrinterService printerService;
  @Autowired
  private NamingScheme namingScheme;
  @Autowired
  private LibraryService libraryService;
  @Autowired
  private LibraryDilutionService dilutionService;
  @Autowired
  private BoxService boxService;

  public JSONObject validateLibraryAlias(HttpSession session, JSONObject json) {
    if (json.has("alias")) {
      String alias = json.getString("alias");
      if (isStringEmptyOrNull(alias) && namingScheme.hasLibraryAliasGenerator()) {
        // alias will be generated by DAO during save
        return JSONUtils.SimpleJSONResponse("OK");
      }
      ValidationResult aliasValidation = namingScheme.validateLibraryAlias(alias);
      if (aliasValidation.isValid()) {
        log.debug("Library alias OK!");
        return JSONUtils.SimpleJSONResponse("OK");
      } else {
        log.error("Library alias not valid: " + alias);
        return JSONUtils.SimpleJSONError(aliasValidation.getMessage());
      }
    } else {
      return JSONUtils.SimpleJSONError("No alias specified");
    }
  }

  public JSONObject addLibraryNote(HttpSession session, JSONObject json) {
    Long libraryId = json.getLong("libraryId");
    String internalOnly = json.getString("internalOnly");
    String text = json.getString("text");

    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Library library = libraryService.get(libraryId);
      Note note = new Note();
      internalOnly = internalOnly.equals("on") ? "true" : "false";
      note.setInternalOnly(Boolean.parseBoolean(internalOnly));
      note.setText(text);
      note.setOwner(user);
      libraryService.addNote(library, note);
    } catch (IOException e) {
      log.error("add library note", e);
      return JSONUtils.SimpleJSONError(e.getMessage());
    }

    return JSONUtils.SimpleJSONResponse("Note saved successfully");
  }

  public JSONObject deleteLibraryNote(HttpSession session, JSONObject json) {
    Long libraryId = json.getLong("libraryId");
    Long noteId = json.getLong("noteId");

    try {
      Library library = libraryService.get(libraryId);
      libraryService.deleteNote(library, noteId);
      return JSONUtils.SimpleJSONResponse("OK");
    } catch (IOException e) {
      log.error("delete library note", e);
      return JSONUtils.SimpleJSONError("Cannot remove note: " + e.getMessage());
    }
  }

  public JSONObject getLibraryBarcode(HttpSession session, JSONObject json) {
    Long libraryId = json.getLong("libraryId");
    File temploc = getBarcodeFileLocation(session);
    try {
      Library library = libraryService.get(libraryId);
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
          bi = barcodeFactory.generateBarcode(library, bg, dim);
        } else {
          return JSONUtils.SimpleJSONError("'" + json.getString("barcodeGenerator") + "' is not a valid barcode generator type");
        }
      } else {
        bi = barcodeFactory.generateSquareDataMatrix(library, 400);
      }

      if (bi != null) {
        File tempimage = misoFileManager.generateTemporaryFile("barcode-", ".png", temploc);
        if (ImageIO.write(bi, "png", tempimage)) {
          return JSONUtils.JSONObjectResponse("img", tempimage.getName());
        }
        return JSONUtils.SimpleJSONError("Writing temp image file failed.");
      } else {
        return JSONUtils.SimpleJSONError("Library has no parseable barcode");
      }
    } catch (IOException e) {
      log.error("get library barcode", e);
      return JSONUtils.SimpleJSONError(e.getMessage() + ": Cannot seem to generate temp file for barcode");
    }
  }

  public JSONObject printLibraryBarcodes(HttpSession session, JSONObject json) {
    return ControllerHelperServiceUtils.printBarcodes(printerService, json, new LibraryBarcodeAssister(libraryService));
  }

  public JSONObject printLibraryDilutionBarcodes(HttpSession session, JSONObject json) {
    return ControllerHelperServiceUtils.printBarcodes(printerService, json,
        new LibraryDilutionBarcodeAssister(dilutionService));
  }

  public JSONObject changeLibraryLocation(HttpSession session, JSONObject json) {
    Long libraryId = json.getLong("libraryId");
    String locationBarcode = json.getString("locationBarcode");

    try {
      String newLocation = LimsUtils.lookupLocation(locationBarcode);
      if (newLocation != null) {
        Library library = libraryService.get(libraryId);
        library.setLocationBarcode(newLocation);
        libraryService.update(library);
      } else {
        return JSONUtils.SimpleJSONError("New location barcode not recognised");
      }
    } catch (IOException e) {
      log.error("change library location", e);
      return JSONUtils.SimpleJSONError(e.getMessage());
    }

    return JSONUtils.SimpleJSONResponse("Note saved successfully");
  }

  public JSONObject changeLibraryIdBarcode(HttpSession session, JSONObject json) {
    Long libraryId = json.getLong("libraryId");
    String idBarcode = json.getString("identificationBarcode");

    try {
      if (isStringEmptyOrNull(idBarcode)) {
        // if the user accidentally deletes a barcode, the changelogs will have a record of the original barcode
        idBarcode = null;
      } else {
        List<BoxableView> previouslyBarcodedItems = new ArrayList<>(boxService.getViewsFromBarcodeList(Arrays.asList(idBarcode)));
        if (!previouslyBarcodedItems.isEmpty() && (
            previouslyBarcodedItems.size() != 1
                || previouslyBarcodedItems.get(0).getId().getTargetType() != Boxable.EntityType.LIBRARY
                || previouslyBarcodedItems.get(0).getId().getTargetId() != libraryId)) {
          BoxableView previouslyBarcodedItem = previouslyBarcodedItems.get(0);
          String error = String.format(
              "Could not change library identification barcode to '%s'. This barcode is already in use by an item with the name '%s' and the alias '%s'.",
              idBarcode, previouslyBarcodedItem.getName(), previouslyBarcodedItem.getAlias());
          log.debug(error);
          return JSONUtils.SimpleJSONError(error);
        }
      }
      Library library = libraryService.get(libraryId);
      library.setIdentificationBarcode(idBarcode);
      libraryService.update(library);
    } catch (IOException e) {
      log.debug("Could not change Library identificationBarcode: " + e.getMessage());
      return JSONUtils.SimpleJSONError(e.getMessage());
    }

    return JSONUtils.SimpleJSONResponse("New identification barcode successfully assigned.");
  }

  public JSONObject getLibraryQcTypes(HttpSession session, JSONObject json) {
    try {
      StringBuilder sb = new StringBuilder();
      Collection<QcType> types = libraryService.listLibraryQcTypes();
      for (QcType s : types) {
        sb.append("<option units='" + s.getUnits() + "' value='" + s.getQcTypeId() + "'>" + s.getName() + "</option>");
      }
      Map<String, Object> map = new HashMap<>();
      map.put("types", sb.toString());
      return JSONUtils.JSONObjectResponse(map);
    } catch (IOException e) {
      log.error("cannot list all library QC types", e);
    }
    return JSONUtils.SimpleJSONError("Cannot list all Library QC Types");
  }

  public JSONObject addLibraryQC(HttpSession session, JSONObject json) {
    try {
      for (Object k : json.keySet()) {
        String key = (String) k;
        if (isStringEmptyOrNull(json.getString(key))) {
          return JSONUtils.SimpleJSONError("Please enter a value for '" + key + "'");
        }
      }
      if (json.has("libraryId") && !isStringEmptyOrNull(json.getString("libraryId"))) {
        Long libraryId = Long.parseLong(json.getString("libraryId"));
        Library library = libraryService.get(libraryId);
        LibraryQC newQc = new LibraryQCImpl();
        newQc.setQcDate(new SimpleDateFormat("dd/MM/yyyy").parse(json.getString("qcDate")));
        newQc.setQcType(libraryService.getLibraryQcType(json.getLong("qcType")));
        newQc.setResults(Double.parseDouble(json.getString("results")));
        libraryService.addQc(library, newQc);

        StringBuilder sb = new StringBuilder();
        sb.append("<tr><th>QCed By</th><th>QC Date</th><th>Method</th><th>Results</th></tr>");
        for (LibraryQC qc : library.getLibraryQCs()) {
          sb.append("<tr>");
          sb.append("<td>" + qc.getQcCreator() + "</td>");
          sb.append("<td>" + qc.getQcDate() + "</td>");
          sb.append("<td>" + qc.getQcType().getName() + "</td>");
          sb.append("<td>" + LimsUtils.round(qc.getResults(), 2) + " " + qc.getQcType().getUnits() + "</td>");
          sb.append("</tr>");
        }
        return JSONUtils.SimpleJSONResponse(sb.toString());
      }
    } catch (Exception e) {
      log.error("Failed to add Library QC to this Library: ", e);
      return JSONUtils.SimpleJSONError("Failed to add Library QC to this Library: " + e.getMessage());
    }
    return JSONUtils.SimpleJSONError("Cannot add LibraryQC");
  }

  public JSONObject bulkAddLibraryQCs(HttpSession session, JSONObject json) {
    try {
      JSONArray qcs = JSONArray.fromObject(json.getString("qcs"));
      // validate
      boolean ok = true;
      for (JSONObject qc : (Iterable<JSONObject>) qcs) {
        String qcType = qc.getString("qcType");
        String results = qc.getString("results");
        String qcCreator = qc.getString("qcCreator");
        String qcDate = qc.getString("qcDate");

        if (isStringEmptyOrNull(qcType) || isStringEmptyOrNull(results) || isStringEmptyOrNull(qcCreator) || isStringEmptyOrNull(qcDate)) {
          ok = false;
        }
      }

      // persist
      if (ok) {
        Map<String, Object> map = new HashMap<>();
        JSONArray a = new JSONArray();
        JSONArray errors = new JSONArray();
        for (JSONObject qc : (Iterable<JSONObject>) qcs) {
          JSONObject j = addLibraryQC(session, qc);
          j.put("libraryId", qc.getString("libraryId"));
          if (j.has("error")) {
            errors.add(j);
          } else {
            a.add(j);
          }
        }
        map.put("saved", a);
        if (!errors.isEmpty()) {
          map.put("errors", errors);
        }
        return JSONUtils.JSONObjectResponse(map);
      } else {
        log.error("Failed to add Library QC to this Library: one of the required fields of the selected QCs is missing or invalid");
        return JSONUtils.SimpleJSONError(
            "Failed to add Library QC to this Library: one of the required fields of the selected QCs is missing or invalid");
      }
    } catch (Exception e) {
      log.error("Failed to add Library QC to this Library: ", e);
      return JSONUtils.SimpleJSONError("Failed to add Library QC to this Library: " + e.getMessage());
    }
  }

  public JSONObject changeLibraryQCRow(HttpSession session, JSONObject json) {
    try {
      JSONObject response = new JSONObject();
      Long qcId = Long.parseLong(json.getString("qcId"));
      LibraryQC libraryQc = libraryService.getLibraryQC(qcId);
      Long libraryId = Long.parseLong(json.getString("libraryId"));

      response.put("results", "<input type='text' id='results" + qcId + "' value='" + libraryQc.getResults() + "'/>");
      response.put("edit",
          "<a href='javascript:void(0);' onclick='Library.qc.editLibraryQC(\"" + qcId + "\",\"" + libraryId + "\");'>Save</a>");
      return response;
    } catch (Exception e) {
      log.error("Failed to display library QC of this library: ", e);
      return JSONUtils.SimpleJSONError("Failed to display library QC of this library: " + e.getMessage());
    }
  }

  public JSONObject editLibraryQC(HttpSession session, JSONObject json) {
    try {
      if (json.has("qcId") && !isStringEmptyOrNull(json.getString("qcId"))) {
        Long qcId = Long.parseLong(json.getString("qcId"));
        LibraryQC libraryQc = libraryService.getLibraryQC(qcId);

        libraryQc.setResults(Double.parseDouble(json.getString("result")));
        libraryService.addQc(libraryService.get(libraryQc.getLibrary().getId()), libraryQc);

      }
      return JSONUtils.SimpleJSONResponse("done");
    } catch (Exception e) {
      log.error("Failed to add library QC to this library: ", e);
      return JSONUtils.SimpleJSONError("Failed to add library QC to this library: " + e.getMessage());
    }
  }

  public JSONObject deleteLibrary(HttpSession session, JSONObject json) {
    if (json.has("libraryId")) {
      Long libraryId = json.getLong("libraryId");
      try {
        libraryService.delete(libraryService.get(libraryId));
        return JSONUtils.SimpleJSONResponse("Library deleted");
      } catch (IOException e) {
        log.error("cannot delete library", e);
        return JSONUtils.SimpleJSONError("Cannot delete library: " + e.getMessage());
      }
    } else {
      return JSONUtils.SimpleJSONError("No library specified to delete.");
    }
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setMisoFileManager(MisoFilesManager misoFileManager) {
    this.misoFileManager = misoFileManager;
  }

  public void setPrinterService(PrinterService printerService) {
    this.printerService = printerService;
  }

  public void setLibraryNamingScheme(NamingScheme namingScheme) {
    this.namingScheme = namingScheme;
  }

  public void setLibraryService(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

  public void setDilutionService(LibraryDilutionService dilutionService) {
    this.dilutionService = dilutionService;
  }
}

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
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;

import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryQCImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.factory.barcode.BarcodeFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.IndexStore;
import uk.ac.bbsrc.tgac.miso.core.util.AliasComparator;
import uk.ac.bbsrc.tgac.miso.core.util.DilutionPaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.integration.context.ApplicationContextProvider;
import uk.ac.bbsrc.tgac.miso.service.KitService;
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
      DilutionPaginationFilter filter = new DilutionPaginationFilter();
      filter.setProjectId(projectId);
      return dilutionService.list(filter, 0, 0, false, "id");
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
  private RequestManager requestManager;
  @Autowired
  private MisoFilesManager misoFileManager;
  @Autowired
  private PrinterService printerService;
  @Autowired
  private IndexStore indexStore;
  @Autowired
  private NamingScheme namingScheme;
  @Autowired
  private LibraryService libraryService;
  @Autowired
  private LibraryDilutionService dilutionService;
  @Autowired
  private KitService kitService;

  public JSONObject validateLibraryAlias(HttpSession session, JSONObject json) {
    if (json.has("alias")) {
      String alias = json.getString("alias");
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
      note.setCreationDate(new Date());
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
        List<Boxable> previouslyBarcodedItems = new ArrayList<>(requestManager.getBoxablesFromBarcodeList(Arrays.asList(idBarcode)));
        if (!previouslyBarcodedItems.isEmpty()
            && !(previouslyBarcodedItems.size() == 1 && previouslyBarcodedItems.get(0).getId() == libraryId)) {
          Boxable previouslyBarcodedItem = previouslyBarcodedItems.get(0);
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

  public JSONObject getLibraryDilutionBarcode(HttpSession session, JSONObject json) {
    Long dilutionId = json.getLong("dilutionId");
    File temploc = getBarcodeFileLocation(session);
    try {
      LibraryDilution dil = dilutionService.get(dilutionId);
      BarcodeFactory barcodeFactory = new BarcodeFactory();
      barcodeFactory.setPointPixels(1.5f);
      barcodeFactory.setBitmapResolution(600);
      RenderedImage bi = barcodeFactory.generateSquareDataMatrix(dil, 400);
      if (bi != null) {
        File tempimage = misoFileManager.generateTemporaryFile("barcode-", ".png", temploc);
        if (ImageIO.write(bi, "png", tempimage)) {
          return JSONUtils.JSONObjectResponse("img", tempimage.getName());
        }
        return JSONUtils.SimpleJSONError("Writing temp image file failed.");
      } else {
        return JSONUtils.SimpleJSONError("Dilution has no parseable barcode");
      }
    } catch (IOException e) {
      log.error("library dilution barcode", e);
      return JSONUtils.SimpleJSONError(e.getMessage() + ": Cannot seem to generate temp file for barcode");
    }
  }

  public JSONObject bulkSaveLibraries(HttpSession session, JSONObject json) {
    if (json.has("libraries")) {
      try {
        User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
        Project p = requestManager.getProjectById(json.getLong("projectId"));
        JSONArray a = JSONArray.fromObject(json.get("libraries"));
        Set<Library> saveSet = new HashSet<>();

        for (JSONObject j : (Iterable<JSONObject>) a) {
          try {
            SecurityProfile sp = null;
            Sample sample = null;
            String sampleAlias = j.getString("parentSample");

            for (Sample s : p.getSamples()) {
              if (s.getAlias().equals(sampleAlias)) {
                sp = s.getSecurityProfile();
                sample = s;
                break;
              }
            }

            if (sample != null) {
              String descr = j.getString("description");
              String platform = j.getString("platform");
              String type = j.getString("libraryType");
              String selectionType = j.getString("selectionType");
              String strategyType = j.getString("strategyType");
              String locationBarcode = j.getString("locationBarcode");

              Library library = new LibraryImpl();
              library.setSample(sample);

              String libAlias = namingScheme.generateLibraryAlias(library);

              library.setAlias(libAlias);
              library.setSecurityProfile(sp);
              library.setDescription(descr);
              library.setPlatformType(platform);
              library.setCreationDate(new Date());
              library.setLocationBarcode(locationBarcode);
              library.setQcPassed(false);
              library
                  .setLibraryType(
                      libraryService.getLibraryTypeByDescriptionAndPlatform(type, library.getPlatformType()));
              library.setLibrarySelectionType(libraryService.getLibrarySelectionTypeByName(selectionType));
              library.setLibraryStrategyType(libraryService.getLibraryStrategyTypeByName(strategyType));
              library.setLastModifier(user);

              boolean paired = false;
              if (!isStringEmptyOrNull(j.getString("paired"))) {
                paired = j.getBoolean("paired");
              }
              library.setPaired(paired);

              if (j.has("indices") && !isStringEmptyOrNull(j.getString("indices")) && !j.getString("indices").contains("Select")) {
                String[] codes = j.getString("indices").split(Pattern.quote("|"));
                List<Index> indices = new ArrayList<>();
                for (String code : codes) {
                  try {
                    long cl = Long.parseLong(code);
                    Index index = indexStore.getIndexById(cl);
                    indices.add(index);
                  } catch (NumberFormatException e) {
                    log.error("cannot save library", e);
                    return JSONUtils.SimpleJSONError("Cannot save Library. It looks like there are indices for the library of "
                        + sample.getAlias() + ", but they cannot be processed");
                  }
                }
                library.setIndices(indices);
              }

              saveSet.add(library);
            } else {
              throw new IOException("Could not find a selected Sample to generate Libraries.");
            }
          } catch (IOException e) {
            log.error("cannot save library", e);
            return JSONUtils.SimpleJSONError("Cannot save Library generated from " + j.getString("parentSample") + ": " + e.getMessage());
          } catch (JSONException e) {
            log.error("cannot save library", e);
            return JSONUtils
                .SimpleJSONError("Cannot save Library. Something cannot be retrieved from the bulk input table: " + e.getMessage());
          }
        }

        try {
          List<Library> sortedList = new ArrayList<>(saveSet);
          Collections.sort(sortedList, new AliasComparator<>());
          for (Library library : sortedList) {
            libraryService.create(library);
          }
        } catch (Exception e) {
          log.error("Error saving bulk libraries", e);
          return JSONUtils.SimpleJSONError("Error saving libraries, please contact your administrator.");
        }

        return JSONUtils.SimpleJSONResponse("All libraries saved successfully");
      } catch (Exception e) {
        log.error("cannot retrieve parent project", e);
        return JSONUtils.SimpleJSONError("Cannot retrieve parent project with ID " + json.getLong("projectId"));
      }
    } else {
      return JSONUtils.SimpleJSONError("No libraries specified");
    }
  }

  public JSONObject changePlatformName(HttpSession session, JSONObject json) {
    // For whatever reason, Fluxion doesn't autowire this class correctly, so, we do it again.
    if (indexStore == null) {
      ApplicationContext ctxt = ApplicationContextProvider.getApplicationContext();
      ctxt.getAutowireCapableBeanFactory().autowireBean(this);
    }
    try {
      if (json.has("platform") && !isStringEmptyOrNull(json.getString("platform"))) {
        String platform = json.getString("platform");
        long originalLibraryTypeId = json.has("originalLibraryTypeId") ? json.getLong("originalLibraryTypeId") : 0L;

        JSONObject result = new JSONObject();

        StringBuilder libsb = new StringBuilder();
        List<LibraryType> types = new ArrayList<>();
        for (LibraryType type : libraryService.listLibraryTypesByPlatform(PlatformType.get(platform))) {
          if (!type.getArchived() || type.getId() == originalLibraryTypeId) {
            types.add(type);
          }
        }
        Collections.sort(types);
        for (LibraryType s : types) {
          libsb.append("<option value='" + s.getId() + "'>" + s.getDescription() + "</option>");
        }
        result.put("libraryTypes", libsb.toString());

        JSONArray families = new JSONArray();
        JsonConfig config = new JsonConfig();
        config.setExcludes(new String[] { "family" });
        families.add(IndexFamily.NULL);
        families.addAll(indexStore.getIndexFamiliesByPlatform(PlatformType.get(platform)), config);
        result.put("indexFamilies", families);

        return result;
      }
    } catch (IOException e) {
      log.error("Failed to retrieve library types given platform type: ", e);
      return JSONUtils.SimpleJSONError("Failed to retrieve library types given platform type: " + e.getMessage());
    }
    return JSONUtils.SimpleJSONError("Cannot resolve LibraryType from selected Platform");
  }

  public JSONObject getIndicesForFamily(HttpSession session, JSONObject json) {
    if (json.has("indexFamily")) {
      IndexFamily ifam = indexStore.getIndexFamilyByName(json.getString("indexFamily"));
      if (ifam != null) {
        Map<String, Object> map = new HashMap<>();
        StringBuilder indexsb = new StringBuilder();
        for (int i = 1; i <= ifam.getMaximumNumber(); i++) {
          // select
          indexsb.append("Index " + i + ": " + "<select id='indices[\"" + i + "\"]' name='indices[\"" + i + "\"]'>");
          indexsb.append("<option value=''>No Index</option>");
          for (Index tb : ifam.getIndicesForPosition(i)) {
            // option
            indexsb.append("<option value='" + tb.getId() + "'>" + tb.getName() + "</option>");
          }
          indexsb.append("</select><br/>");
          indexsb.append("<input type='hidden' value='on' name='indices[\"" + i + "\"]'/>");
        }
        map.put("indices", indexsb.toString());
        return JSONUtils.JSONObjectResponse(map);
      } else {
        return JSONUtils.SimpleJSONError("No such Index Family: " + json.getString("indexFamily"));
      }
    } else {
      return JSONUtils.SimpleJSONError("No valid Index Family selected");
    }
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

  public JSONObject getTargetedSequencingTypes(HttpSession session, JSONObject json) {
    Long libraryPrepKitId = null;
    if (json.has("libraryPrepKitId")) {
      libraryPrepKitId = json.getLong("libraryPrepKitId");
    }

    Map<String, Object> targetedSequencingTypes = Maps.newHashMap();
    try {
      if (libraryPrepKitId != null) {
        targetedSequencingTypes.put("targetedSequencings", getTargetedSequencingTypes(libraryPrepKitId));
      } else {
        targetedSequencingTypes.put("targetedSequencings", getTargetedSequencingTypes());
      }
      return JSONUtils.JSONObjectResponse(targetedSequencingTypes);
    } catch (IOException e) {
      log.error("Cannot list all Targeted Sequencing entries ", e);
    }
    return JSONUtils.SimpleJSONError("Cannot list all Targeted Sequencing entries");
  }

  public JSONArray getTargetedSequencingTypes() throws IOException {
    Collection<TargetedSequencing> targetedSequencings = getNonArchivedTargetedSequencing(
        requestManager.listAllTargetedSequencing());
    JSONArray fullTargetedSequencingCollection = new JSONArray();

    for (TargetedSequencing targetedSequencing : targetedSequencings) {
      for (KitDescriptor kitDescriptor : targetedSequencing.getKitDescriptors()) {
        Map<String, Object> targetedSequencingMap = Maps.newHashMap();
        targetedSequencingMap.put("targetedSequencingId", targetedSequencing.getId());
        targetedSequencingMap.put("alias", targetedSequencing.getAlias());
        targetedSequencingMap.put("kitDescriptorId", kitDescriptor.getId());
        fullTargetedSequencingCollection.add(targetedSequencingMap);
      }
    }

    return fullTargetedSequencingCollection;
  }

  public JSONArray getTargetedSequencingTypes(Long libraryPrepKitId) throws IOException {
    JSONArray targetedSequencingByKit = new JSONArray();
    KitDescriptor kd = kitService.getKitDescriptorById(libraryPrepKitId);
    for (TargetedSequencing targetedSequencing : getNonArchivedTargetedSequencing(
        kd.getTargetedSequencing())) {
      Map<String, Object> targetedSequencingMap = Maps.newHashMap();
      targetedSequencingMap.put("targetedSequencingId", targetedSequencing.getId());
      targetedSequencingMap.put("alias", targetedSequencing.getAlias());
      targetedSequencingMap.put("kitDescriptorId", libraryPrepKitId);
      targetedSequencingByKit.add(targetedSequencingMap);
    }

    return targetedSequencingByKit;
  }

  private Collection<TargetedSequencing> getNonArchivedTargetedSequencing(Collection<TargetedSequencing> targetedSequencings) {
    List<TargetedSequencing> result = Lists.newArrayList();
    for (TargetedSequencing targetedSequencing : targetedSequencings) {
      if (!targetedSequencing.isArchived()) {
        result.add(targetedSequencing);
      }
    }
    return result;
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
        newQc.setQcType(requestManager.getLibraryQcTypeById(json.getLong("qcType")));
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

  public JSONObject addLibraryDilution(HttpSession session, JSONObject json) {
    try {
      for (Object k : json.keySet()) {
        String key = (String) k;
        if (isStringEmptyOrNull(json.getString(key)) && !key.equals("idBarcode")) {
          return JSONUtils.SimpleJSONError("Please enter a value for '" + key + "'");
        }
      }
      if (json.has("libraryId") && !isStringEmptyOrNull(json.getString("libraryId"))) {
        boolean autoGenerateIdBarcodes = json.getBoolean("autoGenerateIdBarcodes");
        boolean detailedSample = json.getBoolean("detailedSample");
        Long libraryId = Long.parseLong(json.getString("libraryId"));
        Library library = libraryService.get(libraryId);
        LibraryDilution newDilution = new LibraryDilution();
        newDilution.setCreationDate(new SimpleDateFormat("dd/MM/yyyy").parse(json.getString("dilutionDate")));
        newDilution.setLastUpdated(newDilution.getCreationDate());
        newDilution.setConcentration(Double.parseDouble(json.getString("results")));
        if (json.has("targetedSequencing")) {
          Long libraryDilutionTargetedSequencingId = Long.parseLong(json.getString("targetedSequencing"));
          if (libraryDilutionTargetedSequencingId > 0) {
            TargetedSequencing targetedSequencing = requestManager.getTargetedSequencingById(libraryDilutionTargetedSequencingId);
            newDilution.setTargetedSequencing(targetedSequencing);
          }
        }
        if (json.has("idBarcode")) {
          newDilution.setIdentificationBarcode(json.getString("idBarcode"));
        }
        library.addDilution(newDilution);
        dilutionService.create(newDilution);

        StringBuilder sb = new StringBuilder();
        sb.append("<tr>");
        sb.append("<th>LD Name</th><th>Done By</th><th>Date</th><th>Results</th>");
        if (json.has("targetedSequencing")) {
          sb.append("<th>Targeted Sequencing</th>");
        }
        sb.append("<th>ID barcode</th>");
        sb.append("</tr>");

        File temploc = getBarcodeFileLocation(session);
        for (LibraryDilution dil : library.getLibraryDilutions()) {
          SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
          sb.append("<tr>");
          sb.append("<td>" + dil.getName() + "</td>");
          sb.append("<td>" + dil.getDilutionCreator() + "</td>");
          sb.append("<td>" + date.format(dil.getCreationDate()) + "</td>");
          sb.append("<td>" + LimsUtils.round(dil.getConcentration(), 2) + " " + dil.getUnits() + "</td>");
          if (json.has("targetedSequencing")) {
            sb.append("<td>");
            if (dil.getTargetedSequencing() == null) {
              sb.append("NONE");
            } else {
              sb.append(dil.getTargetedSequencing().getAlias());
            }
            sb.append("</td>");
          }
          sb.append("<td>");

          try {
            BarcodeFactory barcodeFactory = new BarcodeFactory();

            barcodeFactory.setPointPixels(1.5f);
            barcodeFactory.setBitmapResolution(600);
            RenderedImage bi = barcodeFactory.generateSquareDataMatrix(dil, 400);
            if (bi != null) {
              File tempimage = misoFileManager.generateTemporaryFile("barcode-", ".png", temploc);
              if (ImageIO.write(bi, "png", tempimage)) {
                sb.append("<img style='border:0;' src='/temp/" + tempimage.getName() + "'/>");
              }
            }
          } catch (IOException e) {
            log.error("Error generating library dilution barcode", e);
          }
          sb.append("</td>");
          sb.append("<td id='edit").append(dil.getId()).append("' align='center'>");
          sb.append("<a href='javascript:void(0);' onclick='Library.dilution.changeLibraryDilutionRow(");
          sb.append(dil.getId()).append(", ").append(autoGenerateIdBarcodes).append(", ");
          sb.append(detailedSample).append(")'>");
          sb.append("<span class='ui-icon ui-icon-pencil'></span></a></td>");

          sb.append(
              "<td><a href='/miso/poolwizard/new/" + library.getSample().getProject().getProjectId() + "'>Construct New Pool</a></td>");

          sb.append("</tr>");
        }
        return JSONUtils.SimpleJSONResponse(sb.toString());
      }
    } catch (Exception e) {
      log.error("Failed to add Library Dilution to this Library: ", e);
      return JSONUtils.SimpleJSONError("Failed to add Library Dilution to this Library: " + e.getMessage());
    }
    return JSONUtils.SimpleJSONError("Cannot add LibraryDilution");
  }

  public JSONObject bulkAddLibraryDilutions(HttpSession session, JSONObject json) {
    try {
      JSONArray dilutions = JSONArray.fromObject(json.getString("dilutions"));
      // validate
      boolean ok = true;
      for (JSONObject dil : (Iterable<JSONObject>) dilutions) {
        String results = dil.getString("results");
        String dilutionCreator = dil.getString("dilutionCreator");
        String dilutionDate = dil.getString("dilutionDate");

        if (isStringEmptyOrNull(results) || isStringEmptyOrNull(dilutionCreator) || isStringEmptyOrNull(dilutionDate)) {
          ok = false;
        }
      }

      // persist
      if (ok) {
        Map<String, Object> map = new HashMap<>();
        JSONArray a = new JSONArray();
        JSONArray errors = new JSONArray();
        for (JSONObject dil : (Iterable<JSONObject>) dilutions) {
          JSONObject j = addLibraryDilution(session, dil);
          j.put("libraryId", dil.getString("libraryId"));
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
        log.error(
            "Failed to add Library Dilutions to this Library: one of the required fields of the selected Library Dilutions is missing or invalid");
        return JSONUtils.SimpleJSONError(
            "Failed to add Library Dilutions to this Library: one of the required fields of the selected Library Dilutions is missing or invalid");
      }
    } catch (Exception e) {
      log.error("Failed to add Library Dilutions to this Library: ", e);
      return JSONUtils.SimpleJSONError("Failed to add Library Dilutions to this Library: " + e.getMessage());
    }
  }

  public JSONObject changeLibraryDilutionRow(HttpSession session, JSONObject json) {
    try {
      JSONObject response = new JSONObject();
      Long dilutionId = Long.parseLong(json.getString("dilutionId"));
      LibraryDilution dilution = dilutionService.get(dilutionId);
      response.put("results", "<input type='text' id='" + dilutionId + "' value='" + dilution.getConcentration() + "'/>");
      if (!json.getBoolean("autoGenerateIdBarcodes")) {
        response.put("idBarcode",
            "<input type='text' id='idBarcodeValue" + dilutionId + "' value='"
                + (isStringEmptyOrNull(dilution.getIdentificationBarcode()) ? "" : dilution.getIdentificationBarcode()) + "'/>");
      }
      if (LimsUtils.isDetailedLibrary(dilution.getLibrary()) && ((DetailedLibrary) dilution.getLibrary()).getKitDescriptor() != null
          && ((DetailedLibrary) dilution.getLibrary()).getKitDescriptor().getId() != null && json.getBoolean("detailedSample")) {
        response.put("targetedSequencings",
            getTargetedSequencingTypes(((DetailedLibrary) dilution.getLibrary()).getKitDescriptor().getId()));
      }
      response.put("edit",
          "<a href='javascript:void(0);' onclick='Library.dilution.editLibraryDilution(\"" + dilutionId + "\", "
              + json.getBoolean("autoGenerateIdBarcodes") + ", " + json.getBoolean("detailedSample") + ");'>Save</a>");
      return response;
    } catch (Exception e) {
      log.error("Failed to display Library Dilution of this Library: ", e);
      return JSONUtils.SimpleJSONError("Failed to display Library Dilution of this sample: " + e.getMessage());
    }
  }

  public JSONObject editLibraryDilution(HttpSession session, JSONObject json) {
    try {
      if (json.has("dilutionId") && !isStringEmptyOrNull(json.getString("dilutionId"))) {
        Long dilutionId = Long.parseLong(json.getString("dilutionId"));
        LibraryDilution dilution = dilutionService.get(dilutionId);
        dilution.setConcentration(Double.parseDouble(json.getString("result")));
        if (json.has("targetedSequencing")) {
          if (isStringEmptyOrNull(json.getString("targetedSequencing"))) {
            dilution.setTargetedSequencing(null);
          } else {
            dilution.setTargetedSequencing(requestManager.getTargetedSequencingById(json.getLong("targetedSequencing")));
          }
        }
        if (json.has("idBarcode")) dilution.setIdentificationBarcode(json.getString("idBarcode"));
        dilutionService.update(dilution);
        return JSONUtils.SimpleJSONResponse("OK");
      }
    } catch (Exception e) {
      log.error("Failed to edit Library Dilution of this Library: ", e);
      return JSONUtils.SimpleJSONError("Failed to edit Library Dilution of this Library: " + e.getMessage());
    }
    return JSONUtils.SimpleJSONError("Cannot add LibraryDilution");
  }

  public JSONObject changeLibraryQCRow(HttpSession session, JSONObject json) {
    try {
      JSONObject response = new JSONObject();
      Long qcId = Long.parseLong(json.getString("qcId"));
      LibraryQC libraryQc = requestManager.getLibraryQCById(qcId);
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
        LibraryQC libraryQc = requestManager.getLibraryQCById(qcId);

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

  public JSONObject deleteLibraryDilution(HttpSession session, JSONObject json) {
    if (json.has("libraryDilutionId")) {
      Long libraryDilutionId = json.getLong("libraryDilutionId");
      try {
        dilutionService.delete(dilutionService.get(libraryDilutionId));
        return JSONUtils.SimpleJSONResponse("LibraryDilution deleted");
      } catch (IOException e) {
        log.error("delete library dilution", e);
        return JSONUtils.SimpleJSONError("Cannot delete library dilution: " + e.getMessage());
      }
    } else {
      return JSONUtils.SimpleJSONError("No library dilution specified to delete.");
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

  public void setLibraryNamingScheme(NamingScheme namingScheme) {
    this.namingScheme = namingScheme;
  }

  public void setIndexStore(IndexStore indexStore) {
    this.indexStore = indexStore;
  }

  public void setLibraryService(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

  public void setDilutionService(LibraryDilutionService dilutionService) {
    this.dilutionService = dilutionService;
  }
}

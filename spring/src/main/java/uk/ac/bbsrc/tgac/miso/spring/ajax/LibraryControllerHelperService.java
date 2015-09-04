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

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.PrintJob;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.TagBarcode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCR;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoPrintException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.factory.barcode.BarcodeFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.manager.PrintManager;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.printing.MisoPrintService;
import uk.ac.bbsrc.tgac.miso.core.service.printing.context.PrintContext;
import uk.ac.bbsrc.tgac.miso.core.service.tagbarcode.TagBarcodeStrategy;
import uk.ac.bbsrc.tgac.miso.core.service.tagbarcode.TagBarcodeStrategyResolverService;
import uk.ac.bbsrc.tgac.miso.core.util.AliasComparator;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

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
  protected static final Logger log = LoggerFactory.getLogger(LibraryControllerHelperService.class);
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private RequestManager requestManager;
  @Autowired
  private DataObjectFactory dataObjectFactory;
  @Autowired
  private BarcodeFactory barcodeFactory;
  @Autowired
  private MisoFilesManager misoFileManager;
  @Autowired
  private PrintManager<MisoPrintService, Queue<?>> printManager;
  @Autowired
  private TagBarcodeStrategyResolverService tagBarcodeStrategyResolverService;
  @Autowired
  private MisoNamingScheme<Sample> sampleNamingScheme;
  @Autowired
  private MisoNamingScheme<Library> libraryNamingScheme;

  public JSONObject validateLibraryAlias(HttpSession session, JSONObject json) {
    if (json.has("alias")) {
      String alias = json.getString("alias");
      try {
        if (libraryNamingScheme.validateField("alias", alias)) {
          log.debug("Library alias OK!");
          return JSONUtils.SimpleJSONResponse("OK");
        } else {
          log.error("Library alias not valid: " + alias);
          return JSONUtils.SimpleJSONError("The following Library alias doesn't conform to the chosen naming scheme ("
              + libraryNamingScheme.getValidationRegex("alias") + ") or already exists: " + json.getString("alias"));
        }
      } catch (MisoNamingException e) {
        log.error("Cannot validate Library alias " + json.getString("alias") + ": " + e.getMessage());
        e.printStackTrace();
        return JSONUtils.SimpleJSONError("Cannot validate Library alias " + json.getString("alias") + ": " + e.getMessage());
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
      Library library = requestManager.getLibraryById(libraryId);
      Note note = new Note();
      internalOnly = internalOnly.equals("on") ? "true" : "false";
      note.setInternalOnly(Boolean.parseBoolean(internalOnly));
      note.setText(text);
      note.setOwner(user);
      note.setCreationDate(new Date());
      library.getNotes().add(note);
      requestManager.saveLibraryNote(library, note);
      requestManager.saveLibrary(library);
    } catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError(e.getMessage());
    }

    return JSONUtils.SimpleJSONResponse("Note saved successfully");
  }

  public JSONObject deleteLibraryNote(HttpSession session, JSONObject json) {
    Long libraryId = json.getLong("libraryId");
    Long noteId = json.getLong("noteId");

    try {
      Library library = requestManager.getLibraryById(libraryId);
      Note note = requestManager.getNoteById(noteId);
      if (library.getNotes().contains(note)) {
        library.getNotes().remove(note);
        requestManager.deleteNote(note);
        requestManager.saveLibrary(library);
        return JSONUtils.SimpleJSONResponse("OK");
      } else {
        return JSONUtils.SimpleJSONError("Library does not have note " + noteId + ". Cannot remove");
      }
    } catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Cannot remove note: " + e.getMessage());
    }
  }

  public JSONObject getLibraryBarcode(HttpSession session, JSONObject json) {
    Long libraryId = json.getLong("libraryId");
    File temploc = new File(session.getServletContext().getRealPath("/") + "temp/");
    try {
      Library library = requestManager.getLibraryById(libraryId);
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
      e.printStackTrace();
      return JSONUtils.SimpleJSONError(e.getMessage() + ": Cannot seem to generate temp file for barcode");
    }
  }

  public JSONObject printLibraryBarcodes(HttpSession session, JSONObject json) {
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
        } else {
          return JSONUtils
              .SimpleJSONError("No serviceName specified, but more than one available service able to print this barcode type.");
        }
      } else {
        mps = printManager.getPrintService(serviceName);
      }

      Queue<File> thingsToPrint = new LinkedList<File>();

      JSONArray ls = JSONArray.fromObject(json.getString("libraries"));
      for (JSONObject l : (Iterable<JSONObject>) ls) {
        try {
          Long libraryId = l.getLong("libraryId");
          Library library = requestManager.getLibraryById(libraryId);
          // autosave the barcode if none has been previously generated
          if (library.getIdentificationBarcode() == null || "".equals(library.getIdentificationBarcode())) {
            requestManager.saveLibrary(library);
          }

          File f = mps.getLabelFor(library);
          if (f != null) thingsToPrint.add(f);
        } catch (IOException e) {
          e.printStackTrace();
          return JSONUtils.SimpleJSONError("Error printing barcodes: " + e.getMessage());
        }
      }
      PrintJob pj = printManager.print(thingsToPrint, mps.getName(), user);
      return JSONUtils.SimpleJSONResponse("Job " + pj.getJobId() + " : Barcodes printed.");
    } catch (MisoPrintException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Failed to print barcodes: " + e.getMessage());
    } catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Failed to print barcodes: " + e.getMessage());
    }
  }

  public JSONObject printLibraryDilutionBarcodes(HttpSession session, JSONObject json) {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());

      String serviceName = null;
      if (json.has("serviceName")) {
        serviceName = json.getString("serviceName");
      }

      MisoPrintService<File, Barcodable, PrintContext<File>> mps = null;
      if (serviceName == null) {
        Collection<MisoPrintService> services = printManager.listPrintServicesByBarcodeableClass(LibraryDilution.class);
        if (services.size() == 1) {
          mps = services.iterator().next();
        } else {
          return JSONUtils
              .SimpleJSONError("No serviceName specified, but more than one available service able to print this barcode type.");
        }
      } else {
        mps = printManager.getPrintService(serviceName);
      }

      Queue<File> thingsToPrint = new LinkedList<File>();

      JSONArray ls = JSONArray.fromObject(json.getString("dilutions"));
      for (JSONObject l : (Iterable<JSONObject>) ls) {
        try {
          Long dilutionId = l.getLong("dilutionId");
          // String platform = l.getString("platform");
          LibraryDilution dilution = requestManager.getLibraryDilutionById(dilutionId);
          // autosave the barcode if none has been previously generated
          if (dilution.getIdentificationBarcode() == null || "".equals(dilution.getIdentificationBarcode())) {
            requestManager.saveLibraryDilution(dilution);
          }
          File f = mps.getLabelFor(dilution);
          if (f != null) thingsToPrint.add(f);
          thingsToPrint.add(f);
        } catch (IOException e) {
          e.printStackTrace();
          return JSONUtils.SimpleJSONError("Error printing barcodes: " + e.getMessage());
        }
      }
      PrintJob pj = printManager.print(thingsToPrint, mps.getName(), user);
      return JSONUtils.SimpleJSONResponse("Job " + pj.getJobId() + " : Barcodes printed.");
    } catch (MisoPrintException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Failed to print barcodes: " + e.getMessage());
    } catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Failed to print barcodes: " + e.getMessage());
    }
  }

  public JSONObject changeLibraryLocation(HttpSession session, JSONObject json) {
    Long libraryId = json.getLong("libraryId");
    String locationBarcode = json.getString("locationBarcode");

    try {
      String newLocation = LimsUtils.lookupLocation(locationBarcode);
      if (!"".equals(newLocation)) {
        User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
        Library library = requestManager.getLibraryById(libraryId);
        String oldLocation = library.getLocationBarcode();
        library.setLocationBarcode(newLocation);

        Note note = new Note();
        note.setInternalOnly(true);
        note.setText("Location changed from " + oldLocation + " to " + newLocation + " by " + user.getLoginName() + " on " + new Date());
        note.setOwner(user);
        note.setCreationDate(new Date());
        library.getNotes().add(note);
        requestManager.saveLibraryNote(library, note);
        requestManager.saveLibrary(library);
      } else {
        return JSONUtils.SimpleJSONError("New location barcode not recognised");
      }
    } catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError(e.getMessage());
    }

    return JSONUtils.SimpleJSONResponse("Note saved successfully");
  }

  public JSONObject getLibraryDilutionBarcode(HttpSession session, JSONObject json) {
    Long dilutionId = json.getLong("dilutionId");
    File temploc = new File(session.getServletContext().getRealPath("/") + "temp/");
    try {
      LibraryDilution dil = requestManager.getLibraryDilutionById(dilutionId);
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
      e.printStackTrace();
      return JSONUtils.SimpleJSONError(e.getMessage() + ": Cannot seem to generate temp file for barcode");
    }
  }

  public JSONObject getBarcodesPositions(HttpSession session, JSONObject json) {
    if (json.has("strategy")) {
      TagBarcodeStrategy tbs = tagBarcodeStrategyResolverService.getTagBarcodeStrategy(json.getString("strategy"));
      if (tbs != null) {
        JSONObject j = new JSONObject();
        j.put("numApplicableBarcodes", tbs.getNumApplicableBarcodes());
        return j;
      }
      return JSONUtils.SimpleJSONError("No strategy found with the name: \"" + json.getString("strategy") + "\"");
    } else {
      return JSONUtils.SimpleJSONError("No valid strategy given");
    }
  }

  public JSONObject bulkSaveLibraries(HttpSession session, JSONObject json) {
    if (json.has("libraries")) {
      try {
        Project p = requestManager.getProjectById(json.getLong("projectId"));
        JSONArray a = JSONArray.fromObject(json.get("libraries"));
        Set<Library> saveSet = new HashSet<Library>();

        for (JSONObject j : (Iterable<JSONObject>) a) {
          try {
            SecurityProfile sp = null;
            Sample sample = null;
            // String libAlias = null;
            String sampleAlias = j.getString("parentSample");

            for (Sample s : p.getSamples()) {
              if (s.getAlias().equals(sampleAlias)) {
                sp = s.getSecurityProfile();
                sample = s;
                break;
                /*
                 * if (sampleNamingScheme.validateField("alias", s.getAlias())) { Pattern pat =
                 * Pattern.compile(sampleNamingScheme.getValidationRegex("alias")); Matcher mat = pat.matcher(s.getAlias()); //convert the
                 * sample alias automatically to a library alias int numLibs = requestManager.listAllLibrariesBySampleId(s.getId()).size();
                 * String la = mat.group(1) + "_" + "L" + mat.group(2) + "-"+(numLibs+1)+"_" + mat.group(3); if
                 * (libraryNamingScheme.validateField("alias", la)) { libAlias = la; } }
                 */
              }
            }

            if (sample != null) { // && libAlias != null) {
              String descr = j.getString("description");
              String platform = j.getString("platform");
              String type = j.getString("libraryType");
              String selectionType = j.getString("selectionType");
              String strategyType = j.getString("strategyType");
              String locationBarcode = j.getString("locationBarcode");

              Library library = new LibraryImpl();
              library.setSample(sample);

              library.setSecurityProfile(sp);
              library.setDescription(descr);
              library.setPlatformName(platform);
              library.setCreationDate(new Date());
              library.setLocationBarcode(locationBarcode);
              library.setQcPassed(false);
              library.setLibraryType(requestManager.getLibraryTypeByDescription(type));
              library.setLibrarySelectionType(requestManager.getLibrarySelectionTypeByName(selectionType));
              library.setLibraryStrategyType(requestManager.getLibraryStrategyTypeByName(strategyType));

              String libAlias = libraryNamingScheme.generateNameFor("alias", library);
              library.setAlias(libAlias);

              boolean paired = false;
              if (!"".equals(j.getString("paired"))) {
                paired = j.getBoolean("paired");
              }
              library.setPaired(paired);

              if (j.has("tagBarcodes") && !"".equals(j.getString("tagBarcodes")) && !j.getString("tagBarcodes").contains("Select")) {
                String[] codes = j.getString("tagBarcodes").split(Pattern.quote("|"));
                HashMap<Integer, TagBarcode> barcodes = new HashMap<Integer, TagBarcode>();
                int count = 1;
                for (String code : codes) {
                  try {
                    long cl = Long.parseLong(code);
                    barcodes.put(count, requestManager.getTagBarcodeById(cl));
                  } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return JSONUtils.SimpleJSONError("Cannot save Library. It looks like there are tag barcodes for the library of "
                        + sample.getAlias() + ", but they cannot be processed");
                  } finally {
                    count++;
                  }
                }
                library.setTagBarcodes(barcodes);
              }

              saveSet.add(library);
            } else {
              throw new IOException(
                  "Could not process a selected Sample to generate Libraries. Please check that all selected samples' aliases conform to the chosen naming convention ("
                      + sampleNamingScheme.getValidationRegex("alias") + ")");
            }
          } catch (IOException e) {
            e.printStackTrace();
            return JSONUtils.SimpleJSONError("Cannot save Library generated from " + j.getString("parentSample") + ": " + e.getMessage());
          } catch (JSONException e) {
            e.printStackTrace();
            return JSONUtils.SimpleJSONError("Cannot save Library. Something cannot be retrieved from the bulk input table: "
                + e.getMessage());
          }
        }

        /*
         * Set<Library> complement = LimsUtils.relativeComplementByProperty( Library.class, "getAlias", saveSet, new
         * HashSet(requestManager.listAllLibrariesByProjectId(json.getLong("projectId"))));
         */
        List<Library> sortedList = new ArrayList<Library>(saveSet);
        Collections.sort(sortedList, new AliasComparator(Library.class));
        for (Library library : sortedList) {
          requestManager.saveLibrary(library);
        }

        return JSONUtils.SimpleJSONResponse("All libraries saved successfully");
      } catch (Exception e) {
        e.printStackTrace();
        return JSONUtils.SimpleJSONError("Cannot retrieve parent project with ID " + json.getLong("projectId"));
      }
    } else {
      return JSONUtils.SimpleJSONError("No libraries specified");
    }
  }

  public JSONObject changePlatformName(HttpSession session, JSONObject json) {
    try {
      if (json.has("platform") && !json.get("platform").equals("")) {
        String platform = json.getString("platform");
        Map<String, Object> map = new HashMap<String, Object>();

        StringBuilder libsb = new StringBuilder();
        List<LibraryType> types = new ArrayList<LibraryType>(requestManager.listLibraryTypesByPlatform(platform));
        Collections.sort(types);
        for (LibraryType s : types) {
          libsb.append("<option value='" + s.getLibraryTypeId() + "'>" + s.getDescription() + "</option>");
        }

        StringBuilder tagsb = new StringBuilder();
        List<TagBarcodeStrategy> strategies = new ArrayList<TagBarcodeStrategy>(
            tagBarcodeStrategyResolverService.getTagBarcodeStrategiesByPlatform(PlatformType.get(platform)));
        tagsb.append("<option value=''>No Barcode Strategy</option>");
        for (TagBarcodeStrategy tb : strategies) {
          tagsb.append("<option value='" + tb.getName() + "'>" + tb.getName() + "</option>");
        }

        map.put("libraryTypes", libsb.toString());
        map.put("tagBarcodeStrategies", tagsb.toString());

        return JSONUtils.JSONObjectResponse(map);
      }
    } catch (IOException e) {
      e.printStackTrace();
      log.error("Failed to retrieve library types given platform type: ", e);
      return JSONUtils.SimpleJSONError("Failed to retrieve library types given platform type: " + e.getMessage());
    }
    return JSONUtils.SimpleJSONError("Cannot resolve LibraryType from selected Platform");
  }

  public JSONObject getTagBarcodesForStrategy(HttpSession session, JSONObject json) {
    if (json.has("strategy")) {
      TagBarcodeStrategy tbs = tagBarcodeStrategyResolverService.getTagBarcodeStrategy(json.getString("strategy"));
      if (tbs != null) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<Integer, Set<TagBarcode>> barcodes = tbs.getApplicableBarcodes();
        StringBuilder tagsb = new StringBuilder();
        for (Integer i : barcodes.keySet()) {
          // select
          tagsb.append("Barcode " + i + ": " + "<select id='tagBarcodes[\"" + i + "\"]' name='tagBarcodes[\"" + i + "\"]'>");
          tagsb.append("<option value=''>No Barcode</option>");
          List<TagBarcode> bs = new ArrayList<TagBarcode>(barcodes.get(i));
          Collections.sort(bs);
          for (TagBarcode tb : bs) {
            // option
            tagsb.append("<option value='" + tb.getId() + "'>" + tb.getName() + "</option>");
          }
          tagsb.append("</select><br/>");
          tagsb.append("<input type='hidden' value='on' name='_tagBarcodes[\"" + i + "\"]'/>");
        }
        map.put("tagBarcodes", tagsb.toString());
        return JSONUtils.JSONObjectResponse(map);
      } else {
        return JSONUtils.SimpleJSONError("No such TagBarcodeStrategy: " + json.getString("strategy"));
      }
    } else {
      return JSONUtils.SimpleJSONError("No valid TagBarcodeStrategy selected");
    }
  }

  public JSONObject getLibraryQcTypes(HttpSession session, JSONObject json) {
    try {
      StringBuilder sb = new StringBuilder();
      Collection<QcType> types = requestManager.listAllLibraryQcTypes();
      for (QcType s : types) {
        sb.append("<option units='" + s.getUnits() + "' value='" + s.getQcTypeId() + "'>" + s.getName() + "</option>");
      }
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("types", sb.toString());
      return JSONUtils.JSONObjectResponse(map);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return JSONUtils.SimpleJSONError("Cannot list all Library QC Types");
  }

  public JSONObject addLibraryQC(HttpSession session, JSONObject json) {
    try {
      for (Object key : json.keySet()) {
        if (json.get(key) == null || json.get(key).equals("")) {
          String k = (String) key;
          return JSONUtils.SimpleJSONError("Please enter a value for '" + k + "'");
        }
      }
      if (json.has("libraryId") && !json.get("libraryId").equals("")) {
        Long libraryId = Long.parseLong(json.getString("libraryId"));
        Library library = requestManager.getLibraryById(libraryId);
        LibraryQC newQc = dataObjectFactory.getLibraryQC();
        if (json.has("qcPassed") && json.getString("qcPassed").equals("true")) {
          library.setQcPassed(true);
        }
        newQc.setQcCreator(json.getString("qcCreator"));
        newQc.setQcDate(new SimpleDateFormat("dd/MM/yyyy").parse(json.getString("qcDate")));
        newQc.setQcType(requestManager.getLibraryQcTypeById(json.getLong("qcType")));
        newQc.setResults(Double.parseDouble(json.getString("results")));
        newQc.setInsertSize(Integer.parseInt(json.getString("insertSize")));
        library.addQc(newQc);
        requestManager.saveLibraryQC(newQc);

        StringBuilder sb = new StringBuilder();
        // sb.append("<tr><th>ID</th><th>QCed By</th><th>QC Date</th><th>Method</th><th>Results</th><th>Insert Size</th></tr>");
        sb.append("<tr><th>QCed By</th><th>QC Date</th><th>Method</th><th>Results</th><th>Insert Size</th></tr>");
        for (LibraryQC qc : library.getLibraryQCs()) {
          sb.append("<tr>");
          // sb.append("<td>"+qc.getQcId()+"</td>");
          sb.append("<td>" + qc.getQcCreator() + "</td>");
          sb.append("<td>" + qc.getQcDate() + "</td>");
          sb.append("<td>" + qc.getQcType().getName() + "</td>");
          sb.append("<td>" + qc.getResults() + " " + qc.getQcType().getUnits() + "</td>");
          sb.append("<td>" + qc.getInsertSize() + " bp</td>");
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
        String qcPassed = qc.getString("qcPassed");
        String qcType = qc.getString("qcType");
        String results = qc.getString("results");
        String qcCreator = qc.getString("qcCreator");
        String qcDate = qc.getString("qcDate");
        String insertSize = qc.getString("insertSize");

        // if (qcPassed == null || qcPassed.equals("") ||
        if (qcType == null || qcType.equals("") || results == null || results.equals("") || qcCreator == null || qcCreator.equals("")
            || qcDate == null || qcDate.equals("") || insertSize == null || insertSize.equals("")) {
          ok = false;
        }
      }

      // persist
      if (ok) {
        Map<String, Object> map = new HashMap<String, Object>();
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
        return JSONUtils
            .SimpleJSONError("Failed to add Library QC to this Library: one of the required fields of the selected QCs is missing or invalid");
      }
    } catch (Exception e) {
      log.error("Failed to add Library QC to this Library: ", e);
      return JSONUtils.SimpleJSONError("Failed to add Library QC to this Library: " + e.getMessage());
    }
  }

  public JSONObject addLibraryDilution(HttpSession session, JSONObject json) {
    try {
      for (Object key : json.keySet()) {
        if (json.get(key) == null || json.get(key).equals("")) {
          String k = (String) key;
          return JSONUtils.SimpleJSONError("Please enter a value for '" + k + "'");
        }
      }
      if (json.has("libraryId") && !json.get("libraryId").equals("")) {
        Long libraryId = Long.parseLong(json.getString("libraryId"));
        Library library = requestManager.getLibraryById(libraryId);
        LibraryDilution newDilution = dataObjectFactory.getLibraryDilution();
        newDilution.setSecurityProfile(library.getSecurityProfile());
        newDilution.setDilutionCreator(json.getString("dilutionCreator"));
        newDilution.setCreationDate(new SimpleDateFormat("dd/MM/yyyy").parse(json.getString("dilutionDate")));
        // newDilution.setLocationBarcode(json.getString("locationBarcode"));
        newDilution.setConcentration(Double.parseDouble(json.getString("results")));
        library.addDilution(newDilution);
        requestManager.saveLibraryDilution(newDilution);

        StringBuilder sb = new StringBuilder();
        sb.append("<tr>");
        // sb.append("<th>ID</th><th>Done By</th><th>Date</th><th>Barcode</th><th>Results</th>");
        sb.append("<th>LD Name</th><th>Done By</th><th>Date</th><th>Results</th><th>ID barcode</th>");
        if (!library.getPlatformName().equals("Illumina")) {
          sb.append("<th>Add emPCR</th>");
        }
        sb.append("</tr>");

        File temploc = new File(session.getServletContext().getRealPath("/") + "temp/");
        for (LibraryDilution dil : library.getLibraryDilutions()) {
          sb.append("<tr>");
          sb.append("<td>" + dil.getName() + "</td>");
          sb.append("<td>" + dil.getDilutionCreator() + "</td>");
          sb.append("<td>" + dil.getCreationDate() + "</td>");
          sb.append("<td>" + dil.getConcentration() + " " + dil.getUnits() + "</td>");
          sb.append("<td>");

          try {
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
            e.printStackTrace();
          }
          sb.append("</td>");

          if (!library.getPlatformName().equals("Illumina")) {
            sb.append("<td><a href='javascript:void(0);' onclick='Library.empcr.insertEmPcrRow(" + dil.getId() + ");'>Add emPCR</a></td>");
          } else {
            // sb.append("<td><a href='/miso/poolwizard/new/"+library.getPlatformName().toLowerCase()+"/new/'>Construct New Pool</a></td>");
            sb.append("<td><a href='/miso/poolwizard/new/" + library.getSample().getProject().getProjectId()
                + "'>Construct New Pool</a></td>");
          }

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

        if (results == null || results.equals("") || dilutionCreator == null || dilutionCreator.equals("") || dilutionDate == null
            || dilutionDate.equals("")) {
          ok = false;
        }
      }

      // persist
      if (ok) {
        Map<String, Object> map = new HashMap<String, Object>();
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
        log.error("Failed to add Library Dilutions to this Library: one of the required fields of the selected Library Dilutions is missing or invalid");
        return JSONUtils
            .SimpleJSONError("Failed to add Library Dilutions to this Library: one of the required fields of the selected Library Dilutions is missing or invalid");
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
      LibraryDilution dilution = requestManager.getLibraryDilutionById(dilutionId);
      response.put("results", "<input type='text' id='" + dilutionId + "' value='" + dilution.getConcentration() + "'/>");
      response
          .put("edit", "<a href='javascript:void(0);' onclick='Library.dilution.editLibraryDilution(\"" + dilutionId + "\");'>Save</a>");
      return response;
    } catch (Exception e) {
      log.error("Failed to display Library Dilution of this Library: ", e);
      return JSONUtils.SimpleJSONError("Failed to display Library Dilution of this sample: " + e.getMessage());
    }
  }

  public JSONObject editLibraryDilution(HttpSession session, JSONObject json) {
    try {
      if (json.has("dilutionId") && !json.get("dilutionId").equals("")) {
        Long dilutionId = Long.parseLong(json.getString("dilutionId"));
        LibraryDilution dilution = requestManager.getLibraryDilutionById(dilutionId);
        dilution.setConcentration(Double.parseDouble(json.getString("result")));
        requestManager.saveLibraryDilution(dilution);
        return JSONUtils.SimpleJSONResponse("OK");
      }
    } catch (Exception e) {
      log.error("Failed to edit Library Dilution of this Library: ", e);
      return JSONUtils.SimpleJSONError("Failed to edit Library Dilution of this Library: " + e.getMessage());
    }
    return JSONUtils.SimpleJSONError("Cannot add LibraryDilution");
  }

  public JSONObject addEmPcr(HttpSession session, JSONObject json) {
    try {
      for (Object key : json.keySet()) {
        if (json.get(key) == null || json.get(key).equals("")) {
          String k = (String) key;
          return JSONUtils.SimpleJSONError("Please enter a value for '" + k + "'");
        }
      }
      if (json.has("dilutionId") && !json.get("dilutionId").equals("")) {
        Long dilutionId = Long.parseLong(json.getString("dilutionId"));
        LibraryDilution dilution = requestManager.getLibraryDilutionById(dilutionId);
        emPCR pcr = dataObjectFactory.getEmPCR();
        pcr.setSecurityProfile(dilution.getSecurityProfile());
        pcr.setPcrCreator(json.getString("pcrCreator"));
        pcr.setCreationDate(new SimpleDateFormat("dd/MM/yyyy").parse(json.getString("pcrDate")));
        pcr.setConcentration(Double.parseDouble(json.getString("results")));
        pcr.setLibraryDilution(dilution);
        requestManager.saveEmPCR(pcr);

        StringBuilder sb = new StringBuilder();
        sb.append("<tr>");
        sb.append("<th>ID</th><th>Done By</th><th>Date</th><th>Results</th>");
        sb.append("<th>Add emPCR Dilution</th>");
        sb.append("</tr>");
        for (emPCR p : requestManager.listAllEmPCRsByDilutionId(dilutionId)) {
          sb.append("<tr>");
          sb.append("<td>" + p.getId() + "</td>");
          sb.append("<td>" + p.getPcrCreator() + "</td>");
          sb.append("<td>" + p.getCreationDate() + "</td>");
          sb.append("<td>" + p.getConcentration() + " " + p.getUnits() + "</td>");
          sb.append("<td><a href='javascript:void(0);' onclick='Library.empcr.insertEmPcrDilutionRow(" + p.getId()
              + ");'>Add emPCR Dilution</a></td>");
          sb.append("</tr>");
        }
        return JSONUtils.SimpleJSONResponse(sb.toString());
      } else {
        log.error("Failed to add emPCR to this LibraryDilution: No parent Dilution ID found");
        return JSONUtils.SimpleJSONError("Failed to add emPCR to this LibraryDilution: No parent Dilution ID found");
      }
    } catch (Exception e) {
      log.error("Failed to add emPCR to this LibraryDilution: ", e);
      return JSONUtils.SimpleJSONError("Failed to add emPCR to this LibraryDilution: " + e.getMessage());
    }
  }

  public JSONObject addEmPcrDilution(HttpSession session, JSONObject json) {
    try {
      for (Object key : json.keySet()) {
        if (json.get(key) == null || json.get(key).equals("")) {
          String k = (String) key;
          return JSONUtils.SimpleJSONError("Please enter a value for '" + k + "'");
        }
      }
      if (json.has("pcrId") && !json.get("pcrId").equals("")) {
        Long pcrId = Long.parseLong(json.getString("pcrId"));
        emPCR pcr = requestManager.getEmPcrById(pcrId);
        emPCRDilution newDilution = dataObjectFactory.getEmPCRDilution();
        newDilution.setSecurityProfile(pcr.getSecurityProfile());
        newDilution.setDilutionCreator(json.getString("pcrDilutionCreator"));
        newDilution.setCreationDate(new SimpleDateFormat("dd/MM/yyyy").parse(json.getString("pcrDilutionDate")));
        newDilution.setConcentration(Double.parseDouble(json.getString("results")));
        newDilution.setEmPCR(pcr);
        requestManager.saveEmPCRDilution(newDilution);

        StringBuilder sb = new StringBuilder();
        sb.append("<tr>");
        // sb.append("<th>ID</th><th>Done By</th><th>Date</th><th>Barcode</th><th>Results</th>");
        sb.append("<th>ID</th><th>Done By</th><th>Date</th><th>Results</th><th>ID Barcode</th>");
        sb.append("</tr>");

        File temploc = new File(session.getServletContext().getRealPath("/") + "temp/");
        for (emPCRDilution dil : requestManager.listAllEmPcrDilutionsByEmPcrId(pcrId)) {
          sb.append("<tr>");
          sb.append("<td>" + dil.getId() + "</td>");
          sb.append("<td>" + dil.getDilutionCreator() + "</td>");
          sb.append("<td>" + dil.getCreationDate() + "</td>");
          sb.append("<td>" + dil.getConcentration() + " " + dil.getUnits() + "</td>");

          sb.append("<td>");
          try {
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
            e.printStackTrace();
          }
          sb.append("</td>");

          // sb.append("<td><a href='/miso/pool/"+pcr.getLibraryDilution().getLibrary().getPlatformName().toLowerCase()+"/new/'>Construct New Pool</a></td>");
          sb.append("<td><a href='/miso/poolwizard/new/" + pcr.getLibraryDilution().getLibrary().getSample().getProject().getProjectId()
              + "'>Construct New Pool</a></td>");
          sb.append("</tr>");
        }
        return JSONUtils.SimpleJSONResponse(sb.toString());
      }
    } catch (Exception e) {
      log.error("Failed to add EmPCRDilution to this EmPCR: ", e);
      return JSONUtils.SimpleJSONError("Failed to add EmPCRDilution to this EmPCR: " + e.getMessage());
    }
    return JSONUtils.SimpleJSONError("Cannot add EmPCRDilution");
  }

  public JSONObject bulkAddEmPcrs(HttpSession session, JSONObject json) {
    try {
      JSONArray pcrs = JSONArray.fromObject(json.getString("pcrs"));
      // validate
      boolean ok = true;
      for (JSONObject pcr : (Iterable<JSONObject>) pcrs) {
        String pcrCreator = pcr.getString("pcrCreator");
        String pcrDate = pcr.getString("pcrDate");
        String concentration = pcr.getString("results");

        if (concentration == null || concentration.equals("") || pcrCreator == null || pcrCreator.equals("") || pcrDate == null
            || pcrDate.equals("")) {
          ok = false;
        }
      }

      // persist
      if (ok) {
        Map<String, Object> map = new HashMap<String, Object>();
        JSONArray a = new JSONArray();
        JSONArray errors = new JSONArray();
        for (JSONObject pcr : (Iterable<JSONObject>) pcrs) {
          JSONObject j = addEmPcr(session, pcr);
          j.put("dilutionId", pcr.getString("dilutionId"));
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
        log.error("Failed to add EmPCRs to this Library Dilution: one of the required fields of the selected EmPCR is missing or invalid");
        return JSONUtils
            .SimpleJSONError("Failed to add EmPCRs to this Library Dilution: one of the required fields of the EmPCR is missing or invalid");
      }
    } catch (Exception e) {
      log.error("Failed to add EmPCRs to this Library Dilution: ", e);
      return JSONUtils.SimpleJSONError("Failed to add EmPCRs to this Library Dilution: " + e.getMessage());
    }
  }

  public JSONObject bulkAddEmPcrDilutions(HttpSession session, JSONObject json) {
    try {
      JSONArray dilutions = JSONArray.fromObject(json.getString("dilutions"));
      // validate
      boolean ok = true;
      for (JSONObject dil : (Iterable<JSONObject>) dilutions) {
        String dilutionCreator = dil.getString("pcrDilutionCreator");
        String dilutionDate = dil.getString("pcrDilutionDate");
        String concentration = dil.getString("results");

        if (concentration == null || concentration.equals("") || dilutionCreator == null || dilutionCreator.equals("")
            || dilutionDate == null || dilutionDate.equals("")) {
          ok = false;
        }
      }

      // persist
      if (ok) {
        Map<String, Object> map = new HashMap<String, Object>();
        JSONArray a = new JSONArray();
        JSONArray errors = new JSONArray();
        for (JSONObject dil : (Iterable<JSONObject>) dilutions) {
          JSONObject j = addEmPcrDilution(session, dil);
          j.put("pcrId", dil.getString("pcrId"));
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
        log.error("Failed to add EmPCR Dilutions to this EmPCR: one of the required fields of the selected EmPCR Dilutions is missing or invalid");
        return JSONUtils
            .SimpleJSONError("Failed to add EmPCR Dilutions to this EmPCR: one of the required fields of the selected EmPCR Dilutions is missing or invalid");
      }
    } catch (Exception e) {
      log.error("Failed to add EmPCR Dilutions to this EmPCR: ", e);
      return JSONUtils.SimpleJSONError("Failed to add EmPCR Dilutions to this EmPCR: " + e.getMessage());
    }
  }

  public JSONObject changeLibraryQCRow(HttpSession session, JSONObject json) {
    try {
      JSONObject response = new JSONObject();
      Long qcId = Long.parseLong(json.getString("qcId"));
      LibraryQC libraryQc = requestManager.getLibraryQCById(qcId);
      Long libraryId = Long.parseLong(json.getString("libraryId"));

      response.put("results", "<input type='text' id='results" + qcId + "' value='" + libraryQc.getResults() + "'/>");
      response.put("insertSize", "<input type='text' id='insertSize" + qcId + "' value='" + libraryQc.getInsertSize() + "'/>");
      response.put("edit", "<a href='javascript:void(0);' onclick='Library.qc.editLibraryQC(\"" + qcId + "\",\"" + libraryId
          + "\");'>Save</a>");
      return response;
    } catch (Exception e) {
      log.error("Failed to display library QC of this library: ", e);
      return JSONUtils.SimpleJSONError("Failed to display library QC of this library: " + e.getMessage());
    }
  }

  public JSONObject editLibraryQC(HttpSession session, JSONObject json) {
    try {
      if (json.has("qcId") && !json.get("qcId").equals("")) {
        Long qcId = Long.parseLong(json.getString("qcId"));
        LibraryQC libraryQc = requestManager.getLibraryQCById(qcId);

        libraryQc.setResults(Double.parseDouble(json.getString("result")));
        libraryQc.setInsertSize(Integer.parseInt(json.getString("insertSize")));
        requestManager.saveLibraryQC(libraryQc);

      }
      return JSONUtils.SimpleJSONResponse("done");
    } catch (Exception e) {
      log.error("Failed to add library QC to this library: ", e);
      return JSONUtils.SimpleJSONError("Failed to add library QC to this library: " + e.getMessage());
    }
  }

  public JSONObject deleteLibrary(HttpSession session, JSONObject json) {
    User user;
    try {
      user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    } catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Error getting currently logged in user.");
    }

    if (user != null && user.isAdmin()) {
      if (json.has("libraryId")) {
        Long libraryId = json.getLong("libraryId");
        try {
          requestManager.deleteLibrary(requestManager.getLibraryById(libraryId));
          return JSONUtils.SimpleJSONResponse("Library deleted");
        } catch (IOException e) {
          e.printStackTrace();
          return JSONUtils.SimpleJSONError("Cannot delete library: " + e.getMessage());
        }
      } else {
        return JSONUtils.SimpleJSONError("No library specified to delete.");
      }
    } else {
      return JSONUtils.SimpleJSONError("Only logged-in admins can delete objects.");
    }
  }

  public JSONObject deleteLibraryDilution(HttpSession session, JSONObject json) {
    User user;
    try {
      user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    } catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Error getting currently logged in user.");
    }

    if (user != null && user.isAdmin()) {
      if (json.has("libraryDilutionId")) {
        Long libraryDilutionId = json.getLong("libraryDilutionId");
        try {
          requestManager.deleteLibraryDilution(requestManager.getLibraryDilutionById(libraryDilutionId));
          return JSONUtils.SimpleJSONResponse("LibraryDilution deleted");
        } catch (IOException e) {
          e.printStackTrace();
          return JSONUtils.SimpleJSONError("Cannot delete library dilution: " + e.getMessage());
        }
      } else {
        return JSONUtils.SimpleJSONError("No library dilution specified to delete.");
      }
    } else {
      return JSONUtils.SimpleJSONError("Only logged-in admins can delete objects.");
    }
  }

  public JSONObject deleteEmPCR(HttpSession session, JSONObject json) {
    User user;
    try {
      user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    } catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Error getting currently logged in user.");
    }

    if (user != null && user.isAdmin()) {
      if (json.has("empcrId")) {
        Long empcrId = json.getLong("empcrId");
        try {
          requestManager.deleteEmPCR(requestManager.getEmPcrById(empcrId));
          return JSONUtils.SimpleJSONResponse("EmPCR deleted");
        } catch (IOException e) {
          e.printStackTrace();
          return JSONUtils.SimpleJSONError("Cannot delete EmPCR: " + e.getMessage());
        }
      } else {
        return JSONUtils.SimpleJSONError("No EmPCR specified to delete.");
      }
    } else {
      return JSONUtils.SimpleJSONError("Only logged-in admins can delete objects.");
    }
  }

  public JSONObject deleteEmPCRDilution(HttpSession session, JSONObject json) {
    User user;
    try {
      user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    } catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Error getting currently logged in user.");
    }

    if (user != null && user.isAdmin()) {
      if (json.has("deleteEmPCRDilution")) {
        Long deleteEmPCRDilution = json.getLong("deleteEmPCRDilution");
        try {
          requestManager.deleteEmPcrDilution(requestManager.getEmPcrDilutionById(deleteEmPCRDilution));
          return JSONUtils.SimpleJSONResponse("EmPCRDilution deleted");
        } catch (IOException e) {
          e.printStackTrace();
          return JSONUtils.SimpleJSONError("Cannot delete EmPCR dilution: " + e.getMessage());
        }
      } else {
        return JSONUtils.SimpleJSONError("No EmPCR dilution specified to delete.");
      }
    } else {
      return JSONUtils.SimpleJSONError("Only logged-in admins can delete objects.");
    }
  }

  public JSONObject listLibrariesDataTable(HttpSession session, JSONObject json) {
    try {
      JSONObject j = new JSONObject();
      JSONArray jsonArray = new JSONArray();
      for (Library library : requestManager.listAllLibraries()) {
        String qcpassed = "Unknown";
        if (library.getQcPassed() != null) {
          qcpassed = library.getQcPassed().toString();
        }
        jsonArray.add("['" + 
                      TableHelper.hyperLinkify("/miso/library/" + library.getId(), 
                                                library.getName(), true) + "','" +
                      TableHelper.hyperLinkify("/miso/library/" + library.getId(), 
                                                library.getAlias()) + "','" +
                      library.getLibraryType().getDescription() + "','" +
                      library.getSample().getName()+ "','" +
                      qcpassed + "','" + "']");
                      //"<a href=\"/miso/library/" + library.getId() + "\"><span class=\"ui-icon ui-icon-pencil\"></span></a>" + "']");
      }
      j.put("array", jsonArray);
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

  public void setTagBarcodeStrategyResolverService(TagBarcodeStrategyResolverService tagBarcodeStrategyResolverService) {
    this.tagBarcodeStrategyResolverService = tagBarcodeStrategyResolverService;
  }

  public void setSampleNamingScheme(MisoNamingScheme<Sample> sampleNamingScheme) {
    this.sampleNamingScheme = sampleNamingScheme;
  }

  public void setLibraryNamingScheme(MisoNamingScheme<Library> libraryNamingScheme) {
    this.libraryNamingScheme = libraryNamingScheme;
  }
}

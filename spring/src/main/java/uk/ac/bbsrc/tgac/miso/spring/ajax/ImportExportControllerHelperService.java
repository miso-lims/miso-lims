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
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.*;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.InputFormException;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.service.tagbarcode.TagBarcodeStrategy;
import uk.ac.bbsrc.tgac.miso.core.service.tagbarcode.TagBarcodeStrategyResolverService;
import uk.ac.bbsrc.tgac.miso.core.util.FormUtils;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * uk.ac.bbsrc.tgac.miso.spring.ajax
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.1.2
 */
@Ajaxified
public class ImportExportControllerHelperService {
  protected static final Logger log = LoggerFactory.getLogger(ImportExportControllerHelperService.class);
  @Autowired
  private RequestManager requestManager;
  @Autowired
  private MisoFilesManager misoFileManager;
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private TagBarcodeStrategyResolverService tagBarcodeStrategyResolverService;

  private static final Pattern digitPattern = Pattern.compile("(^[0-9]+)[\\.0-9]*");

  public JSONObject searchSamples(HttpSession session, JSONObject json) {
    String searchStr = json.getString("str");
    try {
      List<Sample> samples;
      StringBuilder b = new StringBuilder();
      if (!"".equals(searchStr)) {
        samples = new ArrayList<Sample>(requestManager.listAllSamplesBySearch(searchStr));
      }
      else {
        samples = new ArrayList<Sample>(requestManager.listAllSamplesWithLimit(250));
      }

      if (samples.size() > 0) {
        Collections.sort(samples);
        for (Sample s : samples) {
          String dnaOrRNA = "O";
          if ("GENOMIC".equals(s.getSampleType())
              || "METAGENOMIC".equals(s.getSampleType())) {
            dnaOrRNA = "D";
          }
          else if ("NON GENOMIC".equals(s.getSampleType())
                   || "VIRAL RNA".equals(s.getSampleType())
                   || "TRANSCRIPTOMIC".equals(s.getSampleType())
                   || "METATRANSCRIPTOMIC".equals(s.getSampleType())) {
            dnaOrRNA = "R";
          }
          b.append("<div id=\"sample" + s.getId() + "\" onMouseOver=\"this.className=&#39dashboardhighlight&#39\" onMouseOut=\"this.className=&#39dashboard&#39\" "
                   + " " + "class=\"dashboard\">");
          b.append("<input type=\"hidden\" id=\"" + s.getId() + "\" name=\"" + s.getName() + "\" projectname=\"" + s.getProject().getName() + "\" projectalias=\"" + s.getProject().getAlias() + "\" samplealias=\"" + s.getAlias() + "\" dnaOrRNA=\"" + dnaOrRNA + "\"/>");
          b.append("Name: <b>" + s.getName() + "</b><br/>");
          b.append("Alias: <b>" + s.getAlias() + "</b><br/>");
          b.append("From Project: <b>" + s.getProject().getName() + "</b><br/>");
          b.append("<button type=\"button\" class=\"fg-button ui-state-default ui-corner-all\" onclick=\"ImportExport.insertSampleNextAvailable(jQuery('#sample" + s.getId() + "'));\">Add</button>");
          b.append("</div>");
        }
      }
      else {
        b.append("No matches");
      }
      return JSONUtils.JSONObjectResponse("html", b.toString());
    }
    catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONObject exportSampleForm(HttpSession session, JSONObject json) {
    try {
      JSONArray a = JSONArray.fromObject(json.getString("form"));
      File f = misoFileManager.getNewFile(
          Sample.class,
          "forms",
          "SampleExportForm-" + LimsUtils.getCurrentDateAsString(new SimpleDateFormat("yyyyMMdd-hhmmss")) + ".xlsx");
      FormUtils.createSampleExportForm(f, a);
      return JSONUtils.SimpleJSONResponse("" + f.getName().hashCode());
    }
    catch (Exception e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Failed to get plate input form: " + e.getMessage());
    }
  }

  public JSONObject exportLibraryPoolForm(HttpSession session, JSONObject json) {
    try {
      String barcodekit = json.getString("barcodekit");
      JSONArray a = JSONArray.fromObject(json.getString("form"));
      File f = misoFileManager.getNewFile(
          Library.class,
          "forms",
          "LibraryPoolExportForm-" + LimsUtils.getCurrentDateAsString(new SimpleDateFormat("yyyyMMdd-hhmmss")) + ".xlsx");
      FormUtils.createLibraryPoolExportFormFromWeb(f, a, barcodekit);
      return JSONUtils.SimpleJSONResponse("" + f.getName().hashCode());
    }
    catch (Exception e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Failed to get plate input form: " + e.getMessage());
    }
  }

  public JSONObject confirmSamplesUpload(HttpSession session, JSONObject json) throws Exception {
    JSONArray jsonArray = JSONArray.fromObject(json.get("table"));
    // add samples
    for (JSONArray jsonArrayElement : (Iterable<JSONArray>) jsonArray) {

      Sample s = null;
      if (jsonArrayElement.get(3) != null && !"".equals(jsonArrayElement.getString(3))) {
        String salias = jsonArrayElement.getString(3);
        Collection<Sample> ss = requestManager.listSamplesByAlias(salias);
        if (!ss.isEmpty()) {
          if (ss.size() == 1) {
            s = ss.iterator().next();
            log.info("Got sample: " + s.getAlias());
          }
          else {
            throw new InputFormException("Multiple samples retrieved with this alias: '" + salias + "'. Cannot process.");
          }
        }
        else {
          throw new InputFormException("No such sample '" + salias + "'in database. Samples need to be created before using the form input functionality");
        }
      }
      else {
        log.info("Blank sample row found. Ending import.");
        break;
      }

      Date date = new Date();
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      //sample OK - good to go

      try {
        if (s != null) {
          if (jsonArrayElement.get(6) != null) {
            SampleQC sqc = new SampleQCImpl();
            sqc.setSample(s);
            sqc.setResults(jsonArrayElement.getDouble(6));
            sqc.setQcCreator(user.getLoginName());
            sqc.setQcDate(date);
            if (requestManager.getSampleQcTypeByName("Picogreen") != null) {
              sqc.setQcType(requestManager.getSampleQcTypeByName("Picogreen"));
            }
            else {
              sqc.setQcType(requestManager.getSampleQcTypeByName("QuBit"));
            }
            if (!s.getSampleQCs().contains(sqc)) {
              s.addQc(sqc);
              requestManager.saveSampleQC(sqc);
              requestManager.saveSample(s);
              log.info("Added sample QC: " + sqc.toString());
            }
            if (jsonArrayElement.get(7) != null && !"".equals(jsonArrayElement.getString(7))) {
              s.setQcPassed(Boolean.parseBoolean(jsonArrayElement.getString(7)));
              requestManager.saveSample(s);
            }
            if (jsonArrayElement.get(8) != null && !"".equals(jsonArrayElement.getString(8))) {
              List<String> notesList = Arrays.asList((jsonArrayElement.getString(8)).split(";"));
              for (String notetext : notesList) {
                Note note = new Note();
                note.setCreationDate(date);
                note.setOwner(user);
                note.setText(notetext);
                if (!s.getNotes().contains(note)) {
                  s.addNote(note);
                  requestManager.saveSampleNote(s, note);
                  requestManager.saveSample(s);
                  log.info("Added sample Note for Well: " + note.toString());
                }
              }
            }
          }
        }
      }
      catch (Exception e) {
        throw new Exception(e);
      }
    }
    // create library & pool sheet
//    File file = misoFileManager.getNewFile(
//        Library.class,
//        "forms",
//        "LibraryPoolExportForm-" + LimsUtils.getCurrentDateAsString(new SimpleDateFormat("yyyyMMdd-hhmmss")) + ".xlsx");
//    FormUtils.createLibraryPoolExportForm(file, jsonArray);
    return JSONUtils.SimpleJSONResponse("ok");
  }

  public JSONObject confirmLibrariesPoolsUpload(HttpSession session, JSONObject json) throws Exception {
    Date date = new Date();
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    JSONObject jsonObject = JSONObject.fromObject(json.get("sheet"));
    Map<String, Pool> pools = new HashMap<String, Pool>();

    Boolean paired = Boolean.parseBoolean(jsonObject.getString("paired"));


    PlatformType pt = null;
    if (jsonObject.getString("platform") != null) {
      pt = PlatformType.get(jsonObject.getString("platform"));
    }

    LibraryType lt = null;
    if (jsonObject.getString("type") != null) {
      lt = requestManager.getLibraryTypeByDescriptionAndPlatform(jsonObject.getString("type"), pt);
    }

    LibrarySelectionType ls = null;
    if (jsonObject.getString("selection") != null) {
      ls = requestManager.getLibrarySelectionTypeByName(jsonObject.getString("selection"));
    }


    LibraryStrategyType lst = null;
    if (jsonObject.getString("strategy") != null) {
      lst = requestManager.getLibraryStrategyTypeByName(jsonObject.getString("strategy"));
    }

    if (jsonObject.get("rows") != null) {

      for (JSONArray jsonArrayElement : (Iterable<JSONArray>) JSONArray.fromObject(jsonObject.get("rows").toString().replace("\\\"", "'"))) {

        Sample s = null;
        if (jsonArrayElement.get(1) != null && !"".equals(jsonArrayElement.getString(1))) {
          String salias = jsonArrayElement.getString(1);
          Collection<Sample> ss = requestManager.listSamplesByAlias(salias);
          if (!ss.isEmpty()) {
            if (ss.size() == 1) {
              s = ss.iterator().next();
              log.info("Got sample: " + s.getAlias());
            }
            else {
              throw new InputFormException("Multiple samples retrieved with this alias: '" + salias + "'. Cannot process.");
            }
          }
          else {
            throw new InputFormException("No such sample '" + salias + "'in database. Samples need to be created before using the form input functionality");
          }
        }
        else {
          log.info("Blank sample row found. Ending import.");
          break;
        }


        try {

          //sample OK - good to go
          if (s != null) {
            Library library = new LibraryImpl();
            library.setSample(s);
            library.setAlias(jsonArrayElement.getString(3));
            library.setSecurityProfile(s.getSecurityProfile());
            library.setDescription(jsonArrayElement.getString(4));
            library.setCreationDate(new Date());
            library.setPlatformName(pt.name());
            library.setLibraryType(lt);
            library.setLibrarySelectionType(ls);
            library.setLibraryStrategyType(lst);
            library.setPaired(paired);


            int insertSize = 0;
            try {
              String bp = jsonArrayElement.getString(6);
              Matcher m = digitPattern.matcher(bp);
              if (m.matches()) {
                insertSize = Integer.valueOf(m.group(1));
              }
              else {
                throw new InputFormException("Supplied Library insert size for library '" + jsonArrayElement.getString(3) + "' (" + s.getAlias() + ") is invalid");
              }
            }
            catch (NumberFormatException nfe) {
              throw new InputFormException("Supplied Library insert size for library '" + jsonArrayElement.getString(3) + "' (" + s.getAlias() + ") is invalid", nfe);
            }


            if (jsonArrayElement.getString(5) != null && !"".equals(jsonArrayElement.getString(5))) {
              try {
                LibraryQC lqc = new LibraryQCImpl();
                lqc.setLibrary(library);
                lqc.setInsertSize(insertSize);
                lqc.setResults(Double.valueOf(jsonArrayElement.getString(5)));
                lqc.setQcCreator(user.getLoginName());
                lqc.setQcDate(new Date());
                lqc.setQcType(requestManager.getLibraryQcTypeByName("Qubit"));

                if (!library.getLibraryQCs().contains(lqc)) {
                  library.addQc(lqc);
                  requestManager.saveLibraryQC(lqc);
                  log.info("Added library QC: " + lqc.toString());
                }

                if (insertSize == 0 && lqc.getResults() == 0) {
                  library.setQcPassed(false);
                }
                else {
                  if (jsonArrayElement.getString(8) != null && !"".equals(jsonArrayElement.getString(8))) {
                    library.setQcPassed(Boolean.parseBoolean(jsonArrayElement.getString(8)));
                  }
                }
              }
              catch (NumberFormatException nfe) {
                throw new InputFormException("Supplied Library QC concentration for library '" + jsonArrayElement.getString(3) + "' (" + s.getAlias() + ") is invalid", nfe);
              }
            }

            if (jsonArrayElement.getString(7) != null && !"".equals(jsonArrayElement.getString(7))) {
              try {
                LibraryQC lqc = new LibraryQCImpl();
                lqc.setLibrary(library);
                lqc.setInsertSize(insertSize);
                lqc.setResults(Double.valueOf(jsonArrayElement.getString(7)));
                lqc.setQcCreator(user.getLoginName());
                lqc.setQcDate(new Date());
                lqc.setQcType(requestManager.getLibraryQcTypeByName("Bioanalyzer"));
                if (!library.getLibraryQCs().contains(lqc)) {
                  library.addQc(lqc);
                  requestManager.saveLibraryQC(lqc);
                  log.info("Added library QC: " + lqc.toString());
                }

                if (insertSize == 0 && lqc.getResults() == 0) {
                  library.setQcPassed(false);
                }
                else {
                  if (jsonArrayElement.getString(8) != null && !"".equals(jsonArrayElement.getString(8))) {
                    library.setQcPassed(Boolean.parseBoolean(jsonArrayElement.getString(8)));
                  }
                }
              }
              catch (NumberFormatException nfe) {
                throw new InputFormException("Supplied Library QC concentration for library '" + jsonArrayElement.getString(3) + "' (" + s.getAlias() + ") is invalid", nfe);
              }
            }

            if (library.getQcPassed() || library.getQcPassed() == null) {

              if (jsonArrayElement.getString(9) != null && !"".equals(jsonArrayElement.getString(9))) {
                Collection<TagBarcode> bcs = requestManager.listAllTagBarcodesByStrategyName(jsonArrayElement.getString(9));
                if (!bcs.isEmpty()) {
                  String tags = jsonArrayElement.getString(10);
                  if (!"".equals(tags)) {
                    HashMap<Integer, TagBarcode> tbs = new HashMap<Integer, TagBarcode>();
                    if (tags.contains("-")) {
                      String[] splits = tags.split("-");
                      int count = 1;
                      for (String tag : splits) {
                        for (TagBarcode tb : bcs) {
                          if (tb.getName().equals(tag)) {
                            //set tag barcodes
                            tbs.put(count, tb);
                            count++;
                          }
                        }
                      }
                    }
                    else {
                      for (TagBarcode tb : bcs) {
                        if (tb.getName().equals(tags) || tb.getSequence().equals(tags)) {
                          //set tag barcode
                          tbs.put(1, tb);
                          log.info("Got tag barcode: " + tb.getName());
                          break;
                        }
                      }
                    }

                    library.setTagBarcodes(tbs);
                  }
                  else {
                    throw new InputFormException("Barcode Kit specified but no tag barcodes entered for library '" + jsonArrayElement.getString(3) + "'.");
                  }
                }
                else {
                  throw new InputFormException("No tag barcodes associated with the kit definition '" + jsonArrayElement.getString(9) + "' library '" + jsonArrayElement.getString(3) + "'.");
                }
              }


              LibraryDilution ldi = new LibraryDilution();

              if (jsonArrayElement.getString(11) != null && !"".equals(jsonArrayElement.getString(11))) {
                try {
                  ldi.setLibrary(library);
                  ldi.setSecurityProfile(library.getSecurityProfile());
                  ldi.setConcentration(Double.valueOf(jsonArrayElement.getString(11)));
                  ldi.setCreationDate(new Date());
                  ldi.setDilutionCreator(user.getLoginName());
                  if (!library.getLibraryDilutions().contains(ldi)) {
                    library.addDilution(ldi);
                    log.info("Added library dilution: " + ldi.toString());
                  }
                  requestManager.saveLibraryDilution(ldi);
                }
                catch (NumberFormatException nfe) {
                  throw new InputFormException("Supplied LibraryDilution concentration for library '" + jsonArrayElement.getString(3) + "' (" + s.getAlias() + ") is invalid", nfe);
                }
              }

              log.info("Added library: " + library.toString());
              requestManager.saveLibrary(library);

              Pattern poolPattern = Pattern.compile("^[IiUu][Pp][Oo]([0-9]*)");
              if (jsonArrayElement.getString(12) != null && !"".equals(jsonArrayElement.getString(12))) {
                String poolName = jsonArrayElement.getString(12);

                Matcher m = poolPattern.matcher(poolName);
                if (m.matches()) {
                  Pool existedPool = requestManager.getPoolById(Integer.valueOf(m.group(1)));
                  pools.put(poolName, existedPool);
                  if (jsonArrayElement.getString(13) != null && !"".equals(jsonArrayElement.getString(13))) {
                    existedPool.setConcentration(Double.valueOf(jsonArrayElement.getString(13)));
                  }
                  if (ldi != null) {
                    existedPool.addPoolableElement(ldi);
                  }
                  requestManager.savePool(existedPool);
                }
                else {
                  Pool pool = new PoolImpl();
                  if (!pools.containsKey(poolName)) {
                    pool.setAlias(poolName);
                    pool.setPlatformType(pt);
                    pool.setReadyToRun(true);
                    pool.setCreationDate(new Date());
                    if (jsonArrayElement.getString(13) != null && !"".equals(jsonArrayElement.getString(13))) {
                      pool.setConcentration(Double.valueOf(jsonArrayElement.getString(13)));
                    }
                    else {
                      pool.setConcentration(0.0);
                    }
                    pools.put(poolName, pool);
                    log.info("Added pool: " + poolName);
                    if (ldi != null) {
                      pool.addPoolableElement(ldi);
                    }
                    requestManager.savePool(pool);
                  }
                  else {
                    pool = pools.get(poolName);
                    if (ldi != null) {
                      pool.addPoolableElement(ldi);
                      requestManager.savePool(pool);
                    }
                  }
                }
              }
            }

          }

        }
        catch (Exception e) {
          throw new Exception(e);
        }
      }
    }
    return JSONUtils.SimpleJSONResponse("ok");
  }

  public JSONObject platformsOptions(HttpSession session, JSONObject json) {
    try {
      StringBuilder b = new StringBuilder();
      List<String> pn = new ArrayList<String>(populatePlatformNames());
      for (String name : pn) {
        b.append("<option>" + name + "</option>");
      }
      return JSONUtils.JSONObjectResponse("html", b.toString());
    }
    catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }

  }

  public Collection<String> populatePlatformNames() throws IOException {
    List<String> types = new ArrayList<String>(requestManager.listDistinctPlatformNames());
    Collections.sort(types);
    return types;
  }

  public Collection<LibraryStrategyType> populateLibraryStrategyTypes() throws IOException {
    List<LibraryStrategyType> types = new ArrayList<LibraryStrategyType>(requestManager.listAllLibraryStrategyTypes());
    Collections.sort(types);
    return types;
  }

  public JSONObject libraryStrategyTypesString(HttpSession session, JSONObject json) throws IOException {
    StringBuilder b = new StringBuilder();
    for (LibraryStrategyType t : populateLibraryStrategyTypes()) {
      b.append("<option>" + t.getName() + "</option>");
    }
    return JSONUtils.JSONObjectResponse("html", b.toString());
  }

  public Collection<LibrarySelectionType> populateLibrarySelectionTypes() throws IOException {
    List<LibrarySelectionType> types = new ArrayList<LibrarySelectionType>(requestManager.listAllLibrarySelectionTypes());
    Collections.sort(types);
    return types;
  }

  public JSONObject librarySelectionTypesString(HttpSession session, JSONObject json) throws IOException {
    StringBuilder b = new StringBuilder();
    for (LibrarySelectionType t : populateLibrarySelectionTypes()) {
      b.append("<option>" + t.getName() + "</option>");
    }
    return JSONUtils.JSONObjectResponse("html", b.toString());
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
          libsb.append("<option>" + platform + "-" + s.getDescription() + "</option>");
        }

        StringBuilder tagsb = new StringBuilder();
        List<TagBarcodeStrategy> strategies = new ArrayList<TagBarcodeStrategy>(tagBarcodeStrategyResolverService.getTagBarcodeStrategiesByPlatform(PlatformType.get(platform)));
        tagsb.append("<option >No Barcode Strategy</option>");
        for (TagBarcodeStrategy tb : strategies) {
          tagsb.append("<option>" + tb.getName() + "</option>");
        }

        map.put("libraryTypes", libsb.toString());
        map.put("tagBarcodeStrategies", tagsb.toString());

        return JSONUtils.JSONObjectResponse(map);
      }
    }
    catch (IOException e) {
      e.printStackTrace();
      log.error("Failed to retrieve library types given platform type: ", e);
      return JSONUtils.SimpleJSONError("Failed to retrieve library types given platform type: " + e.getMessage());
    }
    return JSONUtils.SimpleJSONError("Cannot resolve LibraryType from selected Platform");
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setMisoFileManager(MisoFilesManager misoFileManager) {
    this.misoFileManager = misoFileManager;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setTagBarcodeStrategyResolverService(TagBarcodeStrategyResolverService tagBarcodeStrategyResolverService) {
    this.tagBarcodeStrategyResolverService = tagBarcodeStrategyResolverService;
  }


}
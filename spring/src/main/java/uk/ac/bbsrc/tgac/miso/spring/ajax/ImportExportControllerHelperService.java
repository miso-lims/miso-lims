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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

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

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.exception.InputFormException;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.service.IndexService;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.service.LibraryDilutionService;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.service.InstrumentModelService;
import uk.ac.bbsrc.tgac.miso.service.PoolService;
import uk.ac.bbsrc.tgac.miso.service.PoolableElementViewService;
import uk.ac.bbsrc.tgac.miso.service.QualityControlService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;
import uk.ac.bbsrc.tgac.miso.spring.util.FormUtils;

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
  private QualityControlService qcService;
  @Autowired
  private SampleService sampleService;
  @Autowired
  private LibraryDilutionService dilutionService;
  @Autowired
  private PoolableElementViewService poolableElementViewService;
  @Autowired
  private IndexService indexService;
  @Autowired
  private LibraryService libraryService;
  @Autowired
  private InstrumentModelService platformService;
  @Autowired
  private PoolService poolService;
  @Autowired
  private MisoFilesManager misoFileManager;
  @Autowired
  private SecurityManager securityManager;

  private static final Pattern digitPattern = Pattern.compile("(^[0-9]+)[\\.0-9]*");

  public JSONObject searchSamples(HttpSession session, JSONObject json) {
    String searchStr = json.getString("str");
    try {
      Collection<Sample> samples;
      StringBuilder b = new StringBuilder();
      if (!isStringEmptyOrNull(searchStr)) {
        samples = new ArrayList<>(sampleService.list(0, 0, true, "id",
            PaginationFilter.parse(searchStr, SecurityContextHolder.getContext().getAuthentication().getName(), x -> {
              // Discard errors
            })));
      } else {
        samples = new ArrayList<>(sampleService.list(0, 250, true, "id"));
      }

      if (samples.size() > 0) {
        for (Sample s : samples) {
          String dnaOrRNA = "O";
          if ("GENOMIC".equals(s.getSampleType()) || "METAGENOMIC".equals(s.getSampleType())) {
            dnaOrRNA = "D";
          } else if ("NON GENOMIC".equals(s.getSampleType()) || "VIRAL RNA".equals(s.getSampleType())
              || "TRANSCRIPTOMIC".equals(s.getSampleType()) || "METATRANSCRIPTOMIC".equals(s.getSampleType())) {
            dnaOrRNA = "R";
          }
          b.append("<div id=\"sample" + s.getId()
              + "\" onMouseOver=\"this.className=&#39dashboardhighlight&#39\" onMouseOut=\"this.className=&#39dashboard&#39\" " + " "
              + "class=\"dashboard\">");
          b.append("<input type=\"hidden\" id=\"" + s.getId() + "\" name=\"" + s.getName() + "\" projectname=\"" + s.getProject().getName()
              + "\" projectalias=\"" + s.getProject().getAlias() + "\" samplealias=\"" + s.getAlias() + "\" dnaOrRNA=\"" + dnaOrRNA
              + "\"/>");
          b.append("Name: <b>" + s.getName() + "</b><br/>");
          b.append("Alias: <b>" + s.getAlias() + "</b><br/>");
          b.append("From Project: <b>" + s.getProject().getName() + "</b><br/>");
          b.append(
              "<button type=\"button\" class=\"fg-button ui-state-default ui-corner-all\" onclick=\"ImportExport.insertSampleNextAvailable(jQuery('#sample"
                  + s.getId() + "'));\">Add</button>");
          b.append("</div>");
        }
      } else {
        b.append("No matches");
      }
      return JSONUtils.JSONObjectResponse("html", b.toString());
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONObject exportSampleForm(HttpSession session, JSONObject json) {
    try {
      JSONArray a = JSONArray.fromObject(json.getString("form"));
      File f = misoFileManager.getNewFile(Sample.class, "forms",
          "SampleExportForm-" + LimsUtils.getCurrentDateAsString(new SimpleDateFormat("yyyyMMdd-hhmmss")) + ".xlsx");
      FormUtils.createSampleExportForm(f, a);
      return JSONUtils.SimpleJSONResponse("" + f.getName().hashCode());
    } catch (Exception e) {
      log.error("failed to get plate input form", e);
      return JSONUtils.SimpleJSONError("Failed to get plate input form: " + e.getMessage());
    }
  }

  public JSONObject exportLibraryPoolForm(HttpSession session, JSONObject json) {
    try {
      String indexKit = json.getString("indexfamily");
      JSONArray a = JSONArray.fromObject(json.getString("form"));
      File f = misoFileManager.getNewFile(Library.class, "forms",
          "LibraryPoolExportForm-" + LimsUtils.getCurrentDateAsString(new SimpleDateFormat("yyyyMMdd-hhmmss")) + ".xlsx");
      FormUtils.createLibraryPoolExportFormFromWeb(f, a, indexKit);
      return JSONUtils.SimpleJSONResponse("" + f.getName().hashCode());
    } catch (Exception e) {
      log.error("failed to get plate input form", e);
      return JSONUtils.SimpleJSONError("Failed to get plate input form: " + e.getMessage());
    }
  }

  public JSONObject confirmSamplesUpload(HttpSession session, JSONObject json) throws Exception {
    JSONArray jsonArray = JSONArray.fromObject(json.get("table"));
    // add samples
    for (JSONArray jsonArrayElement : (Iterable<JSONArray>) jsonArray) {

      Sample s = null;
      if (jsonArrayElement.get(3) != null && !isStringEmptyOrNull(jsonArrayElement.getString(3))) {
        String salias = jsonArrayElement.getString(3);
        Collection<Sample> ss = sampleService.getByAlias(salias);
        if (!ss.isEmpty()) {
          if (ss.size() == 1) {
            s = ss.iterator().next();
            log.info("Got sample: {}", s.getAlias());
          } else {
            throw new InputFormException("Multiple samples retrieved with this alias: '" + salias + "'. Cannot process.");
          }
        } else {
          throw new InputFormException(
              "No such sample '" + salias + "'in database. Samples need to be created before using the form input functionality");
        }
      } else {
        log.info("Blank sample row found. Ending import.");
        break;
      }

      Date date = new Date();
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      // sample OK - good to go

      try {
        if (s != null) {
          if (jsonArrayElement.get(6) != null) {
            SampleQC sqc = new SampleQC();
            sqc.setSample(s);
            sqc.setResults(jsonArrayElement.getDouble(6));
            sqc.setCreator(user);
            sqc.setDate(date);
            QcType qcType = qcService.getQcType(QcTarget.Sample, "Picogreen");
            if (qcType != null) {
              sqc.setType(qcType);
            } else {
              sqc.setType(qcService.getQcType(QcTarget.Sample, "QuBit"));
            }
            if (s.getQCs().stream().noneMatch(existing -> existing.getType().getId() == sqc.getType().getId())) {
              qcService.createQC(sqc);
              log.info("Added sample QC: {}", sqc);
            }
            if (jsonArrayElement.get(7) != null && !isStringEmptyOrNull(jsonArrayElement.getString(7))) {
              s.setQcPassed(Boolean.parseBoolean(jsonArrayElement.getString(7)));
              sampleService.update(s);
            }
            if (jsonArrayElement.get(8) != null && !isStringEmptyOrNull(jsonArrayElement.getString(8))) {
              List<String> notesList = Arrays.asList((jsonArrayElement.getString(8)).split(";"));
              for (String notetext : notesList) {
                Note note = new Note();
                note.setText(notetext);
                if (!s.getNotes().contains(note)) {
                  sampleService.addNote(s, note);
                  sampleService.update(s);
                  log.info("Added sample Note for Well: {}", note);
                }
              }
            }
          }
        }
      } catch (Exception e) {
        throw new Exception(e);
      }
    }
    return JSONUtils.SimpleJSONResponse("ok");
  }

  public JSONObject confirmLibrariesPoolsUpload(HttpSession session, JSONObject json) throws Exception {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    JSONObject jsonObject = JSONObject.fromObject(json.get("sheet"));
    Map<String, Pool> pools = new HashMap<>();

    Boolean paired = Boolean.parseBoolean(jsonObject.getString("paired"));

    PlatformType pt = null;
    if (jsonObject.getString("platform") != null) {
      pt = PlatformType.get(jsonObject.getString("platform"));
    }

    LibraryType lt = null;
    if (jsonObject.getString("type") != null) {
      lt = libraryService.getLibraryTypeByDescriptionAndPlatform(jsonObject.getString("type"), pt);
    }

    LibrarySelectionType ls = null;
    if (jsonObject.getString("selection") != null) {
      ls = libraryService.getLibrarySelectionTypeByName(jsonObject.getString("selection"));
    }

    LibraryStrategyType lst = null;
    if (jsonObject.getString("strategy") != null) {
      lst = libraryService.getLibraryStrategyTypeByName(jsonObject.getString("strategy"));
    }

    if (jsonObject.get("rows") != null) {

      for (JSONArray jsonArrayElement : (Iterable<JSONArray>) JSONArray
          .fromObject(jsonObject.get("rows").toString().replace("\\\"", "'"))) {

        Sample s = null;
        if (jsonArrayElement.get(1) != null && !isStringEmptyOrNull(jsonArrayElement.getString(1))) {
          String salias = jsonArrayElement.getString(1);
          Collection<Sample> ss = sampleService.getByAlias(salias);
          if (!ss.isEmpty()) {
            if (ss.size() == 1) {
              s = ss.iterator().next();
              log.info("Got sample: {}", s.getAlias());
            } else {
              throw new InputFormException("Multiple samples retrieved with this alias: '" + salias + "'. Cannot process.");
            }
          } else {
            throw new InputFormException(
                "No such sample '" + salias + "'in database. Samples need to be created before using the form input functionality");
          }
        } else {
          log.info("Blank sample row found. Ending import.");
          break;
        }

        try {

          // sample OK - good to go
          if (s != null) {
            String proceedKey = jsonArrayElement.getString(14);

            Library library = new LibraryImpl();

            if ("A".equals(proceedKey) || "L".equals(proceedKey)) {
              library.setAlias(jsonArrayElement.getString(3));
            } else if ("U".equals(proceedKey) || "P".equals(proceedKey)) {
              Collection<Library> byAlias = libraryService.listByAlias(jsonArrayElement.getString(3));
              if (byAlias.size() == 1) {
                library = byAlias.iterator().next();
              } else {
                throw new InputFormException(byAlias.size() + " Libraries found matching alias '" + jsonArrayElement.getString(3) + "'");
              }
            }

            if ("A".equals(proceedKey) || "L".equals(proceedKey) || "U".equals(proceedKey)) {

              library.setSample(s);
              library.setSecurityProfile(s.getSecurityProfile());
              library.setDescription(jsonArrayElement.getString(4));
              library.setCreationDate(new Date());
              library.setPlatformType(pt.getKey());
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
                  library.setDnaSize(insertSize);
                } else {
                  throw new InputFormException(
                      "Supplied Library insert size for library '" + jsonArrayElement.getString(3) + "' (" + s.getAlias() + ") is invalid");
                }
              } catch (NumberFormatException nfe) {
                throw new InputFormException(
                    "Supplied Library insert size for library '" + jsonArrayElement.getString(3) + "' (" + s.getAlias() + ") is invalid",
                    nfe);
              }

              log.info("Added library: {}", library);
              libraryService.create(library);
              if (jsonArrayElement.get(5) != null && !isStringEmptyOrNull(jsonArrayElement.getString(5))) {
                try {
                  LibraryQC lqc = new LibraryQC();
                  lqc.setLibrary(library);
                  lqc.setResults(Double.valueOf(jsonArrayElement.getString(5)));
                  lqc.setCreator(user);
                  lqc.setDate(new Date());
                  lqc.setType(qcService.getQcType(QcTarget.Library, "Qubit"));

                  if (library.getQCs().stream().noneMatch(existing -> existing.getType().getId() == lqc.getType().getId())) {
                    qcService.createQC(lqc);
                    log.info("Added library QC: {}", lqc);
                  }

                  if (insertSize == 0 && lqc.getResults() == 0) {
                    library.setQcPassed(false);
                  } else {
                    if (jsonArrayElement.get(8) != null && !isStringEmptyOrNull(jsonArrayElement.getString(8))) {
                      library.setQcPassed(Boolean.parseBoolean(jsonArrayElement.getString(8)));
                    }
                  }
                } catch (NumberFormatException nfe) {
                  throw new InputFormException("Supplied Library QC concentration for library '" + jsonArrayElement.getString(3) + "' ("
                      + s.getAlias() + ") is invalid", nfe);
                }
              }

              if (jsonArrayElement.get(7) != null && !isStringEmptyOrNull(jsonArrayElement.getString(7))) {
                try {
                  LibraryQC lqc = new LibraryQC();
                  lqc.setLibrary(library);
                  lqc.setResults(Double.valueOf(jsonArrayElement.getString(7)));
                  lqc.setType(qcService.getQcType(QcTarget.Library, "Bioanalyzer"));

                  if (library.getQCs().stream().noneMatch(existing -> existing.getType().getId() == lqc.getType().getId())) {
                    qcService.createQC(lqc);
                    log.info("Added library QC: {}", lqc);
                  }

                  if (insertSize == 0 && lqc.getResults() == 0) {
                    library.setQcPassed(false);
                  } else {
                    if (jsonArrayElement.get(8) != null && !isStringEmptyOrNull(jsonArrayElement.getString(8))) {
                      library.setQcPassed(Boolean.parseBoolean(jsonArrayElement.getString(8)));
                    }
                  }
                } catch (NumberFormatException nfe) {
                  throw new InputFormException("Supplied Library QC concentration for library '" + jsonArrayElement.getString(3) + "' ("
                      + s.getAlias() + ") is invalid", nfe);
                }
              }

              if (jsonArrayElement.get(9) != null && !isStringEmptyOrNull(jsonArrayElement.getString(9))
                  && (library.getQcPassed() || library.getQcPassed() == null)) {
                Iterable<Index> index = indexService.getIndexFamilyByName(jsonArrayElement.getString(9)).getIndices();
                if (index != null) {
                  String tags = jsonArrayElement.getString(10);
                  if (!isStringEmptyOrNull(tags)) {
                    library.setIndices(FormUtils.matchIndicesFromText(index, tags));
                  } else {
                    throw new InputFormException(
                        "Index Family specified but no indices entered for library '" + jsonArrayElement.getString(3) + "'.");
                  }
                } else {
                  throw new InputFormException("No indices associated with the index family '" + jsonArrayElement.getString(9)
                      + "' library '" + jsonArrayElement.getString(3) + "'.");
                }
              }

              log.info("Added library: {}", library);
              libraryService.create(library);
            }

            if ((library.getQcPassed() || library.getQcPassed() == null) && ("A".equals(proceedKey) || "P".equals(proceedKey))) {

              LibraryDilution ldi = new LibraryDilution();

              if (jsonArrayElement.get(11) != null && !isStringEmptyOrNull(jsonArrayElement.getString(11))) {
                try {
                  ldi.setLibrary(library);
                  ldi.setSecurityProfile(library.getSecurityProfile());
                  ldi.setConcentration(Double.valueOf(jsonArrayElement.getString(11)));
                  ldi.setCreationDate(new Date());
                  ldi.setLastModified(ldi.getCreationDate());
                  ldi.setCreator(user);
                  if (!library.getLibraryDilutions().contains(ldi)) {
                    library.addDilution(ldi);
                    log.info("Added library dilution: {}", ldi);
                  }
                  dilutionService.create(ldi);
                } catch (NumberFormatException nfe) {
                  throw new InputFormException("Supplied LibraryDilution concentration for library '" + jsonArrayElement.getString(3)
                      + "' (" + s.getAlias() + ") is invalid", nfe);
                }
              }

              log.info("Added library: {}", library);
              libraryService.create(library);

              Pattern poolPattern = Pattern.compile("^[IiUu][Pp][Oo]([0-9]*)");
              if (jsonArrayElement.get(12) != null && !isStringEmptyOrNull(jsonArrayElement.getString(12))) {
                String poolName = jsonArrayElement.getString(12);

                PoolableElementView ldiView = null;
                if (ldi != null) {
                  ldiView = poolableElementViewService.get(ldi.getId());
                }

                Matcher m = poolPattern.matcher(poolName);

                if (m.matches()) {
                  Pool existedPool = poolService.get(Integer.valueOf(m.group(1)));
                  pools.put(poolName, existedPool);
                  if (jsonArrayElement.get(13) != null && !isStringEmptyOrNull(jsonArrayElement.getString(13))) {
                    existedPool.setConcentration(Double.valueOf(jsonArrayElement.getString(13)));
                  }

                  if (ldiView != null) {
                    existedPool.getPoolDilutions().add(new PoolDilution(existedPool, ldiView));
                  }
                  poolService.save(existedPool);
                } else {
                  Pool pool = new PoolImpl();
                  if (!pools.containsKey(poolName)) {
                    pool.setAlias(poolName);
                    pool.setPlatformType(pt);
                    pool.setCreationDate(new Date());
                    if (jsonArrayElement.get(13) != null && !isStringEmptyOrNull(jsonArrayElement.getString(13))) {
                      pool.setConcentration(Double.valueOf(jsonArrayElement.getString(13)));
                    } else {
                      pool.setConcentration(0.0);
                    }
                    pools.put(poolName, pool);
                    log.info("Added pool: {}", poolName);
                    if (ldiView != null) {
                      pool.getPoolDilutions().add(new PoolDilution(pool, ldiView));
                    }
                    poolService.save(pool);
                  } else {
                    pool = pools.get(poolName);
                    if (ldiView != null) {
                      pool.getPoolDilutions().add(new PoolDilution(pool, ldiView));
                      poolService.save(pool);
                    }
                  }
                }
              }

              log.info("Added library: {}", library);
              libraryService.create(library);
            }
          }

        } catch (Exception e) {
          throw new Exception(e);
        }
      }
    }
    return JSONUtils.SimpleJSONResponse("ok");
  }

  public JSONObject platformsOptions(HttpSession session, JSONObject json) {
    try {
      StringBuilder b = new StringBuilder();
      List<String> pn = new ArrayList<>(populatePlatformNames());
      for (String name : pn) {
        b.append("<option>" + name + "</option>");
      }
      return JSONUtils.JSONObjectResponse("html", b.toString());
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }

  }

  public Collection<String> populatePlatformNames() throws IOException {
    List<String> types = new ArrayList<>(platformService.listDistinctPlatformTypeNames());
    Collections.sort(types);
    return types;
  }

  public Collection<LibraryStrategyType> populateLibraryStrategyTypes() throws IOException {
    List<LibraryStrategyType> types = new ArrayList<>(libraryService.listLibraryStrategyTypes());
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
    List<LibrarySelectionType> types = new ArrayList<>(libraryService.listLibrarySelectionTypes());
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
      if (json.has("platform") && !isStringEmptyOrNull((String) json.get("platform"))) {
        String platform = json.getString("platform");
        Map<String, Object> map = new HashMap<>();

        StringBuilder libsb = new StringBuilder();
        List<LibraryType> types = new ArrayList<>(libraryService.listLibraryTypesByPlatform(PlatformType.get(platform)));
        Collections.sort(types);
        for (LibraryType s : types) {
          libsb.append("<option>" + platform + "-" + s.getDescription() + "</option>");
        }

        StringBuilder indexFamilies = new StringBuilder();
        indexFamilies.append("<option >No Index Family</option>");
        for (IndexFamily ifam : indexService.getIndexFamiliesByPlatform(PlatformType.get(platform))) {
          indexFamilies.append("<option>" + ifam.getName() + "</option>");
        }

        map.put("libraryTypes", libsb.toString());
        map.put("indexFamilies", indexFamilies.toString());

        return JSONUtils.JSONObjectResponse(map);
      }
    } catch (IOException e) {
      log.error("Failed to retrieve library types given platform type: ", e);
      return JSONUtils.SimpleJSONError("Failed to retrieve library types given platform type: " + e.getMessage());
    }
    return JSONUtils.SimpleJSONError("Cannot resolve LibraryType from selected Platform");
  }

  public void setMisoFileManager(MisoFilesManager misoFileManager) {
    this.misoFileManager = misoFileManager;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setIndexService(IndexService indexService) {
    this.indexService = indexService;
  }

  public void setSampleService(SampleService sampleService) {
    this.sampleService = sampleService;
  }

  public void setDilutionService(LibraryDilutionService dilutionService) {
    this.dilutionService = dilutionService;
  }

  public void setLibraryService(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

}

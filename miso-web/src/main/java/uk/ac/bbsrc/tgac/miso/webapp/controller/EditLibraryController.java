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

package uk.ac.bbsrc.tgac.miso.webapp.controller;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.util.JSONUtils;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryQCImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedLibraryException;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.security.util.LimsSecurityUtils;
import uk.ac.bbsrc.tgac.miso.core.service.IndexService;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.util.AliasComparator;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.dto.DetailedLibraryDto;
import uk.ac.bbsrc.tgac.miso.dto.DilutionDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryDto;
import uk.ac.bbsrc.tgac.miso.dto.PoolDto;
import uk.ac.bbsrc.tgac.miso.service.ChangeLogService;
import uk.ac.bbsrc.tgac.miso.service.KitService;
import uk.ac.bbsrc.tgac.miso.service.LibraryDilutionService;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.service.PoolService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;
import uk.ac.bbsrc.tgac.miso.service.impl.RunService;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkEditTableBackend;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkMergeTableBackend;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkPropagateTableBackend;

/**
 * uk.ac.bbsrc.tgac.miso.webapp.controller
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
@Controller
@RequestMapping("/library")
@SessionAttributes("library")
public class EditLibraryController {
  protected static final Logger log = LoggerFactory.getLogger(EditLibraryController.class);

  private static final IndexFamily INDEX_FAMILY_NEEDS_PLATFORM = new IndexFamily();

  static {
    INDEX_FAMILY_NEEDS_PLATFORM.setName("Please select a platform...");
  }

  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private RequestManager requestManager;

  @Autowired
  private IndexService indexService;
  @Autowired
  private LibraryService libraryService;
  @Autowired
  private SampleService sampleService;

  @Autowired
  private ChangeLogService changeLogService;

  @Autowired
  private KitService kitService;

  @Autowired
  private NamingScheme namingScheme;
  @Autowired
  private RunService runService;
  @Autowired
  private PoolService poolService;

  @Autowired
  private LibraryDilutionService dilutionService;

  public NamingScheme getNamingScheme() {
    return namingScheme;
  }

  public void setNamingScheme(NamingScheme namingScheme) {
    this.namingScheme = namingScheme;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setIndexService(IndexService indexService) {
    this.indexService = indexService;
  }

  public void setLibraryService(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

  public void setSampleService(SampleService sampleService) {
    this.sampleService = sampleService;
  }

  public void setKitService(KitService kitService) {
    this.kitService = kitService;
  }

  public void setRunService(RunService runService) {
    this.runService = runService;
  }

  @Value("${miso.notification.interop.enabled}")
  private Boolean metrixEnabled;
  @Value("${miso.autoGenerateIdentificationBarcodes}")
  private Boolean autoGenerateIdBarcodes;
  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;
  @Value("${miso.display.library.bulk.libraryalias}")
  private Boolean showLibraryAlias;
  @Value("${miso.display.library.bulk.description}")
  private Boolean showDescription;
  @Value("${miso.display.library.bulk.volume}")
  private Boolean showVolume;

  @ModelAttribute("metrixEnabled")
  public Boolean isMetrixEnabled() {
    return metrixEnabled;
  }

  @ModelAttribute("autoGenerateIdBarcodes")
  public Boolean autoGenerateIdentificationBarcodes() {
    return autoGenerateIdBarcodes;
  }

  @ModelAttribute("aliasGenerationEnabled")
  public Boolean isAliasGenerationEnabled() {
    return namingScheme != null && namingScheme.hasLibraryAliasGenerator();
  }

  @ModelAttribute("detailedSample")
  public Boolean isDetailedSampleEnabled() {
    return detailedSample;
  }

  public void addAdjacentLibraries(Library l, ModelMap model) throws IOException {
    if (l.getId() == AbstractLibrary.UNSAVED_ID) {
      return;
    }
    model.put("previousLibrary", libraryService.getAdjacentLibrary(l.getId(), true));
    model.put("nextLibrary", libraryService.getAdjacentLibrary(l.getId(), false));

  }

  public List<Pool> getPoolsByLibrary(Library l) throws IOException {
    if (!l.getLibraryDilutions().isEmpty()) {
      List<Pool> pools = new ArrayList<>(poolService.listPoolsByLibraryId(l.getId()));
      Collections.sort(pools);
      return pools;
    }
    return Collections.emptyList();
  }

  public Set<Run> getRunsByLibraryPools(List<Pool> pools) throws IOException {
    Set<Run> runs = new TreeSet<>();
    for (Pool pool : pools) {
      Collection<Run> prs = runService.listByPoolId(pool.getId());
      runs.addAll(prs);
    }
    return runs;
  }

  public Collection<LibraryType> populateLibraryTypesByPlatform(String platform) throws IOException {
    List<LibraryType> types = new ArrayList<>();
    for (LibraryType type : libraryService.listLibraryTypesByPlatform(PlatformType.get(platform))) {
      if (!type.getArchived()) {
        types.add(type);
      }
    }
    Collections.sort(types);
    return types;
  }

  public Collection<LibraryType> populateLibraryTypes() throws IOException {
    List<LibraryType> types = new ArrayList<>(libraryService.listLibraryTypes());
    Collections.sort(types);
    return types;
  }

  @ModelAttribute("maxLengths")
  public Map<String, Integer> maxLengths() throws IOException {
    return libraryService.getLibraryColumnSizes();
  }

  private List<String> populatePlatformTypes() throws IOException {
    return populatePlatformTypes(Lists.newArrayList());
  }

  private List<String> populatePlatformTypes(Library library) throws IOException {
    if (library.getPlatformType() == null) {
      return populatePlatformTypes();
    } else {
      return populatePlatformTypes(Lists.newArrayList(library.getPlatformType().getKey()));
    }
  }

  private List<String> populatePlatformTypes(Collection<String> current) throws IOException {
    Collection<PlatformType> base = requestManager.listActivePlatformTypes();
    if (base.isEmpty()) {
      base = Arrays.asList(PlatformType.values());
    }
    List<String> types = new ArrayList<>(PlatformType.platformTypeNames(base));
    for (String s : current) {
      if (s != null && !types.contains(s)) {
        types.add(s);
      }
    }
    Collections.sort(types);
    return types;
  }

  @ModelAttribute("platformTypesString")
  public String platformTypesString() throws IOException {
    List<String> names = new ArrayList<>();
    List<String> pn = populatePlatformTypes();
    for (String name : pn) {
      names.add("\"" + name + "\"" + ":" + "\"" + name + "\"");
    }
    if (!pn.isEmpty()) {
      names.add("\"selected\":" + "\"" + pn.get(0) + "\"");
    }
    return LimsUtils.join(names, ",");
  }

  @ModelAttribute("libraryTypesString")
  public String libraryTypesString() throws IOException {
    List<String> types = new ArrayList<>();
    for (LibraryType t : populateLibraryTypes()) {
      types.add("\"" + t.getDescription() + "\"" + ":" + "\"" + t.getDescription() + "\"");
    }
    return LimsUtils.join(types, ",");
  }

  @ModelAttribute("librarySelectionTypes")
  public Collection<LibrarySelectionType> populateLibrarySelectionTypes() throws IOException {
    List<LibrarySelectionType> types = new ArrayList<>(libraryService.listLibrarySelectionTypes());
    Collections.sort(types);
    return types;
  }

  @ModelAttribute("librarySelectionTypesString")
  public String librarySelectionTypesString() throws IOException {
    List<String> types = new ArrayList<>();
    for (LibrarySelectionType t : populateLibrarySelectionTypes()) {
      types.add("\"" + t.getName() + "\"" + ":" + "\"" + t.getName() + "\"");
    }
    return LimsUtils.join(types, ",");
  }

  @ModelAttribute("libraryStrategyTypes")
  public Collection<LibraryStrategyType> populateLibraryStrategyTypes() throws IOException {
    List<LibraryStrategyType> types = new ArrayList<>(libraryService.listLibraryStrategyTypes());
    Collections.sort(types);
    return types;
  }

  @ModelAttribute("libraryStrategyTypesString")
  public String libraryStrategyTypesString() throws IOException {
    List<String> types = new ArrayList<>();
    for (LibraryStrategyType t : populateLibraryStrategyTypes()) {
      types.add("\"" + t.getName() + "\"" + ":" + "\"" + t.getName() + "\"");
    }
    return LimsUtils.join(types, ",");
  }

  public void populateAvailableIndexFamilies(Library library, ModelMap model) throws IOException {
    if (library.getPlatformType() == null || isStringEmptyOrNull(library.getPlatformType().getKey())) {
      model.put("indexFamilies", Collections.singleton(INDEX_FAMILY_NEEDS_PLATFORM));
    } else {
      List<IndexFamily> visibleFamilies = new ArrayList<>();
      visibleFamilies.add(IndexFamily.NULL);
      visibleFamilies.addAll(indexService.getIndexFamiliesByPlatform(library.getPlatformType()));
      model.put("indexFamilies", visibleFamilies);
    }
  }

  @ModelAttribute("libraryQCUnits")
  public String libraryQCUnits() {
    return LibraryQCImpl.UNITS;
  }

  @ModelAttribute("libraryDilutionUnits")
  public String libraryDilutionUnits() {
    return LibraryDilution.UNITS;
  }

  @ModelAttribute("poolConcentrationUnits")
  public String poolConcentrationUnits() {
    return PoolImpl.CONCENTRATION_UNITS;
  }

  @ModelAttribute("prepKits")
  public List<KitDescriptor> getPrepKits() throws IOException {
    List<KitDescriptor> list = new ArrayList<>(kitService.listKitDescriptorsByType(KitType.LIBRARY));
    Collections.sort(list, new Comparator<KitDescriptor>() {
      @Override
      public int compare(KitDescriptor kd1, KitDescriptor kd2) {
        return kd1.getName().compareTo(kd2.getName());
      }
    });
    return list;
  }

  private void populateDesigns(ModelMap model, SampleClass sampleClass) throws IOException {
    model.put("libraryDesigns", requestManager.listLibraryDesignByClass(sampleClass));
  }

  private void populateDesignCodes(ModelMap model) throws IOException {
    model.put("libraryDesignCodes", requestManager.listLibraryDesignCodes());
  }

  /**
   * Translates foreign keys to entity objects with only the ID set, to be used in service layer to reload persisted child objects
   * 
   * @param binder
   */
  @InitBinder
  public void includeForeignKeys(WebDataBinder binder) {
    binder.registerCustomEditor(KitDescriptor.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String text) throws IllegalArgumentException {
        if (isStringEmptyOrNull(text)) {
          setValue(null);
        } else {
          KitDescriptor to = new KitDescriptor();
          to.setId(Long.valueOf(text));
          setValue(to);
        }
      }
    });
  }

  // Handsontable
  @ModelAttribute("referenceDataJSON")
  public JSONObject referenceDataJsonString() throws IOException {
    final JSONObject hot = new JSONObject();
    final List<String> qcValues = new ArrayList<>();
    qcValues.add("true");
    qcValues.add("false");
    qcValues.add("");
    JSONArray selectionTypes = new JSONArray();
    for (final LibrarySelectionType lst : populateLibrarySelectionTypes()) {
      JSONObject selType = new JSONObject();
      selType.put("id", lst.getId());
      selType.put("alias", lst.getName());
      selectionTypes.add(selType);
    }
    JSONArray strategyTypes = new JSONArray();
    for (final LibraryStrategyType lstrat : populateLibraryStrategyTypes()) {
      JSONObject stratType = new JSONObject();
      stratType.put("id", lstrat.getId());
      stratType.put("alias", lstrat.getName());
      strategyTypes.add(stratType);
    }
    JSONArray libraryTypes = new JSONArray();
    for (final LibraryType lt : populateLibraryTypes()) {
      JSONObject libType = new JSONObject();
      libType.put("id", lt.getId());
      libType.put("alias", lt.getDescription());
      libType.put("platform", lt.getPlatformType());
      libType.put("archived", lt.getArchived());
      libraryTypes.add(libType);
    }
    JSONArray platformTypes = new JSONArray();
    Collection<PlatformType> activePlatforms = requestManager.listActivePlatformTypes();
    for (final PlatformType platformType : PlatformType.values()) {
      JSONObject platformTypeJson = new JSONObject();
      platformTypeJson.put("id", platformType.getKey());
      platformTypeJson.put("active", activePlatforms.isEmpty() || activePlatforms.contains(platformType));
      platformTypes.add(platformTypeJson);
    }

    hot.put("qcValues", qcValues);
    hot.put("selectionTypes", selectionTypes);
    hot.put("strategyTypes", strategyTypes);
    hot.put("libraryTypes", libraryTypes);

    return hot;
  }

  /* HOT */
  @RequestMapping(value = "indicesJson", method = RequestMethod.GET)
  public @ResponseBody JSONObject indicesJson(@RequestParam("indexFamily") String indexFamily, @RequestParam("position") String position)
      throws IOException {
    final JSONObject rtn = new JSONObject();
    List<JSONObject> rtnList = new ArrayList<>();
    try {
      if (!isStringEmptyOrNull(indexFamily)) {
        final IndexFamily ifam = indexService.getIndexFamilyByName(indexFamily);
        if (ifam != null) {
          rtnList = indicesForPosition(ifam, Integer.parseInt(position));
        }
      }
    } catch (Exception e) {
      log.error("Failed to get indices", e);
    }
    rtn.put("indices", rtnList);
    return rtn;
  }

  /**
   * Each PlatformName holds a null IndexFamily.
   *
   * Structure of this indices object:
   * 
   * <pre>
   *  {
   *    PlatformName : {
   *      IndexFamilyName: {
   *        1: [ { id: ##, name: AAAA, sequence: XXXXX }, ... ],
   *        ... },
   *      ... },
   *  ... }
   * </pre>
   * 
   * @return indices object
   */
  @ModelAttribute("indices")
  public JSONObject indicesString() {
    final JSONObject io = new JSONObject();
    try {
      for (String pfName : requestManager.listDistinctPlatformNames()) {
        JSONObject pf = new JSONObject();
        io.put(pfName, pf);
        io.getJSONObject(pfName).put("No Index", nullIndexFamily());
      }
      for (IndexFamily ifam : indexService.getIndexFamilies()) {
        JSONObject ifamo = new JSONObject();
        for (int i = 1; i <= ifam.getMaximumNumber(); i++) {
          ifamo.put(Integer.toString(i), indicesForPosition(ifam, i));
        }
        String platformKey = ifam.getPlatformType().getKey();
        io.getJSONObject(platformKey).put(ifam.getName(), ifamo);
      }
    } catch (IOException e) {
      log.error("Failed to retrieve all platform names: " + e);
    }
    return io;
  }

  public List<JSONObject> indicesForPosition(IndexFamily ifam, int position) {
    final List<JSONObject> rtnList = new ArrayList<>();
    for (final Index index : ifam.getIndicesForPosition(position)) {
      final JSONObject obj = new JSONObject();
      obj.put("id", index.getId());
      obj.put("name", index.getName());
      obj.put("sequence", index.getSequence());
      obj.put("label", index.getLabel());
      rtnList.add(obj);
    }
    return rtnList;
  }

  public JSONObject nullIndexFamily() {
    final JSONObject nullIndexFam = new JSONObject();
    final JSONArray nullIndices = new JSONArray();
    final JSONObject nullIndex = new JSONObject();
    nullIndex.put("id", IndexFamily.NULL.getId());
    nullIndex.put("name", IndexFamily.NULL.getName());
    nullIndex.put("sequence", "");
    nullIndices.add(nullIndex);
    nullIndexFam.put("1", nullIndices);
    return nullIndexFam;
  }

  /* HOT */
  @RequestMapping(value = "libraryTypesJson", method = RequestMethod.GET)
  public @ResponseBody JSONObject libraryTypesJson(@RequestParam("platform") String platform) throws IOException {
    final JSONObject rtn = new JSONObject();
    final List<String> rtnLibTypes = new ArrayList<>();
    if (!isStringEmptyOrNull(platform)) {
      final Collection<LibraryType> libTypes = libraryService.listLibraryTypesByPlatform(PlatformType.get(platform));
      for (final LibraryType type : libTypes) {
        rtnLibTypes.add(type.getDescription());
      }
    }
    rtn.put("libraryTypes", rtnLibTypes);
    return rtn;
  }

  /* HOT */
  @RequestMapping(value = "indexPositionsJson", method = RequestMethod.GET)
  public @ResponseBody JSONObject indexPositionsJson(@RequestParam("indexFamily") String indexFamily) {
    JSONObject rtn;
    if (!isStringEmptyOrNull(indexFamily)) {
      final IndexFamily ifam = indexService.getIndexFamilyByName(indexFamily.trim());
      if (ifam != null) {
        rtn = new JSONObject();
        rtn.put("numApplicableIndices", ifam.getMaximumNumber());
      } else {
        rtn = JSONUtils.SimpleJSONError("No family found with the name: \"" + indexFamily + "\"");
      }
    } else {
      rtn = JSONUtils.SimpleJSONError("No valid family given");
    }
    return rtn;
  }

  @RequestMapping(value = "/rest/changes", method = RequestMethod.GET)
  public @ResponseBody Collection<ChangeLog> jsonRestChanges() throws IOException {
    return changeLogService.listAll("Library");
  }

  @RequestMapping(value = "/new/{sampleId}", method = RequestMethod.GET)
  public ModelAndView newAssignedLibrary(@PathVariable Long sampleId, ModelMap model) throws IOException {
    return setupForm(AbstractLibrary.UNSAVED_ID, sampleId, model);
  }

  @RequestMapping(value = "/{libraryId}", method = RequestMethod.GET)
  public ModelAndView setupForm(@PathVariable Long libraryId, ModelMap model) throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Library library = libraryService.get(libraryId);

      if (library == null) {
        throw new SecurityException("No such Library.");
      }
      if (!library.userCanRead(user)) {
        throw new SecurityException("Permission denied.");
      }

      model.put("formObj", library);
      model.put("library", library);

      model.put("platformTypes", populatePlatformTypes(library));
      populateAvailableIndexFamilies(library, model);
      addAdjacentLibraries(library, model);

      List<Pool> pools = getPoolsByLibrary(library);
      Map<Long, Library> poolLibraryMap = new HashMap<>();
      for (Pool pool : pools) {
        poolLibraryMap.put(pool.getId(), library);
      }
      model.put("poolLibraryMap", poolLibraryMap);
      model.put("libraryPools", pools);
      model.put("libraryRuns", getRunsByLibraryPools(pools));

      populateDesigns(model,
          LimsUtils.isDetailedSample(library.getSample()) ? ((DetailedSample) library.getSample()).getSampleClass() : null);
      populateDesignCodes(model);

      model.put("owners", LimsSecurityUtils.getPotentialOwners(user, library, securityManager.listAllUsers()));
      model.put("accessibleUsers", LimsSecurityUtils.getAccessibleUsers(user, library, securityManager.listAllUsers()));
      model.put("accessibleGroups", LimsSecurityUtils.getAccessibleGroups(user, library, securityManager.listAllGroups()));
      model.put("title", "Library " + libraryId);
      return new ModelAndView("/pages/editLibrary.jsp", model);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show library", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(value = "/{libraryId}/sample/{sampleId}", method = RequestMethod.GET)
  public ModelAndView setupForm(@PathVariable Long libraryId, @PathVariable Long sampleId, ModelMap model) throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Library library = null;
      if (libraryId == AbstractLibrary.UNSAVED_ID) {
        library = (isDetailedSampleEnabled() ? new DetailedLibraryImpl() : new LibraryImpl(user));
        model.put("title", "New Library");
      } else {
        library = libraryService.get(libraryId);
        model.put("title", "Library " + libraryId);
        if (library.getIndices() != null && !library.getIndices().isEmpty() && library.getIndices().get(1) != null) {
          model.put("selectedIndexFamily", library.getIndices().get(1).getFamily().getName());
          model.put("availableIndexFamilyIndices", library.getIndices().get(1).getFamily().getIndices());
        }
      }

      if (!library.userCanRead(user)) {
        throw new SecurityException("Permission denied.");
      }

      SampleClass sampleClass = null;
      if (sampleId != null) {
        Sample sample = sampleService.get(sampleId);
        library.setSample(sample);
        model.put("sample", sample);
        if (LimsUtils.isDetailedSample(sample)) {
          DetailedSample detailed = (DetailedSample) sample;
          sampleClass = detailed.getSampleClass();
        }

        List<Sample> projectSamples = new ArrayList<>(sampleService.listByProjectId(sample.getProject().getProjectId()));
        Collections.sort(projectSamples, new AliasComparator<>());
        model.put("projectSamples", projectSamples);

        String regex = "([A-z0-9]+)_S([A-z0-9]+)_(.*)";
        Pattern pat = Pattern.compile(regex);
        Matcher mat = pat.matcher(sample.getAlias());
        if (mat.matches()) {
          // convert the sample alias automatically to a library alias
          int numLibs = libraryService.listBySampleId(sample.getId()).size();
          String autogenLibAlias = mat.group(1) + "_" + "L" + mat.group(2) + "-" + (numLibs + 1) + "_" + mat.group(3);
          model.put("autogeneratedLibraryAlias", autogenLibAlias);
        }

        library.setSample(sample);
        if (Arrays.asList(user.getRoles()).contains("ROLE_TECH")) {
          SecurityProfile sp = new SecurityProfile(user);
          LimsUtils.inheritUsersAndGroups(library, sample.getSecurityProfile());
          sp.setOwner(user);
          library.setSecurityProfile(sp);
        } else {
          library.inheritPermissions(sample);
        }
      }

      model.put("sampleClass", sampleClass);

      model.put("formObj", library);
      model.put("library", library);
      model.put("platformTypes", populatePlatformTypes());
      populateDesigns(model,
          LimsUtils.isDetailedSample(library.getSample()) ? ((DetailedSample) library.getSample()).getSampleClass() : null);
      populateDesignCodes(model);

      populateAvailableIndexFamilies(library, model);

      addAdjacentLibraries(library, model);

      List<Pool> pools = getPoolsByLibrary(library);
      Map<Long, Library> poolLibraryMap = new HashMap<>();
      for (Pool pool : pools) {
        poolLibraryMap.put(pool.getId(), library);
      }
      model.put("poolLibraryMap", poolLibraryMap);
      model.put("libraryPools", pools);
      model.put("libraryRuns", getRunsByLibraryPools(pools));

      model.put("owners", LimsSecurityUtils.getPotentialOwners(user, library, securityManager.listAllUsers()));
      model.put("accessibleUsers", LimsSecurityUtils.getAccessibleUsers(user, library, securityManager.listAllUsers()));
      model.put("accessibleGroups", LimsSecurityUtils.getAccessibleGroups(user, library, securityManager.listAllGroups()));
      return new ModelAndView("/pages/editLibrary.jsp", model);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show library", ex);
      }
      throw ex;
    }
  }

  private final BulkEditTableBackend<Library, LibraryDto> libraryBulkEditBackend = new BulkEditTableBackend<Library, LibraryDto>(
      "library", LibraryDto.class, "Libraries") {

    @Override
    protected Iterable<Library> load(List<Long> ids) throws IOException {
      List<Library> results = libraryService.listByIdList(ids);
      SampleClass sampleClass = null;
      for (Library library : results) {
        if (isDetailedSampleEnabled()) {
          if (sampleClass == null) {
            sampleClass = ((DetailedSample) library.getSample()).getSampleClass();
          } else if (((DetailedSample) library.getSample()).getSampleClass().getId() != sampleClass.getId()) {
            throw new IOException("Can only update libraries when samples all have the same class.");
          }
        }
      }
      return results;
    }

    @Override
    protected LibraryDto asDto(Library model) {
      return Dtos.asDto(model);
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) {
      writeLibraryConfiguration(mapper, config);
    }
  };

  private final BulkPropagateTableBackend<Sample, LibraryDto> libraryBulkPropagateBackend = new BulkPropagateTableBackend<Sample, LibraryDto>(
      "library", LibraryDto.class, "Libraries", "Samples") {

    @Override
    protected LibraryDto createDtoFromParent(Sample sample) {
      LibraryDto dto;
      if (LimsUtils.isDetailedSample(sample)) {
        DetailedLibraryDto detailedDto = new DetailedLibraryDto();
        detailedDto.setParentSampleClassId(((DetailedSample) sample).getSampleClass().getId());
        detailedDto.setNonStandardAlias(((DetailedSample) sample).hasNonStandardAlias());

        dto = detailedDto;
      } else {
        dto = new LibraryDto();
      }
      dto.setParentSampleId(sample.getId());
      dto.setParentSampleAlias(sample.getAlias());
      return dto;
    }

    @Override
    protected Iterable<Sample> loadParents(List<Long> ids) throws IOException {
      Collection<Sample> results = sampleService.listByIdList(ids);

      SampleClass sampleClass = null;
      boolean hasPlain = false;
      for (Sample sample : results) {
        if (sample instanceof DetailedSample) {
          DetailedSample detailed = (DetailedSample) sample;
          if (sampleClass == null) {
            sampleClass = detailed.getSampleClass();
          } else if (sampleClass.getId() != detailed.getSampleClass().getId()) {
            throw new IOException("Can only create libraries when samples all have the same class.");
          }
        } else {
          hasPlain = true;
        }
      }
      if (hasPlain && sampleClass != null) {
        throw new IOException("Cannot mix plain and detailed samples.");
      }
      return results;
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) {
      writeLibraryConfiguration(mapper, config);
    }
  };

  private void writeLibraryConfiguration(ObjectMapper mapper, ObjectNode config) {
    config.put("showDescription", showDescription);
    config.put("showVolume", showVolume);
    config.put("showLibraryAlias", showLibraryAlias);
  }

  @RequestMapping(value = "/bulk/propagate/{sampleIds}", method = RequestMethod.GET)
  public ModelAndView propagateFromSamples(@PathVariable String sampleIds, ModelMap model) throws IOException, MisoNamingException {
    return libraryBulkPropagateBackend.propagate(sampleIds, model);
  }

  @RequestMapping(value = "/bulk/edit/{libraryIds}", method = RequestMethod.GET)
  public ModelAndView editBulkLibraries(@PathVariable String libraryIds, ModelMap model) throws IOException {
    return libraryBulkEditBackend.edit(libraryIds, model);
  }

  private final BulkPropagateTableBackend<Library, DilutionDto> dilutionBulkPropagateBackend = new BulkPropagateTableBackend<Library, DilutionDto>(
      "dilution", DilutionDto.class, "Dilutions", "Libraries") {

    @Override
    protected DilutionDto createDtoFromParent(Library item) {
      DilutionDto dto = new DilutionDto();
      dto.setLibrary(Dtos.asMinimalDto(item));
      return dto;
    }

    @Override
    protected Iterable<Library> loadParents(List<Long> ids) throws IOException {
      return libraryService.listByIdList(ids);
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) {
    }
  };

  @RequestMapping(value = "/dilutions/bulk/propagate/{libraryIds}", method = RequestMethod.GET)
  public ModelAndView propagateDilutions(@PathVariable String libraryIds, ModelMap model) throws IOException {
    return dilutionBulkPropagateBackend.propagate(libraryIds, model);
  }

  private final BulkEditTableBackend<LibraryDilution, DilutionDto> dilutionBulkEditBackend = new BulkEditTableBackend<LibraryDilution, DilutionDto>(
      "dilution", DilutionDto.class, "Dilutions") {

    @Override
    protected DilutionDto asDto(LibraryDilution model) {
      return Dtos.asDto(model);
    }

    @Override
    protected Iterable<LibraryDilution> load(List<Long> modelIds) throws IOException {
      return dilutionService.listByIdList(modelIds);
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) {
    }
  };

  @RequestMapping(value = "dilution/bulk/edit/{dilutionIds}", method = RequestMethod.GET)
  public ModelAndView editDilutions(@PathVariable String dilutionIds, ModelMap model) throws IOException {
    return dilutionBulkEditBackend.edit(dilutionIds, model);
  }

  @RequestMapping(method = RequestMethod.POST)
  public String processSubmit(@ModelAttribute("library") Library library, ModelMap model, SessionStatus session)
      throws IOException, MalformedLibraryException {
    try {
      if (library.getId() == AbstractLibrary.UNSAVED_ID) {
        libraryService.create(library);
      } else {
        libraryService.update(library);
      }

      session.setComplete();
      model.clear();
      return "redirect:/miso/library/" + library.getId();
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to save library", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(value = "/bulk/create", method = RequestMethod.POST)
  public String processBulkSubmit(@RequestBody JSONArray librariesDtos) throws IOException, MalformedLibraryException {
    try {
      if (librariesDtos != null && librariesDtos.size() > 0) {
        List<Long> savedLibraryIds = new ArrayList<>();

        for (Object lDto : librariesDtos) {
          ObjectMapper mapper = new ObjectMapper();
          LibraryDto libDto = mapper.readValue(lDto.toString(), LibraryDto.class);
          Library library = Dtos.to(libDto);

          Long savedId = libraryService.create(library);
          savedLibraryIds.add(savedId);
        }
        return "redirect:/miso/library/bulk/edit/" + savedLibraryIds.toString();
      } else {
        throw new IOException("There are no libraries to save!");
      }
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to save library", ex);
      }
      throw ex;
    }
  }

  private final BulkPropagateTableBackend<LibraryDilution, PoolDto> poolBulkPropagateBackend = new BulkPropagateTableBackend<LibraryDilution, PoolDto>(
      "pool", PoolDto.class, "Pools", "Dilutions") {

    @Override
    protected PoolDto createDtoFromParent(LibraryDilution item) {
      PoolDto dto = new PoolDto();
      dto.setAlias(item.getLibrary().getAlias() + "_POOL");
      dto.setPooledElements(Collections.singleton(Dtos.asDto(item)));
      dto.setPlatformType(item.getLibrary().getPlatformType().name());
      dto.setReadyToRun(true);
      return dto;
    }

    @Override
    protected Iterable<LibraryDilution> loadParents(List<Long> ids) throws IOException {
      return dilutionService.listByIdList(ids);
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) {
    }
  };

  @RequestMapping(value = "dilution/bulk/propagate/{dilutionIds}", method = RequestMethod.GET)
  public ModelAndView propagatePoolsIndividual(@PathVariable String dilutionIds, ModelMap model) throws IOException {
    return poolBulkPropagateBackend.propagate(dilutionIds, model);
  }

  private final BulkMergeTableBackend<PoolDto> poolBulkMergeBackend = new BulkMergeTableBackend<PoolDto>(
      "pool", PoolDto.class, "Pools", "Dilutions") {

    /** Given a bunch of strings, find the long substring that matches all of them that doesn't end in numbers or underscores. */
    private String findCommonPrefix(String[] str) {
      StringBuilder commonPrefix = new StringBuilder();

      while (commonPrefix.length() < str[0].length()) {
        char current = str[0].charAt(commonPrefix.length());
        boolean matches = true;
        for (int i = 1; matches && i < str.length; i++) {
          if (str[i].charAt(commonPrefix.length()) != current) {
            matches = false;
          }
        }
        if (matches) {
          commonPrefix.append(current);
        } else {
          break;
        }
      }
      // Chew back any digits at the end
      while (commonPrefix.length() > 0 && Character.isDigit(commonPrefix.charAt(commonPrefix.length() - 1))) {
        commonPrefix.setLength(commonPrefix.length() - 1);
      }
      if (commonPrefix.length() > 0 && commonPrefix.charAt(commonPrefix.length() - 1) == '_') {
        commonPrefix.setLength(commonPrefix.length() - 1);
      }
      return (commonPrefix.length() > 0) ? commonPrefix.toString() : null;

    }

    @Override
    protected PoolDto createDtoFromParents(List<Long> ids) throws IOException {
      List<LibraryDilution> parents = dilutionService.listByIdList(ids);
      if (parents.isEmpty()) {
        throw new IllegalStateException("Cannot have no dilutions for pool propagation.");
      }
      List<PlatformType> platformTypes = parents.stream().map(dilution -> dilution.getLibrary().getPlatformType()).distinct()
          .collect(Collectors.toList());
      if (platformTypes.size() > 1) {
        throw new IllegalArgumentException("Cannot create a pool for multiple platforms: "
            + String.join(", ", platformTypes.stream().map(Enum::name).toArray(CharSequence[]::new)));
      }
      PoolDto dto = new PoolDto();
      dto.setPlatformType(platformTypes.get(0).name());
      dto.setReadyToRun(true);

      if (parents.size() == 1) {
        dto.setAlias(parents.get(0).getLibrary().getAlias() + "_POOL");
      } else {
        String commonPrefix = findCommonPrefix(parents.stream().map(dilution -> dilution.getLibrary().getAlias()).toArray(String[]::new));
        if (commonPrefix != null) {
          dto.setAlias(commonPrefix + "_POOL");
        }
      }
      dto.setPooledElements(parents.stream().map(Dtos::asDto).collect(Collectors.toSet()));
      return dto;
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) {
    }
  };

  @RequestMapping(value = "dilution/bulk/merge/{dilutionIds}", method = RequestMethod.GET)
  public ModelAndView propagatePoolsMerged(@PathVariable String dilutionIds, ModelMap model) throws IOException {
    return poolBulkMergeBackend.propagate(dilutionIds, model);
  }
}

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

import org.codehaus.jackson.map.ObjectMapper;
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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.util.JSONUtils;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractLibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryAdditionalInfo;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAdditionalInfoImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedLibraryException;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.security.util.LimsSecurityUtils;
import uk.ac.bbsrc.tgac.miso.core.service.IndexService;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryDesignCodeDao;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryDesignDao;
import uk.ac.bbsrc.tgac.miso.core.util.AliasComparator;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryAdditionalInfoDto;
import uk.ac.bbsrc.tgac.miso.dto.LibraryDto;
import uk.ac.bbsrc.tgac.miso.service.ChangeLogService;
import uk.ac.bbsrc.tgac.miso.service.LibraryAdditionalInfoService;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

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
  private LibraryAdditionalInfoService libraryAdditionalInfoService;

  @Autowired
  private SampleService sampleService;

  @Autowired
  private LibraryDesignDao libraryDesignDao;

  @Autowired
  private LibraryDesignCodeDao libraryDesignCodeDao;

  @Autowired
  private ChangeLogService changeLogService;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setIndexService(IndexService indexService) {
    this.indexService = indexService;
  }

  public void setLibraryAdditionalInfoService(LibraryAdditionalInfoService libraryAdditionalInfoService) {
    this.libraryAdditionalInfoService = libraryAdditionalInfoService;
  }

  public void setLibraryService(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

  public void setSampleService(SampleService sampleService) {
    this.sampleService = sampleService;
  }

  @Value("${miso.notification.interop.enabled}")
  private Boolean metrixEnabled;
  @Value("${miso.autoGenerateIdentificationBarcodes}")
  private Boolean autoGenerateIdBarcodes;
  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;

  @ModelAttribute("metrixEnabled")
  public Boolean isMetrixEnabled() {
    return metrixEnabled;
  }

  @ModelAttribute("autoGenerateIdBarcodes")
  public Boolean autoGenerateIdentificationBarcodes() {
    return autoGenerateIdBarcodes;
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
      List<Pool> pools = new ArrayList<>(requestManager.listPoolsByLibraryId(l.getId()));
      Collections.sort(pools);
      return pools;
    }
    return Collections.emptyList();
  }

  public Set<Run> getRunsByLibraryPools(List<Pool> pools) throws IOException {
    Set<Run> runs = new TreeSet<>();
    for (Pool pool : pools) {
      Collection<Run> prs = requestManager.listRunsByPoolId(pool.getId());
      runs.addAll(prs);
    }
    return runs;
  }

  public Collection<LibraryType> populateLibraryTypesByPlatform(String platform) throws IOException {
    List<LibraryType> types = new ArrayList<>();
    for (LibraryType type : libraryService.getAllLibraryTypesByPlatform(PlatformType.get(platform))) {
      if (!type.getArchived()) {
        types.add(type);
      }
    }
    Collections.sort(types);
    return types;
  }

  public Collection<LibraryType> populateLibraryTypes() throws IOException {
    List<LibraryType> types = new ArrayList<>(libraryService.getAllLibraryTypes());
    Collections.sort(types);
    return types;
  }

  @ModelAttribute("maxLengths")
  public Map<String, Integer> maxLengths() throws IOException {
    return libraryService.getLibraryColumnSizes();
  }

  private Collection<String> populatePlatformNames(List<String> current) throws IOException {
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

  @ModelAttribute("platformNamesString")
  public String platformNamesString() throws IOException {
    List<String> names = new ArrayList<>();
    List<String> pn = new ArrayList<>(populatePlatformNames(Collections.<String> emptyList()));
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
    List<LibrarySelectionType> types = new ArrayList<>(libraryService.getAllLibrarySelectionTypes());
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
    List<LibraryStrategyType> types = new ArrayList<>(libraryService.getAllLibraryStrategyTypes());
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
    if (isStringEmptyOrNull(library.getPlatformName())) {
      model.put("indexFamiliesJSON", "[]");
      model.put("indexFamilies", Collections.singleton(INDEX_FAMILY_NEEDS_PLATFORM));
    } else {
      List<IndexFamily> visbileFamilies = new ArrayList<>();
      visbileFamilies.add(IndexFamily.NULL);
      visbileFamilies.addAll(indexService.getIndexFamiliesByPlatform(PlatformType.get(library.getPlatformName())));
      MisoWebUtils.populateListAndJson(model, "indexFamilies", visbileFamilies, "family");
    }
  }

  @ModelAttribute("libraryInitialConcentrationUnits")
  public String libraryInitialConcentrationUnits() {
    return AbstractLibrary.UNITS;
  }

  @ModelAttribute("libraryQCUnits")
  public String libraryQCUnits() {
    return AbstractLibraryQC.UNITS;
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
    List<KitDescriptor> list = new ArrayList<>(requestManager.listKitDescriptorsByType(KitType.LIBRARY));
    Collections.sort(list, new Comparator<KitDescriptor>() {
      @Override
      public int compare(KitDescriptor kd1, KitDescriptor kd2) {
        return kd1.getName().compareTo(kd2.getName());
      }
    });
    return list;
  }

  /**
   * Adds child entities to a new detailed Library so they can be bound in the JSP. These will not all be useful for the same object, but
   * are all included to accommodate the JSP.
   * 
   * @param library
   */
  private void addNewDetailedLibraryEntities(Library library) {
    library.setLibraryAdditionalInfo(new LibraryAdditionalInfoImpl());
    library.getLibraryAdditionalInfo().setPrepKit(new KitDescriptor());
  }

  private void populateDesigns(ModelMap model, SampleClass sampleClass) throws IOException {
    MisoWebUtils.populateListAndJson(model, "libraryDesigns", requestManager.listLibraryDesignByClass(sampleClass), "sampleClass");
  }

  private void populateDesignCodes(ModelMap model) throws IOException {
    MisoWebUtils.populateListAndJson(model, "libraryDesignCodes", requestManager.listLibraryDesignCodes());
  }

  /**
   * Translates foreign keys to entity objects with only the ID set, to be used in service layer to reload persisted child objects
   * 
   * @param binder
   */
  @InitBinder
  public void includeForeignKeys(WebDataBinder binder) {
    binder.registerCustomEditor(KitDescriptor.class, "libraryAdditionalInfo.prepKit", new PropertyEditorSupport() {
      @Override
      public void setAsText(String text) throws IllegalArgumentException {
        KitDescriptor to = new KitDescriptor();
        to.setId(Long.valueOf(text));
        setValue(to);
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
      final Collection<LibraryType> libTypes = libraryService.getAllLibraryTypesByPlatform(PlatformType.get(platform));
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

  /* HOT */
  @RequestMapping(value = "indexFamiliesJson", method = RequestMethod.GET)
  public @ResponseBody JSONObject indexFamiliesJson(@RequestParam("platform") String platform) throws IOException {
    final JSONObject rtn = new JSONObject();

    if (platform != null && !"".equals(platform)) {
      final List<String> indexFamilies = new ArrayList<>();
      indexFamilies.add(IndexFamily.NULL.getName());
      for (final IndexFamily ifam : indexService.getIndexFamiliesByPlatform(PlatformType.get(platform))) {
        indexFamilies.add(ifam.getName());
      }
      rtn.put("indexFamilies", indexFamilies);
    }
    return rtn;
  }

  @RequestMapping(value = "librarytypes", method = RequestMethod.GET)
  public @ResponseBody String jsonRestLibraryTypes(@RequestParam("platform") String platform) throws IOException {
    if (!isStringEmptyOrNull(platform)) {
      List<String> types = new ArrayList<>();
      for (LibraryType t : populateLibraryTypesByPlatform(platform)) {
        types.add("\"" + t.getDescription() + "\"" + ":" + "\"" + t.getDescription() + "\"");
      }
      return "{" + LimsUtils.join(types, ",") + "}";
    } else {
      return "{}";
    }
  }

  @RequestMapping(value = "indexFamilies", method = RequestMethod.GET)
  public @ResponseBody String jsonRestIndexFamilies(@RequestParam("platform") String platform) throws IOException {
    if (!isStringEmptyOrNull(platform)) {
      List<String> types = new ArrayList<>();
      for (IndexFamily t : indexService.getIndexFamiliesByPlatform(PlatformType.get(platform))) {
        types.add("\"" + t.getName() + "\"" + ":" + "\"" + t.getName() + "\"");
      }
      return "{" + LimsUtils.join(types, ",") + "}";
    } else {
      return "{}";
    }
  }

  @RequestMapping(value = "indicesForPosition", method = RequestMethod.GET)
  public @ResponseBody String jsonRestIndices(@RequestParam("indexFamily") String indexFamily, @RequestParam("position") String position)
      throws IOException {
    if (!isStringEmptyOrNull(indexFamily)) {
      IndexFamily ifam = indexService.getIndexFamilyByName(indexFamily);
      if (ifam != null) {
        List<String> names = new ArrayList<>();
        for (Index index : ifam.getIndicesForPosition(Integer.parseInt(position))) {
          names.add("\"" + index.getId() + "\"" + ":" + "\"" + index.getName() + " (" + index.getSequence() + ")\"");
        }
        return "{" + LimsUtils.join(names, ",") + "}";
      } else {
        return "{}";
      }
    } else {
      return "{}";
    }
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

      Long libraryPrepKitId = null;
      LibraryAdditionalInfo libraryAdditionalInfo = libraryAdditionalInfoService.getByLibraryId(libraryId);
      library.setLibraryAdditionalInfo(libraryAdditionalInfo);
      if (libraryAdditionalInfo != null && libraryAdditionalInfo.getPrepKit() != null) {
        libraryPrepKitId = libraryAdditionalInfo.getPrepKit().getId();
      } else {
        libraryPrepKitId = -1L;
      }
      model.put("libraryPrepKitId", libraryPrepKitId);

      model.put("formObj", library);
      model.put("library", library);

      model.put("platformNames", populatePlatformNames(Arrays.asList(library.getPlatformName())));
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
        library = new LibraryImpl(user);
        if (isDetailedSampleEnabled()) {
          addNewDetailedLibraryEntities(library);
        }
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

        List<Sample> projectSamples = new ArrayList<>(requestManager.listAllSamplesByProjectId(sample.getProject().getProjectId()));
        Collections.sort(projectSamples, new AliasComparator(Sample.class));
        model.put("projectSamples", projectSamples);

        String regex = "([A-z0-9]+)_S([A-z0-9]+)_(.*)";
        Pattern pat = Pattern.compile(regex);
        Matcher mat = pat.matcher(sample.getAlias());
        if (mat.matches()) {
          // convert the sample alias automatically to a library alias
          int numLibs = libraryService.getAllBySampleId(sample.getId()).size();
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

      populateDesigns(model, sampleClass);
      populateDesignCodes(model);

      model.put("formObj", library);
      model.put("library", library);
      model.put("platformNames", populatePlatformNames(Arrays.asList(library.getPlatformName())));
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
    } catch (NoSuchMethodException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to sort project samples", e);
      }
      throw new IOException(e);
    }
  }

  /**
   * used to edit samples with ids from given {sampleIds} sends Dtos objects which will then be used for editing in grid
   */
  @RequestMapping(value = "/bulk/propagate/{sampleIds}", method = RequestMethod.GET)
  public ModelAndView editPropagateSamples(@PathVariable String sampleIds, ModelMap model) throws IOException {
    try {
      List<Long> idList = getIdsFromString(sampleIds);
      ObjectMapper mapper = new ObjectMapper();
      List<LibraryDto> libraryDtos = new ArrayList<>();
      SampleClass sampleClass = null;
      boolean hasPlain = false;
      for (Sample sample : requestManager.getSamplesByIdList(idList)) {
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
        LibraryDto library = new LibraryDto();
        library.setParentSampleId(sample.getId());
        library.setParentSampleAlias(sample.getAlias());

        if (isDetailedSampleEnabled()) {
          LibraryAdditionalInfoDto lai = new LibraryAdditionalInfoDto();
          lai.setNonStandardAlias(((DetailedSample) sample).hasNonStandardAlias());
          library.setLibraryAdditionalInfo(lai);
        }
        libraryDtos.add(library);
      }
      if (hasPlain && sampleClass != null) {
        throw new IOException("Cannot mix plain and detailed samples.");
      }

      model.put("title", "Bulk Create Libraries");
      model.put("librariesJSON", mapper.writeValueAsString(libraryDtos));
      model.put("platformNames", mapper.writeValueAsString(populatePlatformNames(Collections.<String> emptyList())));
      JSONArray libraryDesigns = new JSONArray();
      libraryDesigns.addAll(requestManager.listLibraryDesignByClass(sampleClass));
      model.put("libraryDesignsJSON", libraryDesigns.toString());
      JSONArray libraryDesignCodes = new JSONArray();
      libraryDesignCodes.addAll(requestManager.listLibraryDesignCodes());
      model.put("libraryDesignCodesJSON", libraryDesignCodes.toString());
      model.put("method", "Propagate");
      return new ModelAndView("/pages/bulkEditLibraries.jsp", model);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.error("Failed to get bulk samples", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(value = "/bulk/edit/{libraryIds}", method = RequestMethod.GET)
  public ModelAndView editBulkLibraries(@PathVariable String libraryIds, ModelMap model) throws IOException {
    try {
      SampleClass sampleClass = null;
      List<Long> idList = getIdsFromString(libraryIds);
      ObjectMapper mapper = new ObjectMapper();
      List<LibraryDto> libraryDtos = new ArrayList<>();
      List<String> currentPlatforms = new ArrayList<>();
      for (Library library : libraryService.getAllByIdList(idList)) {
        LibraryAdditionalInfo lai = null;
        if (isDetailedSampleEnabled()) {
          lai = libraryAdditionalInfoService.get(library.getId());
        }
        libraryDtos.add(Dtos.asDto(library, lai));
        currentPlatforms.add(library.getPlatformName());
        if (!isDetailedSampleEnabled()) {
          // Do nothing about sample classes.
        } else if (sampleClass == null) {
          sampleClass = ((DetailedSample) library.getSample()).getSampleClass();
        } else if (((DetailedSample) library.getSample()).getSampleClass().getId() != sampleClass.getId()) {
          throw new IOException("Can only update libraries when samples all have the same class.");
        }
      }
      model.put("title", "Bulk Edit Libraries");
      model.put("librariesJSON", mapper.writeValueAsString(libraryDtos));
      model.put("method", "Edit");

      JSONArray libraryDesigns = new JSONArray();
      if (sampleClass != null) {
        libraryDesigns.addAll(requestManager.listLibraryDesignByClass(sampleClass));
      }
      model.put("libraryDesignsJSON", libraryDesigns.toString());
      JSONArray libraryDesignCodes = new JSONArray();
      libraryDesignCodes.addAll(requestManager.listLibraryDesignCodes());
      model.put("libraryDesignCodesJSON", libraryDesignCodes.toString());
      model.put("platformNames", mapper.writeValueAsString(populatePlatformNames(currentPlatforms)));

      return new ModelAndView("/pages/bulkEditLibraries.jsp", model);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.error("Failed to get bulk libraries", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(value = "/dilutions/bulk/propagate/{libraryIds}", method = RequestMethod.GET)
  public ModelAndView editPropagateDilutions(@PathVariable String libraryIds, ModelMap model) throws IOException {
    try {
      List<Long> idList = getIdsFromString(libraryIds);
      ObjectMapper mapper = new ObjectMapper();
      List<LibraryDto> libraryDtos = new ArrayList<>();
      for (Library library : libraryService.getAllByIdList(idList)) {
        LibraryAdditionalInfo lai = null;
        if (isDetailedSampleEnabled()) {
          lai = libraryAdditionalInfoService.get(library.getId());
        }
        libraryDtos.add(Dtos.asDto(library, lai));
      }
      model.put("title", "Bulk Create Dilutions");
      model.put("librariesJSON", mapper.writeValueAsString(libraryDtos));
      model.put("method", "Propagate");
      return new ModelAndView("/pages/bulkEditDilutions.jsp", model);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.error("Failed to get bulk libraries", ex);
      }
      throw ex;
    }
  }

  public List<Long> getIdsFromString(String idString) {
    String[] split = idString.split(",");
    List<Long> idList = new ArrayList<>();
    for (int i = 0; i < split.length; i++) {
      idList.add(Long.parseLong(split[i]));
    }
    return idList;
  }

  @RequestMapping(method = RequestMethod.POST)
  public String processSubmit(@ModelAttribute("library") Library library, ModelMap model, SessionStatus session)
      throws IOException, MalformedLibraryException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      if (!library.userCanWrite(user)) {
        throw new SecurityException("Permission denied.");
      }
      library.setLastModifier(user);

      if (library.getLibraryAdditionalInfo() != null) {
        if (library.getLibraryAdditionalInfo().getLibraryDesign() != null) {

          if (library.getLibraryAdditionalInfo().getLibraryDesign().getId() == -1) {
            library.getLibraryAdditionalInfo().setLibraryDesign(null);
            if (library.getLibraryAdditionalInfo().getLibraryDesignCode() != null) {
              LibraryDesignCode ldCode = libraryDesignCodeDao
                  .getLibraryDesignCode(library.getLibraryAdditionalInfo().getLibraryDesignCode().getId());
              library.getLibraryAdditionalInfo().setLibraryDesignCode(ldCode);
            }
          } else {
            // If a design is selected, these form elements are disabled and therefore not submitted.
            LibraryDesign design = libraryDesignDao.getLibraryDesign(library.getLibraryAdditionalInfo().getLibraryDesign().getId());
            library.getLibraryAdditionalInfo().setLibraryDesign(design);
            library.setLibrarySelectionType(libraryService.getLibrarySelectionTypeById(design.getLibrarySelectionType().getId()));
            library.setLibraryStrategyType(libraryService.getLibraryStrategyTypeById(design.getLibraryStrategyType().getId()));
            library.getLibraryAdditionalInfo().setLibraryDesignCode(libraryDesignCodeDao
                .getLibraryDesignCode(library.getLibraryAdditionalInfo().getLibraryDesign().getLibraryDesignCode().getId()));
          }
        }
        if (library.getId() == AbstractLibrary.UNSAVED_ID) {
          library.getLibraryAdditionalInfo().setCreatedBy(user);
        }
        library.getLibraryAdditionalInfo().setUpdatedBy(user);
      }

      libraryService.save(library);

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
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());

      if (librariesDtos != null && librariesDtos.size() > 0) {
        List<Long> savedLibraryIds = new ArrayList<>();

        for (Object lDto : librariesDtos) {
          ObjectMapper mapper = new ObjectMapper();
          LibraryDto libDto = mapper.readValue(lDto.toString(), LibraryDto.class);
          Library library = Dtos.to(libDto);
          library.setSample(sampleService.get(libDto.getParentSampleId()));
          library.setLibrarySelectionType(libraryService.getLibrarySelectionTypeById(libDto.getLibrarySelectionTypeId()));
          library.setLibraryStrategyType(libraryService.getLibraryStrategyTypeById(libDto.getLibraryStrategyTypeId()));
          library.setLibraryType(libraryService.getLibraryTypeById(libDto.getLibraryTypeId()));

          if (!library.userCanWrite(user)) {
            throw new SecurityException("Permission denied.");
          }
          library.setLastModifier(user);

          if (libDto.getLibraryAdditionalInfo() != null) {
            library.setLibraryAdditionalInfo(Dtos.to(libDto.getLibraryAdditionalInfo()));
            if (library.getId() == AbstractLibrary.UNSAVED_ID) {
              library.getLibraryAdditionalInfo().setCreatedBy(user);
            }
            library.getLibraryAdditionalInfo().setUpdatedBy(user);
          }

          Long savedId = libraryService.save(library).getId();
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
}

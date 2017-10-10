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
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.security.util.LimsSecurityUtils;
import uk.ac.bbsrc.tgac.miso.core.service.IndexService;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.util.AliasComparator;
import uk.ac.bbsrc.tgac.miso.core.util.AlphanumericComparator;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.DetailedLibraryDto;
import uk.ac.bbsrc.tgac.miso.dto.DilutionDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryDto;
import uk.ac.bbsrc.tgac.miso.dto.PoolDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleAliquotDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleDto;
import uk.ac.bbsrc.tgac.miso.service.ChangeLogService;
import uk.ac.bbsrc.tgac.miso.service.ExperimentService;
import uk.ac.bbsrc.tgac.miso.service.KitService;
import uk.ac.bbsrc.tgac.miso.service.LibraryDesignCodeService;
import uk.ac.bbsrc.tgac.miso.service.LibraryDesignService;
import uk.ac.bbsrc.tgac.miso.service.LibraryDilutionService;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.service.PlatformService;
import uk.ac.bbsrc.tgac.miso.service.PoolService;
import uk.ac.bbsrc.tgac.miso.service.SampleClassService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;
import uk.ac.bbsrc.tgac.miso.service.SampleValidRelationshipService;
import uk.ac.bbsrc.tgac.miso.service.impl.RunService;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkCreateTableBackend;
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

  protected static final Comparator<LibraryDilution> DILUTION_COMPARATOR = (a, b) -> {
    int nameComparison = AlphanumericComparator.INSTANCE.compare(a.getName(), b.getName());
    return nameComparison == 0 ? new AliasComparator<>().compare(a.getLibrary(), b.getLibrary()) : nameComparison;
  };

  static {
    INDEX_FAMILY_NEEDS_PLATFORM.setName("Please select a platform...");
  }

  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private IndexService indexService;
  @Autowired
  private LibraryService libraryService;
  @Autowired
  private LibraryDesignService libraryDesignService;
  @Autowired
  private LibraryDesignCodeService libraryDesignCodeService;
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
  private PlatformService platformService;
  @Autowired
  private PoolService poolService;
  @Autowired
  private LibraryDilutionService dilutionService;
  @Autowired
  private ExperimentService experimentService;
  @Autowired
  private SampleClassService sampleClassService;
  @Autowired
  private RequestManager requestManager;
  @Autowired
  private SampleValidRelationshipService sampleValidRelationshipService;

  public NamingScheme getNamingScheme() {
    return namingScheme;
  }

  public void setNamingScheme(NamingScheme namingScheme) {
    this.namingScheme = namingScheme;
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

  public void setLibraryDesignService(LibraryDesignService libraryDesignService) {
    this.libraryDesignService = libraryDesignService;
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
  @Value("${miso.defaults.sample.bulk.scientificname:}")
  private String defaultSciName;

  @ModelAttribute("autoGenerateIdBarcodes")
  public Boolean autoGenerateIdentificationBarcodes() {
    return autoGenerateIdBarcodes;
  }

  @ModelAttribute("aliasGenerationEnabled")
  public Boolean isAliasGenerationEnabled() {
    return namingScheme != null && namingScheme.hasLibraryAliasGenerator();
  }

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
    Collection<PlatformType> base = platformService.listActivePlatformTypes();
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

  @ModelAttribute("librarySelectionTypes")
  public Collection<LibrarySelectionType> populateLibrarySelectionTypes() throws IOException {
    List<LibrarySelectionType> types = new ArrayList<>(libraryService.listLibrarySelectionTypes());
    Collections.sort(types);
    return types;
  }

  @ModelAttribute("libraryStrategyTypes")
  public Collection<LibraryStrategyType> populateLibraryStrategyTypes() throws IOException {
    List<LibraryStrategyType> types = new ArrayList<>(libraryService.listLibraryStrategyTypes());
    Collections.sort(types);
    return types;
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
    Collections.sort(list, KitDescriptor::sortByName);
    return list;
  }

  private void populateDesigns(ModelMap model, SampleClass sampleClass) throws IOException {
    model.put("libraryDesigns", libraryDesignService.listByClass(sampleClass));
  }

  private void populateDesignCodes(ModelMap model) throws IOException {
    model.put("libraryDesignCodes", libraryDesignCodeService.list());
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
    binder.registerCustomEditor(Long.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String text) throws IllegalArgumentException {
        setValue(isStringEmptyOrNull(text) ? null : Long.valueOf(text));
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
    Collection<PlatformType> activePlatforms = platformService.listActivePlatformTypes();
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
      for (String pfName : platformService.listDistinctPlatformTypeNames()) {
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
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    Sample sample = sampleService.get(sampleId);
    Library library = (isDetailedSampleEnabled() ? new DetailedLibraryImpl() : new LibraryImpl(user));
    library.setSample(sample);

    String regex = "([A-z0-9]+)_S([A-z0-9]+)_(.*)";
    Pattern pat = Pattern.compile(regex);
    Matcher mat = pat.matcher(sample.getAlias());
    if (mat.matches()) {
      // convert the sample alias automatically to a library alias
      int numLibs = libraryService.listBySampleId(sample.getId()).size();
      String autogenLibAlias = mat.group(1) + "_" + "L" + mat.group(2) + "-" + (numLibs + 1) + "_" + mat.group(3);
      model.put("autogeneratedLibraryAlias", autogenLibAlias);
    }

    if (Arrays.asList(user.getRoles()).contains("ROLE_TECH")) {
      SecurityProfile sp = new SecurityProfile(user);
      LimsUtils.inheritUsersAndGroups(library, sample.getSecurityProfile());
      sp.setOwner(user);
      library.setSecurityProfile(sp);
    } else {
      library.inheritPermissions(sample);
    }

    model.put("title", "Library from Sample " + sample.getId());
    return setupForm(user, library, model);
  }

  @RequestMapping(value = "/{libraryId}", method = RequestMethod.GET)
  public ModelAndView setupForm(@PathVariable Long libraryId, ModelMap model) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    Library library = libraryService.get(libraryId);
    model.put("title", "Library " + library.getId());
    return setupForm(user, library, model);
  }

  private ModelAndView setupForm(User user, Library library, ModelMap model) throws IOException {

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

    Collection<Pool> pools = poolService.listByLibraryId(library.getId());
    model.put("libraryPools", pools.stream().map(p -> Dtos.asDto(p, false)).collect(Collectors.toList()));
    model.put("libraryRuns", pools.stream().flatMap(WhineyFunction.flatRethrow(p -> runService.listByPoolId(p.getId()))).map(Dtos::asDto)
        .collect(Collectors.toList()));
    model.put("libraryDilutions", library.getLibraryDilutions().stream().map(Dtos::asDto).collect(Collectors.toList()));
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode config = mapper.createObjectNode();
    config.putPOJO("library", Dtos.asDto(library));
    model.put("libraryDilutionsConfig", mapper.writeValueAsString(config));
    model.put("experiments", experimentService.listAllByLibraryId(library.getId()).stream().map(Dtos::asDto)
        .collect(Collectors.toList()));

    populateDesigns(model,
        LimsUtils.isDetailedSample(library.getSample()) ? ((DetailedSample) library.getSample()).getSampleClass() : null);
    populateDesignCodes(model);

    model.put("owners", LimsSecurityUtils.getPotentialOwners(user, library, securityManager.listAllUsers()));
    model.put("accessibleUsers", LimsSecurityUtils.getAccessibleUsers(user, library, securityManager.listAllUsers()));
    model.put("accessibleGroups", LimsSecurityUtils.getAccessibleGroups(user, library, securityManager.listAllGroups()));
    return new ModelAndView("/pages/editLibrary.jsp", model);
  }

  private final BulkEditTableBackend<Library, LibraryDto> libraryBulkEditBackend = new BulkEditTableBackend<Library, LibraryDto>(
      "library", LibraryDto.class, "Libraries") {

    @Override
    protected Stream<Library> load(List<Long> ids) throws IOException {
      return libraryService.listByIdList(ids).stream().sorted(new AliasComparator<>());
    }

    @Override
    protected LibraryDto asDto(Library model) {
      return Dtos.asDto(model);
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) {
      config.put("sortableLocation", true);
      writeLibraryConfiguration(mapper, config);
    }
  };

  private final BulkPropagateTableBackend<Sample, LibraryDto> libraryBulkPropagateBackend = new BulkPropagateTableBackend<Sample, LibraryDto>(
      "library", LibraryDto.class, "Libraries", "Samples") {

    @Override
    protected LibraryDto createDtoFromParent(Sample item) {
      LibraryDto dto;
      if (LimsUtils.isDetailedSample(item)) {
        DetailedSample sample = (DetailedSample) item;
        DetailedLibraryDto detailedDto = new DetailedLibraryDto();
        detailedDto.setParentSampleClassId(sample.getSampleClass().getId());
        detailedDto.setNonStandardAlias(sample.hasNonStandardAlias());
        if (sample.getBox() != null) {
          detailedDto.setSampleBoxPositionLabel(BoxUtils.makeBoxPositionLabel(sample.getBox().getAlias(), sample.getBoxPosition()));
        }
        dto = detailedDto;
      } else {
        dto = new LibraryDto();
      }
      dto.setParentSampleId(item.getId());
      dto.setParentSampleAlias(item.getAlias());
      return dto;
    }

    @Override
    protected Stream<Sample> loadParents(List<Long> ids) throws IOException {
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
      return results.stream().sorted(new AliasComparator<>());
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
    config.put("sortableLocation", true);
    config.put("propagate", true);
  }

  @RequestMapping(value = "/bulk/propagate", method = RequestMethod.GET)
  public ModelAndView propagateFromSamples(@RequestParam("ids") String sampleIds, @RequestParam("replicates") int replicates,
      ModelMap model) throws IOException, MisoNamingException {
    return libraryBulkPropagateBackend.propagate(sampleIds, replicates, model);
  }

  @RequestMapping(value = "/bulk/edit", method = RequestMethod.GET)
  public ModelAndView editBulkLibraries(@RequestParam("ids") String libraryIds, ModelMap model) throws IOException {
    return libraryBulkEditBackend.edit(libraryIds, model);
  }

  @RequestMapping(value = "/bulk/receive", method = RequestMethod.GET)
  public ModelAndView receiveBulkLibraries(@RequestParam("quantity") Integer quantity,
      @RequestParam(value = "sampleClassId", required = false) Long aliquotClassId,
      @RequestParam(value = "projectId", required = false) Long projectId,
      ModelMap model) throws IOException {

    LibraryDto libDto = null;
    Project project = null;
    if (projectId != null) {
      project = requestManager.getProjectById(projectId);
      if (project == null) {
        throw new IllegalArgumentException("Invalid Project ID");
      }
    }

    SampleClass aliquotClass = null;
    if (isDetailedSampleEnabled()) {
      if (aliquotClassId == null) {
        throw new IllegalArgumentException("Sample Class ID is required for Detailed Sample");
      }
      aliquotClass = sampleClassService.get(aliquotClassId);
      if (aliquotClass == null) {
        throw new IllegalArgumentException("Invalid Sample Class ID");
      }
      DetailedLibraryDto detailedDto = new DetailedLibraryDto();
      libDto = detailedDto;
      SampleAliquotDto samDto = new SampleAliquotDto();
      detailedDto.setSample(samDto);
      samDto.setSampleClassId(aliquotClassId);
      detailedDto.setParentSampleClassId(aliquotClassId);
    } else {
      libDto = new LibraryDto();
      libDto.setSample(new SampleDto());
    }

    return new BulkReceiveLibraryBackend(libDto, quantity, project, aliquotClass, defaultSciName).create(model);
  }

  private final class BulkReceiveLibraryBackend extends BulkCreateTableBackend<LibraryDto> {

    private final Project project;
    private final SampleClass aliquotClass;
    private final String defaultSciName;
    private final boolean isDnaseTreatable;

    public BulkReceiveLibraryBackend(LibraryDto dto, Integer quantity, Project project, SampleClass aliquotClass, String defaultSciName)
        throws IOException {
      super("libraryReceipt", LibraryDto.class, "Libraries", dto, quantity);
      if (isDetailedSampleEnabled() && aliquotClass == null) {
        throw new IllegalArgumentException("Aliquot Class cannot be null");
      }
      this.project = project;
      this.aliquotClass = aliquotClass;
      this.defaultSciName = defaultSciName;
      this.isDnaseTreatable = isDetailedSampleEnabled() && aliquotClass.hasPathToDnaseTreatable(sampleValidRelationshipService.getAll());
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      config.putPOJO("targetSampleClass", Dtos.asDto(aliquotClass));
      config.put("dnaseTreatable", isDnaseTreatable);
      config.put("create", true);
      config.put("hasProject", project != null);
      if (project == null) {
        requestManager.listAllProjects().stream().map(Dtos::asDto).forEach(config.putArray("projects")::addPOJO);
      } else {
        config.putPOJO("project", Dtos.asDto(project));
      }
      config.put("defaultSciName", defaultSciName);
      config.put("showDescription", showDescription);
      config.put("showVolume", showVolume);
      config.put("showLibraryAlias", showLibraryAlias);
      config.put("sortableLocation", false);
      config.put("propagate", false);
      config.put("isLibraryReceipt", true);
    }

  }

  private final BulkPropagateTableBackend<Library, DilutionDto> dilutionBulkPropagateBackend = new BulkPropagateTableBackend<Library, DilutionDto>(
      "dilution", DilutionDto.class, "Dilutions", "Libraries") {

    @Override
    protected DilutionDto createDtoFromParent(Library item) {
      DilutionDto dto = new DilutionDto();
      dto.setLibrary(Dtos.asDto(item));
      return dto;
    }

    @Override
    protected Stream<Library> loadParents(List<Long> ids) throws IOException {
      return libraryService.listByIdList(ids).stream();
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) {
    }
  };

  @RequestMapping(value = "/dilutions/bulk/propagate", method = RequestMethod.GET)
  public ModelAndView propagateDilutions(@RequestParam("ids") String libraryIds, ModelMap model) throws IOException {
    return dilutionBulkPropagateBackend.propagate(libraryIds, model);
  }

  private final BulkEditTableBackend<LibraryDilution, DilutionDto> dilutionBulkEditBackend = new BulkEditTableBackend<LibraryDilution, DilutionDto>(
      "dilution", DilutionDto.class, "Dilutions") {

    @Override
    protected DilutionDto asDto(LibraryDilution model) {
      return Dtos.asDto(model);
    }

    @Override
    protected Stream<LibraryDilution> load(List<Long> modelIds) throws IOException {
      return dilutionService.listByIdList(modelIds).stream().sorted(DILUTION_COMPARATOR);
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) {
    }
  };

  @RequestMapping(value = "dilution/bulk/edit", method = RequestMethod.GET)
  public ModelAndView editDilutions(@RequestParam("ids") String dilutionIds, ModelMap model) throws IOException {
    return dilutionBulkEditBackend.edit(dilutionIds, model);
  }

  @RequestMapping(method = RequestMethod.POST)
  public String processSubmit(@ModelAttribute("library") Library library, ModelMap model, SessionStatus session)
      throws IOException {
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
  public String processBulkSubmit(@RequestBody JSONArray librariesDtos) throws IOException {
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
    protected Stream<LibraryDilution> loadParents(List<Long> ids) throws IOException {
      return dilutionService.listByIdList(ids).stream().sorted(DILUTION_COMPARATOR);
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) {
    }
  };

  @RequestMapping(value = "dilution/bulk/propagate", method = RequestMethod.GET)
  public ModelAndView propagatePoolsIndividual(@RequestParam("ids") String dilutionIds, ModelMap model) throws IOException {
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

  @RequestMapping(value = "dilution/bulk/merge", method = RequestMethod.GET)
  public ModelAndView propagatePoolsMerged(@RequestParam("ids") String dilutionIds, ModelMap model) throws IOException {
    return poolBulkMergeBackend.propagate(dilutionIds, model);
  }
}

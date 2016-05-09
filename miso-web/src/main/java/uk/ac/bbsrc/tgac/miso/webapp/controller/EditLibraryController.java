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

package uk.ac.bbsrc.tgac.miso.webapp.controller;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
import net.sf.json.JsonConfig;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractLibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractPool;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryAdditionalInfo;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Poolable;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.TagBarcode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAdditionalInfoImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCR;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedLibraryException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.security.util.LimsSecurityUtils;
import uk.ac.bbsrc.tgac.miso.core.service.tagbarcode.TagBarcodeStrategy;
import uk.ac.bbsrc.tgac.miso.core.service.tagbarcode.TagBarcodeStrategyResolverService;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryDesignDao;
import uk.ac.bbsrc.tgac.miso.core.util.AliasComparator;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryAdditionalInfoDto;
import uk.ac.bbsrc.tgac.miso.dto.LibraryDto;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryAdditionalInfoDao;
import uk.ac.bbsrc.tgac.miso.service.LibraryAdditionalInfoService;

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

  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private RequestManager requestManager;

  @Autowired
  private DataObjectFactory dataObjectFactory;

  @Autowired
  private TagBarcodeStrategyResolverService tagBarcodeStrategyResolverService;

  @Autowired
  private LibraryAdditionalInfoService libraryAdditionalInfoService;

  @Autowired
  private LibraryAdditionalInfoDao libraryAdditionalInfoDao;

  @Autowired
  private LibraryDesignDao libraryDesignDao;

  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setTagBarcodeStrategyResolverService(TagBarcodeStrategyResolverService tagBarcodeStrategyResolverService) {
    this.tagBarcodeStrategyResolverService = tagBarcodeStrategyResolverService;
  }

  public void setLibraryAdditionalInfoService(LibraryAdditionalInfoService libraryAdditionalInfoService) {
    this.libraryAdditionalInfoService = libraryAdditionalInfoService;
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

  public Map<String, Library> getAdjacentLibrariesInProject(Library l, Project p) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    Library prevL = null;
    Library nextL = null;

    if (p != null) {
      long projectId = p.getId();
      if (p.getId() == projectId) {
        List<Sample> samples = new ArrayList<>(requestManager.listAllSamplesByProjectId(projectId));
        Collections.sort(samples);

        List<Library> allLibs = new ArrayList<>();
        Map<String, Library> ret = new HashMap<>();

        for (Sample s : samples) {
          List<Library> samLibs = new ArrayList<>(requestManager.listAllLibrariesBySampleId(s.getId()));
          if (!samLibs.isEmpty()) {
            Collections.sort(samLibs);
            allLibs.addAll(samLibs);
          }
        }

        for (int i = 0; i < allLibs.size(); i++) {
          if (allLibs.get(i).equals(l)) {
            if (i != 0 && allLibs.get(i - 1) != null) {
              prevL = allLibs.get(i - 1);
            }

            if (i != allLibs.size() - 1 && allLibs.get(i + 1) != null) {
              nextL = allLibs.get(i + 1);
            }
            break;
          }
        }
        ret.put("previousLibrary", prevL);
        ret.put("nextLibrary", nextL);
        return ret;
      }
    }
    return Collections.emptyMap();
  }

  public List<Pool<? extends Poolable>> getPoolsByLibrary(Library l) throws IOException {
    if (!l.getLibraryDilutions().isEmpty()) {
      List<Pool<? extends Poolable>> pools = new ArrayList<>(requestManager.listPoolsByLibraryId(l.getId()));
      Collections.sort(pools);
      return pools;
    }
    return Collections.emptyList();
  }

  public Set<Run> getRunsByLibraryPools(List<Pool<? extends Poolable>> pools) throws IOException {
    Set<Run> runs = new TreeSet<>();
    for (Pool<? extends Poolable> pool : pools) {
      Collection<Run> prs = requestManager.listRunsByPoolId(pool.getId());
      runs.addAll(prs);
    }
    return runs;
  }

  public Collection<LibraryType> populateLibraryTypesByPlatform(String platform) throws IOException {
    List<LibraryType> types = new ArrayList<LibraryType>(requestManager.listLibraryTypesByPlatform(platform));
    Collections.sort(types);
    return types;
  }

  public Collection<LibraryType> populateLibraryTypes() throws IOException {
    List<LibraryType> types = new ArrayList<LibraryType>(requestManager.listAllLibraryTypes());
    Collections.sort(types);
    return types;
  }

  @ModelAttribute("maxLengths")
  public Map<String, Integer> maxLengths() throws IOException {
    return requestManager.getLibraryColumnSizes();
  }

  @ModelAttribute("platformNames")
  public Collection<String> populatePlatformNames() throws IOException {
    List<String> types = new ArrayList<String>(requestManager.listDistinctPlatformNames());
    Collections.sort(types);
    return types;
  }

  @ModelAttribute("platformNamesString")
  public String platformNamesString() throws IOException {
    List<String> names = new ArrayList<String>();
    List<String> pn = new ArrayList<String>(populatePlatformNames());
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
    List<String> types = new ArrayList<String>();
    for (LibraryType t : populateLibraryTypes()) {
      types.add("\"" + t.getDescription() + "\"" + ":" + "\"" + t.getDescription() + "\"");
    }
    return LimsUtils.join(types, ",");
  }

  @ModelAttribute("librarySelectionTypes")
  public Collection<LibrarySelectionType> populateLibrarySelectionTypes() throws IOException {
    List<LibrarySelectionType> types = new ArrayList<LibrarySelectionType>(requestManager.listAllLibrarySelectionTypes());
    Collections.sort(types);
    return types;
  }

  @ModelAttribute("librarySelectionTypesString")
  public String librarySelectionTypesString() throws IOException {
    List<String> types = new ArrayList<String>();
    for (LibrarySelectionType t : populateLibrarySelectionTypes()) {
      types.add("\"" + t.getName() + "\"" + ":" + "\"" + t.getName() + "\"");
    }
    return LimsUtils.join(types, ",");
  }

  @ModelAttribute("libraryStrategyTypes")
  public Collection<LibraryStrategyType> populateLibraryStrategyTypes() throws IOException {
    List<LibraryStrategyType> types = new ArrayList<LibraryStrategyType>(requestManager.listAllLibraryStrategyTypes());
    Collections.sort(types);
    return types;
  }

  @ModelAttribute("libraryStrategyTypesString")
  public String libraryStrategyTypesString() throws IOException {
    List<String> types = new ArrayList<String>();
    for (LibraryStrategyType t : populateLibraryStrategyTypes()) {
      types.add("\"" + t.getName() + "\"" + ":" + "\"" + t.getName() + "\"");
    }
    return LimsUtils.join(types, ",");
  }

  public Collection<TagBarcodeStrategy> populateAvailableTagBarcodeStrategies(Library l) throws IOException {
    List<TagBarcodeStrategy> strategies = new ArrayList<TagBarcodeStrategy>(
        tagBarcodeStrategyResolverService.getTagBarcodeStrategiesByPlatform(PlatformType.get(l.getPlatformName())));
    return strategies;
  }

  public Collection<TagBarcode> populateAvailableTagBarcodes(Library l) throws IOException {
    List<TagBarcode> barcodes = new ArrayList<TagBarcode>(requestManager.listAllTagBarcodesByPlatform(l.getPlatformName()));
    Collections.sort(barcodes);
    return barcodes;
  }

  public String tagBarcodesString(String platformName) throws IOException {
    List<TagBarcode> tagBarcodes = new ArrayList<TagBarcode>(requestManager.listAllTagBarcodesByPlatform(platformName));
    Collections.sort(tagBarcodes);
    List<String> names = new ArrayList<String>();
    for (TagBarcode tb : tagBarcodes) {
      names.add("\"" + tb.getName() + " (" + tb.getSequence() + ")\"" + ":" + "\"" + tb.getId() + "\"");
    }
    return LimsUtils.join(names, ",");
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
    return AbstractPool.CONCENTRATION_UNITS;
  }

  @ModelAttribute("emPCRUnits")
  public String emPCRUnits() {
    return emPCR.UNITS;
  }

  @ModelAttribute("emPCRDilutionUnits")
  public String emPCRDilutionUnits() {
    return emPCRDilution.UNITS;
  }

  // Handsontable
  @ModelAttribute("referenceDataJSON")
  public JSONObject referenceDataJsonString() throws IOException {
    final JSONObject hot = new JSONObject();
    final List<String> qcValues = new ArrayList<String>();
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
      libraryTypes.add(libType);
    }

    hot.put("qcValues", qcValues);
    hot.put("selectionTypes", selectionTypes);
    hot.put("strategyTypes", strategyTypes);
    hot.put("platformNames", populatePlatformNames());
    hot.put("libraryTypes", libraryTypes);

    return hot;
  }

  /* HOT */
  @RequestMapping(value = "tagBarcodesJson", method = RequestMethod.GET)
  public @ResponseBody JSONObject tagBarcodesJson(@RequestParam("strategy") String barcodeStrategy,
      @RequestParam("position") String position) throws IOException {
    final JSONObject rtn = new JSONObject();
    final List<JSONObject> rtnList = new ArrayList<JSONObject>();
    try {
      if (!isStringEmptyOrNull(barcodeStrategy)) {
        final TagBarcodeStrategy tbs = tagBarcodeStrategyResolverService.getTagBarcodeStrategy(barcodeStrategy);
        if (tbs != null) {
          final List<TagBarcode> tagBarcodes = new ArrayList<TagBarcode>(tbs.getApplicableBarcodesForPosition(Integer.parseInt(position)));
          for (final TagBarcode tb : tagBarcodes) {
            final JSONObject obj = new JSONObject();
            obj.put("id", tb.getId());
            obj.put("name", tb.getName());
            obj.put("sequence", tb.getSequence());
            rtnList.add(obj);
          }
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage());
      e.printStackTrace();
    }
    rtn.put("tagBarcodes", rtnList);
    return rtn;
  }

  /* HOT */
  @RequestMapping(value = "libraryTypesJson", method = RequestMethod.GET)
  public @ResponseBody JSONObject libraryTypesJson(@RequestParam("platform") String platform) throws IOException {
    final JSONObject rtn = new JSONObject();
    final List<String> rtnLibTypes = new ArrayList<String>();
    if (!isStringEmptyOrNull(platform)) {
      final Collection<LibraryType> libTypes = populateLibraryTypesByPlatform(platform);
      for (final LibraryType type : libTypes) {
        rtnLibTypes.add(type.getDescription());
      }
    }
    rtn.put("libraryTypes", rtnLibTypes);
    return rtn;
  }

  /* HOT */
  @RequestMapping(value = "barcodePositionsJson", method = RequestMethod.GET)
  public @ResponseBody JSONObject barcodePositionsJson(@RequestParam("strategy") String strategy) {
    JSONObject rtn;
    if (!isStringEmptyOrNull(strategy)) {
      strategy = strategy.trim();
      log.warn("strategy = " + strategy);
      System.out.println("strategy = " + strategy);
      final TagBarcodeStrategy tbs = tagBarcodeStrategyResolverService.getTagBarcodeStrategy(strategy);
      if (tbs != null) {
        rtn = new JSONObject();
        rtn.put("numApplicableBarcodes", tbs.getNumApplicableBarcodes());
      } else {
        rtn = JSONUtils.SimpleJSONError("No strategy found with the name: \"" + strategy + "\"");
      }
    } else {
      rtn = JSONUtils.SimpleJSONError("No valid strategy given");
    }
    return rtn;
  }

  /* HOT */
  @RequestMapping(value = "barcodeStrategiesJson", method = RequestMethod.GET)
  public @ResponseBody JSONObject barcodeStrategiesJson(@RequestParam("platform") String platform) throws IOException {
    final JSONObject rtn = new JSONObject();

    if (platform != null && !"".equals(platform)) {
      final List<String> rtnStrat = new ArrayList<String>();
      for (final TagBarcodeStrategy t : tagBarcodeStrategyResolverService.getTagBarcodeStrategiesByPlatform(PlatformType.get(platform))) {
        if (!t.getApplicableBarcodesForPosition(1).isEmpty()) {
          rtnStrat.add(t.getName());
        }
      }
      rtn.put("barcodeKits", rtnStrat);
    }
    return rtn;
  }

  @RequestMapping(value = "librarytypes", method = RequestMethod.GET)
  public @ResponseBody String jsonRestLibraryTypes(@RequestParam("platform") String platform) throws IOException {
    if (!isStringEmptyOrNull(platform)) {
      List<String> types = new ArrayList<String>();
      for (LibraryType t : populateLibraryTypesByPlatform(platform)) {
        types.add("\"" + t.getDescription() + "\"" + ":" + "\"" + t.getDescription() + "\"");
      }
      return "{" + LimsUtils.join(types, ",") + "}";
    } else {
      return "{}";
    }
  }

  @RequestMapping(value = "barcodeStrategies", method = RequestMethod.GET)
  public @ResponseBody String jsonRestBarcodeStrategies(@RequestParam("platform") String platform) throws IOException {
    if (!isStringEmptyOrNull(platform)) {
      List<String> types = new ArrayList<String>();
      for (TagBarcodeStrategy t : tagBarcodeStrategyResolverService.getTagBarcodeStrategiesByPlatform(PlatformType.get(platform))) {
        types.add("\"" + t.getName() + "\"" + ":" + "\"" + t.getName() + "\"");
      }
      return "{" + LimsUtils.join(types, ",") + "}";
    } else {
      return "{}";
    }
  }

  @RequestMapping(value = "barcodesForPosition", method = RequestMethod.GET)
  public @ResponseBody String jsonRestTagBarcodes(@RequestParam("barcodeStrategy") String barcodeStrategy,
      @RequestParam("position") String position) throws IOException {
    if (!isStringEmptyOrNull(barcodeStrategy)) {
      TagBarcodeStrategy tbs = tagBarcodeStrategyResolverService.getTagBarcodeStrategy(barcodeStrategy);
      if (tbs != null) {
        List<TagBarcode> tagBarcodes = new ArrayList<TagBarcode>(tbs.getApplicableBarcodesForPosition(Integer.parseInt(position)));
        List<String> names = new ArrayList<String>();
        for (TagBarcode tb : tagBarcodes) {
          names.add("\"" + tb.getId() + "\"" + ":" + "\"" + tb.getName() + " (" + tb.getSequence() + ")\"");
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
    return requestManager.listAllChanges("Library");
  }

  public Collection<emPCR> populateEmPcrs(User user, Library library) throws IOException {
    Collection<emPCR> pcrs = new HashSet<emPCR>();
    for (emPCR pcr : requestManager.listAllEmPCRs()) {
      for (LibraryDilution ldil : library.getLibraryDilutions()) {
        if (pcr.getLibraryDilution().getId() == ldil.getId()) {
          pcrs.add(pcr);
        }
      }
    }
    return pcrs;
  }

  public Collection<emPCRDilution> populateEmPcrDilutions(User user, Collection<emPCR> pcrs) throws IOException {
    Collection<emPCRDilution> dilutions = new HashSet<emPCRDilution>();
    for (emPCR pcr : pcrs) {
      for (emPCRDilution dilution : requestManager.listAllEmPCRDilutionsByEmPcrId(pcr.getId())) {
        dilution.setEmPCR(pcr);
        dilutions.add(dilution);
      }
    }
    return dilutions;
  }

  @RequestMapping(value = "/new/{sampleId}", method = RequestMethod.GET)
  public ModelAndView newAssignedLibrary(@PathVariable Long sampleId, ModelMap model) throws IOException {
    return setupForm(AbstractLibrary.UNSAVED_ID, sampleId, model);
  }

  @RequestMapping(value = "/{libraryId}", method = RequestMethod.GET)
  public ModelAndView setupForm(@PathVariable Long libraryId, ModelMap model) throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Library library = requestManager.getLibraryById(libraryId);

      if (library == null) {
        throw new SecurityException("No such Library.");
      }
      if (!library.userCanRead(user)) {
        throw new SecurityException("Permission denied.");
      }

      Long libraryPrepKitId = null;
      LibraryAdditionalInfo libraryAdditionalInfo = libraryAdditionalInfoDao.getLibraryAdditionalInfoByLibraryId(libraryId);
      library.setLibraryAdditionalInfo(libraryAdditionalInfo);
      if (libraryAdditionalInfo != null && libraryAdditionalInfo.getPrepKit() != null) {
        libraryPrepKitId = libraryAdditionalInfo.getPrepKit().getId();
      } else {
        libraryPrepKitId = -1L;
      }
      model.put("libraryPrepKitId", libraryPrepKitId);

      model.put("formObj", library);
      model.put("library", library);

      Collection<emPCR> pcrs = populateEmPcrs(user, library);
      model.put("emPCRs", pcrs);
      model.put("emPcrDilutions", populateEmPcrDilutions(user, pcrs));

      if (library.getTagBarcodes() != null && !library.getTagBarcodes().isEmpty() && library.getTagBarcodes().get(1) != null) {
        model.put("selectedTagBarcodeStrategy", library.getTagBarcodes().get(1).getStrategyName());
        model.put("availableTagBarcodeStrategyBarcodes", tagBarcodeStrategyResolverService
            .getTagBarcodeStrategy(library.getTagBarcodes().get(1).getStrategyName()).getApplicableBarcodes());
      }
      model.put("availableTagBarcodeStrategies", populateAvailableTagBarcodeStrategies(library));

      Map<String, Library> adjacentLibraries = getAdjacentLibrariesInProject(library, library.getSample().getProject());
      if (!adjacentLibraries.isEmpty()) {
        model.put("previousLibrary", adjacentLibraries.get("previousLibrary"));
        model.put("nextLibrary", adjacentLibraries.get("nextLibrary"));
      }

      List<Pool<? extends Poolable>> pools = getPoolsByLibrary(library);
      Map<Long, Library> poolLibraryMap = new HashMap<>();
      for (Pool pool : pools) {
        poolLibraryMap.put(pool.getId(), library);
      }
      model.put("poolLibraryMap", poolLibraryMap);
      model.put("libraryPools", pools);
      model.put("libraryRuns", getRunsByLibraryPools(pools));

      populateDesigns(model,
          library.getSample().getSampleAdditionalInfo() == null ? null : library.getSample().getSampleAdditionalInfo().getSampleClass());

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
        library = dataObjectFactory.getLibrary(user);
        model.put("title", "New Library");
      } else {
        library = requestManager.getLibraryById(libraryId);
        model.put("title", "Library " + libraryId);
        if (library.getTagBarcodes() != null && !library.getTagBarcodes().isEmpty() && library.getTagBarcodes().get(1) != null) {
          model.put("selectedTagBarcodeStrategy", library.getTagBarcodes().get(1).getStrategyName());
          model.put("availableTagBarcodeStrategyBarcodes", tagBarcodeStrategyResolverService
              .getTagBarcodeStrategy(library.getTagBarcodes().get(1).getStrategyName()).getApplicableBarcodes());
        }
      }

      if (!library.userCanRead(user)) {
        throw new SecurityException("Permission denied.");
      }

      SampleClass sampleClass = null;
      if (sampleId != null) {
        Sample sample = requestManager.getSampleById(sampleId);
        model.put("sample", sample);
        if (sample.getSampleAdditionalInfo() != null) {
          sampleClass = sample.getSampleAdditionalInfo().getSampleClass();
          library.setLibraryAdditionalInfo(new LibraryAdditionalInfoImpl());
        }

        List<Sample> projectSamples = new ArrayList<Sample>(requestManager.listAllSamplesByProjectId(sample.getProject().getProjectId()));
        Collections.sort(projectSamples, new AliasComparator(Sample.class));
        model.put("projectSamples", projectSamples);

        String regex = "([A-z0-9]+)_S([A-z0-9]+)_(.*)";
        Pattern pat = Pattern.compile(regex);
        Matcher mat = pat.matcher(sample.getAlias());
        if (mat.matches()) {
          // convert the sample alias automatically to a library alias
          int numLibs = requestManager.listAllLibrariesBySampleId(sample.getId()).size();
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

      model.put("formObj", library);
      model.put("library", library);
      Collection<emPCR> pcrs = populateEmPcrs(user, library);
      model.put("emPCRs", pcrs);
      model.put("emPcrDilutions", populateEmPcrDilutions(user, pcrs));
      model.put("availableTagBarcodeStrategies", populateAvailableTagBarcodeStrategies(library));

      Map<String, Library> adjacentLibraries = getAdjacentLibrariesInProject(library, library.getSample().getProject());
      if (!adjacentLibraries.isEmpty()) {
        model.put("previousLibrary", adjacentLibraries.get("previousLibrary"));
        model.put("nextLibrary", adjacentLibraries.get("nextLibrary"));
      }

      List<Pool<? extends Poolable>> pools = getPoolsByLibrary(library);
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

  private void populateDesigns(ModelMap model, SampleClass sampleClass) throws IOException {
    JSONArray array = new JSONArray();
    JsonConfig config = new JsonConfig();
    config.setExcludes(new String[] { "sampleClass" });
    Collection<LibraryDesign> designs = requestManager.listLibraryDesignByClass(sampleClass);
    array.addAll(designs, config);
    model.put("libraryDesignsJSON", array.toString());
    model.put("libraryDesigns", designs);
  }

  /**
   * used to edit samples with ids from given {sampleIds} sends Dtos objects which will then be used for editing in grid
   */
  @RequestMapping(value = "/bulk/propagate/{sampleIds}", method = RequestMethod.GET)
  public ModelAndView editPropagateSamples(@PathVariable String sampleIds, ModelMap model) throws IOException {
    try {
      String[] split = sampleIds.split(",");
      List<Long> idList = new ArrayList<Long>();
      for (int i = 0; i < split.length; i++) {
        idList.add(Long.parseLong(split[i]));
      }
      JSONArray libraries = new JSONArray();
      SampleClass sampleClass = null;
      for (Sample sample : requestManager.getSamplesByIdList(idList)) {
        if (sampleClass == null) {
          sampleClass = sample.getSampleAdditionalInfo().getSampleClass();
        } else if (sampleClass.getId() != sample.getSampleAdditionalInfo().getSampleClass().getId()) {
          throw new IOException("Can only create libraries when samples all have the same class.");
        }
        LibraryDto library = new LibraryDto();
        library.setParentSampleId(sample.getId());
        library.setParentSampleAlias(sample.getAlias());

        if (isDetailedSampleEnabled()) {
          LibraryAdditionalInfoDto lai = new LibraryAdditionalInfoDto();
          lai.setTissueOrigin(Dtos.asDto(sample.getSampleAdditionalInfo().getTissueOrigin()));
          lai.setTissueType(Dtos.asDto(sample.getSampleAdditionalInfo().getTissueType()));
          library.setLibraryAdditionalInfo(lai);
        }
        libraries.add(library);
      }
      model.put("librariesJSON", libraries);
      JSONArray libraryDesigns = new JSONArray();
      libraryDesigns.addAll(requestManager.listLibraryDesignByClass(sampleClass));
      model.put("libraryDesignsJSON", libraryDesigns.toString());
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
      String[] split = libraryIds.split(",");
      List<Long> idList = new ArrayList<Long>();
      for (int i = 0; i < split.length; i++) {
        idList.add(Long.parseLong(split[i]));
      }
      JSONArray libraryDtos = new JSONArray();
      for (Library library : requestManager.getLibrariesByIdList(idList)) {
        LibraryAdditionalInfo lai = null;
        if (isDetailedSampleEnabled()) {
          lai = libraryAdditionalInfoService.get(library.getId());
        }
        libraryDtos.add(Dtos.asDto(library, lai));
      }

      model.put("librariesJSON", libraryDtos);
      model.put("method", "Edit");
      model.put("libraryDesignsJSON", "[]");
      return new ModelAndView("/pages/bulkEditLibraries.jsp", model);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.error("Failed to get bulk libraries", ex);
      }
      throw ex;
    }
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
          } else {
            // If a design is selected, these form elements are disabled and therefore not submitted.
            LibraryDesign design = libraryDesignDao.getLibraryDesign(library.getLibraryAdditionalInfo().getLibraryDesign().getId());
            library.getLibraryAdditionalInfo().setLibraryDesign(design);
            library.setLibrarySelectionType(requestManager.getLibrarySelectionTypeById(design.getLibrarySelectionType()));
            library.setLibraryStrategyType(requestManager.getLibraryStrategyTypeById(design.getLibraryStrategyType()));
            library.setPaired(design.getPaired());
            library.setPlatformName(design.getPlatform().getKey());
          }
        }
        if (library.getId() == AbstractLibrary.UNSAVED_ID) {
          library.getLibraryAdditionalInfo().setCreatedBy(user);
        }
        library.getLibraryAdditionalInfo().setUpdatedBy(user);
        library.getLibraryAdditionalInfo().setTissueOrigin(library.getSample().getSampleAdditionalInfo().getTissueOrigin());
        library.getLibraryAdditionalInfo().setTissueType(library.getSample().getSampleAdditionalInfo().getTissueType());
      }

      boolean create = library.getId() == AbstractLibrary.UNSAVED_ID;
      long id = requestManager.saveLibrary(library);
      if (library.getLibraryAdditionalInfo() != null) {
        if (create) {
          libraryAdditionalInfoService.create(library.getLibraryAdditionalInfo(), id);
        } else {
          library.getLibraryAdditionalInfo().setLibrary(library);
          libraryAdditionalInfoService.update(library.getLibraryAdditionalInfo());
        }
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
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());

      if (librariesDtos != null && librariesDtos.size() > 0) {
        JSONArray savedLibraries = new JSONArray();

        for (Object lDto : librariesDtos) {
          ObjectMapper mapper = new ObjectMapper();
          LibraryDto libDto = mapper.readValue(lDto.toString(), LibraryDto.class);
          Library library = Dtos.to(libDto);
          library.setSample(requestManager.getSampleById(libDto.getParentSampleId()));
          library.setLibrarySelectionType(requestManager.getLibrarySelectionTypeById(libDto.getLibrarySelectionTypeId()));
          library.setLibraryStrategyType(requestManager.getLibraryStrategyTypeById(libDto.getLibraryStrategyTypeId()));
          library.setLibraryType(requestManager.getLibraryTypeById(libDto.getLibraryTypeId()));

          if (!library.userCanWrite(user)) {
            throw new SecurityException("Permission denied.");
          }
          library.setLastModifier(user);

          // TODO: fix this hack
          if (libDto.getLibraryAdditionalInfo() != null) {
            library.setLibraryAdditionalInfo(Dtos.to(libDto.getLibraryAdditionalInfo()));
            if (library.getId() == AbstractLibrary.UNSAVED_ID) {
              library.getLibraryAdditionalInfo().setCreatedBy(user);
            }
            library.getLibraryAdditionalInfo().setUpdatedBy(user);
          }

          Long savedId = requestManager.saveLibrary(library);
          savedLibraries.add(savedId);
        }
        return "redirect:/miso/library/bulk/edit/" + savedLibraries.toString();
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

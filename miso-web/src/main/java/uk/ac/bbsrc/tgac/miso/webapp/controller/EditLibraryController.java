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

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.tagbarcode.TagBarcodeStrategy;
import uk.ac.bbsrc.tgac.miso.core.service.tagbarcode.TagBarcodeStrategyResolverService;
import uk.ac.bbsrc.tgac.miso.core.util.AliasComparator;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCR;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedLibraryException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.security.util.LimsSecurityUtils;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;
import uk.ac.bbsrc.tgac.miso.webapp.context.ApplicationContextProvider;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoPropertyExporter;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
  private JdbcTemplate interfaceTemplate;

  @Autowired
  private TagBarcodeStrategyResolverService tagBarcodeStrategyResolverService;

  public void setInterfaceTemplate(JdbcTemplate interfaceTemplate) {
    this.interfaceTemplate = interfaceTemplate;
  }

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

  @ModelAttribute("metrixEnabled")
  public Boolean isMetrixEnabled() {
    MisoPropertyExporter exporter = (MisoPropertyExporter)ApplicationContextProvider.getApplicationContext().getBean("propertyConfigurer");
    Map<String, String> misoProperties = exporter.getResolvedProperties();
    return misoProperties.containsKey("miso.notification.interop.enabled") && Boolean.parseBoolean(misoProperties.get("miso.notification.interop.enabled"));
  }
  
  @ModelAttribute("autoGenerateIdBarcodes")
  public Boolean autoGenerateIdentificationBarcodes() {
    MisoPropertyExporter exporter = (MisoPropertyExporter)ApplicationContextProvider.getApplicationContext().getBean("propertyConfigurer");
    Map<String, String> misoProperties = exporter.getResolvedProperties();
    return misoProperties.containsKey("miso.autoGenerateIdentificationBarcodes") && Boolean.parseBoolean(misoProperties.get("miso.autoGenerateIdentificationBarcodes"));
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
            if (i != 0 && allLibs.get(i-1) != null) {
              prevL = allLibs.get(i-1);
            }

            if (i != allLibs.size()-1 && allLibs.get(i+1) != null) {
              nextL = allLibs.get(i+1);
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
    return DbUtils.getColumnSizes(interfaceTemplate, "Library");
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
      names.add("\"selected\":"+"\"" + pn.get(0) + "\"");
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
    List<TagBarcodeStrategy> strategies = new ArrayList<TagBarcodeStrategy>(tagBarcodeStrategyResolverService.getTagBarcodeStrategiesByPlatform(PlatformType.get(l.getPlatformName())));
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
      names.add("\"" + tb.getName() + " ("+tb.getSequence()+")\"" + ":" + "\"" + tb.getId() + "\"");
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

  @ModelAttribute("emPCRUnits")
  public String emPCRUnits() {
    return emPCR.UNITS;
  }

  @ModelAttribute("emPCRDilutionUnits")
  public String emPCRDilutionUnits() {
    return emPCRDilution.UNITS;
  }

  @RequestMapping(value = "librarytypes", method = RequestMethod.GET)
  public @ResponseBody String jsonRestLibraryTypes(@RequestParam("platform") String platform) throws IOException {
    if (platform != null && !"".equals(platform)) {
      List<String> types = new ArrayList<String>();
      for (LibraryType t : populateLibraryTypesByPlatform(platform)) {
        types.add("\"" + t.getDescription() + "\"" + ":" + "\"" + t.getDescription() + "\"");
      }
      return "{"+ LimsUtils.join(types, ",")+"}";
    }
    else {
      return "{}";
    }
  }

  @RequestMapping(value = "barcodeStrategies", method = RequestMethod.GET)
  public @ResponseBody String jsonRestBarcodeStrategies(@RequestParam("platform") String platform) throws IOException {
    if (platform != null && !"".equals(platform)) {
      List<String> types = new ArrayList<String>();
      for (TagBarcodeStrategy t : tagBarcodeStrategyResolverService.getTagBarcodeStrategiesByPlatform(PlatformType.get(platform))) {
        types.add("\"" + t.getName() + "\"" + ":" + "\"" + t.getName() + "\"");
      }
      return "{"+ LimsUtils.join(types, ",")+"}";
    }
    else {
      return "{}";
    }
  }

  @RequestMapping(value = "barcodesForPosition", method = RequestMethod.GET)
  public @ResponseBody String jsonRestTagBarcodes(@RequestParam("barcodeStrategy") String barcodeStrategy, @RequestParam("position") String position) throws IOException {
    if (barcodeStrategy != null && !"".equals(barcodeStrategy)) {
      TagBarcodeStrategy tbs = tagBarcodeStrategyResolverService.getTagBarcodeStrategy(barcodeStrategy);
      if (tbs != null) {
        List<TagBarcode> tagBarcodes = new ArrayList<TagBarcode>(tbs.getApplicableBarcodesForPosition(Integer.parseInt(position)));
        List<String> names = new ArrayList<String>();
        for (TagBarcode tb : tagBarcodes) {
          names.add("\"" + tb.getId() + "\"" + ":" + "\"" + tb.getName() + " ("+tb.getSequence()+")\"");
        }
        return "{"+LimsUtils.join(names, ",")+"}";
      }
      else {
        return "{}";
      }
    }
    else {
      return "{}";
    }
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
      for (emPCRDilution dilution : requestManager.listAllEmPcrDilutionsByEmPcrId(pcr.getId())) {
        dilution.setEmPCR(pcr);
        dilutions.add(dilution);
      }
    }
    return dilutions;
  }

  @RequestMapping(value = "/new/{sampleId}", method = RequestMethod.GET)
  public ModelAndView newAssignedLibrary(@PathVariable Long sampleId,
                                         ModelMap model) throws IOException {
    return setupForm(AbstractLibrary.UNSAVED_ID, sampleId, model);
  }

  @RequestMapping(value = "/{libraryId}", method = RequestMethod.GET)
  public ModelAndView setupForm(@PathVariable Long libraryId,
                                ModelMap model) throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Library library = requestManager.getLibraryById(libraryId);

      if (library == null) {
        throw new SecurityException("No such Library.");
      }
      if (!library.userCanRead(user)) {
        throw new SecurityException("Permission denied.");
      }

      model.put("formObj", library);
      model.put("library", library);

      Collection<emPCR> pcrs = populateEmPcrs(user, library);
      model.put("emPCRs", pcrs);
      model.put("emPcrDilutions", populateEmPcrDilutions(user, pcrs));

      if (library.getTagBarcodes() != null && !library.getTagBarcodes().isEmpty() && library.getTagBarcodes().get(1) != null) {
        model.put("selectedTagBarcodeStrategy", library.getTagBarcodes().get(1).getStrategyName());
        model.put("availableTagBarcodeStrategyBarcodes", tagBarcodeStrategyResolverService.getTagBarcodeStrategy(library.getTagBarcodes().get(1).getStrategyName()).getApplicableBarcodes());
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

      model.put("owners", LimsSecurityUtils.getPotentialOwners(user, library, securityManager.listAllUsers()));
      model.put("accessibleUsers", LimsSecurityUtils.getAccessibleUsers(user, library, securityManager.listAllUsers()));
      model.put("accessibleGroups", LimsSecurityUtils.getAccessibleGroups(user, library, securityManager.listAllGroups()));
      model.put("title", "Library " + libraryId);
      return new ModelAndView("/pages/editLibrary.jsp", model);
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show library", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(value = "/{libraryId}/sample/{sampleId}", method = RequestMethod.GET)
  public ModelAndView setupForm(@PathVariable Long libraryId,
                                @PathVariable Long sampleId,
                                ModelMap model) throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Library library = null;
      if (libraryId == AbstractLibrary.UNSAVED_ID) {
        library = dataObjectFactory.getLibrary(user);
        model.put("title", "New Library");
      }
      else {
        library = requestManager.getLibraryById(libraryId);
        model.put("title", "Library " + libraryId);
        if (library.getTagBarcodes() != null && !library.getTagBarcodes().isEmpty() && library.getTagBarcodes().get(1) != null) {
          model.put("selectedTagBarcodeStrategy", library.getTagBarcodes().get(1).getStrategyName());
          model.put("availableTagBarcodeStrategyBarcodes", tagBarcodeStrategyResolverService.getTagBarcodeStrategy(
                  library.getTagBarcodes().get(1).getStrategyName()).getApplicableBarcodes());
        }
      }

      if (!library.userCanRead(user)) {
        throw new SecurityException("Permission denied.");
      }

      if (sampleId != null) {
        Sample sample = requestManager.getSampleById(sampleId);
        model.put("sample", sample);

        List<Sample> projectSamples = new ArrayList<Sample>(requestManager.listAllSamplesByProjectId(sample.getProject().getProjectId()));
        Collections.sort(projectSamples, new AliasComparator(Sample.class));
        model.put("projectSamples", projectSamples);

        String regex = "([A-z0-9]+)_S([A-z0-9]+)_(.*)";
        Pattern pat = Pattern.compile(regex);
        Matcher mat = pat.matcher(sample.getAlias());
        if (mat.matches()) {
          //convert the sample alias automatically to a library alias
          int numLibs = requestManager.listAllLibrariesBySampleId(sample.getId()).size();
          String autogenLibAlias = mat.group(1) + "_" + "L" + mat.group(2) + "-"+(numLibs+1)+"_" + mat.group(3);
          model.put("autogeneratedLibraryAlias", autogenLibAlias);
        }
        
        library.setSample(sample);
        if (Arrays.asList(user.getRoles()).contains("ROLE_TECH")) {
          SecurityProfile sp = new SecurityProfile(user);
          LimsUtils.inheritUsersAndGroups(library, sample.getSecurityProfile());
          sp.setOwner(user);
          library.setSecurityProfile(sp);
        }
        else {
          library.inheritPermissions(sample);
        }
      }

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
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show library", ex);
      }
      throw ex;
    }
    catch (NoSuchMethodException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to sort project samples", e);
      }
      throw new IOException(e);
    }
  }

  @RequestMapping(method = RequestMethod.POST)
  public String processSubmit(@ModelAttribute("library") Library library,
                              ModelMap model,
                              SessionStatus session) throws IOException, MalformedLibraryException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      if (!library.userCanWrite(user)) {
        throw new SecurityException("Permission denied.");
      }
      requestManager.saveLibrary(library);
      session.setComplete();
      model.clear();
      return "redirect:/miso/library/" + library.getId();
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to save library", ex);
      }
      throw ex;
    }
  }
}

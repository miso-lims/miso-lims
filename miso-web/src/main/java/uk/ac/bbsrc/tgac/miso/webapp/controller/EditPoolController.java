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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractPool;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Dilution;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Poolable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedDilutionException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.security.util.LimsSecurityUtils;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SequencingParametersDto;
import uk.ac.bbsrc.tgac.miso.service.SequencingParametersService;

/**
 * uk.ac.bbsrc.tgac.miso.webapp.controller
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.1.9
 */
@Controller
@RequestMapping("/pool")
@SessionAttributes("pool")
public class EditPoolController {
  protected static final Logger log = LoggerFactory.getLogger(EditPoolController.class);

  @Autowired
  private SequencingParametersService sequencingParametersService;

  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private RequestManager requestManager;

  @Autowired
  private DataObjectFactory dataObjectFactory;

  @Autowired
  private JdbcTemplate interfaceTemplate;

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

  @RequestMapping(value = "/rest/changes", method = RequestMethod.GET)
  public @ResponseBody Collection<ChangeLog> jsonRestChanges() throws IOException {
    return requestManager.listAllChanges("Pool");
  }

  @ModelAttribute("platformTypes")
  public Collection<String> populatePlatformTypes() throws IOException {
    return PlatformType.platformTypeNames(requestManager.listActivePlatformTypes());
  }

  @ModelAttribute("libraryDilutionUnits")
  public String libraryDilutionUnits() {
    return LibraryDilution.UNITS;
  }

  @ModelAttribute("poolConcentrationUnits")
  public String poolConcentrationUnits() {
    return AbstractPool.CONCENTRATION_UNITS;
  }

  @Value("${miso.autoGenerateIdentificationBarcodes}")
  private Boolean autoGenerateIdBarcodes;

  @ModelAttribute("autoGenerateIdBarcodes")
  public Boolean autoGenerateIdentificationBarcodes() {
    return autoGenerateIdBarcodes;
  }

  @ModelAttribute("maxLengths")
  public Map<String, Integer> maxLengths() throws IOException {
    return requestManager.getPoolColumnSizes();
  }

  private List<? extends Dilution> populateAvailableDilutions(Pool pool) throws IOException {
    ArrayList<LibraryDilution> libs = new ArrayList<LibraryDilution>();
    for (Dilution l : requestManager.listAllLibraryDilutionsByPlatform(PlatformType.ILLUMINA)) {
      if (!pool.getDilutions().contains(l)) {
        libs.add((LibraryDilution) l);
      }
    }
    Collections.sort(libs);
    return libs;
  }

  public Collection<Experiment> populateExperiments(Long experimentId, Pool p) throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Collection<Experiment> es = new ArrayList<Experiment>();
      for (Experiment e : requestManager.listAllExperiments()) {
        if (e.getPlatform().getPlatformType().equals(p.getPlatformType())) {
          if (experimentId != null) {
            if (e.getId() != experimentId) {
              es.add(e);
            }
          } else {
            es.add(e);
          }
        }
      }
      return es;
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to list experiments", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(value = "/new", method = RequestMethod.GET)
  public ModelAndView newUnassignedPool(ModelMap model) throws IOException {
    return setupForm(AbstractPool.UNSAVED_ID, model);
  }

  @RequestMapping(value = "/new/{experimentId}", method = RequestMethod.GET)
  public ModelAndView newAssignedPool(@PathVariable Long experimentId, ModelMap model) throws IOException {
    return setupFormWithExperiment(AbstractPool.UNSAVED_ID, experimentId, model);
  }

  @RequestMapping(value = "/{poolId}", method = RequestMethod.GET)
  public ModelAndView setupForm(@PathVariable Long poolId, ModelMap model) throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Pool pool = null;
      if (poolId == AbstractPool.UNSAVED_ID) {
        pool = dataObjectFactory.getPool(user);
        model.put("title", "New Pool");
      } else {
        pool = requestManager.getPoolById(poolId);
        model.put("title", "Pool " + poolId);
      }

      if (pool == null) {
        throw new SecurityException("No such Pool");
      }
      if (!pool.userCanRead(user)) {
        throw new SecurityException("Permission denied.");
      }

      model.put("formObj", pool);
      model.put("pool", pool);
      model.put("availableDilutions", populateAvailableDilutions(pool));
      model.put("accessibleExperiments", populateExperiments(null, pool));
      model.put("owners", LimsSecurityUtils.getPotentialOwners(user, pool, securityManager.listAllUsers()));
      model.put("accessibleUsers", LimsSecurityUtils.getAccessibleUsers(user, pool, securityManager.listAllUsers()));
      model.put("accessibleGroups", LimsSecurityUtils.getAccessibleGroups(user, pool, securityManager.listAllGroups()));
      model.put("platforms", getFilteredPlatforms(pool.getPlatformType()));

      return new ModelAndView("/pages/editPool.jsp", model);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show pool", ex);
      }
      throw ex;
    }
  }

  private void collectIndices(StringBuilder render, Dilution dilution) {
    for (final Index index : dilution.getLibrary().getIndices()) {
      render.append(index.getPosition());
      render.append(": ");
      render.append(index.getLabel());
      render.append("<br/>");
    }
  }

  @RequestMapping(value = "/elementSelectDataTable", method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody String elementSelectDataTable(@RequestParam String sEcho, @RequestParam String iDisplayStart,
      @RequestParam String iDisplayLength, @RequestParam String poolId, @RequestParam String platform, @RequestParam String sSearch,
      @RequestParam String iSortCol_0, @RequestParam String sSortDir_0) throws IOException {

    String search = LimsUtils.isStringEmptyOrNull(sSearch) ? null : sSearch;
    int draw = Integer.valueOf(sEcho);
    int start = Integer.valueOf(iDisplayStart);
    int length = Integer.valueOf(iDisplayLength);
    int poolInt = Integer.valueOf(poolId);
    int sortColIndex = Integer.valueOf(iSortCol_0);
    String sortCol;
    switch (sortColIndex) {
    case 0:
      sortCol = "ld.name";
      break;
    case 1:
      sortCol = "ld.concentration";
      break;
    default:
      throw new IOException("Unexpected value in elementSelectDataTable sortCol " + sortColIndex);
    }
    if (!Arrays.asList("asc", "desc").contains(sSortDir_0)) {
      throw new IOException("Unexpected value in elementSelectDataTable sortDir " + sSortDir_0);
    }
    PlatformType platformType = PlatformType.get(platform);

    JSONObject rtn = new JSONObject();
    JSONArray data = new JSONArray();

    List<LibraryDilution> dils = requestManager.getLibraryDilutionsForPoolDataTable(start, length, search, sSortDir_0, sortCol,
        platformType);
    int allDilutionsCount = requestManager.countLibraryDilutionsByPlatform(PlatformType.ILLUMINA);

    for (LibraryDilution dil : dils) {
      JSONArray inner = new JSONArray();
      inner.add(dil.getName());
      inner.add(dil.getConcentration());
      inner.add(String.format("<a href='/miso/library/%d'>%s (%s)</a>", dil.getLibrary().getId(), dil.getLibrary().getAlias(),
          dil.getLibrary().getName()));
      inner.add(String.format("<a href='/miso/sample/%d'>%s (%s)</a>", dil.getLibrary().getSample().getId(),
          dil.getLibrary().getSample().getAlias(), dil.getLibrary().getSample().getName()));
      StringBuilder indices = new StringBuilder();
      collectIndices(indices, dil);
      inner.add(indices.toString());
      inner.add(dil.getLibrary().isLowQuality() ? "⚠" : "");
      inner.add("<div style='cursor:inherit;' onclick=\"Pool.search.poolSearchSelectElement(" + poolInt + ", '" + dil.getId() + "', '"
          + dil.getName() + "')\"><span class=\"ui-icon ui-icon-plusthick\"></span></div>");
      data.add(inner);
    }
    rtn.put("iTotalRecords", allDilutionsCount);
    rtn.put("iTotalDisplayRecords", requestManager.countLibraryDilutionsBySearchAndPlatform(search, platformType));
    rtn.put("sEcho", "" + draw);
    rtn.put("aaData", "" + data);
    return rtn.toString();
  }

  @RequestMapping(value = "/{poolId}/experiment/{experimentId}", method = RequestMethod.GET)
  public ModelAndView setupFormWithExperiment(@PathVariable Long poolId, @PathVariable Long experimentId, ModelMap model)
      throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Pool pool = null;
      if (poolId == AbstractPool.UNSAVED_ID) {
        pool = dataObjectFactory.getPool(user);
        model.put("title", "New Pool");
      } else {
        pool = requestManager.getPoolById(poolId);
        model.put("title", "Pool " + poolId);
      }

      if (pool == null) {
        throw new SecurityException("No such Pool");
      }
      if (!pool.userCanRead(user)) {
        throw new SecurityException("Permission denied.");
      }

      if (experimentId != null) {
        model.put("accessibleExperiments", populateExperiments(experimentId, pool));
      } else {
        model.put("accessibleExperiments", populateExperiments(null, pool));
      }

      model.put("formObj", pool);
      model.put("pool", pool);
      model.put("availableDilutions", populateAvailableDilutions(pool));

      model.put("owners", LimsSecurityUtils.getPotentialOwners(user, pool, securityManager.listAllUsers()));
      model.put("accessibleUsers", LimsSecurityUtils.getAccessibleUsers(user, pool, securityManager.listAllUsers()));
      model.put("accessibleGroups", LimsSecurityUtils.getAccessibleGroups(user, pool, securityManager.listAllGroups()));
      model.put("platforms", getFilteredPlatforms(pool.getPlatformType()));

      return new ModelAndView("/pages/editPool.jsp", model);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show pool", ex);
      }
      throw ex;
    }
  }

  private Collection<Platform> getFilteredPlatforms(PlatformType platformType) throws IOException {
    List<Platform> selected = new ArrayList<>();
    for (Platform p : requestManager.listAllPlatforms()) {
      if (p.getPlatformType() == platformType && !sequencingParametersService.getForPlatform(p.getId()).isEmpty()) {
        selected.add(p);
      }
    }
    Collections.sort(selected, new Comparator<Platform>() {

      @Override
      public int compare(Platform o1, Platform o2) {
        return o1.getNameAndModel().compareTo(o2.getNameAndModel());
      }

    });
    return selected;
  }

  @Deprecated
  @RequestMapping(value = "/new/dilution/{dilutionId}/platform/{platform}", method = RequestMethod.GET)
  public ModelAndView setupFormWithDilution(@PathVariable Long dilutionId, String platform, ModelMap model) throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Pool pool = dataObjectFactory.getPool(user);
      model.put("title", "New Pool");

      if (pool == null) {
        throw new SecurityException("No such Pool");
      }

      if (!pool.userCanRead(user)) {
        throw new SecurityException("Permission denied.");
      }

      if (dilutionId != null) {
        Dilution ld = requestManager.getDilutionByIdAndPlatform(dilutionId, PlatformType.get(platform));
        if (ld != null) {
          pool.addPoolableElement(ld);
        }
      }

      model.put("formObj", pool);
      model.put("pool", pool);
      model.put("availableDilutions", populateAvailableDilutions(pool));
      model.put("accessibleExperiments", populateExperiments(null, pool));
      model.put("owners", LimsSecurityUtils.getPotentialOwners(user, pool, securityManager.listAllUsers()));
      model.put("accessibleUsers", LimsSecurityUtils.getAccessibleUsers(user, pool, securityManager.listAllUsers()));
      model.put("accessibleGroups", LimsSecurityUtils.getAccessibleGroups(user, pool, securityManager.listAllGroups()));
      model.put("platforms", getFilteredPlatforms(pool.getPlatformType()));

      return new ModelAndView("/pages/editPool.jsp", model);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show pool", ex);
      }
      throw ex;
    } catch (MalformedDilutionException e) {
      log.error("setup form with dilution", e);
      throw new IOException(e);
    }
  }

  @RequestMapping(value = "/import", method = RequestMethod.POST)
  public String importDilutionsToPool(HttpServletRequest request, ModelMap model) throws IOException {
    Pool<Dilution> p = (PoolImpl) model.get("pool");
    String[] dils = request.getParameterValues("importdilslist");
    for (String s : dils) {
      Dilution ld = requestManager.getDilutionByBarcodeAndPlatform(s, p.getPlatformType());
      if (ld != null) {
        try {
          p.addPoolableElement(ld);
        } catch (MalformedDilutionException e) {
          log.error("Cannot add dilution " + s + " to pool " + p.getName(), e);
        }
      }
    }
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    p.setLastModifier(user);
    requestManager.savePool(p);
    return "redirect:/miso/pool/" + p.getId();
  }

  @RequestMapping(method = RequestMethod.POST)
  public <P extends Poolable<?, ?>> String processSubmit(@ModelAttribute("pool") Pool<P> pool, ModelMap model, SessionStatus session)
      throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      if (!pool.userCanWrite(user)) {
        throw new SecurityException("Permission denied.");
      }
      // The pooled elements may have been modified asynchronously while the form was being edited. Since they can't be edited by form,
      // update them to avoid reverting the state.
      if (pool.getId() != AbstractPool.UNSAVED_ID) {
        @SuppressWarnings("unchecked")
        Pool<P> original = (Pool<P>) requestManager.getPoolById(pool.getId());
        pool.setPoolableElements(original.getPoolableElements());
      }

      pool.setLastModifier(user);
      requestManager.savePool(pool);
      session.setComplete();
      model.clear();
      return "redirect:/miso/pool/" + pool.getId();
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to save pool", ex);
      }
      throw ex;
    }
  }

  @ModelAttribute
  public void addSequencingParameters(ModelMap model) throws IOException {
    Collection<SequencingParametersDto> sequencingParameters = Dtos.asSequencingParametersDtos(sequencingParametersService.getAll());
    JSONArray array = new JSONArray();
    array.addAll(sequencingParameters);
    model.put("sequencingParametersJson", array.toString());
  }
}

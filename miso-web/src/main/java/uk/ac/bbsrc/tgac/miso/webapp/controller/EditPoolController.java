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

import java.io.IOException;
import java.util.ArrayList;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONArray;

import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedDilutionException;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.security.util.LimsSecurityUtils;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SequencingParametersDto;
import uk.ac.bbsrc.tgac.miso.service.ChangeLogService;
import uk.ac.bbsrc.tgac.miso.service.LibraryDilutionService;
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
  private ChangeLogService changeLogService;

  @Autowired
  private LibraryDilutionService dilutionService;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setDilutionService(LibraryDilutionService dilutionService) {
    this.dilutionService = dilutionService;
  }

  @RequestMapping(value = "/rest/changes", method = RequestMethod.GET)
  public @ResponseBody Collection<ChangeLog> jsonRestChanges() throws IOException {
    return changeLogService.listAll("Pool");
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
    return PoolImpl.CONCENTRATION_UNITS;
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

  @RequestMapping(value = "/new", method = RequestMethod.GET)
  public ModelAndView newUnassignedPool(ModelMap model) throws IOException {
    return setupForm(PoolImpl.UNSAVED_ID, model);
  }

  @RequestMapping(value = "/{poolId}", method = RequestMethod.GET)
  public ModelAndView setupForm(@PathVariable Long poolId, ModelMap model) throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Pool pool = null;
      if (poolId == PoolImpl.UNSAVED_ID) {
        pool = new PoolImpl(user);
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
      model.put("owners", LimsSecurityUtils.getPotentialOwners(user, pool, securityManager.listAllUsers()));
      model.put("accessibleUsers", LimsSecurityUtils.getAccessibleUsers(user, pool, securityManager.listAllUsers()));
      model.put("accessibleGroups", LimsSecurityUtils.getAccessibleGroups(user, pool, securityManager.listAllGroups()));
      model.put("platforms", getFilteredPlatforms(pool.getPlatformType()));

      ObjectMapper mapper = new ObjectMapper();
      model.put("runsJSON", mapper.writeValueAsString(
          poolId == PoolImpl.UNSAVED_ID ? Collections.emptyList() : Dtos.asRunDtos(requestManager.getRunsByPool(pool))));

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

  @RequestMapping(value = "/import", method = RequestMethod.POST)
  public String importDilutionsToPool(HttpServletRequest request, ModelMap model) throws IOException {
    Pool p = (PoolImpl) model.get("pool");
    String[] dils = request.getParameterValues("importdilslist");
    for (String s : dils) {
      LibraryDilution ld = dilutionService.getByBarcode(s);
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
  public String processSubmit(@ModelAttribute("pool") Pool pool, ModelMap model, SessionStatus session)
      throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      if (!pool.userCanWrite(user)) {
        throw new SecurityException("Permission denied.");
      }
      // The pooled elements may have been modified asynchronously while the form was being edited. Since they can't be edited by form,
      // update them to avoid reverting the state.
      if (pool.getId() != PoolImpl.UNSAVED_ID) {
        Pool original = requestManager.getPoolById(pool.getId());
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

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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractPool;
import uk.ac.bbsrc.tgac.miso.core.data.Dilution;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.solid.SolidPool;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedDilutionException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedLibraryException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.security.util.LimsSecurityUtils;

/**
 * com.eaglegenomics.miso.web
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Controller
@RequestMapping("/pool/solid")
@SessionAttributes("pool")
@Deprecated
public class EditSolidPoolController {
  protected static final Logger log = LoggerFactory.getLogger(EditSolidPoolController.class);

  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private RequestManager requestManager;

  @Autowired
  private DataObjectFactory dataObjectFactory;

  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  private List<? extends Dilution> populateAvailableDilutions(User user, Pool pool) throws IOException {
    ArrayList<emPCRDilution> libs = new ArrayList<emPCRDilution>();
    for (emPCRDilution l : requestManager.listAllEmPCRDilutions()) {
      if (l.getEmPCR().getLibraryDilution().getLibrary().getPlatformName().equals(PlatformType.SOLID.getKey())) {
        if (!pool.getDilutions().contains(l)) {
          if (l.userCanRead(user)) {
            libs.add(l);
          }
        }
      }
    }
    Collections.sort(libs);
    return libs;
  }

  public Collection<Experiment> populateExperiments(@RequestParam(value = "experimentId", required = false) Long experimentId, Pool p)
      throws IOException {
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
  public ModelAndView newSolidPool(ModelMap model) throws IOException {
    return setupForm(AbstractPool.UNSAVED_ID, model);
  }

  @RequestMapping(value = "/new/{experimentId}", method = RequestMethod.GET)
  public ModelAndView newAssignedSolidPool(@PathVariable Long experimentId, ModelMap model) throws IOException {
    return setupFormWithExperiment(AbstractPool.UNSAVED_ID, experimentId, model);
  }

  @RequestMapping(value = "/{poolId}", method = RequestMethod.GET)
  public ModelAndView setupForm(@PathVariable Long poolId, ModelMap model) throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Pool pool = null;
      if (poolId == AbstractPool.UNSAVED_ID) {
        pool = dataObjectFactory.getSolidPool(user);
        model.put("title", "New SOLiD Pool");
      } else {
        pool = requestManager.getPoolById(poolId);
        model.put("title", "SOLiD Pool " + poolId);
      }

      if (pool == null) {
        throw new SecurityException("No such Solid pool");
      }
      if (!pool.userCanRead(user)) {
        throw new SecurityException("Permission denied.");
      }
      model.put("formObj", pool);
      model.put("pool", pool);
      model.put("availableDilutions", populateAvailableDilutions(user, pool));
      model.put("accessibleExperiments", populateExperiments(null, pool));
      model.put("owners", LimsSecurityUtils.getPotentialOwners(user, pool, securityManager.listAllUsers()));
      model.put("accessibleUsers", LimsSecurityUtils.getAccessibleUsers(user, pool, securityManager.listAllUsers()));
      model.put("accessibleGroups", LimsSecurityUtils.getAccessibleGroups(user, pool, securityManager.listAllGroups()));
      return new ModelAndView("/pages/editSolidPool.jsp", model);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show Solid pool", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(value = "/{poolId}/experiment/{experimentId}", method = RequestMethod.GET)
  public ModelAndView setupFormWithExperiment(@PathVariable Long poolId, @PathVariable Long experimentId, ModelMap model)
      throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Pool pool = null;
      if (poolId == AbstractPool.UNSAVED_ID) {
        pool = dataObjectFactory.getSolidPool(user);
        model.put("title", "New Solid Pool");
      } else {
        pool = requestManager.getPoolById(poolId);
        model.put("title", "Solid Pool " + poolId);
      }

      if (pool == null) {
        throw new SecurityException("No such Solid Pool");
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
      model.put("availableDilutions", populateAvailableDilutions(user, pool));

      model.put("owners", LimsSecurityUtils.getPotentialOwners(user, pool, securityManager.listAllUsers()));
      model.put("accessibleUsers", LimsSecurityUtils.getAccessibleUsers(user, pool, securityManager.listAllUsers()));
      model.put("accessibleGroups", LimsSecurityUtils.getAccessibleGroups(user, pool, securityManager.listAllGroups()));
      return new ModelAndView("/pages/editSolidPool.jsp", model);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show Solid pool", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(value = "/new/dilution/{dilutionId}", method = RequestMethod.GET)
  public ModelAndView setupFormWithDilution(@PathVariable Long dilutionId, ModelMap model) throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      SolidPool pool = dataObjectFactory.getSolidPool(user);
      model.put("title", "New Solid Pool");

      if (!pool.userCanRead(user)) {
        throw new SecurityException("Permission denied.");
      }

      if (dilutionId != null) {
        emPCRDilution ed = requestManager.getEmPCRDilutionById(dilutionId);
        if (ed != null) {
          pool.addPoolableElement(ed);
        }
      }

      model.put("formObj", pool);
      model.put("pool", pool);
      model.put("availableDilutions", populateAvailableDilutions(user, pool));
      model.put("accessibleExperiments", populateExperiments(null, pool));
      model.put("owners", LimsSecurityUtils.getPotentialOwners(user, pool, securityManager.listAllUsers()));
      model.put("accessibleUsers", LimsSecurityUtils.getAccessibleUsers(user, pool, securityManager.listAllUsers()));
      model.put("accessibleGroups", LimsSecurityUtils.getAccessibleGroups(user, pool, securityManager.listAllGroups()));
      return new ModelAndView("/pages/editSolidPool.jsp", model);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show Illumina pool", ex);
      }
      throw ex;
    } catch (MalformedDilutionException e) {
      log.error("new solid pool dilution", e);
      throw new IOException(e);
    }
  }

  @RequestMapping(value = "/import", method = RequestMethod.POST)
  public String importEmPCRDilutionsToPool(HttpServletRequest request, ModelMap model) throws IOException {
    SolidPool p = (SolidPool) model.get("pool");
    String[] dils = request.getParameterValues("importdilslist");
    for (String s : dils) {
      emPCRDilution ld = requestManager.getEmPCRDilutionByBarcode(s);
      if (ld != null) {
        try {
          p.addPoolableElement(ld);
        } catch (MalformedDilutionException e) {
          log.error("Cannot add emPCR dilution " + s + " to pool " + p.getName(), e);
        }
      }
    }

    requestManager.savePool(p);
    return "redirect:/miso/pool/solid/" + p.getId();
  }

  @RequestMapping(method = RequestMethod.POST)
  public String processSubmit(@ModelAttribute("pool") Pool pool, ModelMap model, SessionStatus session)
      throws IOException, MalformedLibraryException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      if (!pool.userCanWrite(user)) {
        throw new SecurityException("Permission denied.");
      }
      requestManager.savePool(pool);
      session.setComplete();
      model.clear();
      return "redirect:/miso/pool/solid/" + pool.getId();
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to save Solid pool", ex);
      }
      throw ex;
    }
  }
}

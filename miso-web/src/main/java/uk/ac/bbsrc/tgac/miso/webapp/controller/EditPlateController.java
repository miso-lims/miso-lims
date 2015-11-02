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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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

import uk.ac.bbsrc.tgac.miso.core.data.AbstractPlate;
import uk.ac.bbsrc.tgac.miso.core.data.Plate;
import uk.ac.bbsrc.tgac.miso.core.data.Plateable;
import uk.ac.bbsrc.tgac.miso.core.data.TagBarcode;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.webapp.context.ApplicationContextProvider;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoPropertyExporter;

@Controller
@RequestMapping("/plate")
@SessionAttributes("plate")
public class EditPlateController {
  protected static final Logger log = LoggerFactory.getLogger(EditPlateController.class);

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

  @ModelAttribute("materialTypes")
  public Collection<String> populateMaterialTypes() throws IOException {
    return requestManager.listAllStudyTypes();
  }
  
  @ModelAttribute("autoGenerateIdBarcodes")
  public Boolean autoGenerateIdentificationBarcodes() {
    MisoPropertyExporter exporter = (MisoPropertyExporter)ApplicationContextProvider.getApplicationContext().getBean("propertyConfigurer");
    Map<String, String> misoProperties = exporter.getResolvedProperties();
    return misoProperties.containsKey("miso.autoGenerateIdentificationBarcodes") && Boolean.parseBoolean(misoProperties.get("miso.autoGenerateIdentificationBarcodes"));
  }

  public Collection<TagBarcode> populateAvailableTagBarcodes() throws IOException {
    List<TagBarcode> barcodes = new ArrayList<TagBarcode>(requestManager.listAllTagBarcodes());
    Collections.sort(barcodes);
    return barcodes;
  }

  public String tagBarcodesString(String platformName) throws IOException {
    List<TagBarcode> tagBarcodes = new ArrayList<TagBarcode>(requestManager.listAllTagBarcodes());
    Collections.sort(tagBarcodes);
    List<String> names = new ArrayList<String>();
    for (TagBarcode tb : tagBarcodes) {
      names.add("\"" + tb.getName() + " ("+tb.getSequence()+")\"" + ":" + "\"" + tb.getId() + "\"");
    }
    return LimsUtils.join(names, ",");
  }

  @RequestMapping(value = "/new", method = RequestMethod.GET)
  public ModelAndView newPlate(ModelMap model) throws IOException {
    return setupForm(AbstractPlate.UNSAVED_ID, model);
  }

  @RequestMapping(value = "/rest/{plateId}", method = RequestMethod.GET)
  public @ResponseBody Plate<? extends List<? extends Plateable>, ? extends Plateable> jsonRest(@PathVariable Long plateId) throws IOException {
    //return requestManager.<LinkedList<Plateable>, Plateable> getPlateById(plateId);
    return requestManager.getPlateById(plateId);
  }

  @RequestMapping(value = "/{plateId}", method = RequestMethod.GET)
  public ModelAndView setupForm(@PathVariable Long plateId,
                                ModelMap model) throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Plate<? extends List<? extends Plateable>, ? extends Plateable> plate = null;
      if (plateId == AbstractPlate.UNSAVED_ID) {
        plate = dataObjectFactory.getPlateOfSize(96, user);
        model.put("title", "New Plate");
      }
      else {
        //plate = requestManager.<LinkedList<Plateable>, Plateable> getPlateById(plateId);
        plate = requestManager.getPlateById(plateId);
        model.put("title", "Plate "+plateId);
      }

      if (plate != null) {
        if (!plate.userCanRead(user)) {
          throw new SecurityException("Permission denied.");
        }
        model.put("formObj", plate);
        model.put("plate", plate);
      }
      else {
        throw new SecurityException("No such Plate");
      }

      model.put("availableTagBarcodes", populateAvailableTagBarcodes());

      /*
      model.put("owners", LimsSecurityUtils.getPotentialOwners(user, study, securityManager.listAllUsers()));
      model.put("accessibleUsers", LimsSecurityUtils.getAccessibleUsers(user, study, securityManager.listAllUsers()));
      model.put("accessibleGroups", LimsSecurityUtils.getAccessibleGroups(user, study, securityManager.listAllGroups()));
      */
      return new ModelAndView("/pages/editPlate.jsp", model);
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show Plate", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(value = "/import", method = RequestMethod.GET)
  public ModelAndView importPlate(ModelMap model) throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Plate<? extends List<? extends Plateable>, ? extends Plateable> plate = dataObjectFactory.getPlateOfSize(96, user);
      model.put("title", "Import Plate");
      model.put("formObj", plate);
      model.put("plate", plate);
      model.put("availableTagBarcodes", populateAvailableTagBarcodes());
      return new ModelAndView("/pages/importPlate.jsp", model);
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show Plate", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(value = "/export", method = RequestMethod.GET)
  public ModelAndView exportPlate(ModelMap model) throws IOException {
      return new ModelAndView("/pages/exportPlate.jsp", model);
  }

  @RequestMapping(method = RequestMethod.POST)
  public String processSubmit(@ModelAttribute("plate") Plate<LinkedList<Plateable>, Plateable> plate,
                              ModelMap model,
                              SessionStatus session) throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      if (!plate.userCanWrite(user)) {
        throw new SecurityException("Permission denied.");
      }
      requestManager.savePlate(plate);
      session.setComplete();
      model.clear();
      return "redirect:/miso/plate/"+plate.getId();
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to save Plate", ex);
      }
      throw ex;
    }
  }
}

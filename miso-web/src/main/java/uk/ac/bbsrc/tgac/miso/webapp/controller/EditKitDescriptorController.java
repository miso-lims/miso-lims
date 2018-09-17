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
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.service.KitService;

@Controller
@RequestMapping("/kitdescriptor")
@SessionAttributes("kitDescriptor")
public class EditKitDescriptorController {
  protected static final Logger log = LoggerFactory.getLogger(EditKitDescriptorController.class);

  @Autowired
  private KitService kitService;

  @Autowired
  private MenuController menuController;

  @ModelAttribute("maxLengths")
  public Map<String, Integer> maxLengths() throws IOException {
    return kitService.getKitDescriptorColumnSizes();
  }

  @ModelAttribute("kitTypes")
  public Collection<KitType> populateKitTypes() {
    return Arrays.asList(KitType.values());
  }

  @ModelAttribute("platformTypes")
  public Collection<PlatformType> populatePlatformTypes() {
    return Arrays.asList(PlatformType.values());
  }

  public void setKitService(KitService kitService) {
    this.kitService = kitService;
  }

  @GetMapping(value = "/new")
  public ModelAndView setupForm(ModelMap model) throws IOException {
    model.addAttribute("kitDescriptor", null);
    return setupForm(KitDescriptor.UNSAVED_ID, model);
  }

  @GetMapping(value = "/{kitDescriptorId}")
  public ModelAndView setupForm(@PathVariable Long kitDescriptorId, ModelMap model) throws IOException {
    try {
      KitDescriptor kitDescriptor = null;
      if (kitDescriptorId == KitDescriptor.UNSAVED_ID) {
        kitDescriptor = new KitDescriptor();
        model.addAttribute("title", "New Kit Descriptor");
      } else {
        kitDescriptor = kitService.getKitDescriptorById(kitDescriptorId);
        if (kitDescriptor == null) throw new NotFoundException("No kit found for ID " + kitDescriptorId.toString());
        model.put("title", "Kit Descriptor " + kitDescriptor.getId());
      }

      model.put("formObj", kitDescriptor);
      model.put("kitDescriptor", kitDescriptor);
      return new ModelAndView("/pages/editKitDescriptor.jsp", model);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show Kit Descriptor", ex);
      }
      throw ex;
    }
  }

  @PostMapping
  public ModelAndView processSubmit(@ModelAttribute("kitDescriptor") KitDescriptor kitDescriptor, ModelMap model, SessionStatus session)
      throws IOException {
    try {
      kitService.saveKitDescriptor(kitDescriptor);
      menuController.refreshConstants();
      session.setComplete();
      model.clear();
      return new ModelAndView("redirect:/miso/kitdescriptor/" + kitDescriptor.getId(), model);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to save Kit Descriptor", ex);
      }
      throw ex;
    }
  }
}

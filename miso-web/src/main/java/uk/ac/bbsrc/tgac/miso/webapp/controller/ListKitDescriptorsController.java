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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;

@Controller
public class ListKitDescriptorsController {
  protected static final Logger log = LoggerFactory.getLogger(ListKitDescriptorsController.class);

  @Autowired
  private SecurityManager securityManager;

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @Autowired
  private RequestManager requestManager;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  @ModelAttribute("title")
  public String title() {
    return "Kits";
  }

  @RequestMapping("/kitdescriptors")
  public ModelAndView listKitDescriptors(ModelMap model) throws IOException {
    try {
      model.addAttribute("sequencing", requestManager.listKitDescriptorsByType(KitType.SEQUENCING));
      model.addAttribute("empcr", requestManager.listKitDescriptorsByType(KitType.EMPCR));
      model.addAttribute("library", requestManager.listKitDescriptorsByType(KitType.LIBRARY));
      model.addAttribute("clustering", requestManager.listKitDescriptorsByType(KitType.CLUSTERING));
      model.addAttribute("multiplexing", requestManager.listKitDescriptorsByType(KitType.MULTIPLEXING));

      return new ModelAndView("/pages/listKitDescriptors.jsp", model);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to list KitComponent Descriptors", ex);
      }
      throw ex;
    }
  }
}

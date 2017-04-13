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
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.DuplicateBarcodes;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.integration.util.SignatureHelper;
import uk.ac.bbsrc.tgac.miso.service.DuplicateBarcodeService;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

@Controller
public class MenuController implements ServletContextAware {
  protected static final Logger log = LoggerFactory.getLogger(MenuController.class);

  public static class DuplicateItem {
    private String url;
    private String name;
    private String alias;
    public String getUrl() {
      return url;
    }
    public void setUrl(String url) {
      this.url = url;
    }
    public String getName() {
      return name;
    }
    public void setName(String name) {
      this.name = name;
    }

    public String getAlias() {
      return alias;
    }

    public void setAlias(String alias) {
      this.alias = alias;
    }
  }
  ServletContext servletContext;
  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private DuplicateBarcodeService duplicateBarcodeService;

  @Value("${miso.autoGenerateIdentificationBarcodes}")
  private Boolean autoGenerateIdBarcodes;
  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;

  @ModelAttribute("autoGenerateIdBarcodes")
  public Boolean autoGenerateIdentificationBarcodes() {
    return autoGenerateIdBarcodes;
  }

  @ModelAttribute("detailedSample")
  public Boolean isDetailedSampleEnabled() { 
    return detailedSample;
  }

  @RequestMapping("/tech/menu")
  public String techMenu() {
    return "/pages/techMenu.jsp";
  }

  @RequestMapping("/admin/menu")
  public String adminMenu() {
    return "/pages/adminMenu.jsp";
  }

  @RequestMapping("/custombarcode")
  public String custombarcode() {
    return "/pages/customBarcodePrinting.jsp";
  }

  @RequestMapping("/myAccount")
  public ModelAndView myAccountMenu(ModelMap model) {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      String realName = user.getFullName();
      StringBuilder groups = new StringBuilder();
      for (String role : user.getRoles()) {
        groups.append(role.replaceAll("ROLE_", "") + "&nbsp;");
      }
      model.put("title", "My Account");
      model.put("userRealName", realName);
      model.put("userId", user.getUserId());
      model.put("apiKey", SignatureHelper.generatePrivateUserKey((user.getLoginName() + "::" + user.getPassword()).getBytes("UTF-8")));
      model.put("userGroups", groups.toString());
      return new ModelAndView("/pages/myAccount.jsp", model);
    } catch (IOException e) {
      log.error("my account menu", e);
      return new ModelAndView("/login.jsp", model);
    } catch (NoSuchAlgorithmException e) {
      log.error("my account menu", e);
      return new ModelAndView("/login.jsp", model);
    }
  }

  public void setSecurityManager(com.eaglegenomics.simlims.core.manager.SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @RequestMapping("/mainMenu")
  public ModelAndView mainMenu(ModelMap model) {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      model.put("title", "Home");
      Map<String, String> checks = MisoWebUtils.checkStorageDirectories((String) servletContext.getAttribute("miso.baseDirectory"));
      if (checks.keySet().contains("error")) {
        model.put("error", checks.get("error"));
      }
      model.put("duplicateBarcodes", createMapForDuplicates(duplicateBarcodeService.getAll()));
      if (Arrays.asList(user.getRoles()).contains("ROLE_EXTERNAL") && !Arrays.asList(user.getRoles()).contains("ROLE_INTERNAL")) {
        return new ModelAndView("/pages/external/externalMain.jsp", model);
      } else {
        return new ModelAndView("/pages/mainMenu.jsp", model);
      }
    } catch (IOException e) {
      return new ModelAndView("/login.jsp", model);
    }
  }

  private Map<String, List<DuplicateItem>> createMapForDuplicates(Collection<DuplicateBarcodes> barcodes) {
    Map<String, List<DuplicateItem>> results = new HashMap<>();
    for (DuplicateBarcodes barcode : barcodes) {
      List<DuplicateItem> resultItems = new ArrayList<>();
      for (Boxable item : barcode.getItems()) {
        DuplicateItem duplicateItem = new DuplicateItem();
        duplicateItem.setName(item.getName());
        duplicateItem.setAlias(item.getAlias());
        if (item instanceof LibraryDilution) {
          duplicateItem.setUrl("/miso/library/" + ((LibraryDilution) item).getLibrary().getId());
        } else if (item instanceof Library) {
          duplicateItem.setUrl("/miso/library/" + item.getId());
        } else if (item instanceof Pool) {
          duplicateItem.setUrl("/miso/pool/" + item.getId());
        } else if (item instanceof Sample) {
          duplicateItem.setUrl("/miso/sample/" + item.getId());
        } else {
          throw new IllegalArgumentException();
        }
        resultItems.add(duplicateItem);
      }
      results.put(barcode.getIdentificationBarcode(), resultItems);
    }
    return results;
  }

  @RequestMapping("/projectMenu")
  public String projectMenu() {
    return "/pages/projectMenu.jsp";
  }

  @RequestMapping("/activity/menu")
  public String activityMenu() {
    return "/pages/activityMenu.jsp";
  }
  
  @RequestMapping("/admin/instituteDefaults")
  public ModelAndView tissueOptions(ModelMap model) {
    model.put("title", "Institute Defaults");
    return new ModelAndView("/pages/instituteDefaults.jsp", model);
  }

  @Override
  public void setServletContext(ServletContext servletContext) {
    this.servletContext = servletContext;
  }
}

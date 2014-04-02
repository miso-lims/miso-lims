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

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.bbsrc.tgac.miso.integration.util.SignatureHelper;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

@Controller
public class MenuController implements ServletContextAware {

  ServletContext servletContext;
  @Autowired
  private SecurityManager securityManager;

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
      model.put("userRealName", realName);
      model.put("userId", user.getUserId());
      model.put("apiKey", SignatureHelper.generatePrivateUserKey((user.getLoginName() + "::" + user.getPassword()).getBytes("UTF-8")));
      model.put("userGroups", groups.toString());
      return new ModelAndView("/pages/myAccount.jsp", model);
    }
    catch (IOException e) {
      e.printStackTrace();
      return new ModelAndView("/login.jsp", model);
    }
    catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
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
      Map<String, String> checks = MisoWebUtils.checkStorageDirectories((String) servletContext.getAttribute("miso.baseDirectory"));
      if (checks.keySet().contains("error")) {
        model.put("error", checks.get("error"));
      }
      if (Arrays.asList(user.getRoles()).contains("ROLE_EXTERNAL") && !Arrays.asList(user.getRoles()).contains("ROLE_INTERNAL")) {
        return new ModelAndView("/pages/external/externalMain.jsp", model);
      }
      else {
        return new ModelAndView("/pages/mainMenu.jsp", model);
      }
    }
    catch (IOException e) {
      return new ModelAndView("/login.jsp", model);
    }
  }

  @RequestMapping("/projectMenu")
  public String projectMenu() {
    return "/pages/projectMenu.jsp";
  }

  @RequestMapping("/activity/menu")
  public String activityMenu() {
    return "/pages/activityMenu.jsp";
  }

  @Override
  public void setServletContext(ServletContext servletContext) {
    this.servletContext = servletContext;
  }
}

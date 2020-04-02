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

package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.integration.util.SignatureHelper;

@Controller
public class MenuController {
  protected static final Logger log = LoggerFactory.getLogger(MenuController.class);

  ServletContext servletContext;
  @Autowired
  private AuthorizationManager authorizationManager;

  @Value("${miso.autoGenerateIdentificationBarcodes}")
  private Boolean autoGenerateIdBarcodes;
  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;
  @Value("${miso.genomeFolder:}")
  private String genomeFolder;

  @Resource
  private Boolean boxScannerEnabled;

  @ModelAttribute("autoGenerateIdBarcodes")
  public Boolean autoGenerateIdentificationBarcodes() {
    return autoGenerateIdBarcodes;
  }

  @RequestMapping("/login")
  public ModelAndView loginPage(ModelMap model, @RequestParam(name = "login_error", required = false) Integer loginError) {
    return new ModelAndView("/WEB-INF/login.jsp", model);
  }

  @RequestMapping("/myAccount")
  public ModelAndView myAccountMenu(ModelMap model) {
    try {
      User user = authorizationManager.getCurrentUser();
      String realName = user.getFullName();
      StringBuilder groups = new StringBuilder();
      for (String role : user.getRoles()) {
        groups.append(role.replaceAll("ROLE_", "") + "&nbsp;");
      }
      model.put("title", "My Account");
      model.put("userRealName", realName);
      model.put("userId", user.getId());
      model.put("apiKey", SignatureHelper.generatePrivateUserKey((user.getLoginName() + "::" + user.getPassword()).getBytes("UTF-8")));
      model.put("userGroups", groups.toString());
      return new ModelAndView("/WEB-INF/pages/myAccount.jsp", model);
    } catch (IOException e) {
      log.error("my account menu", e);
      return new ModelAndView("/WEB-INF/login.jsp", model);
    } catch (NoSuchAlgorithmException e) {
      log.error("my account menu", e);
      return new ModelAndView("/WEB-INF/login.jsp", model);
    }
  }

  @GetMapping("/")
  public ModelAndView redirectMisoRoot(ModelMap model) {
    model.clear();
    return new ModelAndView("redirect:mainMenu", model);
  }

  @RequestMapping("/mainMenu")
  public ModelAndView mainMenu(ModelMap model) throws IOException {
    User user = authorizationManager.getCurrentUser();
    model.put("title", "Home");
    ObjectMapper mapper = new ObjectMapper();
    model.put("favouriteWorkflows",
        user.getFavouriteWorkflows().stream().map(Dtos::asDto).map(dto -> mapper.valueToTree(dto)).collect(Collectors.toList()));
    return new ModelAndView("/WEB-INF/pages/mainMenu.jsp", model);
  }

  @GetMapping("/accessDenied")
  public ModelAndView showAccessDenied(ModelMap model) {
    return new ModelAndView("/WEB-INF/accessDenied.jsp", model);
  }

  @GetMapping("/error")
  public ModelAndView showError(ModelMap model, HttpServletRequest request) {
    int statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
    Status status = Status.fromStatusCode(statusCode);
    model.addAttribute("genericMessage", statusCode);
    model.addAttribute("specificMessage", status.getReasonPhrase());
    model.addAttribute("showBugUrl", true);
    return new ModelAndView("/WEB-INF/pages/handledError.jsp", model);
  }
}

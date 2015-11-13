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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.security.PasswordCodecService;

/**
 * Created by IntelliJ IDEA. User: davey Date: 05-Feb-2010 Time: 12:20:07
 */
@Controller
@RequestMapping("/registerUser")
public class UserRegistrationController {
  protected static final Logger log = LoggerFactory.getLogger(UserRegistrationController.class);

  @Autowired
  private SecurityManager securityManager;

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @Autowired
  private PasswordCodecService passwordCodecService;

  public void setPasswordCodecService(PasswordCodecService passwordCodecService) {
    this.passwordCodecService = passwordCodecService;
  }

  @RequestMapping(method = RequestMethod.GET)
  public ModelAndView setupForm(ModelMap model) throws IOException {
    model.put("user", new UserImpl());
    return new ModelAndView("/pages/registerUser.jsp", model);
  }

  @RequestMapping(method = RequestMethod.POST)
  public String processSubmit(@ModelAttribute("user") User user, ModelMap model, SessionStatus session) throws IOException {
    try {
      // encode the password as set by the passwordCodecService
      if (passwordCodecService != null) {
        user.setPassword(passwordCodecService.getEncoder().encodePassword(user.getPassword(), null));
      } else {
        log.warn("No passwordCodecService set! Passwords will be stored in plaintext!");
      }

      securityManager.saveUser(user);
      session.setComplete();
      model.clear();
      return "redirect:/miso/mainMenu";
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to register user", ex);
      }
      throw ex;
    }
  }
}

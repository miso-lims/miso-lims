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

/**
 * uk.ac.bbsrc.tgac.miso.webapp.controller
 * <p/>
 * Info
 *
 * @author Xingdong Bian
 * @since 0.1.2
 */

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.lowagie.text.Document;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.decorator.itext.ITextProjectDecorator;
import uk.ac.bbsrc.tgac.miso.core.exception.ReportingException;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequestMapping("/flexreports")
@Controller
public class FlexReportsController {
  protected static final Logger log = LoggerFactory.getLogger(FlexReportsController.class);

  private static final String HTML = "html";
  private static final String PDF = "pdf";
  private static final String XLS = "xls";

  @Autowired
  private RequestManager requestManager;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  @Autowired
  private SecurityManager securityManager;

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @Autowired
  private JdbcTemplate interfaceTemplate;

  public void setInterfaceTemplate(JdbcTemplate interfaceTemplate) {
    this.interfaceTemplate = interfaceTemplate;
  }

  @RequestMapping(method = RequestMethod.GET)
  public ModelAndView query(ModelMap modelMap) {
    try {
      modelMap.put("tables", DbUtils.getTables(interfaceTemplate));
    } catch (MetaDataAccessException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return new ModelAndView("/pages/flexreport.jsp", modelMap);
  }

  @RequestMapping(method = RequestMethod.POST)
  public void postReport(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
    try {
      String j = ServletRequestUtils.getRequiredStringParameter(request, "json");
      JSONObject json = JSONObject.fromObject(j);
      log.info(json.toString());
    } catch (ServletRequestBindingException e) {
      e.printStackTrace();
    }
  }

}

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * uk.ac.bbsrc.tgac.miso.webapp.controller
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;

import net.sf.json.JSONObject;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractProject;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.decorator.itext.ITextProjectDecorator;
import uk.ac.bbsrc.tgac.miso.service.ProjectService;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@RequestMapping("/reports")
@Controller
public class ReportsController {
  protected static final Logger log = LoggerFactory.getLogger(ReportsController.class);

  private static final String HTML = "html";
  private static final String PDF = "pdf";
  private static final String XLS = "xls";

  @Autowired
  private ProjectService projectService;

  public void setProjectService(ProjectService projectService) {
    this.projectService = projectService;
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

  @RequestMapping(value = "/project/{projectId}")
  public void fireGetProjectReport(@PathVariable("projectId") Long projectId, ModelMap modelMap, HttpServletResponse response) {

    User user = null;
    String format = PDF;
    try {
      user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Project project = projectId == AbstractProject.UNSAVED_ID ? null : projectService.getProjectById(projectId);
      if (project != null) {
        if (!project.userCanRead(user)) {
          throw new SecurityException("Permission denied.");
        }

        try {
          if (format.equals(PDF)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            new ITextProjectDecorator(project, new Document(), baos).buildReport();

            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setContentType("application/pdf");
            response.setContentLength(baos.size());
            OutputStream os = response.getOutputStream();
            baos.writeTo(os);
            os.flush();
            os.close();
          } else {
            throw new IllegalArgumentException("Unsupported report format");
          }
        } catch (DocumentException e) {
          log.error("project report", e);
        }
      }
    } catch (IOException e) {
      log.error("project report", e);
    }
  }

  @RequestMapping(value = "/sample/{sampleId}")
  public void fireGetSampleReport(@PathVariable("sampleId") Long sampleId, ModelMap modelMap, HttpServletResponse response) {
    log.warn("not implemented");
  }

  @RequestMapping(value = "/samples", method = RequestMethod.GET)
  public void fireGetSamplesReport(ModelMap modelMap, HttpServletResponse response) {
    log.warn("not implemented");
  }

  @RequestMapping(value = "/run/{runId}")
  public void fireGetRunReport(@PathVariable("runId") Long runId, ModelMap modelMap, HttpServletResponse response) {
    log.warn("not implemented");
  }

  @RequestMapping(value = "/runs", method = RequestMethod.GET)
  public void fireGetRunsReport(ModelMap modelMap, HttpServletResponse response) {
    log.warn("not implemented");
  }

  @RequestMapping(method = RequestMethod.GET)
  public ModelAndView setupForm(ModelMap modelMap) {
    try {
      modelMap.put("tables", DbUtils.getTables(interfaceTemplate));
    } catch (MetaDataAccessException e) {
      log.error("reports controller form", e);
    } catch (SQLException e) {
      log.error("reports controller form", e);
    }
    return new ModelAndView("/pages/reporting.jsp", modelMap);
  }

  @RequestMapping(method = RequestMethod.POST)
  public void postReport(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
    try {
      String j = ServletRequestUtils.getRequiredStringParameter(request, "json");
      JSONObject json = JSONObject.fromObject(j);
      log.info(json.toString());
    } catch (ServletRequestBindingException e) {
      log.error("project report post", e);
    }
  }
}

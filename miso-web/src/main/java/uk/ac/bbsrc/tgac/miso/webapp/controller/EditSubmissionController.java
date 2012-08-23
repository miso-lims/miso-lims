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

import org.springframework.jdbc.core.JdbcTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import com.eaglegenomics.simlims.core.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.illumina.IlluminaRun;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ls454.LS454Run;
import uk.ac.bbsrc.tgac.miso.core.data.impl.solid.SolidRun;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedRunException;
import uk.ac.bbsrc.tgac.miso.core.exception.SubmissionException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.SubmissionManager;
import uk.ac.bbsrc.tgac.miso.core.manager.FilesManager;

import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/submission")
@SessionAttributes("submission")
public class EditSubmissionController {
  protected static final Logger log = LoggerFactory.getLogger(EditSubmissionController.class);

  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private RequestManager requestManager;

  @Autowired
  private DataObjectFactory dataObjectFactory;

  @Autowired
  private FilesManager misoFileManager;

  @Autowired
  private SubmissionManager submissionManager;

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

  public void setMisoFileManager(FilesManager misoFileManager) {
    this.misoFileManager = misoFileManager;
  }

  public void setSubmissionManager(SubmissionManager submissionManager) {
    this.submissionManager = submissionManager;
  }

  @ModelAttribute("maxLengths")
  public Map<String, Integer> maxLengths() throws IOException {
    return DbUtils.getColumnSizes(interfaceTemplate, "Submission");
  }

  @ModelAttribute("projects")
  public Collection<Project> populateProjects() throws IOException {
    //User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    List<Project> projects = new ArrayList<Project>(requestManager.listAllProjects());
    /*
    for (Project p : projects) {
      Collection<Run> runs = requestManager.listAllRunsByProjectId(p.getProjectId());
      for (Run r : runs) {
        if (r instanceof IlluminaRun) {
          IlluminaRun ir = (IlluminaRun)r;
          ir.setFlowcells(requestManager.listLaneFlowcellsByRunId(ir.getRunId()));
        }
        else if (r instanceof SolidRun) {
          SolidRun sr = (SolidRun)r;
          sr.setFlowcells(requestManager.listChamberFlowcellsByRunId(sr.getRunId()));
        }
        else if (r instanceof LS454Run) {
          LS454Run lr = (LS454Run)r;
          lr.setFlowcells(requestManager.listChamberFlowcellsByRunId(lr.getRunId()));
        }
      }
      */

      /*
      Collection<Study> studies = requestManager.listAllStudiesByProjectId(p.getProjectId());
      p.setStudies(studies);
      for (Study s : studies) {
        Collection<Experiment> experiments = requestManager.listAllExperimentsByStudyId(s.getStudyId());
        s.setExperiments(experiments);
      }
      */
    //}
    Collections.sort(projects);
    return projects;
  }

  @ModelAttribute("studies")
  public Collection<Study> populateStudies() throws IOException {
    //User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    return requestManager.listAllStudies();
  }

  @ModelAttribute("samples")
  public Collection<Sample> populateSamples() throws IOException {
    //User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    return requestManager.listAllSamples();
  }

  @ModelAttribute("runs")
  public Collection<Run> populateRuns() throws IOException {
    //User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    return requestManager.listAllRuns();
  }

  @ModelAttribute("experiments")
  public Collection<Experiment> populateExperiments() throws IOException {
    //User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    return requestManager.listAllExperiments();
  }

  @ModelAttribute("availableElements")
  public Collection<Submittable> populateElements() throws IOException {
    ArrayList<Submittable> list = new ArrayList<Submittable>();
    list.addAll(populateSamples());
    list.addAll(populateStudies());
    list.addAll(populateExperiments());
    return list;
  }

  @RequestMapping(value = "/new", method = RequestMethod.GET)
  public ModelAndView newSubmission(ModelMap model) throws IOException {
    return setupForm(Submission.UNSAVED_ID, model);
  }

  @RequestMapping(value = "/{submissionId}", method = RequestMethod.GET)
  public ModelAndView setupForm(@PathVariable Long submissionId,
                                ModelMap model) throws IOException {
    try {
      Submission submission = null;
      if (submissionId == Submission.UNSAVED_ID) {
        User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
        submission = dataObjectFactory.getSubmission(user);
        model.put("title", "New Submission");
      }
      else {
        submission = requestManager.getSubmissionById(submissionId);
        model.put("title", "Submission "+submissionId);
        model.put("prettyMetadata", submissionManager.prettifySubmissionMetadata(submission));
      }

      if (submission == null) {
        throw new SecurityException("No such Submission");
      }
     
      model.put("formObj", submission);
      model.put("submission", submission);
      return new ModelAndView("/pages/editSubmission.jsp", model);
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show submission", ex);
      }
      throw ex;
    }
    catch (SubmissionException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show submission", e);
      }
      throw new IOException(e);
    }
  }

  @RequestMapping(method = RequestMethod.POST)
  public String processSubmit(@ModelAttribute("submission") Submission submission,
                              ModelMap model, SessionStatus session) throws IOException, MalformedRunException {
    try {
      requestManager.saveSubmission(submission);
      session.setComplete();
      model.clear();
      return "redirect:/miso/submission/"+submission.getSubmissionId();
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to save submission", ex);
      }
      throw ex;
    }
  }
} 
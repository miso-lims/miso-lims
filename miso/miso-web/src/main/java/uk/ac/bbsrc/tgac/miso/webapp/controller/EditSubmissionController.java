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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SubmissionImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedRunException;
import uk.ac.bbsrc.tgac.miso.core.exception.SubmissionException;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.manager.SubmissionManager;
import uk.ac.bbsrc.tgac.miso.service.ExperimentService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;
import uk.ac.bbsrc.tgac.miso.service.StudyService;
import uk.ac.bbsrc.tgac.miso.service.impl.RunService;

@Controller
@RequestMapping("/submission")
@SessionAttributes("submission")
public class EditSubmissionController {
  protected static final Logger log = LoggerFactory.getLogger(EditSubmissionController.class);

  @Autowired
  private RequestManager requestManager;
  @Autowired
  private RunService runService;
  @Autowired
  private SampleService sampleService;
  @Autowired
  private StudyService studyService;

  @Autowired
  private SubmissionManager submissionManager;

  @Autowired
  private ExperimentService experimentService;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setRunService(RunService runService) {
    this.runService = runService;
  }

  public void setSampleService(SampleService sampleService) {
    this.sampleService = sampleService;
  }

  public void setSubmissionManager(SubmissionManager submissionManager) {
    this.submissionManager = submissionManager;
  }

  @ModelAttribute("maxLengths")
  public Map<String, Integer> maxLengths() throws IOException {
    return requestManager.getSubmissionColumnSizes();
  }

  @ModelAttribute("projects")
  public Collection<Project> populateProjects() throws IOException {
    List<Project> projects = new ArrayList<>(requestManager.listAllProjects());
    Collections.sort(projects);
    return projects;
  }

  @ModelAttribute("studies")
  public Collection<Study> populateStudies() throws IOException {
    return studyService.list(0, 0, true, "id");
  }

  @ModelAttribute("samples")
  public Collection<Sample> populateSamples() throws IOException {
    return sampleService.list();
  }

  @ModelAttribute("runs")
  public Collection<Run> populateRuns() throws IOException {
    return runService.list();
  }

  @ModelAttribute("experiments")
  public Collection<Experiment> populateExperiments() throws IOException {
    return experimentService.listAll();
  }

  @RequestMapping(value = "/new", method = RequestMethod.GET)
  public ModelAndView newSubmission(ModelMap model) throws IOException {
    return setupForm(Submission.UNSAVED_ID, model);
  }

  @RequestMapping(value = "/{submissionId}", method = RequestMethod.GET)
  public ModelAndView setupForm(@PathVariable Long submissionId, ModelMap model) throws IOException {
    try {
      Submission submission = null;
      if (submissionId == Submission.UNSAVED_ID) {
        submission = new SubmissionImpl();
        model.put("title", "New Submission");
      } else {
        submission = requestManager.getSubmissionById(submissionId);
        model.put("title", "Submission " + submissionId);
        model.put("prettyMetadata", submissionManager.prettifySubmissionMetadata(submission));
      }

      if (submission == null) {
        throw new SecurityException("No such Submission");
      }

      model.put("formObj", submission);
      model.put("submission", submission);
      return new ModelAndView("/pages/editSubmission.jsp", model);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show submission", ex);
      }
      throw ex;
    } catch (SubmissionException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show submission", e);
      }
      throw new IOException(e);
    }
  }

  @RequestMapping(method = RequestMethod.POST)
  public String processSubmit(@ModelAttribute("submission") Submission submission, ModelMap model, SessionStatus session)
      throws IOException, MalformedRunException {
    try {
      requestManager.saveSubmission(submission);
      session.setComplete();
      model.clear();
      return "redirect:/miso/submission/" + submission.getId();
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to save submission", ex);
      }
      throw ex;
    }
  }
}

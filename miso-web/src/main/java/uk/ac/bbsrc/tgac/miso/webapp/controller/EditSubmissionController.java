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

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.ws.rs.QueryParam;

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

import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.ExperimentService;
import uk.ac.bbsrc.tgac.miso.service.SubmissionService;

@Controller
@RequestMapping("/submission")
@SessionAttributes("submission")
public class EditSubmissionController {
  protected static final Logger log = LoggerFactory.getLogger(EditSubmissionController.class);

  private static final Pattern COMMA = Pattern.compile(",");

  @Autowired
  private SubmissionService submissionService;

  @Autowired
  private ExperimentService experimentService;

  @ModelAttribute("maxLengths")
  public Map<String, Integer> maxLengths() throws IOException {
    return submissionService.getColumnSizes();
  }

  @RequestMapping(value = "/new", method = RequestMethod.GET)
  public ModelAndView newSubmission(@QueryParam("experimentIds") String experimentIds, ModelMap model) throws IOException {
    Submission submission = new Submission();
    submission.setExperiments(COMMA.splitAsStream(experimentIds).map(Long::parseLong).map(WhineyFunction.rethrow(experimentService::get))
        .collect(Collectors.toSet()));
    submission.setCreationDate(new Date());
    return setupForm(submission, "New Submission", model);
  }

  @RequestMapping(method = RequestMethod.POST)
  public String processSubmit(@ModelAttribute("submission") Submission submission, ModelMap model, SessionStatus session)
      throws IOException {
    try {
      submissionService.save(submission);
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

  @RequestMapping(value = "/{submissionId}", method = RequestMethod.GET)
  public ModelAndView setupForm(@PathVariable Long submissionId, ModelMap model) throws IOException {
    Submission submission = submissionService.get(submissionId);
    return setupForm(submission, "Submission " + submissionId, model);
  }

  private ModelAndView setupForm(Submission submission, String title, ModelMap model) {
    model.put("title", title);
    model.put("formObj", submission);
    model.put("submission", submission);
    model.put("experiments", submission.getExperiments().stream().map(Dtos::asDto).collect(Collectors.toList()));
    return new ModelAndView("/pages/editSubmission.jsp", model);
  }
}

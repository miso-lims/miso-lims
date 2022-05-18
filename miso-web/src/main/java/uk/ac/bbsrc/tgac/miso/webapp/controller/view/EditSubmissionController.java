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
import java.util.Date;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.service.ExperimentService;
import uk.ac.bbsrc.tgac.miso.core.service.SubmissionService;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

@Controller
@RequestMapping("/submission")
public class EditSubmissionController {
  protected static final Logger log = LoggerFactory.getLogger(EditSubmissionController.class);

  private static final Pattern COMMA = Pattern.compile(",");

  @Autowired
  private SubmissionService submissionService;
  @Autowired
  private ExperimentService experimentService;
  @Autowired
  private ObjectMapper mapper;

  @GetMapping(value = "/new")
  public ModelAndView newSubmission(@QueryParam("experimentIds") String experimentIds, ModelMap model) throws IOException {
    Submission submission = new Submission();
    submission.setExperiments(COMMA.splitAsStream(experimentIds).map(Long::parseLong).map(WhineyFunction.rethrow(experimentService::get))
        .collect(Collectors.toSet()));
    submission.setCreationDate(new Date());
    return setupForm(submission, "New Submission", model);
  }

  @GetMapping(value = "/{submissionId}")
  public ModelAndView setupForm(@PathVariable Long submissionId, ModelMap model) throws IOException {
    Submission submission = submissionService.get(submissionId);
    return setupForm(submission, "Submission " + submissionId, model);
  }

  private ModelAndView setupForm(Submission submission, String title, ModelMap model) throws JsonProcessingException {
    model.put("title", title);
    model.put("submission", submission);
    model.put("submissionDto", mapper.writeValueAsString(Dtos.asDto(submission)));
    model.put("experiments", submission.getExperiments().stream().map(expt -> Dtos.asDto(expt))
        .collect(Collectors.toList()));
    return new ModelAndView("/WEB-INF/pages/editSubmission.jsp", model);
  }
}

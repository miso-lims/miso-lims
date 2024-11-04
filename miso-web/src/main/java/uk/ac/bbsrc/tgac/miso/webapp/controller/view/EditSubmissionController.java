package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

import jakarta.ws.rs.QueryParam;
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
  public ModelAndView newSubmission(@QueryParam("experimentIds") String experimentIds, ModelMap model)
      throws IOException {
    Submission submission = new Submission();
    submission.setExperiments(
        COMMA.splitAsStream(experimentIds).map(Long::parseLong).map(WhineyFunction.rethrow(experimentService::get))
            .collect(Collectors.toSet()));
    submission.setCreationDate(LocalDate.now(ZoneId.systemDefault()));
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

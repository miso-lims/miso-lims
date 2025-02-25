package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.eaglegenomics.simlims.core.User;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response.Status;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.data.type.SubmissionActionType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.SubmissionService;
import uk.ac.bbsrc.tgac.miso.core.util.EnaSubmissionPreparation;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SubmissionDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.RestException;

@Controller
@RequestMapping("/rest/submissions")
public class SubmissionRestController extends AbstractRestController {
  @Autowired
  private SubmissionService submissionService;
  @Autowired
  private AuthorizationManager authorizationManager;

  @ResponseBody
  @GetMapping(path = "/{submissionId}/download")
  public byte[] download(@PathVariable("submissionId") Long submissionId, @QueryParam("action") String action,
      @QueryParam("centreName") String centreName, HttpServletResponse response) throws IOException {
    Submission submission = submissionService.get(submissionId);
    if (submission == null) {
      throw new RestException("Submission not found", Status.NOT_FOUND);
    }
    User user = authorizationManager.getCurrentUser();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(new MediaType("application", "zip"));
    response.setHeader("Content-Disposition",
        "attachment; filename="
            + String.format("SUBMISSON%d-%s.zip", submission.getId(),
                new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
    return new EnaSubmissionPreparation(submission, user, centreName, SubmissionActionType.valueOf(action)).toBytes();
  }

  @PostMapping
  public @ResponseBody SubmissionDto create(@RequestBody SubmissionDto dto) throws IOException {
    return RestUtils.createObject("Submission", dto, Dtos::to, submissionService, Dtos::asDto);
  }

  @PutMapping("/{submissionId}")
  public @ResponseBody SubmissionDto update(@PathVariable long submissionId, @RequestBody SubmissionDto dto)
      throws IOException {
    return RestUtils.updateObject("Submission", submissionId, dto, Dtos::to, submissionService, Dtos::asDto);
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Submission", ids, submissionService);
  }

}

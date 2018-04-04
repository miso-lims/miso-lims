package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow;
import uk.ac.bbsrc.tgac.miso.dto.WorkflowStateDto;
import uk.ac.bbsrc.tgac.miso.service.workflow.WorkflowManager;

/**
 * Responsible for handling all workflow AJAX requests
 */
@Controller
@RequestMapping("/rest/workflow")
public class WorkflowRestController extends RestController {
  @Autowired
  WorkflowManager workflowManager;

  @RequestMapping(value = "/{workflowId}/step/{stepNumber}", method = RequestMethod.POST)
  public @ResponseBody WorkflowStateDto process(@PathVariable("workflowId") long workflowId, @PathVariable("stepNumber") int stepNumber,
      @RequestParam("input") String input) throws IOException {
    Workflow workflow = workflowManager.loadWorkflow(workflowId);
    workflowManager.processInput(workflow, stepNumber, input);

    return new WorkflowStateDto(workflow);
  }

  @RequestMapping(value = "/{workflowId}/execute", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public @ResponseBody void execute(@PathVariable("workflowId") long workflowId) throws IOException {
    workflowManager.execute(workflowManager.loadWorkflow(workflowId));
  }

  @RequestMapping(value = "/{workflowId}/step/{stepNumber}", method = RequestMethod.GET)
  public @ResponseBody WorkflowStateDto getStep(@PathVariable("workflowId") long workflowId, @PathVariable("stepNumber") int stepNumber)
      throws IOException {
    Workflow workflow = workflowManager.loadWorkflow(workflowId);
    return new WorkflowStateDto(workflow, stepNumber);
  }

  @RequestMapping(value = "/{workflowId}/step/latest", method = RequestMethod.GET)
  public @ResponseBody WorkflowStateDto nextStep(@PathVariable("workflowId") long workflowId) throws IOException {
    return new WorkflowStateDto(workflowManager.loadWorkflow(workflowId));
  }

  @RequestMapping(value = "/{workflowId}/step/latest", method = RequestMethod.DELETE)
  public @ResponseBody WorkflowStateDto cancelInput(@PathVariable("workflowId") long workflowId) throws IOException {
    Workflow workflow = workflowManager.loadWorkflow(workflowId);
    workflowManager.cancelInput(workflow);
    return new WorkflowStateDto(workflow);
  }
}

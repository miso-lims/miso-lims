package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow.WorkflowName;
import uk.ac.bbsrc.tgac.miso.core.manager.WorkflowManager;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.UserService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.WorkflowStateDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;

/**
 * Responsible for handling all workflow AJAX requests
 */
@Controller
@RequestMapping("/rest/workflows")
public class WorkflowRestController extends AbstractRestController {
  @Autowired
  WorkflowManager workflowManager;

  @Autowired
  private UserService userService;

  @Autowired
  private AuthorizationManager authorizationManager;

  @PostMapping(value = "/{workflowId}/step/{stepNumber}")
  public @ResponseBody WorkflowStateDto process(@PathVariable("workflowId") long workflowId,
      @PathVariable("stepNumber") int stepNumber,
      @RequestParam("input") String input) throws IOException {
    Workflow workflow = workflowManager.loadWorkflow(workflowId);
    workflowManager.processInput(workflow, stepNumber, input);

    return Dtos.asDto(workflow, stepNumber + 1);
  }

  @PostMapping(value = "/{workflowId}/execute")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public @ResponseBody void execute(@PathVariable("workflowId") long workflowId) throws IOException {
    workflowManager.execute(workflowManager.loadWorkflow(workflowId));
  }

  @GetMapping(value = "/{workflowId}/step/{stepNumber}")
  public @ResponseBody WorkflowStateDto getStep(@PathVariable("workflowId") long workflowId,
      @PathVariable("stepNumber") int stepNumber)
      throws IOException {
    Workflow workflow = workflowManager.loadWorkflow(workflowId);
    return Dtos.asDto(workflow, stepNumber);
  }

  @GetMapping(value = "/{workflowId}/step/latest")
  public @ResponseBody WorkflowStateDto nextStep(@PathVariable("workflowId") long workflowId) throws IOException {
    return Dtos.asDto(workflowManager.loadWorkflow(workflowId));
  }

  @DeleteMapping(value = "/{workflowId}/step/latest")
  public @ResponseBody WorkflowStateDto cancelInput(@PathVariable("workflowId") long workflowId) throws IOException {
    Workflow workflow = workflowManager.loadWorkflow(workflowId);
    workflowManager.cancelInput(workflow);
    return Dtos.asDto(workflow);
  }

  @GetMapping(produces = "application/json")
  @ResponseBody
  public List<WorkflowStateDto> list() throws IOException {
    return workflowManager.listUserWorkflows().stream().map(Dtos::asDto).collect(Collectors.toList());
  }

  @PostMapping(value = "/favourites/add/{workflowName}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void addFavourite(@PathVariable WorkflowName workflowName)
      throws IOException {
    User user = authorizationManager.getCurrentUser();
    Set<WorkflowName> favouriteWorkflows = user.getFavouriteWorkflows();
    favouriteWorkflows.add(workflowName);
    userService.update(user);
  }

  @PostMapping(value = "/favourites/remove/{workflowName}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void removeFavourite(@PathVariable WorkflowName workflowName)
      throws IOException {
    User user = authorizationManager.getCurrentUser();
    Set<WorkflowName> favouriteWorkflows = user.getFavouriteWorkflows();
    favouriteWorkflows.remove(workflowName);
    userService.update(user);
  }
}

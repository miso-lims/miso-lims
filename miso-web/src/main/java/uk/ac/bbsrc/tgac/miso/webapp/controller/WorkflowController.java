package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.workflow.WorkflowManager;

/**
 * Responsible for serving workflow-related JSPs
 */
@RequestMapping("/workflow")
@Controller
public class WorkflowController {
  @Autowired
  WorkflowManager workflowManager;

  @RequestMapping("/new/{workflowName}")
  public ModelAndView createWorkflow(@PathVariable String workflowName, ModelMap model) throws IOException {
    Workflow workflow = workflowManager.beginWorkflow(workflowName);
    model.put("title", workflow.getProgress().getWorkflowName().getDescription());
    model.put("state", new ObjectMapper().writeValueAsString(Dtos.asDto(workflow)));
    return new ModelAndView("/pages/workflow.jsp", model);
  }

  @RequestMapping("/edit/{id}")
  public ModelAndView editWorkflow(@PathVariable Long id, ModelMap model) throws IOException {
    Workflow workflow = workflowManager.loadWorkflow(id);
    if (workflow == null) throw new NotFoundException("No workflow found for ID " + id.toString());
    model.put("title", workflow.getProgress().getWorkflowName().getDescription());
    model.put("state", new ObjectMapper().writeValueAsString(Dtos.asDto(workflow)));
    return new ModelAndView("/pages/workflow.jsp", model);
  }
}

package uk.ac.bbsrc.tgac.miso.service.workflow;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;
import com.google.common.annotations.VisibleForTesting;

import jakarta.annotation.Resource;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Progress;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.FactoryType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.InputType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow.WorkflowName;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowExecutor;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.ProgressImpl;
import uk.ac.bbsrc.tgac.miso.core.manager.ProgressStepFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.WorkflowManager;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.persistence.ProgressStore;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultWorkflowManager implements WorkflowManager {
  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private ProgressStore progressStore;

  @Autowired
  private WorkflowExecutor workflowExecutor;

  @Resource()
  private Map<FactoryType, ProgressStepFactory> progressStepFactoryMap;

  @Override
  public Workflow beginWorkflow(String workflowNameString) throws IOException {
    WorkflowName workflowName = WorkflowName.valueOf(workflowNameString);

    Progress progress = new ProgressImpl();
    progress.setWorkflowName(workflowName);

    save(progress);
    return workflowName.createWorkflow(progress);
  }

  @Override
  public Workflow processInput(Workflow workflow, int stepNumber, String input) throws IOException {
    List<String> errors =
        workflow.processInput(stepNumber, makeProgressStep(input, workflow.getStep(stepNumber).getInputTypes()));
    if (!errors.isEmpty()) {
      throw new ValidationException(errors.stream().map(err -> new ValidationError(err)).collect(Collectors.toList()));
    }
    save(workflow.getProgress());
    return workflow;
  }

  @VisibleForTesting
  protected ProgressStep makeProgressStep(String input, Set<InputType> inputTypes) throws IOException {
    for (FactoryType factoryType : getFactoryTypes(inputTypes)) {
      ProgressStep step = progressStepFactoryMap.get(factoryType).create(input, inputTypes);
      if (step != null) {
        return step;
      }
    }

    List<String> names = inputTypes.stream()
        .filter(type -> type != InputType.SKIP)
        .sorted()
        .map(InputType::getName)
        .collect(Collectors.toList());

    throw new ValidationException(Collections.singletonList(
        new ValidationError(
            String.format("No %s found matching '%s'", LimsUtils.joinWithConjunction(names, "or"), input))));
  }

  /**
   * @return sorted, de-duplicated list of FactoryTypes
   */
  private List<FactoryType> getFactoryTypes(Set<InputType> inputTypes) {
    return inputTypes.stream().map(InputType::getFactoryType).distinct().sorted().collect(Collectors.toList());
  }

  @Override
  public Workflow cancelInput(Workflow workflow) throws IOException {
    workflow.cancelInput();
    save(workflow.getProgress());
    return workflow;
  }

  private void save(Progress progress) throws IOException {
    if (progress.getUser() != null)
      authorizationManager.throwIfNotOwner(progress.getUser());
    if (!progress.isSaved()) {
      create(progress);
    } else {
      update(progress);
    }
  }

  private void update(Progress progress) throws IOException {
    Progress managed = progressStore.getManaged(progress.getId());
    managed.getSteps().forEach(step -> progressStore.delete(step));
    managed.setSteps(progress.getSteps());
    setChangeDetails(managed);
    progressStore.save(managed);
  }

  private void create(Progress progress) throws IOException {
    setChangeDetails(progress);
    progressStore.save(progress);
  }

  private void setChangeDetails(Progress progress) throws IOException {
    User user = authorizationManager.getCurrentUser();
    Date now = new Date();

    if (!progress.isSaved()) {
      progress.setUser(user);
      if (progress.getCreationTime() == null) {
        progress.setCreationTime(now);
      }
      if (progress.getLastModified() == null) {
        progress.setLastModified(now);
      }
    } else {
      progress.setLastModified(now);
    }
  }

  @Override
  public Workflow loadWorkflow(long id) throws IOException {
    Progress progress = progressStore.get(id);
    if (progress == null)
      return null;

    authorizationManager.throwIfNotOwner(progress.getUser());
    return progress.getWorkflowName().createWorkflow(progress);
  }

  @Override
  public List<Workflow> listUserWorkflows() throws IOException {
    return progressStore.listByUserId(authorizationManager.getCurrentUser().getId()).stream()
        .map(progress -> progress.getWorkflowName().createWorkflow(progress)).collect(Collectors.toList());
  }

  @Override
  public void execute(Workflow workflow) throws IOException {
    if (!workflow.isComplete())
      throw new IllegalArgumentException("Workflow is not complete");

    workflow.execute(workflowExecutor);
    progressStore.delete(workflow.getProgress());
  }
}

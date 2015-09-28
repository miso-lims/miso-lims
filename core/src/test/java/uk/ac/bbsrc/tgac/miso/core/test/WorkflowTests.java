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

package uk.ac.bbsrc.tgac.miso.core.test;

import com.eaglegenomics.simlims.core.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.workflow.Workflow;
import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowDefinition;
import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowProcessDefinition;
import uk.ac.bbsrc.tgac.miso.core.workflow.impl.WorkflowDefinitionImpl;
import uk.ac.bbsrc.tgac.miso.core.workflow.impl.WorkflowImpl;
import uk.ac.bbsrc.tgac.miso.core.workflow.impl.WorkflowProcessDefinitionImpl;

import java.util.*;

/**
 * uk.ac.bbsrc.tgac.miso.core.test
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 26/09/11
 * @since 0.2.1-SNAPSHOT
 */
public class WorkflowTests {
  protected static final Logger log = LoggerFactory.getLogger(WorkflowTests.class);

  private Workflow w;

  @Before
  public void setUp() {
    WorkflowProcessDefinition wpd1 = new WorkflowProcessDefinitionImpl();
    wpd1.setId(1);
    wpd1.setName("MockWorkflowProcessDefinition1");
    wpd1.setDescription("A dummy workflow process definition");
    wpd1.setStateFields(new HashSet<>(Arrays.asList("field1", "field2")));
    wpd1.setInputType(Nameable.class);
    wpd1.setOutputType(Workflow.class);
    MockTypeProcessor tp = new MockTypeProcessor();
    wpd1.setTypeProcessor(tp);

    WorkflowProcessDefinition wpd2 = new WorkflowProcessDefinitionImpl();
    wpd2.setId(2);
    wpd2.setName("MockWorkflowProcessDefinition2");
    wpd2.setDescription("Another dummy workflow process definition");
    wpd2.setStateFields(new HashSet<>(Arrays.asList("field3", "field4")));

    WorkflowProcessDefinition wpd3 = new WorkflowProcessDefinitionImpl();
    wpd3.setId(3);
    wpd3.setName("MockWorkflowProcessDefinition3");
    wpd3.setDescription("Yet another dummy workflow process definition");
    wpd3.setStateFields(new HashSet<>(Arrays.asList("field5", "field6")));

    SortedMap<Integer, WorkflowProcessDefinition> processDefinitionMap = new TreeMap<>();
    processDefinitionMap.put(0, wpd1);
    processDefinitionMap.put(1, wpd2);
    processDefinitionMap.put(2, wpd3);

    WorkflowDefinition wd = new WorkflowDefinitionImpl(processDefinitionMap);

    w = new WorkflowImpl(wd, null);
    w.setId(1);

    User u1 = new UserImpl();
    u1.setUserId(1L);
    u1.setLoginName("Dummy User 1");

    w.setAssignee(u1);
  }

  @Test
  public void testWorkflow() throws MisoNamingException {
    log.info("Starting workflow: " + w.getId() + " assigned to " + w.getAssignee().getLoginName());

    if (w.peekNextProcess() != null) {
      int stage = w.getWorkflowDefinition().getProcessStage(w.peekNextProcess());
      if (stage != -1 && stage == 1) {
        boolean wasSuccessful = w.advanceWorkflow() != null;
        assert(wasSuccessful);
        log.info("Started new workflow ("+w.getWorkflowDefinition().getProcessStage(w.getCurrentProcess().getDefinition())+"/"+w.getWorkflowDefinition().getWorkflowProcessDefinitions().size()+") at "+w.getStartDate()+" ...");
      }

      log.info("Current process "+w.getWorkflowDefinition().getProcessStage(w.getCurrentProcess().getDefinition())+"/"+w.getWorkflowDefinition().getWorkflowProcessDefinitions().size()+": " + w.getCurrentProcess().getId() + " [" + w.getCurrentProcess().getDefinition().getName()+ "]");
      log.info("Workflow state:" + w.getState().toString());
      log.info("Process state:" + w.getCurrentProcess().getState().toString());

      log.info("Advancing...");
      boolean wasSuccessful = w.advanceWorkflow() != null;
      assert(wasSuccessful);

      log.info("Current process "+w.getWorkflowDefinition().getProcessStage(w.getCurrentProcess().getDefinition())+"/"+w.getWorkflowDefinition().getWorkflowProcessDefinitions().size()+": " + w.getCurrentProcess().getId() + " [" + w.getCurrentProcess().getDefinition().getName()+ "]");
      log.info("Workflow state:" + w.getState().toString());
      log.info("Process state:" + w.getCurrentProcess().getState().toString());

      log.info("Advancing...");
      wasSuccessful = w.advanceWorkflow() != null;
      assert(wasSuccessful);

      log.info("Current process "+w.getWorkflowDefinition().getProcessStage(w.getCurrentProcess().getDefinition())+"/"+w.getWorkflowDefinition().getWorkflowProcessDefinitions().size()+": " + w.getCurrentProcess().getId() + " [" + w.getCurrentProcess().getDefinition().getName()+ "]");
      log.info("Workflow state:" + w.getState().toString());
      log.info("Process state:" + w.getCurrentProcess().getState().toString());

      w.setStatus(HealthType.Completed);
      log.info("Completed workflow at "+w.getCompletionDate());
    }
    else {
      log.error("Workflow has no processes");
    }
  }

  @After
  public void tearDown() {
    w = null;
  }
}
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

package uk.ac.bbsrc.tgac.miso.analysis.manager;

//import com.fasterxml.jackson.core.JsonGenerationException;
//import com.fasterxml.jackson.databind.JsonMappingException;
//import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONObject;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.bbsrc.tgac.miso.analysis.submission.PipelineRequest;
import uk.ac.bbsrc.tgac.miso.analysis.submission.TaskSubmissionRequest;
import uk.ac.ebi.fgpt.conan.dao.ConanPipelineDAO;
import uk.ac.ebi.fgpt.conan.dao.XMLLoadingPipelineDAO;
import uk.ac.ebi.fgpt.conan.model.ConanPipeline;
import uk.ac.ebi.fgpt.conan.model.ConanProcess;
import uk.ac.ebi.fgpt.conan.model.ConanTask;

import uk.ac.ebi.fgpt.conan.model.ConanUser;
import uk.ac.ebi.fgpt.conan.service.*;
import uk.ac.ebi.fgpt.conan.service.exception.SubmissionException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * uk.ac.bbsrc.tgac.miso.analysis.manager
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 28/10/11
 * @since 0.1.2
 */
public class AnalysisRequestManager {
  protected static final Logger log = LoggerFactory.getLogger(AnalysisRequestManager.class);
  private ObjectMapper mapper = new ObjectMapper();

  @Autowired
  private ConanPipelineService conanPipelineService;

  @Autowired
  private ConanTaskService conanTaskService;

  @Autowired
  private ConanUserService conanUserService;

  @Autowired
  private ConanSubmissionService conanSubmissionService;

  @Autowired
  private ConanProcessService conanProcessService;

  public ConanPipelineService getConanPipelineService() {
    return conanPipelineService;
  }

  public void setConanPipelineService(ConanPipelineService conanPipelineService) {
    this.conanPipelineService = conanPipelineService;
  }

  public ConanTaskService getConanTaskService() {
    return this.conanTaskService;
  }

  public void setConanTaskService(ConanTaskService conanTaskService) {
    this.conanTaskService = conanTaskService;
  }

  public ConanUserService getConanUserService() {
    return conanUserService;
  }

  public void setConanUserService(ConanUserService conanUserService) {
    this.conanUserService = conanUserService;
  }

  public void setConanSubmissionService(ConanSubmissionService conanSubmissionService) {
    this.conanSubmissionService = conanSubmissionService;
  }

  public ConanProcessService getConanProcessService() {
    return conanProcessService;
  }

  public void setConanProcessService(ConanProcessService conanProcessService) {
    this.conanProcessService = conanProcessService;
  }

  public String queryTasks(JSONObject query) {
    String queryMethod = query.getString("query");
    try {
      Method m = getConanTaskService().getClass().getMethod(queryMethod);
      if ("getTask".equals(queryMethod)) {
        ConanTask<? extends ConanPipeline> task = (ConanTask<? extends ConanPipeline>)m.invoke(getConanTaskService());
        return mapper.writeValueAsString(task);
      }
      else {
        if (queryMethod.contains("search") || queryMethod.contains("create")) {
          throw new UnsupportedOperationException("Cannot call " +queryMethod+ ".");
        }
        else {
          List<ConanTask<? extends ConanPipeline>> tasks = (List<ConanTask<? extends ConanPipeline>>)m.invoke(getConanTaskService());
          return mapper.writeValueAsString(tasks);
        }
      }
    }
    catch (NoSuchMethodException e) {
      e.printStackTrace();
      return "ERROR: " + e.getMessage();
    }
    catch (InvocationTargetException e) {
      e.printStackTrace();
      return "ERROR: " + e.getMessage();
    }
    catch (IllegalAccessException e) {
      e.printStackTrace();
      return "ERROR: " + e.getMessage();
    }
    catch (JsonMappingException e) {
      e.printStackTrace();
      return "ERROR: " + e.getMessage();
    }
    catch (JsonGenerationException e) {
      e.printStackTrace();
      return "ERROR: " + e.getMessage();
    }
    catch (IOException e) {
      e.printStackTrace();
      return "ERROR: " + e.getMessage();
    }
  }

  //public void generateAndSubmitTask(TaskSubmissionRequest submissionRequest, User user) throws SubmissionException {
  public void generateAndSubmitTask(TaskSubmissionRequest submissionRequest) throws SubmissionException {
    ConanTask.Priority priority = ConanTask.Priority.valueOf(submissionRequest.getPriority());
    log.info(submissionRequest.toString());

    //reload pipelines at runtime - argh!
    if (conanPipelineService instanceof DefaultPipelineService) {
      ConanPipelineDAO c = ((DefaultPipelineService) conanPipelineService).getPipelineDAO();
      if (c instanceof XMLLoadingPipelineDAO) {
        ((XMLLoadingPipelineDAO)c).reset();
      }
    }

    ConanTask<? extends ConanPipeline> conanTask =
            conanTaskService.createNewTask(submissionRequest.getPipelineName(),
                                      submissionRequest.getStartingProcessIndex(),
                                      submissionRequest.getInputParameters(),
                                      priority,
                                      //TODO - don't hardcode in the username
                                      conanUserService.getUserByUserName("tgaclims"));

    // and submit the newly generated task
    conanSubmissionService.submitTask(conanTask);
  }

  //public String queryPipelines(JSONObject query, User user) {
  public String queryPipelines(JSONObject query) {
    String queryMethod = query.getString("query");
    log.info(query.toString());
    JSONObject params = query.getJSONObject("params");
    try {
      if ("getPipeline".equals(queryMethod)) {
        if (params == null || params.isNullObject() || params.isEmpty() || !params.has("name")) {
          throw new UnsupportedOperationException("Cannot call " +queryMethod+ " without a 'name' parameter.");
        }
        else {
          Method m = getConanPipelineService().getClass().getMethod(queryMethod, ConanUser.class, String.class);
          ConanPipeline pipeline = (ConanPipeline) m.invoke(getConanPipelineService(), getConanUserService().getUserByUserName("tgaclims"), params.getString("name"));
          return mapper.writeValueAsString(pipeline);
        }
      }
      else {
        if (queryMethod.contains("reorder") || queryMethod.contains("load") || queryMethod.contains("create")) {
          throw new UnsupportedOperationException("Cannot call " +queryMethod+ ".");
        }
        else {
          if (params == null || params.isNullObject() || params.isEmpty()) {
            Method m = getConanPipelineService().getClass().getMethod(queryMethod, ConanUser.class);
            List<ConanPipeline> pipelines = (List<ConanPipeline>)m.invoke(getConanPipelineService(), getConanUserService().getUserByUserName("tgaclims"));
            return mapper.writeValueAsString(pipelines);
          }
          else {
            throw new UnsupportedOperationException("Cannot call " +queryMethod+ ".");
          }
        }
      }
    }
    catch (NoSuchMethodException e) {
      e.printStackTrace();
      return "ERROR: " + e.getMessage();
    }
    catch (InvocationTargetException e) {
      e.printStackTrace();
      return "ERROR: " + e.getMessage();
    }
    catch (IllegalAccessException e) {
      e.printStackTrace();
      return "ERROR: " + e.getMessage();
    }
    catch (JsonMappingException e) {
      e.printStackTrace();
      return "ERROR: " + e.getMessage();
    }
    catch (JsonGenerationException e) {
      e.printStackTrace();
      return "ERROR: " + e.getMessage();
    }
    catch (IOException e) {
      e.printStackTrace();
      return "ERROR: " + e.getMessage();
    }
  }

  public void generateAndAddPipeline(PipelineRequest request) {
    List<ConanProcess> conanProcesses = new ArrayList<ConanProcess>();
    for (String processName : request.getProcesses()) {
      ConanProcess conanProcess = getConanProcessService().getProcess(processName);
      conanProcesses.add(conanProcess);
    }

    conanPipelineService.createPipeline(request.getName(),
                                        conanProcesses,
                                        getConanUserService().getUserByUserName("tgaclims"),
                                        request.isPrivate());
  }

}

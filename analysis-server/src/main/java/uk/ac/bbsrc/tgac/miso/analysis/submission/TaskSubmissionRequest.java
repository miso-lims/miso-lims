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

package uk.ac.bbsrc.tgac.miso.analysis.submission;

import java.util.Map;

/**
 * uk.ac.bbsrc.tgac.miso.analysis.submission
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 28/10/11
 * @since 0.1.2
 */
public class TaskSubmissionRequest {
  // submission request details
  private String priority;
  private String pipelineName;
  private int startingProcessIndex;
  private Map<String, String> inputParameters;

  // rest api key for this request
  private String restApiKey;

  /**
   * Default constructor to allow deserialization of JSON into a request bean: present to allow Jackson/spring to construct a request bean
   * from POST requests properly.
   */
  private TaskSubmissionRequest() {
  }

  public TaskSubmissionRequest(String priority, String pipelineName, Map<String, String> inputParameters) {
    this.priority = priority;
    this.pipelineName = pipelineName;
    this.inputParameters = inputParameters;
  }

  /**
   * Gets the priority of the task this request should create. High priority tasks should always be executed before medium priority tasks,
   * and medium priority tasks always executed before low priority ones.
   * 
   * @return the priority of this task
   */
  public String getPriority() {
    return priority;
  }

  /**
   * Sets the priority of this task. Takes a string value, which should match the value of
   * {@link uk.ac.ebi.fgpt.conan.model.ConanTask.Priority#toString()} for the priority you wish to use.
   * 
   * @param priority
   *          the string representation of this task's priority
   */
  public void setPriority(String priority) {
    this.priority = priority;
  }

  /**
   * Gets the pipeline that this task runs.
   * 
   * @return the pipeline this request should create a task for
   */
  public String getPipelineName() {
    return pipelineName;
  }

  /**
   * Set the name of the pipeline the task created by this request should run
   * 
   * @param pipelineName
   *          the pipeline this request should create a task for
   */
  public void setPipelineName(String pipelineName) {
    this.pipelineName = pipelineName;
  }

  /**
   * Gets the index of the first process to execute in this pipeline. This is present so users can resume create a new task that skips
   * processes that are known to have completed previously - effectively skipping earlier processes.
   * 
   * @return the name of the first process to execute
   */
  public int getStartingProcessIndex() {
    return startingProcessIndex;
  }

  /**
   * Sets the index of the first process from the pipeline to execute. If this isn't the same as the name of the first process in the
   * pipeline, processes will be skipped.
   * 
   * @param startingProcessIndex
   *          the name of the process to start the task at
   */
  public void setStartingProcessIndex(int startingProcessIndex) {
    this.startingProcessIndex = startingProcessIndex;
  }

  /**
   * Returns a map of parameters to their values that this request should set for on the resulting Task.
   * 
   * @return all supplied parameter values
   */
  public Map<String, String> getInputParameters() {
    return inputParameters;
  }

  /**
   * Sets the map of input parameters to the supplied values for this task.
   * 
   * @param inputParameters
   *          the set of parameters supplied, mapped from parameter name to value
   */
  public void setInputParameters(Map<String, String> inputParameters) {
    this.inputParameters = inputParameters;
  }

  /**
   * Gets the rest api key of the user supplying this request.
   * 
   * @return the rest api key
   */
  public String getRestApiKey() {
    return restApiKey;
  }

  /**
   * Sets the rest api key of the user supplying this request.
   * 
   * @param restApiKey
   *          the users rest api key
   */
  public void setRestApiKey(String restApiKey) {
    this.restApiKey = restApiKey;
  }

  @Override
  public String toString() {
    return "SubmissionRequest: " + "pipelineName='" + pipelineName + "', " + "startingProcessIndex='" + startingProcessIndex + "', "
        + "inputParameters=" + inputParameters + "', " + "priority='" + priority + "'";
  }
}

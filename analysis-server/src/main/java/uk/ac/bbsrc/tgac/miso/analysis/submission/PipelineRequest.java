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

import java.util.List;

/**
 * uk.ac.bbsrc.tgac.miso.analysis.submission
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 08/11/11
 * @since 0.1.3
 */
public class PipelineRequest {
  // pipeline details
  private final String name;
  private final List<String> processDescriptions;
  private final boolean isPrivate;

  // the rest api key to access this service
  private final String restApiKey;

  public PipelineRequest(String name, List<String> processDescriptions, String restApiKey) {
    this(name, processDescriptions, false, restApiKey);
  }

  public PipelineRequest(String name, List<String> processDescriptions, boolean isPrivate, String restApiKey) {
    this.name = name;
    this.restApiKey = restApiKey;
    this.processDescriptions = processDescriptions;

    this.isPrivate = isPrivate;
  }

  /**
   * Gets the name of the new pipeline being requested
   * 
   * @return the pipeline name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the rest api key of the user that is requesting the creation of the new pipeline
   * 
   * @return the rest api key of the creator of this pipeline request
   */
  public String getRestApiKey() {
    return restApiKey;
  }

  /**
   * Gets a list of string arrays representing process name/type pairs. Each element in the list should be a string array with a length of
   * 2, where the element at index 0 is the process name and the element at index 1 is the string representing the process type.
   * 
   * @return a simple representation of the processes to recover when creating this pipeline
   */
  public List<String> getProcesses() {
    return processDescriptions;
  }

  /**
   * Gets whether or not this pipeline should be made public on creation.
   * 
   * @return whether this pipeline is public
   */
  public boolean isPrivate() {
    return isPrivate;
  }

  @Override
  public String toString() {
    return "PipelineRequest: " + "name='" + name + "', " + "processes={" + processDescriptions + "}, " + "isPrivate=" + isPrivate + "}";
  }
}

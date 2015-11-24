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

package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.List;

import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import uk.ac.bbsrc.tgac.miso.core.exception.MalformedRunException;

/**
 * A QC that is specifically carried out on a given {@link uk.ac.bbsrc.tgac.miso.core.data.Run}
 * 
 * @author Rob Davey
 * @since 0.0.3
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC, include = JsonSerialize.Inclusion.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface RunQC extends QC {
  /**
   * Returns the run of this RunQC object.
   * 
   * @return Run run.
   */
  @JsonBackReference(value = "qcrun")
  public Run getRun();

  /**
   * Sets the run of this RunQC object.
   * 
   * @param run
   *          Run.
   * @throws uk.ac.bbsrc.tgac.miso.core.exception.MalformedRunException
   *           when the Run being set is not valid
   */
  public void setRun(Run run) throws MalformedRunException;

  /**
   * Get a free text description about the QC carried out
   * 
   * @return information String.
   */
  public String getInformation();

  /**
   * Set a free text description about the QC carried out
   * 
   * @param information
   *          String.
   */
  public void setInformation(String information);

  /**
   * Returns a boolean describing if this run should be processed via some form of primary analysis
   * 
   * @return boolean doNotProcess
   */
  public boolean getDoNotProcess();

  /**
   * Sets a flag describing if this run should be processed via some form of primary analysis
   * 
   * @param doNotProcess
   *          boolean.
   */
  public void setDoNotProcess(boolean doNotProcess);

  /**
   * Returns a list of partitions that should be processed as part of a downstream primary analysis
   * 
   * @return List<Partition> partitionSelections
   */
  public List<Partition> getPartitionSelections();

  /**
   * Sets a list of partitions that should be processed as part of a downstream primary analysis
   * 
   * @param partitionSelections
   *          List<Partition>
   */
  public void setPartitionSelections(List<Partition> partitionSelections);
}

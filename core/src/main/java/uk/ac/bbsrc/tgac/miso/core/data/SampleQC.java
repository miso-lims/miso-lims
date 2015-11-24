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

import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import uk.ac.bbsrc.tgac.miso.core.exception.MalformedSampleException;

/**
 * A QC that is specifically carried out on a given {@link Sample}
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC, include = JsonSerialize.Inclusion.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface SampleQC extends QC {
  /**
   * Returns the sample of this SampleQC object.
   * 
   * @return Sample sample.
   */
  @JsonBackReference(value = "qcsample")
  public Sample getSample();

  /**
   * Sets the sample of this SampleQC object.
   * 
   * @param sample
   *          sample.
   * @throws MalformedSampleException
   *           when the Sample being set is not valid
   */
  public void setSample(Sample sample) throws MalformedSampleException;

  /**
   * Returns the results of this QC object.
   * 
   * @return Double results.
   */
  public Double getResults();

  /**
   * Sets the results of this QC object.
   * 
   * @param results
   *          results.
   */
  public void setResults(Double results);
}

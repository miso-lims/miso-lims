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

import uk.ac.bbsrc.tgac.miso.core.exception.MalformedSampleException;

/**
 * Skeleton implementation of a SampleQC
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public abstract class AbstractSampleQC extends AbstractQC implements SampleQC {
  public static final String UNITS = "ng/&#181;l";

  private Double results;
  private Sample sample;

  @Override
  public Sample getSample() {
    return sample;
  }

  @Override
  public void setSample(Sample sample) throws MalformedSampleException {
    this.sample = sample;
  }

  @Override
  public Double getResults() {
    return results;
  }

  @Override
  public void setResults(Double results) {
    this.results = results;
  }

  /**
   * Equivalency is based on getRunId() if set, otherwise on name
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof AbstractSampleQC)) return false;
    SampleQC them = (SampleQC) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    if (this.getId() == AbstractQC.UNSAVED_ID || them.getId() == AbstractQC.UNSAVED_ID) {
      return this.getQcCreator().equals(them.getQcCreator()) && this.getQcDate().equals(them.getQcDate())
          && this.getQcType().equals(them.getQcType()) && this.getResults().equals(them.getResults());
    } else {
      return this.getId() == them.getId();
    }
  }

  @Override
  public int hashCode() {
    if (getId() != AbstractQC.UNSAVED_ID) {
      return (int) getId();
    } else {
      int hashcode = getQcCreator().hashCode();
      hashcode = 37 * hashcode + getQcDate().hashCode();
      hashcode = 37 * hashcode + getQcType().hashCode();
      hashcode = 37 * hashcode + getResults().hashCode();
      return hashcode;
    }
  }
}

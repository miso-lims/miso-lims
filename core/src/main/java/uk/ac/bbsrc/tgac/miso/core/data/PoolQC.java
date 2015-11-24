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

import uk.ac.bbsrc.tgac.miso.core.exception.MalformedPoolException;

/**
 * A QC that is specifically carried out on a given {@link uk.ac.bbsrc.tgac.miso.core.data.Pool}
 * 
 * @author Rob Davey
 * @since 0.1.9
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface PoolQC extends QC {
  /**
   * Returns the pool of this PoolQC object.
   * 
   * @return Pool pool.
   */
  @JsonBackReference(value = "qcpool")
  public Pool getPool();

  /**
   * Sets the pool of this PoolQC object.
   * 
   * @param pool
   *          Pool.
   * @throws uk.ac.bbsrc.tgac.miso.core.exception.MalformedPoolException
   *           when the Pool being set is not valid
   */
  public void setPool(Pool pool) throws MalformedPoolException;

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

/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import com.eaglegenomics.simlims.core.Securable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;

/**
 * Definition of a Status object, used to hold metadata about the current status of a particular activity
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface Status extends Securable, Serializable {
  /**
   * Returns the statusId of this Status object.
   * 
   * @return Long statusId.
   */
  public Long getId();

  /**
   * Sets the statusId of this Status object.
   * 
   * @param statusId
   *          statusId.
   */
  public void setId(Long statusId);

  /**
   * Returns the XML status string of this Status object. Illumina for example produces Status.xml files. If the platform doesn't supply xml
   * files, this field is null.
   * 
   * @return String xml.
   * @throws UnsupportedEncodingException
   */
  public String getXml() throws UnsupportedEncodingException;

  /**
   * Sets the underlying XML status string of this Status object. Illumina for example produces Status.xml files.
   * 
   * @param xml
   *          String.
   * @throws UnsupportedEncodingException
   */
  public void setXml(String xml) throws UnsupportedEncodingException;

  /**
   * Returns the health of this Status object.
   * 
   * @return HealthType health.
   */
  public HealthType getHealth();

  /**
   * Sets the health of this Status object.
   * 
   * @param health
   *          health.
   */
  public void setHealth(HealthType health);

  /**
   * Returns the startDate of this Status object.
   * 
   * @return Date startDate.
   */
  public Date getStartDate();

  /**
   * Sets the startDate of this Status object.
   * 
   * @param startDate
   *          startDate.
   */
  public void setStartDate(Date startDate);

  /**
   * Returns the completionDate of this Status object.
   * 
   * @return Date completionDate.
   */
  public Date getCompletionDate();

  /**
   * Sets the completionDate of this Status object.
   * 
   * @param completionDate
   *          completionDate.
   */
  public void setCompletionDate(Date completionDate);

  public String getInstrumentName();

  public void setInstrumentName(String instrumentName);

  public String getRunAlias();

  public void setRunAlias(String runName);

  public Date getLastUpdated();

  public void setLastUpdated(Date lastUpdated);
}
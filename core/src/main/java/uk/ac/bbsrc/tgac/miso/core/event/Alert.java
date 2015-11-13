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

package uk.ac.bbsrc.tgac.miso.core.event;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.event.type.AlertLevel;

/**
 * Interface describing an alert that can be raised, usually as a result of an {@link Event}
 * 
 * @author Rob Davey
 * @date 22-Sep-2011
 * @since 0.1.2
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC, include = JsonSerialize.Inclusion.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface Alert extends Comparable, Deletable {
  /**
   * Returns the alertId of this Alert object.
   * 
   * @return Long alertId.
   */
  public Long getAlertId();

  /**
   * Sets the alertId of this Alert object.
   * 
   * @param alertId
   *          Long.
   */
  public void setAlertId(Long alertId);

  public String getAlertTitle();

  public void setAlertTitle(String title);

  public String getAlertText();

  public void setAlertText(String alert);

  public User getAlertUser();

  public Date getAlertDate();

  public void setAlertDate(Date date);

  public boolean getAlertRead();

  public void setAlertRead(boolean alertRead);

  public AlertLevel getAlertLevel();

  public void setAlertLevel(AlertLevel alertLevel);
}

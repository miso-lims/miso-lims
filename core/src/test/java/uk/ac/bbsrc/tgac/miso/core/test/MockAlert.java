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
import uk.ac.bbsrc.tgac.miso.core.event.Alert;
import uk.ac.bbsrc.tgac.miso.core.event.type.AlertLevel;

import java.util.Date;

/**
 * uk.ac.bbsrc.tgac.miso.core.test
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 29/09/11
 * @since 0.1.2
 */
public class MockAlert implements Alert {
  private long alertId;
  private String alertTitle;
  private String alertText;
  private User user;
  private Date alertDate;
  private boolean alertRead;
  private AlertLevel alertLevel;

  public MockAlert(User user) {
    this(user, AlertLevel.INFO);
  }

  public MockAlert(User user, AlertLevel alertLevel) {
    this.user = user;
    this.alertDate = new Date();
    this.alertLevel = alertLevel;
    this.alertText = "[" + getAlertUser().getFullName() + "] Mock alert text";
  }

  @Override
  public Long getAlertId() {
    return 0L;
  }

  @Override
  public void setAlertId(Long alertId) {
    this.alertId = alertId;
  }

  @Override
  public String getAlertTitle() {
    return alertTitle;
  }

  @Override
  public void setAlertTitle(String alertTitle) {
    this.alertTitle = alertTitle;
  }

  @Override
  public String getAlertText() {
    return alertText;
  }

  @Override
  public void setAlertText(String alertText) {
    this.alertText = alertText;
  }

  @Override
  public User getAlertUser() {
    return this.user;
  }

  @Override
  public Date getAlertDate() {
    return alertDate;
  }

  @Override
  public void setAlertDate(Date alertDate) {
    this.alertDate = alertDate;
  }

  @Override
  public boolean getAlertRead() {
    return false;
  }

  @Override
  public void setAlertRead(boolean alertRead) {
    this.alertRead = alertRead;
  }

  @Override
  public AlertLevel getAlertLevel() {
    return alertLevel;
  }

  @Override
  public void setAlertLevel(AlertLevel alertLevel) {
    this.alertLevel = alertLevel;
  }

  @Override
  public int compareTo(Object o) {
    Alert a = (Alert) o;
    if (getAlertId() != null && a.getAlertId() != null) {
      if (getAlertId() < a.getAlertId()) return -1;
      if (getAlertId() > a.getAlertId()) return 1;
    }
    return 0;
  }

  @Override
  public boolean isDeletable() {
    return true;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getAlertId());
    sb.append(" : ");
    sb.append(getAlertUser().getFullName());
    sb.append(" : ");
    sb.append(getAlertText());
    sb.append(" : ");
    sb.append(getAlertDate());
    return sb.toString();
  }
}

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

package uk.ac.bbsrc.tgac.miso.core.event.impl;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.event.Alert;
import uk.ac.bbsrc.tgac.miso.core.event.type.AlertLevel;

/**
 * uk.ac.bbsrc.tgac.miso.core.event.impl
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 07/10/11
 * @since 0.1.2
 */

@Entity
@Table(name = "Alert")
public class DefaultAlert implements Alert, Serializable {

  private static final long serialVersionUID = 1L;
  public static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long alertId = UNSAVED_ID;

  @Column(name = "title", nullable = true)
  private String alertTitle;

  @Column(name = "text", nullable = false)
  private String alertText;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "userId", nullable = false)
  private User user;

  @Column(name = "date", nullable = false)
  @Temporal(TemporalType.DATE)
  private Date alertDate;

  @Column(name = "isRead", nullable = false)
  private boolean alertRead = Boolean.FALSE;

  @Enumerated(EnumType.STRING)
  @Column(name = "level", nullable = false)
  private AlertLevel alertLevel = AlertLevel.INFO;

  public DefaultAlert() {
    this.alertDate = new Date();
  }

  public DefaultAlert(User user) {
    this(user, AlertLevel.INFO);
  }

  public DefaultAlert(User user, AlertLevel alertLevel) {
    this.user = user;
    this.alertDate = new Date();
    this.alertLevel = alertLevel;
  }

  @Override
  public Long getAlertId() {
    return alertId;
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
  public void setAlertUser(User user) {
    this.user = user;
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
    return alertRead;
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
  public boolean isDeletable() {
    return true;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getAlertId());
    sb.append(" : ");
    if (getAlertUser() != null) {
      sb.append(getAlertUser().getFullName());
      sb.append(" : ");
    }
    sb.append(getAlertTitle());
    sb.append(" : ");
    sb.append(getAlertText());
    sb.append(" : ");
    sb.append(getAlertDate());
    sb.append(" : ");
    sb.append(getAlertRead());
    return sb.toString();
  }

  @Override
  public int compareTo(Alert o) {
    if (o.getAlertId() != null) {
      if (this.alertId < o.getAlertId()) return -1;
      if (this.alertId > o.getAlertId()) return 1;
    }
    return 0;
  }

}

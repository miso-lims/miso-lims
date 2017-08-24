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
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;

@MappedSuperclass
public abstract class QC implements Serializable {
  private static final long serialVersionUID = 1L;

  public static final Long UNSAVED_ID = 0L;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "creator")
  private User creator;

  @Temporal(TemporalType.DATE)
  private Date date = new Date();

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long qcId = QC.UNSAVED_ID;

  private Double results;

  @ManyToOne
  @JoinColumn(name = "type")
  private QcType type;

  public User getCreator() {
    return creator;
  }

  public Date getDate() {
    return date;
  }

  public abstract QualityControlEntity getEntity();

  public long getId() {
    return qcId;
  }

  public Double getResults() {
    return results;
  }

  public QcType getType() {
    return type;
  }

  public void setCreator(User creator) {
    this.creator = creator;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public void setId(long qcId) {
    this.qcId = qcId;
  }

  public void setResults(Double results) {
    this.results = results;
  }

  public void setType(QcType type) {
    this.type = type;
  }
}

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

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.eaglegenomics.simlims.core.User;

@Entity
@Table(name = "RunPacBio")
public class PacBioRun extends Run {
  private Long movieDuration;

  private String wellName;

  @Temporal(TemporalType.DATE)
  private Date creationDate;

  public PacBioRun(User user) {
    super(user);
  }

  public PacBioRun() {
    super();
  }

  public Long getMovieDuration() {
    return movieDuration;
  }

  public String getWellName() {
    return wellName;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setMovieDuration(Long movieDuration) {
    this.movieDuration = movieDuration;
  }

  public void setWellName(String wellName) {
    this.wellName = wellName;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }
}

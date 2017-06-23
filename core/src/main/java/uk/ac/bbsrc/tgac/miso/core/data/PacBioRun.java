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

import javax.persistence.Entity;
import javax.persistence.Table;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

@Entity
@Table(name = "RunPacBio")
public class PacBioRun extends Run {
  private static final long serialVersionUID = 1L;

  private Integer movieDuration;

  public PacBioRun(User user) {
    super(user);
  }

  public PacBioRun() {
    super();
  }

  public Integer getMovieDuration() {
    return movieDuration;
  }

  public void setMovieDuration(Integer movieDuration) {
    this.movieDuration = movieDuration;
  }

  @Override
  public PlatformType getPlatformType() {
    return PlatformType.PACBIO;
  }

}

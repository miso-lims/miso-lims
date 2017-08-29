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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

@Entity
@Table(name = "RunIllumina")
public class IlluminaRun extends Run {
  private static final long serialVersionUID = 1L;

  public IlluminaRun() {
    super();
  }

  public IlluminaRun(User user) {
    super(user);
  }

  private Integer callCycle;
  private Integer imgCycle;
  private Integer numCycles;
  private Integer scoreCycle;
  @Column(nullable = false)
  private boolean pairedEnd = true;

  public Integer getCallCycle() {
    return callCycle;
  }

  public void setCallCycle(Integer callCycle) {
    this.callCycle = callCycle;
  }

  public Integer getImgCycle() {
    return imgCycle;
  }

  public void setImgCycle(Integer imgCycle) {
    this.imgCycle = imgCycle;
  }

  public Integer getNumCycles() {
    return numCycles;
  }

  public void setNumCycles(Integer numCycles) {
    this.numCycles = numCycles;
  }

  public Integer getScoreCycle() {
    return scoreCycle;
  }

  public void setScoreCycle(Integer scoreCycle) {
    this.scoreCycle = scoreCycle;
  }

  @Override
  public Boolean getPairedEnd() {
    return pairedEnd;
  }

  @Override
  public void setPairedEnd(boolean pairedEnd) {
    this.pairedEnd = pairedEnd;
  }

  @Override
  public PlatformType getPlatformType() {
    return PlatformType.ILLUMINA;
  }

}
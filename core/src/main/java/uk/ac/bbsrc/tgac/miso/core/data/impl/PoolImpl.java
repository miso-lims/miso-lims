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

package uk.ac.bbsrc.tgac.miso.core.data.impl;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractPool;
import uk.ac.bbsrc.tgac.miso.core.data.Dilution;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Poolable;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

/**
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public class PoolImpl<D extends Poolable> extends AbstractPool<D> {
  public static final String PREFIX = "MPO";

  private String units = "";
  private PlatformType platformType;

  public PoolImpl() {
    setSecurityProfile(new SecurityProfile());
  }

  public PoolImpl(User user) {
    setSecurityProfile(new SecurityProfile(user));
  }

  public String getUnits() {
    return this.units;
  }

  public void setUnits(String units) {
    this.units = units;
  }

  @Override
  public PlatformType getPlatformType() {
    return platformType;
  }

  @Override
  public void setPlatformType(PlatformType platformType) {
    this.platformType = platformType;
  }

  /**
   * Equivalency is based on getProjectId() if set, otherwise on name,
   * description and creation date.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (!(obj instanceof Pool))
      return false;
    Pool them = (Pool) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    if (getId() == AbstractPool.UNSAVED_ID
        || them.getId() == AbstractPool.UNSAVED_ID) {
      return getCreationDate().equals(them.getCreationDate()); //&& this.getDescription().equals(them.getDescription());
    }
    else {
      return getId() == them.getId();
    }
  }

  @Override
  public int hashCode() {
    if (getId() != AbstractPool.UNSAVED_ID) {
      return (int)getId();
    }
    else {
      return getCreationDate().hashCode();
    }
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(getName());
    sb.append(" : ");
    sb.append(getAlias());
    sb.append(" : ");
    sb.append(getCreationDate());
    sb.append(" : ");
    sb.append(getConcentration());
    return sb.toString();
  }

  public int compareTo(Object o) {
    return this.equals(o) ? 0 : 1;
  }
}

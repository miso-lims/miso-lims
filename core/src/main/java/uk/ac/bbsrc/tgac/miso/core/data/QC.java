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

import java.util.Date;

import com.eaglegenomics.simlims.core.Securable;

import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;

/**
 * A QC represents a validation step carried out on a given model object, e.g. a {@link Library} via a {@link LibraryQC}, a {@link Sample}
 * via a {@link SampleQC}, or a {@link Run} via a {@link RunQC}
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface QC extends Securable, Comparable, Deletable {
  public long getId();

  public void setId(long id);

  /**
   * Returns the qcId of this QC object.
   * 
   * @return Long qcId.
   */
  @Deprecated
  public Long getQcId();

  /**
   * Sets the qcId of this QC object.
   * 
   * @param qcId
   *          qcId.
   */
  @Deprecated
  public void setQcId(Long qcId);

  /**
   * Returns the qcCreator of this QC object.
   * 
   * @return String qcCreator.
   */
  public String getQcCreator();

  /**
   * Sets the qcCreator of this QC object.
   * 
   * @param creator
   *          qcCreator.
   */
  public void setQcCreator(String creator);

  /**
   * Returns the qcMethod of this QC object.
   * 
   * @return String qcMethod.
   */
  public QcType getQcType();

  /**
   * Sets the QcType of this QC object.
   * 
   * @param type
   *          type.
   */
  public void setQcType(QcType type);

  /**
   * Returns the qcDate of this QC object.
   * 
   * @return Date qcDate.
   */
  public Date getQcDate();

  /**
   * Sets the qcDate of this QC object.
   * 
   * @param date
   *          qcDate.
   */
  public void setQcDate(Date date);
}

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

import org.codehaus.jackson.annotate.JsonWriteNullProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

/**
 * TagBarcodes represent adapter sequences that can be prepended to sequencable material in order to facilitate
 * multiplexing.
 *
 * @author Rob Davey
 * @date 10-May-2011
 * @since 0.0.3
 */

@JsonSerialize(typing = JsonSerialize.Typing.STATIC)
@JsonWriteNullProperties(false)
public interface TagBarcode extends Comparable {
  /**
   * Returns the tagId of this TagBarcode object.
   * @return Long tagId
   */
  public Long getTagBarcodeId();

  /**
   * Sets the tagBarcodeId of this TagBarcode object.
   * @param tagId Long
   */
  public void setTagBarcodeId(Long tagId);

  /**
   * Returns the name of this TagBarcode object.
   * @return
   */
  public String getName();

  /**
   * Sets the name of this TagBarcode object.
   * @param name
   */
  public void setName(String name);

  /**
   * Returns the DNA sequence of this TagBarcode object.
   * @return
   */
  public String getSequence();

  /**
   * Sets the DNA sequence of this TagBarcode object.
   * @param sequence
   */
  public void setSequence(String sequence);

  /**
   * Returns the {@link PlatformType} of this TagBarcode object.
   * @return
   */
  public PlatformType getPlatformType();

  /**
   * Sets the {@link PlatformType} of this TagBarcode object.
   * @param platformType
   */
  public void setPlatformType(PlatformType platformType);

  /**
   * Returns the strategy name to which this TagBarcode belongs.
   * @return String strategyName
   */
  public String getStrategyName();

  /**
   * Sets the strategy name to which this TagBarcode belongs.
   * @param strategyName String
   */
  public void setStrategyName(String strategyName);
}

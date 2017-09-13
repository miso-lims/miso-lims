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
import java.util.Set;
import java.util.TreeSet;

/**
 * This interface simply describes an object that can be barcoded to denote its identity, i.e. have an identification String that represents
 * a scannable barcode. For physical barcode printing purposes, Barcodable objects can be assigned names and label text fields which can be
 * made up of existing object fields to aid with barcode label generation.
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface Barcodable extends Nameable, Aliasable {
  /**
   * Returns the label text of this Barcodable object.
   * 
   * @return String labelText.
   */
  public String getLabelText();

  /**
   * Returns the identificationBarcode of this Barcodable object.
   * 
   * @return String identificationBarcode.
   */
  public String getIdentificationBarcode();

  /**
   * The date, if any, that should appear on the label.
   */
  public Date getBarcodeDate();

  /**
   * Supplemental description displayed on larger labels.
   */
  public String getBarcodeExtraInfo();

  /**
   * Sets the identificationBarcode of this Barcodable object.
   * 
   * @param identificationBarcode
   *          identificationBarcode.
   */
  public void setIdentificationBarcode(String identificationBarcode);

  public static <T extends Barcodable> Set<String> extractLabels(Iterable<T> items) {
    Set<String> labels = new TreeSet<>();
    for (T item : items) {
      labels.add(item.getLabelText());
    }
    return labels;
  }
}

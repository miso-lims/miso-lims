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

package uk.ac.bbsrc.tgac.miso.core.data.type;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * uk.ac.bbsrc.tgac.miso.core.data.type
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 01-Aug-2011
 * @since 0.0.3
 */
public enum PlateMaterialType {
  BACTERIA("Bacteria"), DNA("DNA"), RAD("RAD"), NEXTERA("Nextera");

  /** Field key */
  private String key;

  /** Field lookup */
  private static final Map<String, PlateMaterialType> lookup = new HashMap<String, PlateMaterialType>();

  static {
    for (PlateMaterialType s : EnumSet.allOf(PlateMaterialType.class))
      lookup.put(s.getKey(), s);
  }

  /**
   * Constructs a PlateMaterialType based on a given key
   * 
   * @param key
   *          of type String
   */
  PlateMaterialType(String key) {
    this.key = key;
  }

  /**
   * Returns a SubmissionActionType given an enum key
   * 
   * @param key
   *          of type String
   * @return SubmissionActionType
   */
  public static PlateMaterialType get(String key) {
    return lookup.get(key);
  }

  /**
   * Returns the key of this PlateMaterialType enum.
   * 
   * @return String key.
   */
  public String getKey() {
    return key;
  }

  /**
   * Returns the keys of this PlateMaterialType enum.
   * 
   * @return ArrayList<String> keys.
   */
  public static ArrayList<String> getKeys() {
    ArrayList<String> keys = new ArrayList<String>();
    for (PlateMaterialType h : PlateMaterialType.values()) {
      keys.add(h.getKey());
    }
    return keys;
  }
}

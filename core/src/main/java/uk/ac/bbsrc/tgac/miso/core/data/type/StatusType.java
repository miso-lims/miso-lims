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

package uk.ac.bbsrc.tgac.miso.core.data.type;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * This enum represents status stages that any monitored process can utilise.
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public enum StatusType {
  UNKNOWN("Unknown"), ACTIVE("Active"), INACTIVE("Inactive"), CANCELLED("Cancelled"), PROPOSED("Proposed"), PENDING("Pending"), APPROVED(
      "Approved"), COMPLETED("Completed");

  /**
   * Field key
   */
  private String key;
  /**
   * Field lookup
   */
  private static final Map<String, StatusType> lookup = new HashMap<>();

  static {
    for (StatusType s : EnumSet.allOf(StatusType.class))
      lookup.put(s.getKey(), s);
  }

  /**
   * Constructs a StatusType based on a given key
   * 
   * @param key
   *          of type String
   */
  StatusType(String key) {
    this.key = key;
  }

  /**
   * Returns a StatusType given an enum key
   * 
   * @param key
   * @return StatusType
   */
  public static StatusType get(String key) {
    return lookup.get(key);
  }

  /**
   * @return the key of this StatusType value
   */
  public String getKey() {
    return key;
  }

  /**
   * @return the keys of this StatusType enum
   */
  public static ArrayList<String> getKeys() {
    ArrayList<String> keys = new ArrayList<>();
    for (StatusType h : StatusType.values()) {
      keys.add(h.getKey());
    }
    return keys;
  }
}

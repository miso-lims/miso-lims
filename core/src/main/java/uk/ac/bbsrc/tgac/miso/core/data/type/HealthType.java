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
 * This enum represents the health of a particular object, given some kind of underlying process
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public enum HealthType {
  Unknown("Unknown"), Completed("Completed"), Failed("Failed"), Started("Started"), Stopped("Stopped"), Running("Running");

  /** Field key */
  private String key;
  /**
   * Field lookup
   */
  private static final Map<String, HealthType> lookup = new HashMap<String, HealthType>();

  static {
    for (HealthType s : EnumSet.allOf(HealthType.class))
      lookup.put(s.getKey(), s);
  }

  /**
   * Constructs a HealthType based on a given key
   * 
   * @param key
   *          of type String
   */
  HealthType(String key) {
    this.key = key;
  }

  /**
   * Returns a HealthType given an enum key
   * 
   * @param key
   *          of type String
   * @return HealthType
   */
  public static HealthType get(String key) {
    return lookup.get(key);
  }

  /**
   * Returns the key of this HealthType enum.
   * 
   * @return String key.
   */
  public String getKey() {
    return key;
  }

  /**
   * Returns the keys of this HealthType enum.
   * 
   * @return ArrayList<String> keys.
   */
  public static ArrayList<String> getKeys() {
    ArrayList<String> keys = new ArrayList<String>();
    for (HealthType h : HealthType.values()) {
      keys.add(h.getKey());
    }
    return keys;
  }
}

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

package uk.ac.bbsrc.tgac.miso.core.event.type;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * uk.ac.bbsrc.tgac.miso.core.alert.type
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 22-Sep-2011
 * @since 0.1.2
 */
public enum AlertLevel {
  INFO("INFO"), LOW("LOW"), MEDIUM("MEDIUM"), HIGH("HIGH"), CRITICAL("CRITICAL");

  /**
   * Field key
   */
  private String key;
  /**
   * Field lookup
   */
  private static final Map<String, AlertLevel> lookup = new HashMap<String, AlertLevel>();

  static {
    for (AlertLevel s : EnumSet.allOf(AlertLevel.class))
      lookup.put(s.getKey(), s);
  }

  /**
   * Constructs a AlertLevel based on a given key
   * 
   * @param key
   *          of type String
   */
  AlertLevel(String key) {
    this.key = key;
  }

  /**
   * Returns a AlertLevel given an enum key
   * 
   * @param key
   *          of type String
   * @return AlertType
   */
  public static AlertLevel get(String key) {
    return lookup.get(key);
  }

  /**
   * Returns the key of this AlertLevel enum.
   * 
   * @return String key.
   */
  public String getKey() {
    return key;
  }

  /**
   * Returns the keys of this AlertLevel enum.
   * 
   * @return ArrayList<String> keys.
   */
  public static ArrayList<String> getKeys() {
    ArrayList<String> keys = new ArrayList<String>();
    for (AlertLevel h : AlertLevel.values()) {
      keys.add(h.getKey());
    }
    return keys;
  }

}

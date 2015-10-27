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
 * Enum representing all potential submission action types. These types should match the SRA submission schema for submission actions.
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public enum SubmissionActionType {
  ADD("ADD"), MODIFY("MODIFY"), SUPPRESS("SUPPRESS"), HOLD("HOLD"), RELEASE("RELEASE"), CLOSE("CLOSE"), PROTECT("PROTECT"), VALIDATE(
      "VALIDATE");

  /** Field key */
  private String key;
  /** Field lookup */
  private static final Map<String, SubmissionActionType> lookup = new HashMap<String, SubmissionActionType>();

  static {
    for (SubmissionActionType s : EnumSet.allOf(SubmissionActionType.class))
      lookup.put(s.getKey(), s);
  }

  /**
   * Constructs a SubmissionActionType based on a given key
   * 
   * @param key
   *          of type String
   */
  SubmissionActionType(String key) {
    this.key = key;
  }

  /**
   * Returns a SubmissionActionType given an enum key
   * 
   * @param key
   *          of type String
   * @return SubmissionActionType
   */
  public static SubmissionActionType get(String key) {
    return lookup.get(key);
  }

  /**
   * Returns the key of this SubmissionActionType object.
   * 
   * @return String key.
   */
  public String getKey() {
    return key;
  }

  /**
   * Returns the keys of this SubmissionActionType object.
   * 
   * @return ArrayList<String> keys.
   */
  public static ArrayList<String> getKeys() {
    ArrayList<String> keys = new ArrayList<String>();
    for (SubmissionActionType h : SubmissionActionType.values()) {
      keys.add(h.getKey());
    }
    return keys;
  }
}

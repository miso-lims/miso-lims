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
import java.util.Comparator;
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
  Unknown("Unknown", 0, true, false), //
  Completed("Completed", -1, true, true), //
  Failed("Failed", 0, true, true), //
  Started("Started", -1, true, false), //
  Stopped("Stopped", -1, true, true), //
  Running("Running", -1, true, false), //
  Requested("Requested", 1, false, false);

  public static final Comparator<HealthType> COMPARATOR = new Comparator<HealthType>() {
    @Override
    public int compare(HealthType o1, HealthType o2) {
      int p1 = o1 == null ? -1 : o1.ordinal();
      int p2 = o2 == null ? -1 : o2.ordinal();
      return p1 - p2;
    }
  };
  /**
   * Field lookup
   */
  private static final Map<String, HealthType> lookup = new HashMap<>();

  static {
    for (HealthType s : EnumSet.allOf(HealthType.class))
      lookup.put(s.getKey(), s);
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
   * Returns the keys of this HealthType enum.
   * 
   * @return ArrayList<String> keys.
   */
  public static ArrayList<String> getKeys() {
    ArrayList<String> keys = new ArrayList<>();
    for (HealthType h : HealthType.values()) {
      keys.add(h.getKey());
    }
    return keys;
  }

  /** Field key */
  private final String key;

  private final int multiplier;

  private final boolean allowedFromSequencer;

  private final boolean isDone;

  /**
   * Constructs a HealthType based on a given key
   * 
   * @param key
   *          of type String
   */
  HealthType(String key, int multiplier, boolean allowedFromSequencer, boolean isDone) {
    this.key = key;
    this.multiplier = multiplier;
    this.allowedFromSequencer = allowedFromSequencer;
    this.isDone = isDone;
  }

  /**
   * Returns the key of this HealthType enum.
   * 
   * @return String key.
   */
  public String getKey() {
    return key;
  }

  public int getMultiplier() {
    return multiplier;
  }

  /**
   * Whether is health type may be used as a status from a sequencer.
   * 
   * Some health information is used for pool order completions that cannot be a state of an actual sequencer run's status.
   */
  public boolean isAllowedFromSequencer() {
    return allowedFromSequencer;
  }

  public boolean isDone() {
    return isDone;
  }

}

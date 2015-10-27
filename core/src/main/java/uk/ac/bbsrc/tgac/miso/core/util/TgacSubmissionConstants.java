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

package uk.ac.bbsrc.tgac.miso.core.util;

import java.util.ArrayList;

/**
 * Enum holding submission constants. Do not use!
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public enum TgacSubmissionConstants {
  CENTRE_NAME("TGAC"), CENTRE_ACRONYM("TGAC"), ACCOUNT_NUMBER("ERB000046"), DROP_BOX("era-drop-46");

  private String key;

  TgacSubmissionConstants(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public static ArrayList<String> getKeys() {
    ArrayList<String> keys = new ArrayList<String>();
    for (TgacSubmissionConstants cs : TgacSubmissionConstants.values()) {
      keys.add(cs.getKey());
    }
    return keys;
  }
}

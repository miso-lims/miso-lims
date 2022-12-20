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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.util;

import java.util.Comparator;

/**
 * uk.ac.bbsrc.tgac.miso.core.util
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 01/12/11
 * @since 0.1.3
 */
public final class AlphanumericComparator implements Comparator<String> {
  public static final AlphanumericComparator INSTANCE = new AlphanumericComparator();
  @Override
  public final int compare(String firstString, String secondString) {

    if (secondString == null && firstString == null) {
      return 0;
    }

    if (firstString == null && secondString != null) {
      return -1;
    }
    if (firstString != null && secondString == null) {
      return 1;
    }

    int lengthFirstStr = firstString.length();
    int lengthSecondStr = secondString.length();

    int index1 = 0;
    int index2 = 0;

    while (index1 < lengthFirstStr && index2 < lengthSecondStr) {
      char ch1 = firstString.charAt(index1);
      char ch2 = secondString.charAt(index2);

      char[] space1 = new char[lengthFirstStr];
      char[] space2 = new char[lengthSecondStr];

      int loc1 = 0;
      int loc2 = 0;

      do {
        space1[loc1++] = ch1;
        index1++;

        if (index1 < lengthFirstStr) {
          ch1 = firstString.charAt(index1);
        } else {
          break;
        }
      } while (Character.isDigit(ch1) == Character.isDigit(space1[0]));

      do {
        space2[loc2++] = ch2;
        index2++;

        if (index2 < lengthSecondStr) {
          ch2 = secondString.charAt(index2);
        } else {
          break;
        }
      } while (Character.isDigit(ch2) == Character.isDigit(space2[0]));

      String str1 = new String(space1);
      String str2 = new String(space2);

      int result;

      if (Character.isDigit(space1[0]) && Character.isDigit(space2[0])) {
        Integer firstNumberToCompare = Integer.valueOf(str1.trim());
        Integer secondNumberToCompare = Integer.valueOf(str2.trim());
        result = firstNumberToCompare.compareTo(secondNumberToCompare);
      } else {
        result = str1.compareTo(str2);
      }

      if (result != 0) {
        return result;
      }
    }
    return lengthFirstStr - lengthSecondStr;
  }
}

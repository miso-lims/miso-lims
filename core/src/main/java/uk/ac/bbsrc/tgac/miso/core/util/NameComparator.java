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

import java.lang.reflect.Method;

/**
 * Comparator to compare objects by getName()
 *
 * @author Rob Davey
 * @date 12/11/14
 * @since 0.2.1
 */
public class NameComparator extends AlphanumericComparator {
  private Method method;
  private boolean isAscending = true;
  private boolean isNullsLast = true;
  private static final Object[] EMPTY_OBJECT_ARRAY = new Object[]{};

  public NameComparator(Class c) throws NoSuchMethodException, IllegalArgumentException {
    method = c.getMethod("getName");
    Class returnClass = method.getReturnType();
    if (returnClass.getName().equals("void")) {
      String message = method.getName() + " has a void return type";
      throw new IllegalArgumentException(message);
    }
  }

  @Override
  public int compare(Object object1, Object object2) {
    String name1 = null;
    String name2 = null;

    try {
      name1 = (String)method.invoke(object1, EMPTY_OBJECT_ARRAY);
      name2 = (String)method.invoke(object2, EMPTY_OBJECT_ARRAY);
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }

    // Treat empty strings like nulls
    if (name1.length() == 0) {
      name1 = null;
    }

    if (name2.length() == 0) {
      name2 = null;
    }

    // Handle sorting of null values
    if (name1 == null && name2 == null) return 0;
    if (name1 == null) return isNullsLast ? 1 : -1;
    if (name2 == null) return isNullsLast ? -1 : 1;

    //  Compare objects
    String c1;
    String c2;

    if (isAscending) {
      c1 = name1;
      c2 = name2;
    }
    else {
      c1 = name2;
      c2 = name1;
    }

    return super.compare(c1, c2);
  }
}

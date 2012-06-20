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
import java.util.Comparator;
import java.util.Date;

/**
 * uk.ac.bbsrc.tgac.miso.core.util
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 01/12/11
 * @since 0.1.3
 */
public class DateComparator implements Comparator {
  private Method method;
  private boolean isAscending = true;
  private boolean isNullsLast = true;
  private static final Object[] EMPTY_OBJECT_ARRAY = new Object[]{};

  public DateComparator(Class c, String methodName) throws NoSuchMethodException, IllegalArgumentException {
    method = c.getMethod(methodName);
    Class returnClass = method.getReturnType();
    if (returnClass.getName().equals("void")) {
      String message = method.getName() + " has a void return type";
      throw new IllegalArgumentException(message);
    }
    else if (!returnClass.equals(Date.class)) {
      String message = method.getName() + " does not return a java.util.Date";
      throw new IllegalArgumentException(message);
    }
  }

  @Override
  public int compare(Object object1, Object object2) {
    Date date1 = null;
    Date date2  = null;

    try {
      date1 = (Date)method.invoke(object1, EMPTY_OBJECT_ARRAY);
      date2 = (Date)method.invoke(object2, EMPTY_OBJECT_ARRAY);
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }

    // Handle sorting of null values
    if (date1 == null && date2 == null) return 0;
    if (date1 == null) return isNullsLast ? 1 : -1;
    if (date2 == null) return isNullsLast ? -1 : 1;

    //  Compare objects
    Date c1;
    Date c2;

    if (isAscending) {
      c1 = date1;
      c2 = date2;
    }
    else {
      c1 = date2;
      c2 = date1;
    }

    if (c1.before(c2)) {
        return -1;
    } else if (c1.after(c2)) {
        return 1;
    } else {
        return 0;
    }
  }
}

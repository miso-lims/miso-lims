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

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.lang.reflect.Method;

/**
 * uk.ac.bbsrc.tgac.miso.core.util
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 01/12/11
 * @since 0.1.3
 */
public class AliasComparator extends AlphanumericComparator {
  private Method method;
  private boolean isAscending = true;
  private boolean isNullsLast = true;
  private static final Object[] EMPTY_OBJECT_ARRAY = new Object[] {};

  public AliasComparator(Class c) throws NoSuchMethodException, IllegalArgumentException {
    method = c.getMethod("getAlias");
    Class returnClass = method.getReturnType();
    if (returnClass.getName().equals("void")) {
      String message = method.getName() + " has a void return type";
      throw new IllegalArgumentException(message);
    }
  }

  @Override
  public int compare(Object object1, Object object2) {
    String alias1 = null;
    String alias2 = null;

    try {
      alias1 = (String) method.invoke(object1, EMPTY_OBJECT_ARRAY);
      alias2 = (String) method.invoke(object2, EMPTY_OBJECT_ARRAY);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    // Treat empty strings like nulls
    if (isStringEmptyOrNull(alias1)) {
      alias1 = null;
    }

    if (isStringEmptyOrNull(alias2)) {
      alias2 = null;
    }

    // Handle sorting of null values
    if (alias1 == null && alias2 == null) return 0;
    if (alias1 == null) return isNullsLast ? 1 : -1;
    if (alias2 == null) return isNullsLast ? -1 : 1;

    // Compare objects
    String c1;
    String c2;

    if (isAscending) {
      c1 = alias1;
      c2 = alias2;
    } else {
      c1 = alias2;
      c2 = alias1;
    }

    return super.compare(c1, c2);
  }
}

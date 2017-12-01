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

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.util.Comparator;

import uk.ac.bbsrc.tgac.miso.core.data.Aliasable;

/**
 * uk.ac.bbsrc.tgac.miso.core.util
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 01/12/11
 * @since 0.1.3
 */
public class AliasComparator<T extends Aliasable> implements Comparator<T> {
  protected String getProperty(T object) {
    String alias = object.getAlias();
    return isStringEmptyOrNull(alias) ? null : alias;
  }

  @Override
  public int compare(T o1, T o2) {
    int aliasComparison = AlphanumericComparator.INSTANCE.compare(getProperty(o1), getProperty(o2));
    return aliasComparison == 0 ? Long.compare(o1.getId(), o2.getId()) : aliasComparison;
  }

}

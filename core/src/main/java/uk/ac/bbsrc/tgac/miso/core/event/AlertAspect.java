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

package uk.ac.bbsrc.tgac.miso.core.event;

/**
 * AOP aspect interface that reacts to an update to an object, typically thus firing an {@link Event} and raising an {@link Alert}. Any
 * registered watchers (of type {@link com.eaglegenomics.simlims.core.User}) can also be alerted.
 * 
 * @author Rob Davey
 * @date 14/11/11
 * @since 0.1.3
 */
public interface AlertAspect {
  public void updateWatcher(Long userId);

  public void update(Long entityId);
}

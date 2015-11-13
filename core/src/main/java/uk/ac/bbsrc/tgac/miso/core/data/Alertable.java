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

package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.event.listener.MisoListener;

/**
 * Defines a contract whereby an implementing class is able to be listened to by a MisoListener, thereby raising alerts on given events
 * 
 * @author Rob Davey
 * @date 21-Jan-2013
 * @since 0.1.9
 */
public interface Alertable {
  public boolean addListener(MisoListener listener);

  public boolean removeListener(MisoListener listener);

  public Collection<MisoListener> getListeners();
}

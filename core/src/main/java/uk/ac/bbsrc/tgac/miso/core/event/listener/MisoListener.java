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

package uk.ac.bbsrc.tgac.miso.core.event.listener;

import java.util.Collection;
import java.util.EventListener;

import uk.ac.bbsrc.tgac.miso.core.event.Event;
import uk.ac.bbsrc.tgac.miso.core.event.ResponderService;

/**
 * uk.ac.bbsrc.tgac.miso.core.alert
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 23-Sep-2011
 * @since 0.1.2
 */
public interface MisoListener extends EventListener {
  /**
   * Called whenever a {@link Event} changes it's state, either because it has started or stopped running for some reason.
   * 
   * @param event
   *          the event that was fired
   */
  public void stateChanged(Event event);

  public Collection<? extends ResponderService> getResponderServices();

  public void setResponderServices(Collection<? extends ResponderService> responderServices);

  public void setBaseURL(String baseURL);
}

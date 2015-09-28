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

package uk.ac.bbsrc.tgac.miso.core.event.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.bbsrc.tgac.miso.core.event.Event;
import uk.ac.bbsrc.tgac.miso.core.event.ResponderService;
import uk.ac.bbsrc.tgac.miso.core.event.listener.MisoListener;
import uk.ac.bbsrc.tgac.miso.core.event.listener.ProjectOverviewListener;
import uk.ac.bbsrc.tgac.miso.core.event.model.ProjectOverviewEvent;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * uk.ac.bbsrc.tgac.miso.core.event.impl
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 16/11/11
 * @since 0.1.3
 */
@Deprecated
public class DefaultProjectOverviewListener implements MisoListener {
  protected static final Logger log = LoggerFactory.getLogger(DefaultProjectOverviewListener.class);
  private Collection<? extends ResponderService> responderServices = new HashSet<ResponderService>();

  @Override
  public Collection<? extends ResponderService> getResponderServices() {
    return responderServices;
  }

  public void setResponderServices(Collection<? extends ResponderService> responderServices) {
    this.responderServices = responderServices;
  }

  @Override
  public void setBaseURL(String baseURL) {
  }

  @Override
  public void stateChanged(Event poe) {
    for (ResponderService responder : getResponderServices()) {
      if (responder.respondsTo(poe)) {
        responder.generateResponse(poe);
      }
    }
  }
}

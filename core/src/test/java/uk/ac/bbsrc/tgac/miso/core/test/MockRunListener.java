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

package uk.ac.bbsrc.tgac.miso.core.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.bbsrc.tgac.miso.core.event.Event;
import uk.ac.bbsrc.tgac.miso.core.event.ResponderService;
import uk.ac.bbsrc.tgac.miso.core.event.listener.RunListener;
import uk.ac.bbsrc.tgac.miso.core.event.model.RunEvent;
import java.util.Collection;
import java.util.HashSet;

/**
 * uk.ac.bbsrc.tgac.miso.core.event
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 26/09/11
 * @since 0.1.2
 */
public class MockRunListener implements RunListener {
  protected static final Logger log = LoggerFactory.getLogger(MockRunListener.class);
  private Collection<? extends ResponderService> responderServices = new HashSet<ResponderService>();

  private String baseURL = "";

  @Override
  public Collection<? extends ResponderService> getResponderServices() {
    return responderServices;
  }

  @Override
  public void setResponderServices(Collection<? extends ResponderService> responderServices) {
    log.info("Setting " + responderServices.size() + " responder(s)");
    this.responderServices = responderServices;
  }

  @Override
  public void setBaseURL(String baseURL) {
    this.baseURL = "http://mock.miso.server/miso/";
  }

  @Override
  public void runStarted(RunEvent r) {
    log.info("Run " + r.getEventObject().getId() + " started");
    for (ResponderService responder : getResponderServices()) {
      if (responder.respondsTo(r)) {
        responder.generateResponse(r);
      }
    }
  }

  @Override
  public void runCompleted(RunEvent r) {
    log.info("Run " + r.getEventObject().getId() + " completed");
    for (ResponderService responder : getResponderServices()) {
      if (responder.respondsTo(r)) {
        responder.generateResponse(r);
      }
    }
  }

  @Override
  public void runFailed(RunEvent r) {
    log.info("Run " + r.getEventObject().getId() + " failed");
    for (ResponderService responder : getResponderServices()) {
      if (responder.respondsTo(r)) {
        responder.generateResponse(r);
      }
    }
  }

  @Override
  public void runQcAdded(RunEvent r) {
    log.info("Run " + r.getEventObject().getId() + " qc added");
    for (ResponderService responder : getResponderServices()) {
      if (responder.respondsTo(r)) {
        responder.generateResponse(r);
      }
    }
  }

  @Override
  public void stateChanged(Event event) {
    log.info("Run state changed: " + event.getEventMessage());
    for (ResponderService responder : getResponderServices()) {
      if (responder.respondsTo(event)) {
        responder.generateResponse(event);
      }
    }
  }
}

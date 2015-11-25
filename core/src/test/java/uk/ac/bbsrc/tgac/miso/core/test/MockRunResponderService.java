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

import com.eaglegenomics.simlims.core.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Status;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.event.Alert;
import uk.ac.bbsrc.tgac.miso.core.event.AlerterService;
import uk.ac.bbsrc.tgac.miso.core.event.Event;
import uk.ac.bbsrc.tgac.miso.core.event.ResponderService;
import uk.ac.bbsrc.tgac.miso.core.event.model.RunEvent;
import uk.ac.bbsrc.tgac.miso.core.exception.AlertingException;

import java.util.HashSet;
import java.util.Set;

/**
 * uk.ac.bbsrc.tgac.miso.core.test
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 27/09/11
 * @since 0.1.2
 */
public class MockRunResponderService implements ResponderService {
  protected static final Logger log = LoggerFactory.getLogger(MockRunResponderService.class);

  private Set<AlerterService> alerterServices = new HashSet<AlerterService>();

  public Set<AlerterService> getAlerterServices() {
    return alerterServices;
  }

  public void setAlerterServices(Set<AlerterService> alerterServices) {
    log.info("Setting " + alerterServices.size() + " alerter(s)");
    this.alerterServices = alerterServices;
  }

  @Override
  public boolean respondsTo(Event event) {
    if (event instanceof RunEvent) {
      RunEvent re = (RunEvent) event;
      Run r = re.getEventObject();
      log.info("Checking responder for run " + r.getId());
      if (r.getStatus() != null) {
        Status s = r.getStatus();
        if (s.getHealth().equals(HealthType.Failed) || s.getHealth().equals(HealthType.Completed)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public void generateResponse(Event event) {
    log.info("Responding to event: " + event.getEventMessage() + ". Raising alert...");

    User u = new UserImpl();
    u.setFullName("Foo bar");
    Alert a = new MockAlert(u);
    a.setAlertTitle("New alert for " + u.getFullName());
    a.setAlertText(a.getAlertText() + " (" + event.getEventMessage() + ")");

    for (AlerterService as : alerterServices) {
      try {
        as.raiseAlert(a);
      } catch (AlertingException e) {
        log.error("Cannot raise user-level alert", e);
        e.printStackTrace();
      }
    }
  }
}

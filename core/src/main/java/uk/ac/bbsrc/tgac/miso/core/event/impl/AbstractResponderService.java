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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Watchable;
import uk.ac.bbsrc.tgac.miso.core.event.Alert;
import uk.ac.bbsrc.tgac.miso.core.event.AlerterService;
import uk.ac.bbsrc.tgac.miso.core.event.Event;
import uk.ac.bbsrc.tgac.miso.core.event.ResponderService;
import uk.ac.bbsrc.tgac.miso.core.event.alerter.DaoAlerterService;
import uk.ac.bbsrc.tgac.miso.core.exception.AlertingException;

/**
 * uk.ac.bbsrc.tgac.miso.core.event.impl
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 05/03/12
 * @since 0.1.6
 */
public abstract class AbstractResponderService implements ResponderService {
  protected static final Logger log = LoggerFactory.getLogger(AbstractResponderService.class);

  private boolean saveSystemAlert = true;
  private Set<AlerterService> alerterServices = new HashSet<AlerterService>();

  public void setSaveSystemAlert(boolean saveSystemAlert) {
    this.saveSystemAlert = saveSystemAlert;
  }

  public boolean getSaveSystemAlert() {
    return saveSystemAlert;
  }

  public Set<AlerterService> getAlerterServices() {
    return alerterServices;
  }

  public void setAlerterServices(Set<AlerterService> alerterServices) {
    this.alerterServices = alerterServices;
  }

  @Override
  public abstract boolean respondsTo(Event event);

  public void raiseSystemAlert(Event event) {
    raiseSystemAlert(event, DaoAlerterService.class);
  }

  protected void raiseSystemAlert(Event event, Class<? extends AlerterService>... servicesToAlert) {
    Watchable o = (Watchable) event.getEventObject();

    Alert a = new SystemAlert();
    a.setAlertTitle("[" + o.getWatchableIdentifier() + "] " + event.getEventType().name());
    a.setAlertText(event.getEventMessage());

    List<Class<? extends AlerterService>> serviceList = Arrays.asList(servicesToAlert);
    for (AlerterService as : alerterServices) {
      if (serviceList.contains(as.getClass())) {
        log.debug("Raising system alert via " + as.getClass());
        try {
          as.raiseAlert(a);
        } catch (AlertingException e) {
          log.error("Cannot raise system alert", e);
        }
      }
    }
  }

  @Override
  public void generateResponse(Event event) {
    Watchable o = (Watchable) event.getEventObject();

    for (User user : o.getWatchers()) {
      Alert a = new DefaultAlert(user);
      a.setAlertTitle("Raising alert on object");
      a.setAlertText("The object " + o.toString() + " produced an event " + event.getEventType() + ":: " + event.getEventMessage());

      for (AlerterService as : alerterServices) {
        try {
          as.raiseAlert(a);
        } catch (AlertingException e) {
          log.error("Cannot raise user-level alert", e);
        }
      }
    }

    if (saveSystemAlert) {
      raiseSystemAlert(event, AlerterService.class);
    }
  }
}

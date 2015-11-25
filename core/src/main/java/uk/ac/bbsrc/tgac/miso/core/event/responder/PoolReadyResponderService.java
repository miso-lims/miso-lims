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

package uk.ac.bbsrc.tgac.miso.core.event.responder;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.event.Alert;
import uk.ac.bbsrc.tgac.miso.core.event.AlerterService;
import uk.ac.bbsrc.tgac.miso.core.event.Event;
import uk.ac.bbsrc.tgac.miso.core.event.impl.AbstractResponderService;
import uk.ac.bbsrc.tgac.miso.core.event.impl.DefaultAlert;
import uk.ac.bbsrc.tgac.miso.core.event.model.PoolEvent;
import uk.ac.bbsrc.tgac.miso.core.event.type.MisoEventType;
import uk.ac.bbsrc.tgac.miso.core.exception.AlertingException;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * uk.ac.bbsrc.tgac.miso.core.event.responder
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 13/02/12
 * @since 0.1.6
 */
public class PoolReadyResponderService extends AbstractResponderService {
  protected static final Logger log = LoggerFactory.getLogger(PoolReadyResponderService.class);

  private Set<AlerterService> alerterServices = new HashSet<AlerterService>();

  public PoolReadyResponderService() {
  }

  @Override
  public Set<AlerterService> getAlerterServices() {
    return alerterServices;
  }

  @Override
  public void setAlerterServices(Set<AlerterService> alerterServices) {
    this.alerterServices = alerterServices;
  }

  @Override
  public boolean respondsTo(Event event) {
    if (event instanceof PoolEvent) {
      PoolEvent pe = (PoolEvent) event;
      Pool p = pe.getEventObject();
      if (pe.getEventType().equals(MisoEventType.POOL_READY) && p.getReadyToRun()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void generateResponse(Event event) {
    if (event instanceof PoolEvent) {
      PoolEvent pe = (PoolEvent) event;
      Pool p = pe.getEventObject();

      for (User user : p.getWatchers()) {
        Alert a = new DefaultAlert(user);
        if (!LimsUtils.isStringEmptyOrNull(p.getAlias())) {
          a.setAlertTitle("Pool " + p.getAlias() + "(" + p.getName() + ")");
        } else {
          a.setAlertTitle("Pool " + p.getName() + "(" + p.getId() + ")");
        }

        StringBuilder at = new StringBuilder();
        at.append("The following Pool is ready to run: " + p.getName() + " (" + event.getEventMessage() + "). Please view Pool " + p.getId()
            + " in MISO for more information");
        if (event.getEventContext().has("baseURL")) {
          at.append(":\n\n" + event.getEventContext().getString("baseURL") + "/pool/" + p.getId());
        }
        a.setAlertText(at.toString());

        for (AlerterService as : alerterServices) {
          try {
            as.raiseAlert(a);
          } catch (AlertingException e) {
            log.error("Cannot raise user-level alert", e);
          }
        }
      }

      if (getSaveSystemAlert()) {
        raiseSystemAlert(event);
      }
    }
  }
}

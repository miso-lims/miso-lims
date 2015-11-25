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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunQC;
import uk.ac.bbsrc.tgac.miso.core.event.Alert;
import uk.ac.bbsrc.tgac.miso.core.event.AlerterService;
import uk.ac.bbsrc.tgac.miso.core.event.Event;
import uk.ac.bbsrc.tgac.miso.core.event.impl.AbstractResponderService;
import uk.ac.bbsrc.tgac.miso.core.event.impl.DefaultAlert;
import uk.ac.bbsrc.tgac.miso.core.event.model.RunEvent;
import uk.ac.bbsrc.tgac.miso.core.event.type.MisoEventType;
import uk.ac.bbsrc.tgac.miso.core.exception.AlertingException;
import uk.ac.bbsrc.tgac.miso.core.util.DateComparator;

/**
 * uk.ac.bbsrc.tgac.miso.core.event.responder
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 20/10/11
 * @since 0.1.2
 */
public class RunQcAddedResponderService extends AbstractResponderService {
  protected static final Logger log = LoggerFactory.getLogger(RunQcAddedResponderService.class);

  private Set<AlerterService> alerterServices = new HashSet<AlerterService>();

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
    if (event instanceof RunEvent) {
      RunEvent re = (RunEvent) event;
      Run r = re.getEventObject();
      if (re.getEventType().equals(MisoEventType.RUN_QC_ADDED) && r.getRunQCs() != null && !r.getRunQCs().isEmpty()) {
        log.info("Run " + r.getAlias() + ": " + re.getEventMessage());
        return true;
      }
    }
    return false;
  }

  @Override
  public void generateResponse(Event event) {
    if (event instanceof RunEvent) {
      RunEvent re = (RunEvent) event;
      Run r = re.getEventObject();
      RunQC lastAdded = null;

      List<RunQC> lqc = new ArrayList<RunQC>(r.getRunQCs());
      if (!lqc.isEmpty()) {
        try {
          Collections.sort(lqc, new DateComparator(RunQC.class, "getQcDate"));
          lastAdded = lqc.get(lqc.size() - 1);
        } catch (NoSuchMethodException e) {
          log.error("Cannot sort list of run QCs", e);
        }
      }

      for (User user : r.getWatchers()) {
        Alert a = new DefaultAlert(user);

        String qcInfo = "";
        String qcType = "";

        if (lastAdded != null) {
          qcType = lastAdded.getQcType().getName() + " ";
          qcInfo = "QC'ed by " + lastAdded.getQcCreator() + " on " + lastAdded.getQcDate() + " (" + lastAdded.getInformation() + "). ";
        }

        a.setAlertTitle(qcType + "Run QC Added : " + r.getAlias());

        StringBuilder at = new StringBuilder();
        at.append("The following Run has been QCed: " + r.getAlias() + " (" + event.getEventMessage() + "). " + qcInfo + "Please view Run "
            + r.getId() + " in MISO for more information.");
        if (event.getEventContext().has("baseURL")) {
          at.append(":\n\n" + event.getEventContext().getString("baseURL") + "/run/" + r.getId());
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

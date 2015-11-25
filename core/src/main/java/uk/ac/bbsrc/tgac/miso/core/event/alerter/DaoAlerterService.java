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

package uk.ac.bbsrc.tgac.miso.core.event.alerter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.core.event.Alert;
import uk.ac.bbsrc.tgac.miso.core.event.AlerterService;
import uk.ac.bbsrc.tgac.miso.core.exception.AlertingException;
import uk.ac.bbsrc.tgac.miso.core.store.AlertStore;

/**
 * uk.ac.bbsrc.tgac.miso.core.event.service
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 29/09/11
 * @since 0.1.2
 */
public class DaoAlerterService implements AlerterService {
  protected static final Logger log = LoggerFactory.getLogger(DaoAlerterService.class);

  @Autowired
  private AlertStore alertStore;

  public void setAlertStore(AlertStore alertStore) {
    this.alertStore = alertStore;
  }

  @Override
  public void raiseAlert(Alert a) throws AlertingException {
    try {
      if (alertStore != null) {
        alertStore.save(a);
      } else {
        throw new RuntimeException("Cannot persist raised Alert. Specified Alert store is null");
      }
    } catch (IOException e) {
      log.error("Cannot save alert to DAO", e);
      throw new AlertingException("Cannot save alert to DAO", e);
    }
  }
}

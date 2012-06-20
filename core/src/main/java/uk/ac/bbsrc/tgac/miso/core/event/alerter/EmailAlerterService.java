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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.bbsrc.tgac.miso.core.event.Alert;
import uk.ac.bbsrc.tgac.miso.core.event.AlerterService;
import uk.ac.bbsrc.tgac.miso.core.util.EmailUtils;

/**
 * uk.ac.bbsrc.tgac.miso.core.event.service
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 29/09/11
 * @since 0.1.2
 */
public class EmailAlerterService implements AlerterService {
  protected static final Logger log = LoggerFactory.getLogger(EmailAlerterService.class);

  @Override
  public void raiseAlert(Alert a) {
    String to = a.getAlertUser().getEmail();
    String from = "miso@tgac.ac.uk";
    String subject = "MISO ALERT: " + a.getAlertTitle();
    String text = "Hello " +
                  a.getAlertUser().getFullName() +
                  ",\n\nMISO would like to tell you about something:\n\n" +
                  a.getAlertTitle() + " ("+a.getAlertDate()+")" +
                  "\n\n" +
                  a.getAlertText();
    EmailUtils.send(to, from, subject, text);
  }
}
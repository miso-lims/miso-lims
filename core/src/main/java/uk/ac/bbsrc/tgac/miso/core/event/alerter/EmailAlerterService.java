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

import java.util.Properties;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.bbsrc.tgac.miso.core.event.Alert;
import uk.ac.bbsrc.tgac.miso.core.event.AlerterService;
import uk.ac.bbsrc.tgac.miso.core.exception.AlertingException;
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

  private Properties mailProps = new Properties();

  public void setMailProps(Properties mailProps) {
    this.mailProps = mailProps;
  }

  @Override
  public void raiseAlert(Alert a) throws AlertingException {
    if (!mailProps.containsKey("mail.smtp.host")) {
      log.error("No SMTP host specified in the mail.properties configuration file. Cannot send email.");
      throw new AlertingException("No SMTP host specified in the mail.properties configuration file. Cannot send email.");
    } else {
      String from = mailProps.getProperty("mail.from", "miso@your.miso.server");
      String to = a.getAlertUser().getEmail();
      String subject = "MISO ALERT: " + a.getAlertTitle();
      String text = "Hello " + a.getAlertUser().getFullName() + ",\n\nMISO would like to tell you about something:\n\n" + a.getAlertTitle()
          + " (" + a.getAlertDate() + ")" + "\n\n" + a.getAlertText();
      try {
        EmailUtils.send(to, from, subject, text, mailProps);
      } catch (MessagingException e) {
        log.error("Cannot send email to alert recipients", e);
        throw new AlertingException("Cannot send email to alert recipients", e);
      }
    }
  }
}

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
import uk.ac.bbsrc.tgac.miso.core.event.Alert;
import uk.ac.bbsrc.tgac.miso.core.event.AlerterService;
import uk.ac.bbsrc.tgac.miso.core.exception.AlertingException;
import uk.ac.bbsrc.tgac.miso.core.util.EmailUtils;

import javax.mail.MessagingException;
import java.util.Properties;

/**
 * uk.ac.bbsrc.tgac.miso.core.test
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 29/09/11
 * @since 0.1.2
 */
public class MockEmailAlerterService implements AlerterService {
  protected static final Logger log = LoggerFactory.getLogger(MockEmailAlerterService.class);

  @Override
  public void raiseAlert(Alert a) throws AlertingException {
    log.info("Emailing alert -> " + a.toString());

    String to = "someone@somewhere";
    String from = "runstats@miso";
    String subject = "Test runstats delivery";
    String text = "Hello,\n\nMISO would like to tell you about something:\n\n" + a.toString();

    try {
      EmailUtils.send(to, from, subject, text, new Properties());
    } catch (MessagingException e) {
      log.error("Cannot send email to alert recipients", e);
      throw new AlertingException("Cannot send email to alert recipients", e);
    }
  }
}

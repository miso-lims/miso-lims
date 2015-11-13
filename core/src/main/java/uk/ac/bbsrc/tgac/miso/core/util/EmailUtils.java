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

package uk.ac.bbsrc.tgac.miso.core.util;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Utility class to provide email sending functionality
 * 
 * @author Xingdong Bian
 * @author Rob Davey
 * @since 0.0.2
 */
public class EmailUtils {

  /**
   * Send an email to a recipient
   * 
   * @param to
   *          of type String
   * @param from
   *          of type String
   * @param subject
   *          of type String
   * @param text
   *          of type String
   * @param mailProps
   *          of type Properties
   * @throws javax.mail.MessagingException
   */
  public static void send(String to, String from, String subject, String text, Properties mailProps) throws MessagingException {
    Session mailSession = Session.getDefaultInstance(mailProps);
    Message simpleMessage = new MimeMessage(mailSession);

    InternetAddress fromAddress = new InternetAddress(from);
    InternetAddress toAddress = new InternetAddress(to);

    simpleMessage.setFrom(fromAddress);
    simpleMessage.setRecipient(Message.RecipientType.TO, toAddress);
    simpleMessage.setSubject(subject);
    simpleMessage.setText(text);

    Transport.send(simpleMessage);
  }
}

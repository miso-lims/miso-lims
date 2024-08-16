package uk.ac.bbsrc.tgac.miso.core.util;

import java.util.Properties;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

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
   * @param to of type String
   * @param from of type String
   * @param subject of type String
   * @param text of type String
   * @param mailProps of type Properties
   * @throws jakarta.mail.MessagingException
   */
  public static void send(String to, String from, String subject, String text, Properties mailProps)
      throws MessagingException {
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

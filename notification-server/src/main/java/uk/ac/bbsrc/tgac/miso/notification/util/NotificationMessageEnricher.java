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

package uk.ac.bbsrc.tgac.miso.notification.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;
import org.springframework.integration.MessagingException;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.HeaderEnricher;

/**
 * uk.ac.bbsrc.tgac.miso.notification.util
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 09/02/12
 * @since 0.1.6
 */
public class NotificationMessageEnricher extends HeaderEnricher {
  protected static final Logger log = LoggerFactory.getLogger(NotificationMessageEnricher.class);

  private final Map<String, ? extends HeaderValueMessageProcessor<?>> newHeadersToAdd;

  public NotificationMessageEnricher(Map<String, ? extends HeaderValueMessageProcessor<?>> headersToAdd) {
    this.newHeadersToAdd = (headersToAdd != null) ? headersToAdd : new HashMap<String, HeaderValueMessageProcessor<Object>>();
  }

  @Override
  public Message<?> transform(Message<?> message) {
    try {
      Map<String, Object> headerMap = new HashMap<String, Object>(message.getHeaders());
      log.debug("CURRENT HEADERS: " + headerMap.toString());
      for (Map.Entry<String, ? extends HeaderValueMessageProcessor<?>> entry : this.newHeadersToAdd.entrySet()) {
        String key = entry.getKey();
        HeaderValueMessageProcessor<?> valueProcessor = entry.getValue();
        boolean headerDoesNotExist = headerMap.get(key) == null;

        /**
         * Only evaluate value expression if necessary
         */
        if (headerDoesNotExist) {
          Object value = valueProcessor.processMessage(message);
          if (value != null) {
            headerMap.put(key, value);
          }
        }
      }

      Message newMessage = MessageBuilder.withPayload(message.getPayload()).copyHeaders(headerMap).build();
      log.debug("NEW HEADERS: " + newMessage.getHeaders().toString());

      return super.transform(newMessage);
    } catch (Exception e) {
      throw new MessagingException(message, "failed to transform message headers", e);
    }
  }
}
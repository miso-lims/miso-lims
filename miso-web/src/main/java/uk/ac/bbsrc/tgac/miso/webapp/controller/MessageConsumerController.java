/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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

package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.Message;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.service.integration.NotificationGatewayService;
import uk.ac.bbsrc.tgac.miso.core.service.integration.strategy.NotificationGateway;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * A message consumer that will listen for messages and pass them on to respective handlers. This is a REST-aware controller, which means
 * message producers can POST messages to this controller.
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Controller
@RequestMapping(value = "/consumer")
public class MessageConsumerController {
  protected static final Logger log = LoggerFactory.getLogger(MessageConsumerController.class);

  @Autowired
  private NotificationGatewayService notificationGatewayService;

  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private RequestManager requestManager;

  public void setNotificationGatewayService(NotificationGatewayService notificationGatewayService) {
    this.notificationGatewayService = notificationGatewayService;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @RequestMapping(value = "/illumina/run/status", method = RequestMethod.POST)
  public void consumeGatewayIlluminaStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
    consumeGatewayStatus(request, response, PlatformType.ILLUMINA);
  }

  @RequestMapping(value = "/ls454/run/status", method = RequestMethod.POST)
  public void consumeGateway454Status(HttpServletRequest request, HttpServletResponse response) throws IOException {
    consumeGatewayStatus(request, response, PlatformType.LS454);
  }

  @RequestMapping(value = "/solid/run/status", method = RequestMethod.POST)
  public void consumeGatewaySolidStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
    consumeGatewayStatus(request, response, PlatformType.SOLID);
  }

  @RequestMapping(value = "/pacbio/run/status", method = RequestMethod.POST)
  public void consumeGatewayPacbioStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
    consumeGatewayStatus(request, response, PlatformType.PACBIO);
  }

  @RequestMapping(value = "/iontorrent/run/status", method = RequestMethod.POST)
  public void consumeGatewayIonTorrentStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
    consumeGatewayStatus(request, response, PlatformType.IONTORRENT);
  }

  private void consumeGatewayStatus(HttpServletRequest request, HttpServletResponse response, PlatformType platform) throws IOException {
    Set<NotificationGateway> gateways = (Set<NotificationGateway>) notificationGatewayService.getGatewaysFor(platform);
    if (gateways == null || gateways.isEmpty()) {
      log.error("No notification gateways found for platform: " + platform.getKey());
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    for (NotificationGateway s : gateways) {
      log.debug("Using " + s.toString());
      s.consume(buildMessage(exposeRequest(request)));
    }
    response.sendRedirect("/miso");
  }

  private MultiValueMap<String, String> exposeRequest(HttpServletRequest request) {
    log.debug("Request size: " + request.getContentLength());
    Map<String, Object> map = request.getParameterMap();
    log.debug("RAW MAP: " + map.toString());
    MultiValueMap<String, String> message = new LinkedMultiValueMap<String, String>();
    for (String s : map.keySet()) {
      log.debug("EXPOSING PARAM '" + s + "': " + LimsUtils.join(request.getParameterValues(s), ","));
      message.put(s, Arrays.asList(request.getParameterValues(s)));
    }

    return message;
  }

  private <T> Message<T> buildMessage(T payload) {
    return MessageBuilder.withPayload(payload).setHeader("handler", requestManager).build();
  }
}
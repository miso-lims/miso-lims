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

package uk.ac.bbsrc.tgac.miso.core.service.integration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.endpoint.AbstractEndpoint;
import org.springframework.integration.gateway.GatewayProxyFactoryBean;
import org.springframework.integration.handler.ServiceActivatingHandler;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.integration.strategy.NotificationConsumerStrategy;
import uk.ac.bbsrc.tgac.miso.core.service.integration.strategy.NotificationGateway;

/**
 * Concrete implementation of a {@link NotificationGatewayService} that discovers, via the SPI framework {@link java.util.ServiceLoader},
 * available {@link NotificationConsumerService}s and wires them to Spring Integration {@link GatewayProxyFactoryBean} objects so that MISO
 * can accept incoming integration messages on-the-fly.
 * 
 * @author Rob Davey
 * @date 06/02/12
 * @since 0.1.5
 */
public class MisoNotificationGatewayService extends AbstractEndpoint implements NotificationGatewayService {
  protected static final Logger log = LoggerFactory.getLogger(MisoNotificationGatewayService.class);

  private final Map<String, GatewayProxyFactoryBean> proxyMap = new HashMap<String, GatewayProxyFactoryBean>();
  private NotificationConsumerService notificationConsumerService;

  public void setNotificationConsumerService(NotificationConsumerService notificationConsumerService) {
    this.notificationConsumerService = notificationConsumerService;
  }

  public NotificationConsumerService getNotificationConsumerService() {
    return notificationConsumerService;
  }

  public MisoNotificationGatewayService(NotificationConsumerService consumerService) {
    setNotificationConsumerService(consumerService);
  }

  @Override
  public void onInit() {
    wireGatewaysToConsumers();
  }

  private void wireGatewaysToConsumers() {
    log.info("Building gateway service...");
    if (getNotificationConsumerService() != null) {
      for (NotificationConsumerStrategy s : getNotificationConsumerService().getConsumerStrategies()) {
        log.info("Wiring up gateway for consumer strategy " + s.getName() + "...");
        DirectChannel reply = new DirectChannel();
        reply.setBeanName("reply-" + s.getName());

        DirectChannel mc = new DirectChannel();
        mc.setBeanName("channel-" + s.getName());

        GatewayProxyFactoryBean gatewayProxy = new GatewayProxyFactoryBean();
        gatewayProxy.setDefaultRequestChannel(mc);
        gatewayProxy.setServiceInterface(NotificationGateway.class);
        gatewayProxy.setBeanFactory(getBeanFactory());
        gatewayProxy.setBeanName("gateway-" + s.getName());
        gatewayProxy.setComponentName("gateway-" + s.getName());
        gatewayProxy.setDefaultReplyChannel(reply);

        mc.subscribe(new ServiceActivatingHandler(s, "consume"));

        gatewayProxy.afterPropertiesSet();

        this.proxyMap.put(s.getName(), gatewayProxy);
      }
    } else {
      log.info("Null consumer service");
    }
  }

  @Override
  public Set<NotificationGateway> getGatewaysFor(PlatformType pt) {
    Set<NotificationGateway> gateways = new HashSet<NotificationGateway>();

    for (String str : this.proxyMap.keySet()) {
      NotificationConsumerStrategy ncs = getNotificationConsumerService().getConsumerStrategy(str);
      if (ncs.isStrategyFor(pt)) {
        GatewayProxyFactoryBean gateway = this.proxyMap.get(str);
        if (gateway.isRunning()) {
          try {
            NotificationGateway s = (NotificationGateway) gateway.getObject();
            gateways.add(s);
          } catch (Exception e) {
            log.error("get gateways for", e);
          }
        }
      }
    }

    return gateways;
  }

  @Override
  protected void doStart() {
    for (GatewayProxyFactoryBean gateway : this.proxyMap.values()) {
      gateway.start();
    }
  }

  @Override
  protected void doStop() {
    for (GatewayProxyFactoryBean gateway : this.proxyMap.values()) {
      gateway.stop();
    }
  }
}

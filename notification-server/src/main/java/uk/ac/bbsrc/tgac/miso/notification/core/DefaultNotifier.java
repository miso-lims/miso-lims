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

package uk.ac.bbsrc.tgac.miso.notification.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.integration.Message;
import org.springframework.integration.channel.ChannelInterceptor;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.interceptor.WireTap;
import org.springframework.integration.endpoint.SourcePollingChannelAdapter;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.handler.MethodInvokingMessageProcessor;
import org.springframework.integration.http.outbound.HttpRequestExecutingMessageHandler;
import org.springframework.integration.http.support.DefaultHttpHeaderMapper;
import org.springframework.integration.ip.tcp.TcpOutboundGateway;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;
import org.springframework.integration.splitter.MethodInvokingSplitter;
import org.springframework.integration.support.channel.BeanFactoryChannelResolver;
import org.springframework.integration.support.channel.ChannelResolver;
import org.springframework.integration.transformer.HeaderEnricher;
import org.springframework.integration.transformer.MessageTransformingHandler;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.integration.ws.pacbio.PacBioServiceWrapper;
import uk.ac.bbsrc.tgac.miso.core.service.integration.ws.solid.SolidServiceWrapper;
import uk.ac.bbsrc.tgac.miso.notification.manager.NotificationRequestManager;
import uk.ac.bbsrc.tgac.miso.notification.util.NotificationMessageEnricher;
import uk.ac.bbsrc.tgac.miso.notification.util.NotificationUtils;
import uk.ac.bbsrc.tgac.miso.tools.run.MultiFileQueueMessageSource;
import uk.ac.bbsrc.tgac.miso.tools.run.RunFolderScanner;

/**
 * uk.ac.bbsrc.tgac.miso.notification.service.impl
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 06-Dec-2010
 * @since 0.1.4
 */
public class DefaultNotifier {
  protected static final Logger log = LoggerFactory.getLogger(DefaultNotifier.class);

  public static void main(String[] args) {
    log.info("Starting notification system...");
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/notification.xml");

    ChannelResolver channelResolver = new BeanFactoryChannelResolver(context);
    NotificationUtils notificationUtils = new NotificationUtils();

    File propsPath = new File(".", "notification.properties");
    Properties props = new Properties();
    try {
      props.load(new FileReader(propsPath));

      CompositeFileListFilter statusFilter = (CompositeFileListFilter) context.getBean("statusFilter");

      Map<String, Set<File>> allDataPaths = new HashMap<String, Set<File>>();

      for (String platformType : PlatformType.getKeys()) {
        platformType = platformType.toLowerCase();

        if (props.containsKey(platformType + ".splitterBatchSize")) {
          notificationUtils.setSplitterBatchSize(Integer.parseInt(props.getProperty(platformType + ".splitterBatchSize")));
        }

        if (props.containsKey(platformType + ".dataPaths")) {
          log.debug("Resolving " + platformType + ".dataPaths ...");
          String dataPaths = props.getProperty(platformType + ".dataPaths");
          Set<File> paths = new HashSet<File>();
          for (String path : dataPaths.split(",")) {
            File f = new File(path);
            if (f.exists() && f.canRead() && f.isDirectory()) {
              paths.add(f);
              log.debug("Added " + path);
            }
          }

          allDataPaths.put(platformType, paths);

          if (platformType.equals("solid")) {
            for (String key : props.stringPropertyNames()) {
              if (key.startsWith("solid.wsdl.url.")) {
                String serviceName = key.substring(key.lastIndexOf(".") + 1);
                log.debug("Creating service: " + serviceName);
                SolidServiceWrapper ssw = new SolidServiceWrapper(serviceName, URI.create(props.getProperty(key)).toURL());
                context.getBeanFactory().registerSingleton(serviceName, ssw);
              }
            }
          }

          if (platformType.equals("pacbio")) {
            for (String key : props.stringPropertyNames()) {
              if (key.startsWith("pacbio.ws.url.")) {
                String serviceName = key.substring(key.lastIndexOf(".") + 1);
                log.debug("Creating service: " + serviceName);
                PacBioServiceWrapper psw = new PacBioServiceWrapper(serviceName, URI.create(props.getProperty(key)));
                context.getBeanFactory().registerSingleton(serviceName, psw);
              }
            }
          }

          RunFolderScanner rfs = (RunFolderScanner) context.getBean(platformType + "StatusRecursiveScanner");

          MultiFileQueueMessageSource mfqms = new MultiFileQueueMessageSource();
          mfqms.setBeanName(platformType + "MultiFileQueueMessageSource");
          mfqms.setBeanFactory(context.getBeanFactory());
          mfqms.setScanner(rfs);
          mfqms.setFilter(statusFilter);
          mfqms.setDirectories(paths);
          // make sure all the directories are rescanned each poll
          mfqms.setScanEachPoll(false);
          mfqms.afterPropertiesSet();

          SourcePollingChannelAdapter spca = new SourcePollingChannelAdapter();
          spca.setBeanName(platformType + "StatusFileSource");
          spca.setBeanFactory(context.getBeanFactory());
          spca.setMaxMessagesPerPoll(1);

          DynamicTrigger trigger;
          if (props.containsKey(platformType + ".scanRate")) {
            trigger = new DynamicTrigger(Integer.parseInt(props.getProperty(platformType + ".scanRate")), TimeUnit.MILLISECONDS);
          } else {
            trigger = new DynamicTrigger(600000, TimeUnit.MILLISECONDS);
          }
          trigger.setFixedRate(false);
          spca.setTrigger(trigger);

          spca.setSource(mfqms);
          spca.setOutputChannel(channelResolver.resolveChannelName(platformType + "StatusFileInputChannel"));
          spca.setAutoStartup(false);
          spca.afterPropertiesSet();

          DirectChannel outputChannel = (DirectChannel) channelResolver.resolveChannelName(platformType + "StatusChannel");
          outputChannel.setBeanName(platformType + "StatusChannel");
          outputChannel.setBeanFactory(context.getBeanFactory());

          if (props.containsKey("wiretap.enabled") && "true".equals(props.get("wiretap.enabled"))) {
            // set up wire tap
            DirectChannel wireTapChannel = (DirectChannel) channelResolver.resolveChannelName("wireTapChannel");
            wireTapChannel.setBeanName("wireTapChannel");
            wireTapChannel.setBeanFactory(context.getBeanFactory());

            LoggingHandler wireTapLogger = new LoggingHandler("TRACE");
            wireTapLogger.setBeanName("OutputWireTapper");
            wireTapLogger.setBeanFactory(context.getBeanFactory());
            wireTapLogger.setLoggerName("wiretap");
            wireTapLogger.setShouldLogFullMessage(true);
            wireTapLogger.afterPropertiesSet();
            wireTapChannel.subscribe(wireTapLogger);

            List<ChannelInterceptor> ints = new ArrayList<ChannelInterceptor>();
            WireTap wt = new WireTap(wireTapChannel);
            ints.add(wt);
            outputChannel.setInterceptors(ints);
          }

          DirectChannel signChannel = (DirectChannel) channelResolver.resolveChannelName(platformType + "MessageSignerChannel");
          signChannel.setBeanFactory(context.getBeanFactory());

          DirectChannel splitterChannel = (DirectChannel) channelResolver.resolveChannelName(platformType + "SplitterChannel");
          splitterChannel.setBeanFactory(context.getBeanFactory());

          if (props.containsKey(platformType + ".http.statusEndpointURIs")) {
            log.debug("Resolving " + platformType + ".http.statusEndpointURIs ...");
            String statusEndpointURIs = props.getProperty(platformType + ".http.statusEndpointURIs");
            for (String uri : statusEndpointURIs.split(",")) {
              // split into multiple messages
              MethodInvokingSplitter mis = new MethodInvokingSplitter(notificationUtils, "splitMessage");
              mis.setBeanName(platformType + "Splitter");
              mis.setBeanFactory(context.getBeanFactory());
              mis.setChannelResolver(channelResolver);
              mis.setOutputChannel(signChannel);
              splitterChannel.subscribe(mis);

              // sign messages and inject url into message headers via HeaderEnricher
              Map<String, SignedHeaderValueMessageProcessor<String>> urlHeaderToSign = new HashMap<String, SignedHeaderValueMessageProcessor<String>>();
              URI su = URI.create(uri);
              urlHeaderToSign.put("x-url", new SignedHeaderValueMessageProcessor<String>(su.getPath()));
              urlHeaderToSign.put("x-user", new SignedHeaderValueMessageProcessor<String>("notification"));
              NotificationMessageEnricher signer = new NotificationMessageEnricher(urlHeaderToSign);
              signer.setMessageProcessor(new MethodInvokingMessageProcessor(notificationUtils, "signMessageHeaders"));

              MessageTransformingHandler mth = new MessageTransformingHandler(signer);
              mth.setBeanName(platformType + "Signer");
              mth.setBeanFactory(context.getBeanFactory());
              mth.setChannelResolver(channelResolver);
              mth.setOutputChannel(outputChannel);
              mth.setRequiresReply(false);
              signChannel.subscribe(mth);

              DefaultHttpHeaderMapper hm = new DefaultHttpHeaderMapper();
              hm.setUserDefinedHeaderPrefix("");
              String[] names = { "Accept", "x-url", "x-signature", "x-user" };
              hm.setBeanFactory(context.getBeanFactory());
              hm.setOutboundHeaderNames(names);
              hm.setInboundHeaderNames(names);

              HttpRequestExecutingMessageHandler statusNotifier = new HttpRequestExecutingMessageHandler(uri);
              statusNotifier.setBeanName(platformType + "StatusNotifier");
              statusNotifier.setBeanFactory(context.getBeanFactory());
              statusNotifier.setChannelResolver(channelResolver);
              statusNotifier.setHttpMethod(HttpMethod.POST);
              statusNotifier.setCharset("UTF-8");
              statusNotifier.setHeaderMapper(hm);
              statusNotifier.setExtractPayload(true);
              statusNotifier.setOrder(3);
              statusNotifier.setRequiresReply(false);
              statusNotifier.setExpectReply(false);

              outputChannel.subscribe(statusNotifier);
            }
          }

          if (props.containsKey(platformType + ".tcp.host") && props.containsKey(platformType + ".tcp.port")) {
            String host = props.getProperty(platformType + ".tcp.host");
            int port = Integer.parseInt(props.getProperty(platformType + ".tcp.port"));

            TcpNetClientConnectionFactory tnccf = new TcpNetClientConnectionFactory(host, port);
            tnccf.setSoTimeout(10000);
            tnccf.setSingleUse(true);

            TcpOutboundGateway tog = new TcpOutboundGateway();
            tog.setBeanName(platformType + "StatusNotifier");
            tog.setBeanFactory(context.getBeanFactory());
            tog.setChannelResolver(channelResolver);
            tog.setConnectionFactory(tnccf);
            tog.setRequestTimeout(10000);
            tog.setRequiresReply(false);
            tog.setOutputChannel(outputChannel);

            outputChannel.subscribe(tog);
          }

          if ((!props.containsKey(platformType + ".tcp.host") || !props.containsKey(platformType + ".tcp.port"))
              && !props.containsKey(platformType + ".http.statusEndpointURIs")) {
            log.error(
                "You have specified a list of paths to scan, but no valid endpoint to notify. Please add a <platform>.http.statusEndpointURIs property and/or <platform>.tcp.host/port properties in notification.properties");
          } else {
            spca.start();
          }
        } else {
          log.warn("You have not specified a list of " + platformType + " paths to scan. Please add a " + platformType.toLowerCase()
              + ".dataPaths property in notification.properties if you wish to track this platform");
        }

        NotificationRequestManager nrm = (NotificationRequestManager) context.getBean("notificationRequestManager");
        nrm.setApplicationContext(context);
        nrm.setDataPaths(allDataPaths);
      }
    } catch (FileNotFoundException e) {
      log.error("Cannot find a notification.properties file in the same directory as the notification jar. Please add one", e);
    } catch (IOException e) {
      log.error("Cannot read notification.properties. Please check permissions/availability", e);
    } catch (Exception e) {
      log.error("Something else went wrong", e);
    }
  }

  static class SignedHeaderValueMessageProcessor<T> implements HeaderEnricher.HeaderValueMessageProcessor<T> {
    private volatile Boolean overwrite = null;
    private final T value;

    public SignedHeaderValueMessageProcessor(T value) {
      this.value = value;
    }

    @Override
    public T processMessage(Message<?> message) {
      return this.value;
    }

    public void setOverwrite(Boolean overwrite) {
      this.overwrite = overwrite;
    }

    @Override
    public Boolean isOverwrite() {
      return this.overwrite;
    }
  }
}

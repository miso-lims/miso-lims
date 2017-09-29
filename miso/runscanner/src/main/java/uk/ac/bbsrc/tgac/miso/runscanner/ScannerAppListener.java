package uk.ac.bbsrc.tgac.miso.runscanner;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;

import io.prometheus.client.hotspot.DefaultExports;

@Component
public class ScannerAppListener implements ApplicationListener<ApplicationContextEvent> {

  @Override
  public void onApplicationEvent(ApplicationContextEvent event) {
    if (event instanceof ContextStartedEvent || event instanceof ContextRefreshedEvent) {
      DefaultExports.initialize();
      event.getApplicationContext().getBean(Scheduler.class).start();
    } else if (event instanceof ContextStoppedEvent || event instanceof ContextStoppedEvent) {
      event.getApplicationContext().getBean(Scheduler.class).stop();
    }

  }

}

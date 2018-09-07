package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util;

import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestListener extends RunListener {

  private static final Logger log = LoggerFactory.getLogger(TestListener.class);

  @Override
  public void testStarted(Description description) throws Exception {
    log.info("Running test {}#{}", description.getClassName(), description.getMethodName());
  }

}

package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class TestLoggingExtension implements BeforeEachCallback {

  private static final Logger log = LoggerFactory.getLogger(TestLoggingExtension.class);

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    log.info("Running test {}#{}", context.getTestClass().map(Class::getName).orElse("Unknown"),
        context.getTestMethod().map(Method::getName).orElse("Unknown"));
  }

}

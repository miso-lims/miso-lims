package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.web.context.WebApplicationContext;

import jakarta.servlet.ServletContextEvent;
import uk.ac.bbsrc.tgac.miso.webapp.context.MisoAppListener;

/**
 * The point of this class is to run MisoAppListener#contextInitialized, which does a bunch of
 * setup.
 * 
 * One thing that does not work here is injecting/autowiring dependencies into the naming scheme
 * objects. This might be because it's running before the context is fully initialized and/or
 * because it's a mock context, which also does not support the addListener method, which would
 * otherwise be more ideal.
 */
public class SpringTestExecutionListener extends AbstractTestExecutionListener {

  private static boolean initialized = false;

  @Override
  public void prepareTestInstance(TestContext testContext) throws Exception {
    if (!initialized) {
      initialized = true;
      WebApplicationContext wac = (WebApplicationContext) testContext.getApplicationContext();
      new MisoAppListener().contextInitialized(new ServletContextEvent(wac.getServletContext()));
    }
  }
}

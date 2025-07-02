package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.web.context.WebApplicationContext;

import jakarta.servlet.ServletContextEvent;
import uk.ac.bbsrc.tgac.miso.webapp.context.MisoAppListener;


public class SpringTestExecutionListener extends AbstractTestExecutionListener {

  private static boolean initialized = false;

  @Override
  public void prepareTestInstance(TestContext testContext) throws Exception {
    if (!initialized) {
      initialized = true;
      WebApplicationContext wac = (WebApplicationContext) testContext.getApplicationContext();
      MockServletContext ctx = new MockServletContext();
      ctx.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
      new MisoAppListener().contextInitialized(new ServletContextEvent(ctx));
    }
  }
}

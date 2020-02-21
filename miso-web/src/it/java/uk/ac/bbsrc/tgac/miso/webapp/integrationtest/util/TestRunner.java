package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util;

import org.junit.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

public class TestRunner extends SpringJUnit4ClassRunner {

  private static final Logger log = LoggerFactory.getLogger(TestRunner.class);

  private static final int MAX_RETRIES = 2;

  public TestRunner(Class<?> klass) throws InitializationError {
    super(klass);
  }

  @Override
  protected void runChild(FrameworkMethod method, RunNotifier notifier) {
    if (isTestMethodIgnored(method)) {
      notifier.fireTestIgnored(describeChild(method));
      return;
    }

    Statement statement = methodBlock(method);
    EachTestNotifier testNotifier = new EachTestNotifier(notifier, describeChild(method));
    testNotifier.fireTestStarted();

    try {
      statement.evaluate();
    } catch (AssumptionViolatedException e) {
      testNotifier.addFailedAssumption(e);
    } catch (WebDriverException e) {
      handleWebDriverException(testNotifier, statement, e);
    } catch (Throwable e) {
      testNotifier.addFailure(e);
    } finally {
      testNotifier.fireTestFinished();
    }
  }

  private void handleWebDriverException(EachTestNotifier testNotifier, Statement statement, WebDriverException exception) {
    // only retry on WebDriverException; not subclasses
    if (exception.getClass() == WebDriverException.class) {
      retry(testNotifier, statement, exception);
    } else {
      testNotifier.addFailure(exception);
    }
  }

  private void retry(EachTestNotifier notifier, Statement statement, WebDriverException exception) {
    Throwable lastException = exception;
    for (int i = 0; i < MAX_RETRIES; i++) {
      try {
        log.warn("Retrying test after WebDriverException #{}", i + 1, lastException);
        statement.evaluate();
        return;
      } catch (WebDriverException e) {
        lastException = e;
      } catch (Throwable e) {
        notifier.addFailure(e);
      }
    }
    log.error("Failing test after WebDriverException #3", lastException);
    notifier.addFailure(lastException);
  }

}

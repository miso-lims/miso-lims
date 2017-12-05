package uk.ac.bbsrc.tgac.miso.webapp.context;

import java.util.Collection;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;

public class PrometheusInterceptor implements MethodInterceptor {

  private final static Counter hits = Counter.build().name("miso_method_requests").labelNames("javaclass", "method")
      .help("The number of requests for this method.").register();
  private final static Histogram times = Histogram.build().name("miso_method_time").labelNames("javaclass", "method")
      .help("The time, in nano seconds, this method takes to run.").exponentialBuckets(1000, 10, 9).register();
  private final static Counter throwCounts = Counter.build().name("miso_method_throws").labelNames("javaclass", "method")
      .help("The number of times this method has thrown an exception.").register();
  private final static Histogram resultCounts = Histogram.build().name("miso_method_results").labelNames("javaclass", "method")
      .help("The number of items this method returns (if a collection).").buckets(0, 1, 10, 50, 100, 500, 1000).register();

  @Override
  public Object invoke(MethodInvocation method) throws Throwable {
    // Don't bother with the interfaces since tracking will happen on the concrete class.
    if (method.getMethod().getDeclaringClass().isInterface()) {
      return method.proceed();
    }

    String className = method.getMethod().getDeclaringClass().getSimpleName();
    String methodName = method.getMethod().getName();

    long startTime = System.nanoTime();
    try {
      Object retVal = method.proceed();
      if (retVal != null && retVal instanceof Collection) {
        resultCounts.labels(className, methodName).observe(((Collection<?>) retVal).size());
      }
      return retVal;
    } catch (Throwable e) {
      throwCounts.labels(className, methodName).inc();
      throw e;
    } finally {
      long duration = System.nanoTime() - startTime;

      times.labels(className, methodName).observe(duration);
      hits.labels(className, methodName).inc();
    }
  }
}

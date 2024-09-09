package uk.ac.bbsrc.tgac.miso.webapp.context;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import io.prometheus.metrics.core.metrics.Histogram;

public class PrometheusInterceptor implements MethodInterceptor {

  private static final Histogram controller_times = Histogram.builder().name("miso_controller_method_time")
      .labelNames("javaclass", "method", "success")
      .help("The time, in milliseconds, this method takes to run.")
      .classicUpperBounds(100, 500, 1000, 2000, 3000, 5000, 8000, 15000, 30000)
      .register();
  private static final Histogram service_times = Histogram.builder().name("miso_service_method_time")
      .labelNames("javaclass", "method", "success")
      .help("The time, in milliseconds, this method takes to run.")
      .classicUpperBounds(100, 500, 1000, 2000, 3000, 5000, 8000, 15000, 30000)
      .register();

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    Method method = invocation.getMethod();
    Class<?> clazz = invocation.getThis().getClass();
    if (isServiceClass(clazz)) {
      return monitor(invocation, service_times, clazz.getSimpleName(), method.getName());
    } else if (isMappingMethod(method)) {
      return monitor(invocation, controller_times, clazz.getSimpleName(), method.getName());
    }
    return invocation.proceed();
  }

  private static boolean isServiceClass(Class<?> clazz) {
    return clazz.isAnnotationPresent(Service.class);
  }

  private static boolean isMappingMethod(Method method) {
    return AnnotationUtils.findAnnotation(method, RequestMapping.class) != null;
  }

  private static Object monitor(MethodInvocation invocation, Histogram histogram, String className, String methodName)
      throws Throwable {
    long startTime = System.currentTimeMillis();
    boolean success = true;
    try {
      return invocation.proceed();
    } catch (Throwable e) {
      success = false;
      throw e;
    } finally {
      long duration = System.currentTimeMillis() - startTime;
      histogram.labelValues(className, methodName, Boolean.toString(success)).observe(duration);
    }
  }

}

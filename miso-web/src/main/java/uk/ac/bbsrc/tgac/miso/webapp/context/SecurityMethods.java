package uk.ac.bbsrc.tgac.miso.webapp.context;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class SecurityMethods {

  private static boolean securityMethodIn(String... methods) {
    String securityMethod = System.getProperty("security.method");
    for (String method : methods) {
      if (method.equals(securityMethod)) {
        return true;
      }
    }
    return false;
  }

  public static class JdbcSecurityEnabled implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
      return securityMethodIn("jdbc");
    }

  }

  public static class LdapSecurityEnabled implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
      return securityMethodIn("ldap");
    }

  }

  public static class AdSecurityEnabled implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
      return securityMethodIn("ad");
    }

  }

  public static class LdapOrAdSecurityEnabled implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
      return securityMethodIn("ldap", "ad");
    }

  }

}

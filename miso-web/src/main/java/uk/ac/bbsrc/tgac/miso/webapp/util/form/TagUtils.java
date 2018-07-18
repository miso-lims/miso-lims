package uk.ac.bbsrc.tgac.miso.webapp.util.form;

import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class TagUtils {

  private static final Logger log = Logger.getLogger(TagUtils.class);

  public static boolean instanceOf(Object o, String className) {
    try {
      return Class.forName(className).isInstance(o);
    } catch (ClassNotFoundException e) {
      log.error(String.format("Failed to find the class name '%s'. %s", className, e.getMessage()), e);
      return false;
    }
  }

  public static boolean isCurrentUser(String username) {
    return username.toLowerCase()
        .equals(getAuthentication().getName().toLowerCase());
  }

  public static boolean isAdmin() {
    return hasRole("ROLE_ADMIN");
  }

  public static boolean isTech() {
    return hasRole("ROLE_TECH");
  }

  private static boolean hasRole(String role) {
    for (GrantedAuthority authority : getAuthentication().getAuthorities()) {
      if (authority.getAuthority().toUpperCase().equals(role)) {
        return true;
      }
    }
    return false;
  }

  private static Authentication getAuthentication() {
    return SecurityContextHolder.getContextHolderStrategy().getContext().getAuthentication();
  }

}

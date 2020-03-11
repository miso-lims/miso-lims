package uk.ac.bbsrc.tgac.miso.webapp.util.form;

import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import uk.ac.bbsrc.tgac.miso.Version;

public class TagUtils {

  private static final Logger log = Logger.getLogger(TagUtils.class);

  private TagUtils() {
    throw new IllegalStateException("Static util class not intended for instantiation");
  }

  public static boolean instanceOf(Object o, String className) {
    try {
      return Class.forName(className).isInstance(o);
    } catch (ClassNotFoundException e) {
      log.error(String.format("Failed to find the class name '%s'. %s", className, e.getMessage()), e);
      return false;
    }
  }

  public static boolean isCurrentUser(String username) {
    Authentication auth = getAuthentication();
    if (auth == null || auth.getName() == null) {
      return false;
    }
    return username.equalsIgnoreCase(auth.getName());
  }

  public static boolean isAdmin() {
    return hasRole("ROLE_ADMIN");
  }

  private static boolean hasRole(String role) {
    Authentication auth = getAuthentication();
    if (auth != null && auth.getAuthorities() != null) {
      for (GrantedAuthority authority : auth.getAuthorities()) {
        if (authority.getAuthority().equalsIgnoreCase(role)) {
          return true;
        }
      }
    }
    return false;
  }

  private static Authentication getAuthentication() {
    return SecurityContextHolder.getContextHolderStrategy().getContext().getAuthentication();
  }

  public static String version() {
    return Version.VERSION;
  }

}

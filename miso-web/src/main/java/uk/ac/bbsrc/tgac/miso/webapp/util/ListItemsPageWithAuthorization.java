package uk.ac.bbsrc.tgac.miso.webapp.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Supplier;

import org.springframework.security.core.context.SecurityContextHolder;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ListItemsPageWithAuthorization extends ListItemsPage {

  private final Supplier<SecurityManager> securityManager;

  public ListItemsPageWithAuthorization(String targetType, Supplier<SecurityManager> securityManager) {
    super(targetType);
    this.securityManager = securityManager;
  }

  @Override
  protected final void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
    User user = securityManager.get().getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    config.put("isAdmin", user.isAdmin());
    config.put("isInternal", user.isInternal());
    config.put("isTech", Arrays.asList(user.getRoles()).contains("ROLE_TECH"));
    writeConfigurationExtra(mapper, config);
  }

  protected void writeConfigurationExtra(ObjectMapper mapper, ObjectNode config) throws IOException {
    // Optionally overridable if more parameters are needed
  }
}

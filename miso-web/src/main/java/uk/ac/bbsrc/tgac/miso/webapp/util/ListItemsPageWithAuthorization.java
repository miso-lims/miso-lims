package uk.ac.bbsrc.tgac.miso.webapp.util;

import java.io.IOException;
import java.util.function.Supplier;

import com.eaglegenomics.simlims.core.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;

public class ListItemsPageWithAuthorization extends ListItemsPage {

  private final Supplier<AuthorizationManager> authorizationManager;

  public ListItemsPageWithAuthorization(String targetType, Supplier<AuthorizationManager> authorizationManager) {
    super(targetType);
    this.authorizationManager = authorizationManager;
  }

  @Override
  protected final void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
    AuthorizationManager manager = authorizationManager.get();
    User user = manager.getCurrentUser();
    config.put("isAdmin", user.isAdmin());
    config.put("isInternal", user.isInternal());
    writeConfigurationExtra(mapper, config);
  }

  protected void writeConfigurationExtra(ObjectMapper mapper, ObjectNode config) throws IOException {
    // Optionally overridable if more parameters are needed
  }
}

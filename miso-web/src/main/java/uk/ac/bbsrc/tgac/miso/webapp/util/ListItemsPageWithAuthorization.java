package uk.ac.bbsrc.tgac.miso.webapp.util;

import java.io.IOException;

import com.eaglegenomics.simlims.core.User;

import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;

public class ListItemsPageWithAuthorization extends ListItemsPage {

  private AuthorizationManager authorizationManager;

  public ListItemsPageWithAuthorization(String targetType, AuthorizationManager authorizationManager,
      JsonMapper mapper) {
    super(targetType, mapper);
    this.authorizationManager = authorizationManager;
  }

  @Override
  protected final void writeConfiguration(JsonMapper mapper, ObjectNode config) throws IOException {
    User user = authorizationManager.getCurrentUser();
    config.put("isAdmin", user.isAdmin());
    config.put("isInternal", user.isInternal());
    writeConfigurationExtra(mapper, config);
  }

  protected void writeConfigurationExtra(JsonMapper mapper, ObjectNode config) throws IOException {
    // Optionally overridable if more parameters are needed
  }
}

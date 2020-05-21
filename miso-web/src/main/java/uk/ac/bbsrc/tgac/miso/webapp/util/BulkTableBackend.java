package uk.ac.bbsrc.tgac.miso.webapp.util;

import java.io.IOException;
import java.util.List;

import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Create a Handsontable for propagating or editing a particular entity type
 *
 * @param <Dto> The DTO for the entity being edited
 */
public abstract class BulkTableBackend<Dto> {

  private static final String OLD_JSP = "/WEB-INF/pages/handsontables.jsp";
  private static final String NEW_JSP = "/WEB-INF/pages/bulkPage.jsp";

  private final String targetType;
  private final Class<? extends Dto> dtoClass;

  public BulkTableBackend(String targetType, Class<? extends Dto> dtoClass) {
    super();
    this.targetType = targetType;
    this.dtoClass = dtoClass;
  }

  /**
   * 
   * @param model ModelMap
   * @param create indicates whether the IDs exist in the database already or if they will be created on save
   * @param title page title
   * @param dtos
   * @return
   * @throws IOException
   */
  protected final ModelAndView prepare(ModelMap model, boolean create, String title, List<Dto> dtos) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode config = mapper.createObjectNode();
    writeConfiguration(mapper, config);
    model.put("title", title);
    model.put("config", mapper.writeValueAsString(config));

    if (isNewInterface()) {
      model.put("target", targetType);
    } else {
      model.put("targetType", "HotTarget." + targetType);
      model.put("create", create);
      model.put("method", "Propagate");
    }

    model.put("input",
        mapper.writerFor(mapper.getTypeFactory().constructCollectionType(List.class, dtoClass)).writeValueAsString(dtos));
    return new ModelAndView(isNewInterface() ? NEW_JSP : OLD_JSP, model);
  }

  /**
   * Pass arbitrary configuration data to the front end so that it can display the correct interface.
   */
  protected abstract void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException;

  // TODO: remove this after migrating all targets to new interface
  protected boolean isNewInterface() {
    return false;
  }
}

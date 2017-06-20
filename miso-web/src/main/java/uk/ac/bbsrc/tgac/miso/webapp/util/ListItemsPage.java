package uk.ac.bbsrc.tgac.miso.webapp.util;

import java.io.IOException;

import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.Project;

public class ListItemsPage {
  private final String targetType;

  public ListItemsPage(String targetType) {
    this.targetType = targetType;
  }

  public final ModelAndView list(ModelMap model) throws IOException {
    return list("null", model);
  }

  private ModelAndView list(String projectId, ModelMap model) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode config = mapper.createObjectNode();
    writeConfiguration(mapper, config);
    model.put("config", mapper.writeValueAsString(config));
    model.put("targetType", "ListTarget." + targetType);
    model.put("projectId", projectId);
    return new ModelAndView("/pages/list.jsp", model);
  }

  public final ModelAndView listByProject(long project, ModelMap model) throws IOException {
    return list(Long.toString(project), model);
  }

  public final ModelAndView listByProject(Project project, ModelMap model) throws IOException {
    return listByProject(project.getId(), model);
  }

  /**
   * Pass arbitrary configuration data to the front end so that it can display the correct interface.
   */
  protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) {
  }
}

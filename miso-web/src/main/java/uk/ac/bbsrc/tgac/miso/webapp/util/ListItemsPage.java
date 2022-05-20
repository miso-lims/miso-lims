package uk.ac.bbsrc.tgac.miso.webapp.util;

import java.io.IOException;
import java.util.stream.Stream;

import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.Project;

public class ListItemsPage {
  private final String targetType;
  private final ObjectMapper mapper;

  public ListItemsPage(String targetType, ObjectMapper mapper) {
    this.targetType = targetType;
    this.mapper = mapper;
  }

  public final ModelAndView list(ModelMap model) throws IOException {
    prepare(model);
    model.put("projectId", "null");
    return new ModelAndView("/WEB-INF/pages/list.jsp", model);
  }

  public final <T> ModelAndView list(ModelMap model, Stream<T> data) throws IOException {
    ObjectMapper mapper = prepare(model);
    ArrayNode array = mapper.createArrayNode();
    data.forEach(array::addPOJO);
    model.put("data", mapper.writeValueAsString(array));
    return new ModelAndView("/WEB-INF/pages/listStatic.jsp", model);

  }

  public final ModelAndView listByProject(long project, ModelMap model) throws IOException {
    prepare(model);
    model.put("projectId", Long.toString(project));
    return new ModelAndView("/WEB-INF/pages/list.jsp", model);
  }

  public final ModelAndView listByProject(Project project, ModelMap model) throws IOException {
    return listByProject(project.getId(), model);
  }

  private ObjectMapper prepare(ModelMap model) throws IOException {
    ObjectNode config = mapper.createObjectNode();
    writeConfiguration(mapper, config);
    model.put("config", mapper.writeValueAsString(config));
    model.put("targetType", "ListTarget." + targetType);
    return mapper;
  }

  /**
   * Pass arbitrary configuration data to the front end so that it can display the correct interface.
   */
  protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
  }
}

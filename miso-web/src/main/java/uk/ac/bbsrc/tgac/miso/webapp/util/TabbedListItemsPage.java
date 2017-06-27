package uk.ac.bbsrc.tgac.miso.webapp.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;

public class TabbedListItemsPage {

  public static TabbedListItemsPage createForPlatformType(String targetType, RequestManager requestManager)
      throws IOException {
    return TabbedListItemsPage.createWithJson(targetType, "platformType", getPlatformTypes(requestManager), PlatformType::getKey,
        PlatformType::name);
  }
  public static <T> TabbedListItemsPage createWithJson(String targetType, String property, Stream<T> tabItems, Function<T, String> getName,
      Function<T, Object> getValue) {
    ObjectMapper mapper = new ObjectMapper();
    return new TabbedListItemsPage(targetType, property, tabItems.collect(Collectors.toMap(getName, v -> {
      try {
        return mapper.writeValueAsString(getValue.apply(v));
      } catch (JsonProcessingException e) {
        throw new IllegalStateException("Failed to serialised tab value as JSON", e);
      }
    }, (left, right) -> left, TreeMap::new)));

  }

  private static Stream<PlatformType> getPlatformTypes(RequestManager requestManager) throws IOException {
    Collection<PlatformType> platforms = requestManager.listActivePlatformTypes();
    if (platforms.size() > 0) {
      return platforms.stream();
    } else {
      return Arrays.stream(PlatformType.values());
    }
  }

  private final String property;
  private final SortedMap<String, String> tabs;
  private final String targetType;

  /**
   * Create a page which lists items broken into tabs.
   * 
   * @param targetType The ListTarget object group to use.
   * @param property The name of property to set in the JavaScript configuration to indicate which tab is selected.
   * @param tabs The tabs to create. The key is a HTML-encoded string for the tab name and the value is a JavaScript-encoded value to set
   *          the configuration property to.
   */
  public TabbedListItemsPage(String targetType, String property, SortedMap<String, String> tabs) {
    this.targetType = targetType;
    this.property = property;
    this.tabs = tabs;
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
    model.put("property", property);
    model.put("tabs", tabs);
    return new ModelAndView("/pages/listTabbed.jsp", model);
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

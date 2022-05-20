package uk.ac.bbsrc.tgac.miso.webapp.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentModelService;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;

public class TabbedListItemsPage {

  public static TabbedListItemsPage createForPlatformType(String targetType,
      InstrumentModelService instrumentModelService, ObjectMapper mapper) throws IOException {
    return new TabbedListItemsPage(targetType, "platformType",
        getPlatformTypes(instrumentModelService), PlatformType::getKey, PlatformType::name, mapper);
  }

  public static Stream<PlatformType> getPlatformTypes(InstrumentModelService instrumentModelService)
      throws IOException {
    Set<PlatformType> platforms = instrumentModelService.listActivePlatformTypes();

    if (platforms.size() > 0) {
      return platforms.stream();
    } else {
      return Arrays.stream(PlatformType.values());
    }
  }

  private final String property;
  private final Map<String, String> tabs;
  private final String targetType;
  private final ObjectMapper mapper;

  public <T> TabbedListItemsPage(String targetType, String property, Stream<T> tabItems, Function<T, String> getName,
      Function<T, Object> getValue, ObjectMapper mapper) {
    this(targetType, property, tabItems, Comparator.naturalOrder(), getName, getValue, mapper);
  }

  public <T> TabbedListItemsPage(String targetType, String property, Stream<T> tabItems, Comparator<String> tabSorter,
      Function<T, String> getName, Function<T, Object> getValue, ObjectMapper mapper) {
    this(targetType, property, tabItems.collect(Collectors.toMap(getName, v -> {
      try {
        return mapper.writeValueAsString(getValue.apply(v));
      } catch (JsonProcessingException e) {
        throw new IllegalStateException("Failed to serialised tab value as JSON", e);
      }
    }, (left, right) -> left, () -> tabSorter == null ? new LinkedHashMap<>() : new TreeMap<>(tabSorter))), mapper);
  }

  /**
   * Create a page which lists items broken into tabs.
   * 
   * @param targetType The ListTarget object group to use.
   * @param property The name of property to set in the JavaScript configuration to indicate which tab is selected.
   * @param tabs The tabs to create. The key is a HTML-encoded string for the tab name and the value is a JavaScript-encoded value to set
   *          the configuration property to.
   */
  public TabbedListItemsPage(String targetType, String property, Map<String, String> tabs, ObjectMapper mapper) {
    this.targetType = targetType;
    this.property = property;
    this.tabs = tabs;
    this.mapper = mapper;
  }

  public final ModelAndView list(ModelMap model) throws IOException {
    return list("null", model);
  }

  private ModelAndView list(String projectId, ModelMap model) throws IOException {
    ObjectNode config = mapper.createObjectNode();
    writeConfiguration(mapper, config);
    model.put("config", mapper.writeValueAsString(config));
    model.put("targetType", "ListTarget." + targetType);
    model.put("projectId", projectId);
    model.put("property", property);
    model.put("tabs", tabs);
    return new ModelAndView("/WEB-INF/pages/listTabbed.jsp", model);
  }

  public <V> ModelAndView list(Function<String, Stream<V>> getter, ModelMap model) throws IOException {
    ObjectNode config = mapper.createObjectNode();
    writeConfiguration(mapper, config);
    model.put("config", mapper.writeValueAsString(config));
    model.put("targetType", "ListTarget." + targetType);
    model.put("projectId", null);
    model.put("property", property);
    model.put("tabs", tabs);

    model.put("data", tabs.keySet().stream().collect(Collectors.toMap(Function.identity(), WhineyFunction.rethrow(key -> {
      ArrayNode array = mapper.createArrayNode();
      getter.apply(key).forEach(array::addPOJO);
      return mapper.writeValueAsString(array);
    }))));
    return new ModelAndView("/WEB-INF/pages/listTabbedStatic.jsp", model);
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
  protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
  }
}

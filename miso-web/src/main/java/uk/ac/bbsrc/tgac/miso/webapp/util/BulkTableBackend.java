package uk.ac.bbsrc.tgac.miso.webapp.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Create a Handsontable for propagating or editing a particular entity type
 *
 * @param <Model> The database model for the entity being edited
 * @param <Dto> The DTO for the entity being edited
 * @param <ParentModel> The database model for the entity from which the model can be propagated
 */
public abstract class BulkTableBackend<Model, Dto, ParentModel> {
  private final String name;
  private final String parentName;
  private final String targetType;

  public BulkTableBackend(String name, String parentName, String targetType) {
    super();
    this.name = name;
    this.parentName = parentName;
    this.targetType = targetType;
  }

  /**
   * Convert a model to its DTO representation
   */
  protected abstract Dto asDto(Model model);

  /**
   * Edit the specified entities.
   * 
   * @param idString A comma-delimited string of entity IDs.
   */
  public ModelAndView edit(String idString, ModelMap model) throws IOException {
    List<Long> ids = parseIds(idString);
    List<Dto> dtos = new ArrayList<>();
    for (Model item : load(ids)) {
      dtos.add(asDto(item));
    }
    return prepare(idString, model, false, "Edit " + name, dtos);
  }

  /**
   * The Java class of the DTO.
   */
  protected abstract Class<? extends Dto> getDtoClass();

  /**
   * Read all the specified models from the database by their IDs.
   */
  protected abstract Iterable<Model> load(List<Long> modelIds) throws IOException;

  /**
   * Read all the specified parents from the database by their IDs.
   */
  protected abstract Iterable<ParentModel> loadParents(List<Long> parentIds) throws IOException;

  /**
   * Create a DTO from the parent for propagation.
   */
  protected abstract Dto packageDtoFromParent(ParentModel item);

  private List<Long> parseIds(String idString) {
    String[] split = idString.split(",");
    List<Long> ids = new ArrayList<>();
    for (int i = 0; i < split.length; i++) {
      ids.add(Long.parseLong(split[i]));
    }
    return ids;
  }

  private ModelAndView prepare(String idString, ModelMap model, boolean create, String title, List<Dto> dtos) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode config = mapper.createObjectNode();
    writeConfiguration(mapper, config);
    model.put("title", title);
    model.put("config", mapper.writeValueAsString(config));
    model.put("targetType", "HotTarget." + targetType);
    model.put("create", create);
    model.put("input",
        mapper.writerFor(mapper.getTypeFactory().constructCollectionType(List.class, getDtoClass())).writeValueAsString(dtos));
    model.put("method", "Propagate");
    return new ModelAndView("/pages/handsontables.jsp", model);
  }

  /**
   * Create a view to propagate parents to new entities.
   * 
   * @param idString a comma-delimited list of parent IDs
   */
  public ModelAndView propagate(String idString, ModelMap model) throws IOException {
    List<Long> ids = parseIds(idString);
    List<Dto> dtos = new ArrayList<>();
    for (ParentModel item : loadParents(ids)) {
      dtos.add(packageDtoFromParent(item));
    }
    return prepare(idString, model, true, "Create " + name + " from " + parentName, dtos);
  }

  /**
   * Pass arbitrary configuration data to the front end so that it can display the correct interface.
   */
  protected abstract void writeConfiguration(ObjectMapper mapper, ObjectNode config);
}

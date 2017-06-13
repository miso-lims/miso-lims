package uk.ac.bbsrc.tgac.miso.webapp.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

/**
 * Create a Handsontable for editing a particular entity type
 *
 * @param <Model> The database model for the entity being edited
 * @param <Dto> The DTO for the entity being edited
 */
public abstract class BulkEditTableBackend<Model, Dto> extends BulkTableBackend<Dto> {
  private final String name;

  public BulkEditTableBackend(String targetType, Class<? extends Dto> dtoClass, String name) {
    super(targetType, dtoClass);
    this.name = name;
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
  public final ModelAndView edit(String idString, ModelMap model) throws IOException {
    List<Long> ids = parseIds(idString);
    List<Dto> dtos = new ArrayList<>();
    for (Model item : load(ids)) {
      dtos.add(asDto(item));
    }
    return prepare(model, false, "Edit " + name, dtos);
  }

  /**
   * Read all the specified models from the database by their IDs.
   */
  protected abstract Iterable<Model> load(List<Long> modelIds) throws IOException;

}

package uk.ac.bbsrc.tgac.miso.webapp.util;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * Create a Handsontable for editing a particular entity type
 *
 * @param <Model> The database model for the entity being edited
 * @param <Dto> The DTO for the entity being edited
 */
public abstract class BulkEditTableBackend<Model, Dto> extends BulkTableBackend<Dto> {
  private final String name;

  public BulkEditTableBackend(String targetType, Class<? extends Dto> dtoClass, String name, ObjectMapper mapper) {
    super(targetType, dtoClass, mapper);
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
    List<Long> ids = LimsUtils.parseIds(idString);
    return prepare(model, PageMode.EDIT, "Edit " + name, load(ids).map(this::asDto).collect(Collectors.toList()));
  }

  /**
   * Read all the specified models from the database by their IDs.
   */
  protected abstract Stream<Model> load(List<Long> modelIds) throws IOException;

}

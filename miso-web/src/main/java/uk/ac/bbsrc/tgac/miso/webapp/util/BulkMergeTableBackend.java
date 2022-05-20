package uk.ac.bbsrc.tgac.miso.webapp.util;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * Create a Handsontable for propagating a particular entity type
 *
 * @param <Dto> The DTO for the entity being propagated
 */
public abstract class BulkMergeTableBackend<Dto> extends BulkTableBackend<Dto> {
  private final String name;
  private final String parentName;

  public BulkMergeTableBackend(String targetType, Class<? extends Dto> dtoClass, String name, String parentName,
      ObjectMapper mapper) {
    super(targetType, dtoClass, mapper);
    this.name = name;
    this.parentName = parentName;
  }

  /**
   * Create a DTO from the parents' ids for propagation.
   * 
   * @throws IOException
   */
  protected abstract Dto createDtoFromParents(List<Long> parentIds) throws IOException;

  /**
   * Create a view to propagate parents to new entities.
   * 
   * @param idString a comma-delimited list of parent IDs
   */
  public final ModelAndView propagate(String idString, ModelMap model) throws IOException {
    List<Long> ids = LimsUtils.parseIds(idString);
    return prepare(model, PageMode.PROPAGATE, "Create " + name + " from " + parentName,
        Collections.singletonList(createDtoFromParents(ids)));
  }

}

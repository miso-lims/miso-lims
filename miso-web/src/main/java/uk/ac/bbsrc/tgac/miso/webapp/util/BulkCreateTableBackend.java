package uk.ac.bbsrc.tgac.miso.webapp.util;

import java.io.IOException;
import java.util.Collections;

import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

/**
 * Create a Handsontable for creating a particular entity type that has no parent
 *
 * @param <Dto> The DTO for the entity being edited
 */
public abstract class BulkCreateTableBackend<Dto> extends BulkTableBackend<Dto> {
  private final String name;
  private final Dto dto;
  private final Integer quantity;

  public BulkCreateTableBackend(String targetType, Class<? extends Dto> dtoClass, String name, Dto dto, Integer quantity) {
    super(targetType, dtoClass);
    this.name = name;
    this.dto = dto;
    this.quantity = quantity;
  }

  public final ModelAndView create(ModelMap model) throws IOException {
    return prepare(model, true, "Create " + name, Collections.nCopies(quantity, dto));
  }

}

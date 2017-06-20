package uk.ac.bbsrc.tgac.miso.webapp.util;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Create a Handsontable for creating a particular entity type that has no parent
 *
 * @param <Dto> The DTO for the entity being edited
 */
public class BulkCreateTableBackend<Dto> extends BulkTableBackend<Dto> {
  private final String name;
  private final Dto dto;

  public BulkCreateTableBackend(String targetType, Class<? extends Dto> dtoClass, String name, Dto dto) {
    super(targetType, dtoClass);
    this.name = name;
    this.dto = dto;
  }

  public final ModelAndView create(ModelMap model) throws IOException {
    List<Dto> dtos = Collections.singletonList(dto);
    return prepare(model, true, "Create " + name, dtos);
  }

  @Override
  protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) {

  }

}

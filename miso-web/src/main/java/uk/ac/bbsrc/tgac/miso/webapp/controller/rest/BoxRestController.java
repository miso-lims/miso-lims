package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView.BoxableId;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.BoxDto;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.BoxService;

@Controller
@RequestMapping("/rest")
public class BoxRestController extends RestController {
  @Autowired
  private BoxService boxService;

  private final JQueryDataTableBackend<Box, BoxDto> jQueryBackend = new JQueryDataTableBackend<Box, BoxDto>() {
    @Override
    protected BoxDto asDto(Box model) {
      return Dtos.asDto(model, false);
    }

    @Override
    protected PaginatedDataSource<Box> getSource() throws IOException {
      return boxService;
    }
  };

  @RequestMapping(value = "/box/dt", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<BoxDto> dataTable(HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder);
  }

  @RequestMapping(value = "/box/dt/use/{id}", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<BoxDto> dataTableByUse(@PathVariable("id") Long id, HttpServletRequest request,
      HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.boxUse(id));
  }

  @RequestMapping(value = "/boxes/rest/", method = RequestMethod.GET)
  public @ResponseBody Collection<Box> jsonRest() throws IOException {
    return boxService.list(0, 0, true, "id");
  }

  @RequestMapping(value = "/box/{boxId}/position/{position}", method = RequestMethod.PUT, consumes = { "application/json" },
      produces = { "application/json" })
  public @ResponseBody BoxDto setPosition(@PathVariable("boxId") Long boxId, @PathVariable("position") String position,
      @RequestParam("entity") String entity) throws IOException {
    BoxableId id = parseEntityIdentifier(entity);
    Box box = boxService.get(boxId);
    if (!box.isValidPosition(position)) {
      throw new RestException("Invalid position given: " + position, Status.BAD_REQUEST);
    }
    BoxableView boxable = boxService.getBoxableView(id);
    if (boxable == null) {
      throw new RestException(String.format("Item not found (type=%s, id=%s)", id.getTargetType(), id.getTargetId()),
          Status.BAD_REQUEST);
    } else if (boxable.isDiscarded()) {
      throw new RestException("Cannot add discarded item to box", Status.BAD_REQUEST);
    }
    // if the selected item is already in the box, remove it here and add it to the correct position in next step
    if (Long.valueOf(box.getId()).equals(boxable.getBoxId())) {
      box.removeBoxable(boxable.getBoxPosition());
    }
    // if an item already exists at this position, its location will be set to unknown.
    box.setBoxable(position, boxable);
    boxService.save(box);
    Box saved = boxService.get(boxId);
    return Dtos.asDto(saved, true);
  }

  @RequestMapping(value = "/boxes/search")
  public @ResponseBody List<BoxDto> search(@RequestParam("q") String search) {
    List<Box> results = boxService.getBySearch(search);
    return Dtos.asBoxDtos(results, true);
  }

  private static BoxableId parseEntityIdentifier(String identifier) {
    try {
      String[] pieces = identifier.split(":");
      EntityType et = EntityType.valueOf(pieces[0]);
      long id = Long.parseLong(pieces[1]);
      return new BoxableId(et, id);
    } catch (NullPointerException | IllegalArgumentException e) {
      throw new RestException("Invalid entity identifier: " + identifier, Status.BAD_REQUEST);
    }
  }

  public void setBoxService(BoxService boxService) {
    this.boxService = boxService;
  }

}

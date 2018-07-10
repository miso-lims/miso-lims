package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.dto.BoxableDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.BoxService;

@Controller
@RequestMapping("/rest/boxables")

public class BoxableRestController extends RestController {
  @Autowired
  private BoxService boxService;

  @GetMapping(value = "/search", produces = "application/json")
  @ResponseBody
  public List<BoxableDto> search(@RequestParam("q") String search, HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) {
    List<BoxableView> results = boxService.getBoxableViewsBySearch(search);
    return Dtos.asBoxableDtos(results);
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    List<Box> boxes = new ArrayList<>();
    for (Long id : ids) {
      if (id == null) {
        throw new RestException("Cannot delete null box", Status.BAD_REQUEST);
      }
      Box box = boxService.get(id);
      if (box == null) {
        throw new RestException("Box " + id + " not found", Status.BAD_REQUEST);
      }
      boxes.add(box);
    }
    boxService.bulkDelete(boxes);
  }

}

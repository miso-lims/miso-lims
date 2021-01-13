package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.service.BoxService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.BoxableDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

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

  @PostMapping(value = "/query-by-box", produces = { "application/json" })
  @ResponseBody
  public List<BoxableDto> getSamplesInBulk(@RequestBody List<String> names) throws IOException {
    List<Box> boxes = PaginationFilter.bulkSearch(names, boxService, box -> box,
        message -> new RestException(message, Status.BAD_REQUEST));

    List<BoxableDto> dtos = new ArrayList<>();
    for (Box box : boxes) {
      List<BoxableView> contents = boxService.getBoxContents(box.getId());
      for (BoxableView item : contents) {
        dtos.add(Dtos.asDto(item));
      }
    }
    return dtos;
  }

}

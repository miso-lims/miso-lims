package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.box.BoxableView;
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
  public List<BoxableDto> search(@RequestParam("q") String search, HttpServletRequest request,
      HttpServletResponse response,
      UriComponentsBuilder uriBuilder) {
    List<BoxableView> results = boxService.getBoxableViewsBySearch(search);
    return Dtos.asBoxableDtos(results);
  }

  @PostMapping(value = "/query-by-box", produces = {"application/json"})
  @ResponseBody
  public List<BoxableDto> getBoxablesInBulk(@RequestBody List<String> names) throws IOException {
    List<Box> boxes = boxService.list(0, 0, true, "id", PaginationFilter.bulkLookup(names));

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

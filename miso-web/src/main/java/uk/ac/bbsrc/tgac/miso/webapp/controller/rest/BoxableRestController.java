package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.service.BoxService;
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

}

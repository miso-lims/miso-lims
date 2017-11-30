package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.dto.BoxableDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.BoxService;

@Controller
@RequestMapping("/rest/boxables")

public class BoxableRestController extends RestController {
  @Autowired
  private BoxService boxService;

  @RequestMapping(value = "/search", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public List<BoxableDto> search(@RequestParam("q") String search, HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    List<BoxableView> results = boxService.getBoxableViewsBySearch(search);
    return Dtos.asBoxableDtos(results);
  }

}

package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BarcodableView;
import uk.ac.bbsrc.tgac.miso.core.service.BarcodableViewService;
import uk.ac.bbsrc.tgac.miso.dto.BarcodableDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;

@Controller
@RequestMapping("/rest/barcodables")
public class BarcodableSearchRestController extends AbstractRestController {
  private static final String BASEURL = "/miso";

  @Autowired
  private BarcodableViewService barcodableViewService;

  @GetMapping(value = "/search")
  @ResponseBody
  public List<BarcodableDto> search(@RequestParam(name = "q", required = false) String query) {
    if (isStringEmptyOrNull(query)) {
      return new ArrayList<>();
    }

    return barcodableViewService.search(query).stream().map(this::toDto).collect(Collectors.toList());
  }

  private BarcodableDto toDto(BarcodableView view) {
    BarcodableDto dto = Dtos.asDto(view);

    String urlComponent = makeUrlComponent(view.getId().getTargetType());
    if (urlComponent != null) {
      dto.setUrl(makeUrl(urlComponent, view.getId().getTargetId()));
    }

    return dto;
  }

  private String makeUrlComponent(Barcodable.EntityType entityType) {
    switch (entityType) {
      case SAMPLE:
        return "sample";
      case BOX:
        return "box";
      case POOL:
        return "pool";
      case LIBRARY:
        return "library";
      case LIBRARY_ALIQUOT:
        return "libraryaliquot";
      case CONTAINER:
        return "container";
      default:
        return null;
    }
  }

  private String makeUrl(String urlComponent, long id) {
    return BASEURL + "/" + urlComponent + "/" + Long.toString(id);
  }
}

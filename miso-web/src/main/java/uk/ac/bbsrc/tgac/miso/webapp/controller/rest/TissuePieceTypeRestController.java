package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import uk.ac.bbsrc.tgac.miso.core.service.TissuePieceTypeService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.TissuePieceTypeDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;

@Controller
@RequestMapping("/rest/tissuepiecetypes")
public class TissuePieceTypeRestController extends RestController {

  @Autowired
  private TissuePieceTypeService tissuePieceTypeService;

  @Autowired
  private ConstantsController constantsController;

  @PostMapping
  public @ResponseBody TissuePieceTypeDto create(@RequestBody TissuePieceTypeDto dto) throws IOException {
    return RestUtils.createObject("Tissue Piece Type", dto, Dtos::to, tissuePieceTypeService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PutMapping("/{typeId}")
  public @ResponseBody TissuePieceTypeDto update(@PathVariable long typeId, @RequestBody TissuePieceTypeDto dto) throws IOException {
    return RestUtils.updateObject("Tissue Piece Type", typeId, dto, Dtos::to, tissuePieceTypeService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Tissue Piece Type", ids, tissuePieceTypeService);
    constantsController.refreshConstants();
  }

}

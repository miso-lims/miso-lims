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

import uk.ac.bbsrc.tgac.miso.core.service.PartitionQcTypeService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.PartitionQCTypeDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;

@Controller
@RequestMapping("/rest/partitionqctypes")
public class PartitionQcTypeRestController extends RestController {

  @Autowired
  private PartitionQcTypeService partitionQcTypeService;

  @Autowired
  private ConstantsController constantsController;

  @PostMapping
  public @ResponseBody PartitionQCTypeDto create(@RequestBody PartitionQCTypeDto dto) throws IOException {
    return RestUtils.createObject("Partition QC Type", dto, Dtos::to, partitionQcTypeService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PutMapping("/{typeId}")
  public @ResponseBody PartitionQCTypeDto update(@PathVariable long typeId, @RequestBody PartitionQCTypeDto dto) throws IOException {
    return RestUtils.updateObject("Partition QC Type", typeId, dto, Dtos::to, partitionQcTypeService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Partition QC Type", ids, partitionQcTypeService);
  }

}

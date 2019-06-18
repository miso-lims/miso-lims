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

import uk.ac.bbsrc.tgac.miso.core.service.OrderPurposeService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.OrderPurposeDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.MenuController;

@Controller
@RequestMapping("/rest/orderpurposes")
public class OrderPurposeRestController extends RestController {

  @Autowired
  private OrderPurposeService orderPurposeService;

  @Autowired
  private MenuController menuController;

  @PostMapping
  public @ResponseBody OrderPurposeDto create(@RequestBody OrderPurposeDto dto) throws IOException {
    return RestUtils.createObject("Order Purpose", dto, Dtos::to, orderPurposeService, d -> {
      menuController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PutMapping("/{purposeId}")
  public @ResponseBody OrderPurposeDto update(@PathVariable long purposeId, @RequestBody OrderPurposeDto dto) throws IOException {
    return RestUtils.updateObject("Order Purpose", purposeId, dto, Dtos::to, orderPurposeService, d -> {
      menuController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Order Purpose", ids, orderPurposeService);
  }

}

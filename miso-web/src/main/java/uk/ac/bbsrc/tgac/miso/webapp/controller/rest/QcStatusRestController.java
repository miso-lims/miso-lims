package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.QcStatusUpdate;
import uk.ac.bbsrc.tgac.miso.core.service.QcStatusService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.QcNodeDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;

@Controller
@RequestMapping("/rest/qcstatuses")
public class QcStatusRestController extends AbstractRestController {

  @Autowired
  private QcStatusService qcStatusService;

  @PutMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public @ResponseBody void update(@RequestBody QcNodeDto dto) throws IOException {
    QcStatusUpdate update = Dtos.to(dto);
    qcStatusService.update(update);
  }

  @PutMapping("/bulk")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public @ResponseBody void bulkUpdate(@RequestBody List<QcNodeDto> dtos) throws IOException {
    List<QcStatusUpdate> updates = dtos.stream().map(Dtos::to).collect(Collectors.toList());
    qcStatusService.update(updates);
  }

}

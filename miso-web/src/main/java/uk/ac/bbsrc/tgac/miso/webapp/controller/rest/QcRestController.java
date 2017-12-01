package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import uk.ac.bbsrc.tgac.miso.core.data.QC;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.QcDto;
import uk.ac.bbsrc.tgac.miso.service.QualityControlService;

@Controller
@RequestMapping("/rest/qc")
public class QcRestController extends RestController {
  @Autowired
  private QualityControlService qcService;

  @RequestMapping(method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public QcDto create(@RequestBody QcDto qc) throws IOException {
    QC result = qcService.createQC(Dtos.to(qc));
    return Dtos.asDto(result);
  }

  @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public QcDto update(@PathVariable("id") Long id, @RequestBody QcDto qc) throws IOException {
    QC updated = Dtos.to(qc);
    updated.setId(id);
    return Dtos.asDto(qcService.updateQc(updated));
  }

}

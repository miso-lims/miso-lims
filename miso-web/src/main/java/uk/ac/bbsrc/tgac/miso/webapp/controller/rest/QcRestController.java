package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.ws.rs.core.Response.Status;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.service.QualityControlService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.QcDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.RestException;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AsyncOperationManager;

@Controller
@RequestMapping("/rest/qcs")
public class QcRestController extends AbstractRestController {
  @Autowired
  private QualityControlService qcService;
  @Autowired
  private AsyncOperationManager asyncOperationManager;

  @PostMapping(headers = {"Content-type=application/json"})
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public QcDto create(@RequestBody QcDto qc) throws IOException {
    QC result = qcService.create(Dtos.to(qc));
    return Dtos.asDto(result);
  }

  @PutMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public QcDto update(@PathVariable("id") Long id, @RequestBody QcDto qc) throws IOException {
    QC updated = Dtos.to(qc);
    updated.setId(id);
    return Dtos.asDto(qcService.update(updated));
  }

  @PostMapping("/bulk")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public @ResponseBody ObjectNode bulkCreateAsync(@RequestBody List<QcDto> dtos) throws IOException {
    return asyncOperationManager.startAsyncBulkQcCreate(dtos);
  }

  @PutMapping("/bulk")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public @ResponseBody ObjectNode bulkUpdateAsync(@RequestBody List<QcDto> dtos) throws IOException {
    return asyncOperationManager.startAsyncBulkQcUpdate(dtos);
  }

  @GetMapping("/bulk/{uuid}")
  public @ResponseBody ObjectNode getProgress(@PathVariable String uuid) throws Exception {
    return asyncOperationManager.getAsyncQcProgress(uuid);
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public @ResponseBody void bulkDelete(@RequestParam String qcTarget, @RequestBody(required = true) List<Long> ids)
      throws IOException {
    List<QC> items = new ArrayList<>();
    QcTarget target = null;
    try {
      target = QcTarget.valueOf(qcTarget);
    } catch (IllegalArgumentException e) {
      throw new RestException("Unsupported QC target: " + qcTarget, Status.BAD_REQUEST);
    }
    for (Long id : ids) {
      if (id == null) {
        throw new RestException("QC id cannot be null", Status.BAD_REQUEST);
      }
      QC item = qcService.get(target, id);
      if (item == null) {
        throw new RestException(String.format("No %s QC found with ID: %d", qcTarget, id), Status.BAD_REQUEST);
      }
      items.add(item);
    }
    qcService.bulkDelete(items);
  }

}

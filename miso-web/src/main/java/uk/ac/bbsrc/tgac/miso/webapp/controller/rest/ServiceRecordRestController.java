package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response.Status;

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

import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentService;
import uk.ac.bbsrc.tgac.miso.core.service.ServiceRecordService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.ServiceRecordDto;

@Controller
@RequestMapping("/rest/instruments/{instrumentId}/servicerecords")
public class ServiceRecordRestController extends RestController {

  @Autowired
  private ServiceRecordService serviceRecordService;
  @Autowired
  private InstrumentService instrumentService;

  @PostMapping(value = "/bulk-delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public @ResponseBody void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    List<ServiceRecord> records = new ArrayList<>();
    for (Long id : ids) {
      if (id == null) {
        throw new RestException("Cannot delete null service record", Status.BAD_REQUEST);
      }
      ServiceRecord record = serviceRecordService.get(id);
      if (record == null) {
        throw new RestException("Service record " + id + " not found", Status.BAD_REQUEST);
      }
      records.add(record);
    }
    serviceRecordService.bulkDelete(records);
  }

  @PostMapping
  public @ResponseBody ServiceRecordDto create(@PathVariable long recordId, long instrumentId,
      @RequestBody ServiceRecordDto dto)
      throws IOException {
    ServiceRecord record = serviceRecordService.get(recordId);
    Instrument instrument = instrumentService.get(instrumentId);
    instrumentService.addServiceRecord(record, instrument);
    return RestUtils.createObject("Service record", dto, Dtos::to, serviceRecordService, Dtos::asDto);
  }

  @PutMapping("/{recordId}")
  public @ResponseBody ServiceRecordDto update(@PathVariable long recordId, @RequestBody ServiceRecordDto dto)
      throws IOException {
    return RestUtils.updateObject("Service record", recordId, dto, Dtos::to, serviceRecordService, Dtos::asDto);
  }

}

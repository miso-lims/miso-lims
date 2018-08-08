/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.manager.FilesManager;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.service.InstrumentService;
import uk.ac.bbsrc.tgac.miso.service.ServiceRecordService;

@Controller
@RequestMapping("/instrument/servicerecord")
@SessionAttributes("serviceRecord")
public class EditServiceRecordController {
  
  protected static final Logger log = LoggerFactory.getLogger(EditServiceRecordController.class);
  
  private enum ModelKeys {
    RECORD("serviceRecord"),
    FILES("serviceRecordFiles");
    
    private final String key;
    
    ModelKeys(String key) {
      this.key = key;
    }

    public String getKey() {
      return key;
    }
  }

  @Autowired
  private InstrumentService instrumentService;
  @Autowired
  private ServiceRecordService serviceRecordService;
  @Autowired
  private FilesManager filesManager;

  @Value("${miso.timeCorrection.uiZone:}")
  private String uiZone;

  PropertyEditor timestampEditor = new PropertyEditorSupport() {
    @Override
    public void setAsText(String text) {
      if (LimsUtils.isStringEmptyOrNull(text)) {
        setValue(null);
      } else {
        // Set UI timezone so that Hibernate can save correctly in the DB timezone
        DateFormat format = LimsUtils.getDateTimeFormat();
        if (!LimsUtils.isStringEmptyOrNull(uiZone)) {
          format.setTimeZone(TimeZone.getTimeZone(uiZone));
        }
        try {
          setValue(format.parse(text));
        } catch (ParseException e) {
          throw new IllegalArgumentException("Invalid datetime string");
        }
      }
    }

    @Override
    public String getAsText() {
      Date value = (Date) getValue();
      // TimeShiftingInterceptor will have already shifted to UI time
      return value == null ? "" : LimsUtils.formatDateTime(value);
    }
  };

  public void setFilesManager(FilesManager filesManager) {
    this.filesManager = filesManager;
  }
  
  @ModelAttribute("maxLengths")
  public Map<String, Integer> maxLengths() throws IOException {
    return serviceRecordService.getColumnSizes();
  }

  @RequestMapping(value = "/{recordId}", method = RequestMethod.GET)
  public ModelAndView viewServiceRecord(@PathVariable(value = "recordId") Long recordId, ModelMap model) throws IOException {
    ServiceRecord sr = serviceRecordService.get(recordId);
    if (sr != null) {
      model.put(ModelKeys.RECORD.getKey(), sr);
      model.put("title", "Service Record " + sr.getId());
    } else {
      throw new NotFoundException("No service found for ID " + recordId.toString());
    }
    return new ModelAndView("/pages/editServiceRecord.jsp", model);
  }
  
  @RequestMapping(value = "/new/{instrumentId}", method = RequestMethod.GET)
  public ModelAndView newServiceRecord(@PathVariable(value = "instrumentId") Long instrumentId, ModelMap model) throws IOException {
    Instrument instrument = instrumentService.get(instrumentId);
    if (instrument == null) throw new NotFoundException("No instrument found for ID " + instrumentId.toString());
    ServiceRecord record = new ServiceRecord();
    record.setInstrument(instrument);
    record.setServiceDate(new Date());
    model.put(ModelKeys.RECORD.getKey(), record);
    model.put("title", "New Service Record");
    return new ModelAndView("/pages/editServiceRecord.jsp", model);
  }
  
  @RequestMapping(method = RequestMethod.POST)
  public String processSubmit(@ModelAttribute("serviceRecord") ServiceRecord record, ModelMap model, SessionStatus session)
      throws IOException {
    Long recordId = null;
    if (record.getId() == ServiceRecord.UNSAVED_ID) {
      recordId = serviceRecordService.create(record);
    } else {
      serviceRecordService.update(record);
      recordId = record.getId();
    }
    session.setComplete();
    model.clear();
    return "redirect:/miso/instrument/servicerecord/" + recordId;
  }
  
  @InitBinder
  public void initBinder(WebDataBinder binder) {
    binder.registerCustomEditor(Date.class, "shutdownTime", timestampEditor);
    binder.registerCustomEditor(Date.class, "restoredTime", timestampEditor);
  }

}

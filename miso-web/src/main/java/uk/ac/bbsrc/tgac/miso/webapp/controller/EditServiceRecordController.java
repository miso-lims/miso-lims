package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;

@Controller
@RequestMapping("/stats/sequencer/servicerecord")
@SessionAttributes("serviceRecord")
public class EditServiceRecordController {
  
  protected static final Logger log = LoggerFactory.getLogger(EditServiceRecordController.class);
  
  @Autowired
  private RequestManager requestManager;

  @Autowired
  private DataObjectFactory dataObjectFactory;

  public void setRequestManager(uk.ac.bbsrc.tgac.miso.core.manager.RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
  }

  @RequestMapping(value = "/{recordId}", method = RequestMethod.GET)
  public ModelAndView viewServiceRecord(@PathVariable(value = "recordId") Long recordId, ModelMap model) throws IOException {
    SequencerServiceRecord sr = requestManager.getSequencerServiceRecordById(recordId);
    if (sr != null) {
      model.put("serviceRecord", sr);
    } else {
      throw new IOException("Cannot retrieve the named Service record");
    }
    return new ModelAndView("/pages/editServiceRecord.jsp", model);
  }
  
  @RequestMapping(value = "/new/{sequencerReferenceId}", method = RequestMethod.GET)
  public ModelAndView newServiceRecord(@PathVariable(value = "sequencerReferenceId") Long sequencerReferenceId, ModelMap model) throws IOException {
    SequencerReference sequencer = requestManager.getSequencerReferenceById(sequencerReferenceId);
    if (sequencer == null) {
      throw new IOException("Cannot retrieve the requested Sequencer Reference");
    }
    SequencerServiceRecord record = dataObjectFactory.getSequencerServiceRecord();
    record.setSequencerReference(sequencer);
    model.put("serviceRecord", record);
    return new ModelAndView("/pages/editServiceRecord.jsp", model);
  }
  
  @RequestMapping(method = RequestMethod.POST)
  public String processSubmit(@ModelAttribute("serviceRecord") SequencerServiceRecord record, ModelMap model, SessionStatus session)
      throws IOException {
    try {
      requestManager.saveSequencerServiceRecord(record);
      session.setComplete();
      model.clear();
      return "redirect:/miso/stats/sequencer/" + record.getSequencerReference().getId();
    } catch (IOException ex) {
      log.debug("Failed to save Sequencer Service Record", ex);
      throw ex;
    }
  }

}

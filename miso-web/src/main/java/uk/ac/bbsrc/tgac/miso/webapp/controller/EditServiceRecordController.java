package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.jdbc.core.JdbcTemplate;
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

import uk.ac.bbsrc.tgac.miso.core.data.AbstractSequencerServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.FilesManager;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@Controller
@RequestMapping("/stats/sequencer/servicerecord")
@SessionAttributes("serviceRecord")
public class EditServiceRecordController {
  
  protected static final Logger log = LoggerFactory.getLogger(EditServiceRecordController.class);
  
  @Autowired
  private RequestManager requestManager;

  @Autowired
  private FilesManager filesManager;

  @Autowired
  private DataObjectFactory dataObjectFactory;
  
  @Autowired
  private JdbcTemplate jdbcTemplate;

  public void setInterfaceTemplate(JdbcTemplate interfaceTemplate) {
    this.jdbcTemplate = interfaceTemplate;
  }

  public void setRequestManager(uk.ac.bbsrc.tgac.miso.core.manager.RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setFilesManager(FilesManager filesManager) {
    this.filesManager = filesManager;
  }

  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
  }
  
  public Map<Integer, String> populateServiceRecordFiles(SequencerServiceRecord record) throws IOException {
    if (record.getId() != AbstractSequencerServiceRecord.UNSAVED_ID) {
      Map<Integer, String> fileMap = new HashMap<Integer, String>();
      for (String s : filesManager.getFileNames(SequencerServiceRecord.class, String.valueOf(record.getId()))) {
        fileMap.put(s.hashCode(), s);
      }
      return fileMap;
    }
    return Collections.emptyMap();
  }
  
  @ModelAttribute("maxLengths")
  public Map<String, Integer> maxLengths() throws IOException {
    return DbUtils.getColumnSizes(jdbcTemplate, "SequencerServiceRecord");
  }

  @RequestMapping(value = "/{recordId}", method = RequestMethod.GET)
  public ModelAndView viewServiceRecord(@PathVariable(value = "recordId") Long recordId, ModelMap model) throws IOException {
    SequencerServiceRecord sr = requestManager.getSequencerServiceRecordById(recordId);
    if (sr != null) {
      model.put("serviceRecord", sr);
      model.put("serviceRecordFiles", populateServiceRecordFiles(sr));
    } else {
      throw new IOException("Cannot retrieve the requested Service Record");
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
      return "redirect:/miso/stats/sequencer/servicerecord/" + record.getId();
    } catch (IOException ex) {
      log.debug("Failed to save Sequencer Service Record", ex);
      throw ex;
    }
  }
  
  @InitBinder
  public void initBinder(WebDataBinder binder) {
    // set format for datetime data bindings
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    CustomDateEditor dateEditor = new CustomDateEditor(dateFormat, true);
    binder.registerCustomEditor(Date.class, "shutdownTime", dateEditor);
    binder.registerCustomEditor(Date.class, "restoredTime", dateEditor);
  }

}

package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractBox;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.integration.BoxScanner;
import uk.ac.bbsrc.tgac.miso.webapp.context.ApplicationContextProvider;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoPropertyExporter;

@Controller
@RequestMapping("/box")
@SessionAttributes("box")
public class EditBoxController {
  protected static final Logger log = LoggerFactory.getLogger(EditBoxController.class);

  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private RequestManager requestManager;

  @Autowired
  private DataObjectFactory dataObjectFactory;

  @Autowired
  private JdbcTemplate interfaceTemplate;

  @Autowired
  private BoxScanner boxScanner;

  public void setInterfaceTemplate(JdbcTemplate interfaceTemplate) {
    this.interfaceTemplate = interfaceTemplate;
  }

  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @ModelAttribute("scannerEnabled")
  public Boolean isScannerEnabled() {
    MisoPropertyExporter exporter = (MisoPropertyExporter) ApplicationContextProvider.getApplicationContext().getBean("propertyConfigurer");
    Map<String, String> misoProperties = exporter.getResolvedProperties();
    return misoProperties.containsKey("miso.boxscanner.enabled") && Boolean.parseBoolean(misoProperties.get("miso.boxscanner.enabled"));
  }

  public List<String> boxSizesAsRowsByColumns() throws IOException {
    List<String> sizes = new ArrayList<String>();
    for (BoxSize boxSize : requestManager.listAllBoxSizes()) {
      sizes.add("\"" + boxSize.getRowsByColumns() + "\"" + ":" + "\"" + boxSize.getRowsByColumns() + "\"");
    }
    return sizes;
  }

  @RequestMapping(value = "/new", method = RequestMethod.GET)
  public ModelAndView newBox(ModelMap model) throws IOException {
    return setupForm(AbstractBox.UNSAVED_ID, model);
  }

  @RequestMapping(value = "/rest/{boxId}", method = RequestMethod.GET)
  public @ResponseBody Box jsonRest(@PathVariable Long boxId) throws IOException {
    return requestManager.getBoxById(boxId);
  }

  @RequestMapping(value = "/rest/changes", method = RequestMethod.GET)
  public @ResponseBody Collection<ChangeLog> jsonRestChanges() throws IOException {
    return requestManager.listAllChanges("Box");
  }

  @RequestMapping(value = "/{boxId}", method = RequestMethod.GET)
  public ModelAndView setupForm(@PathVariable Long boxId, ModelMap model) throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Box box = null;

      if (boxId == AbstractBox.UNSAVED_ID) {
        box = dataObjectFactory.getBox(user);
        model.put("title", "New Box");
      } else {
        box = requestManager.getBoxById(boxId);
        model.put("title", "Box " + boxId);
      }

      if (box == null) {
        throw new SecurityException("No such Box");
      }
      if (!box.userCanRead(user)) {
        throw new SecurityException("Permission denied.");
      }

      model.put("formObj", box);
      model.put("box", box);

      // add all BoxUses
      LinkedHashMap<Long, String> uses = new LinkedHashMap<Long, String>();
      for (BoxUse boxUse : requestManager.listAllBoxUses()) {
        uses.put(boxUse.getId(), boxUse.getAlias());
      }
      model.put("boxUses", uses);

      // add all BoxSizes
      LinkedHashMap<Long, String> sizes = new LinkedHashMap<Long, String>();
      for (BoxSize boxSize : requestManager.listAllBoxSizes()) {
        boolean scannable = boxSize.getScannable();
        if (scannable) {
          sizes.put(boxSize.getId(), boxSize.getRowsByColumns() + (isScannerEnabled() ? "  scannable" : ""));
        } else {
          sizes.put(boxSize.getId(), boxSize.getRowsByColumns() + (isScannerEnabled() ? "  not scannable" : ""));
        }
        // hides scannability if a lab does not have the bulk scanner enabled
      }
      model.put("boxSizes", sizes);

      // add all box contents
      model.put("boxables", box.getBoxables());

      // add JSON
      ObjectMapper mapper = new ObjectMapper();
      model.put("boxJSON", mapper.writer().writeValueAsString(box));

      return new ModelAndView("/pages/editBox.jsp", model);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.error("Failed to show Box", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(method = RequestMethod.POST)
  public String processSubmit(@ModelAttribute("box") Box box, ModelMap model, SessionStatus session) throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      if (!box.userCanWrite(user)) {
        throw new SecurityException("Permission denied.");
      }

      box.setLastModifier(user);
      requestManager.saveBox(box);
      session.setComplete();
      model.clear();
      return "redirect:/miso/box/" + box.getId();
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.error("Failed to save box ", ex);
      }
      throw ex;
    }
  }
}

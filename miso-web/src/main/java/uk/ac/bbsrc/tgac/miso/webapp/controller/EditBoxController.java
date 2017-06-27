package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractBox;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.BoxService;
import uk.ac.bbsrc.tgac.miso.service.ChangeLogService;

@Controller
@RequestMapping("/box")
@SessionAttributes("box")
public class EditBoxController {
  protected static final Logger log = LoggerFactory.getLogger(EditBoxController.class);

  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private BoxService boxService;

  @Autowired
  private ChangeLogService changeLogService;

  public void setBoxService(BoxService boxService) {
    this.boxService = boxService;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @Value("${miso.boxscanner.enabled}")
  private Boolean scannerEnabled;

  @ModelAttribute("scannerEnabled")
  public Boolean isScannerEnabled() {
    return scannerEnabled;
  }

  @ModelAttribute("maxLengths")
  public Map<String, Integer> maxLengths() throws IOException {
    return boxService.getColumnSizes();
  }

  public List<String> boxSizesAsRowsByColumns() throws IOException {
    List<String> sizes = new ArrayList<>();
    for (BoxSize boxSize : boxService.listSizes()) {
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
    return boxService.get(boxId);
  }

  @RequestMapping(value = "/rest/changes", method = RequestMethod.GET)
  public @ResponseBody Collection<ChangeLog> jsonRestChanges() throws IOException {
    return changeLogService.listAll("Box");
  }

  @RequestMapping(value = "/{boxId}", method = RequestMethod.GET)
  public ModelAndView setupForm(@PathVariable Long boxId, ModelMap model) throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Box box = null;

      if (boxId == AbstractBox.UNSAVED_ID) {
        box = new BoxImpl(user);
        model.put("title", "New Box");
      } else {
        box = boxService.get(boxId);
        if (box == null) {
          throw new SecurityException("No such Box");
        }
        model.put("title", box.getAlias());
      }
      if (!box.userCanRead(user)) {
        throw new SecurityException("Permission denied.");
      }

      model.put("formObj", box);
      model.put("box", box);

      // add all BoxUses
      model.put("boxUses", boxService.listUses());

      // add all BoxSizes
      model.put("boxSizes", boxService.listSizes());

      // add JSON
      ObjectMapper mapper = new ObjectMapper();
      model.put("boxJSON", mapper.writer().writeValueAsString(Dtos.asDto(box, true)));

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

      // The user may have modified the box contents while editing the form. Update the contents.
      if (box.getId() != AbstractBox.UNSAVED_ID) {
        Box original = boxService.get(box.getId());
        box.setBoxables(original.getBoxables());
      }
      box.setLastModifier(user);
      boxService.save(box);
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

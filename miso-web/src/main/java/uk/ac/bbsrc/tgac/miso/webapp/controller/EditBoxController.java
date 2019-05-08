package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Resource;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.dto.BoxDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.integration.BoxScanner;
import uk.ac.bbsrc.tgac.miso.service.BoxService;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.RestException;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkCreateTableBackend;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkEditTableBackend;

@Controller
@RequestMapping("/box")
public class EditBoxController {
  protected static final Logger log = LoggerFactory.getLogger(EditBoxController.class);

  private static final String MODEL_ATTR_PAGEMODE = "pageMode";

  @Autowired
  private BoxService boxService;

  public void setBoxService(BoxService boxService) {
    this.boxService = boxService;
  }

  @Resource
  private Boolean boxScannerEnabled;

  @Resource
  private Map<String, BoxScanner> boxScanners;

  @ModelAttribute("scannerEnabled")
  public Boolean isScannerEnabled() {
    return boxScannerEnabled;
  }

  @ModelAttribute("scannerNames")
  public Set<String> getScannerNames() {
    return boxScanners.keySet();
  }

  @ModelAttribute("maxLengths")
  public Map<String, Integer> maxLengths() throws IOException {
    return boxService.getColumnSizes();
  }

  @InitBinder
  public void includeForeignKeys(WebDataBinder binder) {
    binder.registerCustomEditor(StorageLocation.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String text) throws IllegalArgumentException {
        StorageLocation location = new StorageLocation();
        location.setId(Long.valueOf(text));
        setValue(location);
      }
    });
  }

  public List<String> boxSizesAsRowsByColumns() throws IOException {
    List<String> sizes = new ArrayList<>();
    for (BoxSize boxSize : boxService.listSizes()) {
      sizes.add("\"" + boxSize.getRowsByColumns() + "\"" + ":" + "\"" + boxSize.getRowsByColumns() + "\"");
    }
    return sizes;
  }

  @GetMapping(value = "/bulk/new")
  public ModelAndView newBoxes(@RequestParam("quantity") Integer quantity, ModelMap model) throws IOException {
    if (quantity == null || quantity <= 0) throw new RestException("Must specify quantity of boxes to create", Status.BAD_REQUEST);

    return new BulkCreateBoxBackend(quantity).create(model);
  }

  @GetMapping(value = "/bulk/edit")
  public ModelAndView editBoxes(@RequestParam("ids") String boxIds, ModelMap model) throws IOException {
    return new BulkEditBoxBackend().edit(boxIds, model);
  }

  @GetMapping(value = "/new")
  public ModelAndView newBox(ModelMap model) throws IOException {
    model.put("title", "New Box");
    model.put(MODEL_ATTR_PAGEMODE, "create");
    return setupForm(new BoxImpl(), model);
  }

  @GetMapping(value = "/{boxId}")
  public ModelAndView setupForm(@PathVariable Long boxId, ModelMap model) throws IOException {
    Box box = boxService.get(boxId);
    if (box == null) throw new NotFoundException("No box found for ID " + boxId.toString());
    model.put("title", "Box " + box.getId());
    model.put(MODEL_ATTR_PAGEMODE, "edit");
    return setupForm(box, model);
  }

  private ModelAndView setupForm(Box box, ModelMap model) throws IOException {
    try {
      model.put("formObj", box);
      model.put("box", box);

      // add all BoxUses
      model.put("boxUses", boxService.listUses());

      // add all BoxSizes
      model.put("boxSizes", boxService.listSizes());

      // add JSON
      Collection<BoxableView> contents = boxService.getBoxContents(box.getId());
      ObjectMapper mapper = new ObjectMapper();
      model.put("boxJSON", mapper.writer().writeValueAsString(Dtos.asDtoWithBoxables(box, contents)));

      // add FreezerUrl
      if (box.getStorageLocation() != null) {
        StorageLocation freezer = box.getStorageLocation().getFreezerLocation();
        if (freezer != null) {
          model.put("freezerURL", "../freezer/" + freezer.getId());
        } else {
          model.put("freezerURL", "javascript:void(0)");
        }
      }

      return new ModelAndView("/WEB-INF/pages/editBox.jsp", model);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.error("Failed to show Box", ex);
      }
      throw ex;
    }
  }

  @PostMapping
  public ModelAndView processSubmit(@ModelAttribute("box") Box box, ModelMap model, SessionStatus session) throws IOException {
    // The user may have modified the box contents while editing the form. Update the contents.
    if (box.isSaved()) {
      Box original = boxService.get(box.getId());
      box.setBoxPositions(original.getBoxPositions());
    }
    boxService.save(box);
    session.setComplete();
    model.clear();
    return new ModelAndView("redirect:/miso/box/" + box.getId(), model);
  }

  private final class BulkCreateBoxBackend extends BulkCreateTableBackend<BoxDto> {
    public BulkCreateBoxBackend(Integer quantity) {
      super("box", BoxDto.class, "Boxes", new BoxDto(), quantity);
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      // No configuration required for box HandsOnTable
    }
  }

  private final class BulkEditBoxBackend extends BulkEditTableBackend<Box, BoxDto> {

    private BulkEditBoxBackend() {
      super("box", BoxDto.class, "Boxes");
    }

    @Override
    protected BoxDto asDto(Box model) {
      return Dtos.asDto(model, false);
    }

    @Override
    protected Stream<Box> load(List<Long> boxIds) throws IOException {
      return boxService.listByIdList(boxIds).stream();
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) {
      // No configuration required for box HandsOnTable
    }
  }
}

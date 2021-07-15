package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.RequisitionService;
import uk.ac.bbsrc.tgac.miso.dto.RequisitionDto;
import uk.ac.bbsrc.tgac.miso.webapp.util.ListItemsPage;
import uk.ac.bbsrc.tgac.miso.webapp.util.PageMode;

@Controller
@RequestMapping("/requisition")
public class RequisitionController {

  @Autowired
  private RequisitionService requisitionService;
  @Autowired
  private AuthorizationManager authorizationManager;

  private final ListItemsPage listPage = new ListItemsPage("requisition") {

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      config.put("isAdmin", authorizationManager.isAdminUser());
    }

  };

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    return listPage.list(model);
  }

  @GetMapping("/new")
  public ModelAndView create(ModelMap model) throws IOException {
    Requisition requisition = new Requisition();
    model.put("title", "New Requisition");
    return setupForm(requisition, PageMode.CREATE, model);
  }

  @GetMapping("/{id}")
  public ModelAndView edit(@PathVariable long id, ModelMap model) throws IOException {
    Requisition requisition = requisitionService.get(id);
    if (requisition == null) {
      throw new NotFoundException("No requisition found for ID: " + id);
    }
    model.put("title", "Requisition " + id);
    return setupForm(requisition, PageMode.EDIT, model);
  }

  private ModelAndView setupForm(Requisition requisition, PageMode pageMode, ModelMap model) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    model.put(PageMode.PROPERTY, pageMode.getLabel());
    model.put("requisition", requisition);
    model.put("requisitionDto", mapper.writeValueAsString(RequisitionDto.from(requisition)));
    return new ModelAndView("/WEB-INF/pages/editRequisition.jsp", model);
  }

}

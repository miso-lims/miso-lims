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

import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentModelService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.InstrumentModelDto;
import uk.ac.bbsrc.tgac.miso.webapp.util.ListItemsPageWithAuthorization;

@Controller
@RequestMapping("/instrumentmodel")
public class InstrumentModelsController {

  @Autowired
  private InstrumentModelService instrumentModelService;
  @Autowired
  private AuthorizationManager authorizationManager;

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    return new ListItemsPageWithAuthorization("instrumentmodel", () -> this.authorizationManager).list(model);
  }

  @GetMapping("/new")
  public ModelAndView create(ModelMap model) throws JsonProcessingException {
    model.put("title", "New Instrument Model");
    model.put("pageMode", "create");
    InstrumentModel instrumentModel = new InstrumentModel();
    return instrumentModelPage(instrumentModel, model);
  }

  @GetMapping("/{id}")
  public ModelAndView edit(@PathVariable long id, ModelMap model) throws IOException {
    InstrumentModel instrumentModel = instrumentModelService.get(id);
    if (instrumentModel == null) {
      throw new NotFoundException("Instrument model not found");
    }
    model.put("title", "Instrument Model " + id);
    model.put("pageMode", "edit");
    model.put("instrumentType", instrumentModel.getInstrumentType().name());
    return instrumentModelPage(instrumentModel, model);
  }

  private ModelAndView instrumentModelPage(InstrumentModel instrumentModel, ModelMap model) throws JsonProcessingException {
    InstrumentModelDto dto = Dtos.asDto(instrumentModel);
    ObjectMapper mapper = new ObjectMapper();
    model.put("modelDto", mapper.writeValueAsString(dto));
    return new ModelAndView("/WEB-INF/pages/editInstrumentModel.jsp", model);
  }

}

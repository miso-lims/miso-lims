package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.ws.rs.NotFoundException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.LabelledEnum;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheet;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheetFieldCommonSource;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.SampleSheetService;
import uk.ac.bbsrc.tgac.miso.dto.SampleSheetDto;
import uk.ac.bbsrc.tgac.miso.webapp.util.PageMode;

@Controller
@RequestMapping("/samplesheet")
public class SampleSheetController {

  @Autowired
  private SampleSheetService sampleSheetService;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private JsonMapper mapper;

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    List<SampleSheet> items = sampleSheetService.list();
    model.put("title", "Sample Sheets");
    model.put("data", mapper.writeValueAsString(items.stream().map(SampleSheetDto::from).collect(Collectors.toList())));
    ObjectNode config = mapper.createObjectNode();
    config.put("isAdmin", authorizationManager.isAdminUser());
    model.put("config", mapper.writeValueAsString(config));
    model.put("targetType", "ListTarget.samplesheet");
    return new ModelAndView("/WEB-INF/pages/listStatic.jsp", model);
  }

  @GetMapping("/new")
  public ModelAndView create(ModelMap model) throws IOException {
    model.put("title", "New Sample Sheet");
    return setupForm(new SampleSheet(), PageMode.CREATE, model);
  }

  @GetMapping("/{id}")
  public ModelAndView edit(@PathVariable long id, ModelMap model) throws IOException {
    SampleSheet sampleSheet = sampleSheetService.get(id);
    if (sampleSheet == null) {
      throw new NotFoundException("No sample sheet found for ID: " + id);
    }
    model.put("title", "Sample Sheet " + id);
    return setupForm(sampleSheet, PageMode.EDIT, model);
  }

  private ModelAndView setupForm(SampleSheet sampleSheet, PageMode pageMode, ModelMap model) throws IOException {
    model.put(PageMode.PROPERTY, pageMode.getLabel());
    model.put("isAdmin", authorizationManager.isAdminUser());
    model.put("sampleSheet", sampleSheet);
    model.put("sampleSheetDto", mapper.writeValueAsString(SampleSheetDto.from(sampleSheet)));
    model.put("sampleSheetFieldCommonSources", mapper.writeValueAsString(makeSampleSheetFieldCommonSourcesJson()));
    return new ModelAndView("/WEB-INF/pages/editSampleSheet.jsp", model);
  }

  private JsonNode makeSampleSheetFieldCommonSourcesJson() {
    ArrayNode list = mapper.createArrayNode();
    for (SampleSheetFieldCommonSource source : SampleSheetFieldCommonSource.values()) {
      ObjectNode sourceNode = mapper.createObjectNode();
      sourceNode.put("label", source.getLabel());
      sourceNode.put("value", source.name());
      LabelledEnum[] sourceProperties = source.getProperties();
      if (sourceProperties != null) {
        ArrayNode properties = mapper.createArrayNode();
        for (LabelledEnum sourceProperty : sourceProperties) {
          ObjectNode property = mapper.createObjectNode();
          property.put("label", sourceProperty.getLabel());
          property.put("value", sourceProperty.name());
          properties.add(property);
        }
        sourceNode.set("properties", properties);
      }
      list.add(sourceNode);
    }
    return list;
  }

}

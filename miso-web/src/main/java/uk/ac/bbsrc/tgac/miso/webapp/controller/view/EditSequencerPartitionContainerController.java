package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoreVersion;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.service.ContainerService;
import uk.ac.bbsrc.tgac.miso.core.service.RunService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingContainerModelService;
import uk.ac.bbsrc.tgac.miso.core.util.IndexChecker;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.NotFoundException;

@Controller
@RequestMapping("/container")
@SessionAttributes("container")
public class EditSequencerPartitionContainerController {
  protected static final Logger log = LoggerFactory.getLogger(EditSequencerPartitionContainerController.class);

  @Autowired
  private ContainerService containerService;
  @Autowired
  private RunService runService;
  @Autowired
  private SequencingContainerModelService containerModelService;
  @Autowired
  private IndexChecker indexChecker;
  @Autowired
  private ObjectMapper mapper;

  /**
   * Translates foreign keys to entity objects with only the ID set, to be used in service layer to
   * reload persisted child objects
   *
   * @param binder
   */
  @InitBinder
  public void includeForeignKeys(WebDataBinder binder) {
    binder.registerCustomEditor(PoreVersion.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String text) {
        if (text.isEmpty()) {
          setValue(null);
        } else {
          PoreVersion v = new PoreVersion();
          v.setId(Long.valueOf(text));
          setValue(v);
        }
      }
    });

    binder.registerCustomEditor(Date.class, "receivedDate", new CustomDateEditor(LimsUtils.getDateFormat(), false));
    binder.registerCustomEditor(Date.class, "returnedDate", new CustomDateEditor(LimsUtils.getDateFormat(), true));
  }

  @PostMapping
  public ModelAndView processSubmit(@ModelAttribute("container") SequencerPartitionContainer container, ModelMap model,
      SessionStatus session)
      throws IOException {
    try {
      SequencerPartitionContainer saved = containerService.save(container);
      session.setComplete();
      model.clear();
      return new ModelAndView("redirect:/container/" + saved.getId(), model);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to save container", ex);
      }
      throw ex;
    }
  }

  public void setContainerService(ContainerService containerService) {
    this.containerService = containerService;
  }

  public void setRunService(RunService runService) {
    this.runService = runService;
  }

  @GetMapping(value = "/new/{modelId}")
  public ModelAndView setupNewForm(@PathVariable("modelId") Long modelId, ModelMap model)
      throws IOException {
    SequencingContainerModel containerModel = containerModelService.get(modelId);
    if (containerModel == null)
      throw new NotFoundException("No container model found for ID " + modelId.toString());
    SequencerPartitionContainer container = containerModel.getPlatformType().createContainer();
    container.setModel(containerModel);

    model.put("title", "New " + containerModel.getPlatformType().getContainerName());

    container.setPartitions(
        IntStream.range(0, containerModel.getPartitionCount())
            .mapToObj(number -> new PartitionImpl(container, number + 1))
            .collect(Collectors.toList()));
    return setupForm(container, model);
  }

  @GetMapping(value = "/{containerId}")
  public ModelAndView setupEditForm(@PathVariable Long containerId, ModelMap model) throws IOException {
    SequencerPartitionContainer container = containerService.get(containerId);
    if (container == null)
      throw new NotFoundException("No container found with ID " + containerId);
    model.put("title", container.getModel().getPlatformType().getContainerName() + " " + containerId);
    return setupForm(container, model);
  }

  private ModelAndView setupForm(SequencerPartitionContainer container, ModelMap model) throws IOException {
    model.put("container", container);
    model.put("containerPartitions",
        container.getPartitions().stream().map(partition -> Dtos.asDto(partition, false, indexChecker))
            .collect(Collectors.toList()));
    model.put("containerRuns",
        runService.listByContainerId(container.getId()).stream().map(Dtos::asDto).collect(Collectors.toList()));
    model.put("containerJSON", mapper.writer().writeValueAsString(Dtos.asDto(container, indexChecker)));
    model.put("poreVersions", containerService.listPoreVersions());
    return new ModelAndView("/WEB-INF/pages/editSequencerPartitionContainer.jsp", model);
  }
}

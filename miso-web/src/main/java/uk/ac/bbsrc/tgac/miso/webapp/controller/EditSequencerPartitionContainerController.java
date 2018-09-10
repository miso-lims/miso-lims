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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
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

import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoreVersion;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.ContainerModelService;
import uk.ac.bbsrc.tgac.miso.service.ContainerService;
import uk.ac.bbsrc.tgac.miso.service.KitService;
import uk.ac.bbsrc.tgac.miso.service.RunService;

@Controller
@RequestMapping("/container")
@SessionAttributes("container")
public class EditSequencerPartitionContainerController {
  protected static final Logger log = LoggerFactory.getLogger(EditSequencerPartitionContainerController.class);

  @Autowired
  private ContainerService containerService;
  @Autowired
  private KitService kitService;
  @Autowired
  private RunService runService;
  @Autowired
  private ContainerModelService containerModelService;

  /**
   * Translates foreign keys to entity objects with only the ID set, to be used in service layer to reload persisted child objects
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

  @RequestMapping(method = RequestMethod.POST)
  public String processSubmit(@ModelAttribute("container") SequencerPartitionContainer container, ModelMap model, SessionStatus session)
      throws IOException {
    try {
      SequencerPartitionContainer saved = containerService.save(container);
      session.setComplete();
      model.clear();
      return "redirect:/miso/container/" + saved.getId();
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

  @RequestMapping(value = "/new/{modelId}", method = RequestMethod.GET)
  public ModelAndView setupNewForm(@PathVariable("modelId") Long modelId, ModelMap model)
      throws IOException {
    SequencingContainerModel containerModel = containerModelService.get(modelId);
    if (containerModel == null) throw new NotFoundException("No container model found for ID " + modelId.toString());
    SequencerPartitionContainer container = containerModel.getPlatformType().createContainer();
    container.setModel(containerModel);

    model.put("title", "New " + containerModel.getPlatformType().getContainerName());

    container.setPartitions(
        IntStream.range(0, containerModel.getPartitionCount()).mapToObj(number -> new PartitionImpl(container, number + 1))
            .collect(Collectors.toList()));
    return setupForm(container, model);
  }

  @RequestMapping(value = "/{containerId}", method = RequestMethod.GET)
  public ModelAndView setupEditForm(@PathVariable Long containerId, ModelMap model) throws IOException {
    SequencerPartitionContainer container = containerService.get(containerId);
    model.put("title", container.getModel().getPlatformType().getContainerName() + " " + containerId);
    return setupForm(container, model);
  }

  private ModelAndView setupForm(SequencerPartitionContainer container, ModelMap model) throws IOException {
    model.put("container", container);
    model.put("containerPartitions",
        container.getPartitions().stream().map(partition -> Dtos.asDto(partition, true)).collect(Collectors.toList()));
    model.put("containerRuns", runService.listByContainerId(container.getId()).stream().map(Dtos::asDto).collect(Collectors.toList()));
    model.put("clusteringKits",
        kitService.listKitDescriptorsByType(KitType.CLUSTERING).stream()
            .filter(descriptor -> descriptor.getPlatformType() == container.getModel().getPlatformType())
            .sorted(KitDescriptor::sortByName).collect(Collectors.toList()));
    model.put("multiplexingKits",
        kitService.listKitDescriptorsByType(KitType.MULTIPLEXING).stream()
            .filter(descriptor -> descriptor.getPlatformType() == container.getModel().getPlatformType())
            .sorted(KitDescriptor::sortByName).collect(Collectors.toList()));
    model.put("poreVersions", containerService.listPoreVersions());
    return new ModelAndView("/pages/editSequencerPartitionContainer.jsp", model);
  }
}

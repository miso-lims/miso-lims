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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.fasterxml.jackson.databind.node.ArrayNode;

import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.service.KitDescriptorService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

@Controller
@RequestMapping("/kitdescriptor")
public class EditKitDescriptorController {
  protected static final Logger log = LoggerFactory.getLogger(EditKitDescriptorController.class);

  @Autowired
  private KitDescriptorService kitService;
  @Autowired
  private ObjectMapper mapper;

  public void setKitService(KitDescriptorService kitService) {
    this.kitService = kitService;
  }

  @GetMapping(value = "/new")
  public ModelAndView setupForm(ModelMap model) throws IOException {
    model.addAttribute("title", "New Kit Descriptor");
    return setupForm(new KitDescriptor(), model);
  }

  @GetMapping(value = "/{kitDescriptorId}")
  public ModelAndView setupForm(@PathVariable Long kitDescriptorId, ModelMap model) throws IOException {
    KitDescriptor kitDescriptor = kitService.get(kitDescriptorId);
    if (kitDescriptor == null) {
      throw new NotFoundException("No kit found for ID " + kitDescriptorId.toString());
    }
    model.put("title", "Kit Descriptor " + kitDescriptor.getId());
    return setupForm(kitDescriptor, model);
  }

  public ModelAndView setupForm(KitDescriptor kitDescriptor, ModelMap model) throws JsonProcessingException {
    model.put("kitDescriptor", kitDescriptor);
    model.put("associatedTargetedSequencings", Dtos.asTargetedSequencingDtos(kitDescriptor.getTargetedSequencing()));

    model.put("kitDescriptorDto", mapper.writeValueAsString(Dtos.asDto(kitDescriptor)));
    ArrayNode kitTypes = mapper.createArrayNode();
    for (String item : KitType.getKeys()) {
      kitTypes.add(item);
    }
    model.put("kitTypes", kitTypes);

    return new ModelAndView("/WEB-INF/pages/editKitDescriptor.jsp", model);
  }
}

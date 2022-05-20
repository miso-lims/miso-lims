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
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.Group;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.service.GroupService;
import uk.ac.bbsrc.tgac.miso.core.service.UserService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

@Controller
@RequestMapping("/admin/group")
public class EditGroupController {

  @Autowired
  private GroupService groupService;
  @Autowired
  private UserService userService;
  @Autowired
  private ObjectMapper mapper;

  @RequestMapping(value = "/new", method = RequestMethod.GET)
  public ModelAndView setupForm(ModelMap model) throws IOException {
    model.put("title", "New Group");
    return setupForm(new Group(), model);
  }

  @RequestMapping(value = "/{groupId}", method = RequestMethod.GET)
  public ModelAndView adminSetupForm(@PathVariable long groupId, ModelMap model) throws IOException {
    model.put("title", "Group " + groupId);
    Group group = groupService.get(groupId);
    if (group == null) {
      throw new NotFoundException("No group found for ID " + groupId);
    }

    model.put("includedUsers", group.getUsers().stream().map(Dtos::asDto).collect(Collectors.toList()));
    model.put("availableUsers", userService.list().stream().map(Dtos::asDto).collect(Collectors.toList()));

    return setupForm(group, model);
  }

  private ModelAndView setupForm(Group group, ModelMap model) throws JsonProcessingException {
    model.put("group", group);
    model.put("groupDto", mapper.writeValueAsString(Dtos.asDto(group)));

    return new ModelAndView("/WEB-INF/pages/editGroup.jsp", model);
  }

}

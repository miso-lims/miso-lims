package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.User;

import jakarta.ws.rs.core.Response.Status;
import uk.ac.bbsrc.tgac.miso.core.service.GroupService;
import uk.ac.bbsrc.tgac.miso.core.service.UserService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.GroupDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.RestException;

@Controller
@RequestMapping("/rest/groups")
public class GroupRestController extends AbstractRestController {

  @Autowired
  private GroupService groupService;
  @Autowired
  private UserService userService;

  @PostMapping
  public @ResponseBody GroupDto create(@RequestBody GroupDto dto) throws IOException {
    return RestUtils.createObject("Group", dto, Dtos::to, groupService, Dtos::asDto);
  }

  @PutMapping("/{groupId}")
  public @ResponseBody GroupDto update(@PathVariable long groupId, @RequestBody GroupDto dto) throws IOException {
    return RestUtils.updateObject("Group", groupId, dto, Dtos::to, groupService, Dtos::asDto);
  }

  @PostMapping("/{groupId}/users")
  public @ResponseBody GroupDto addMembers(@PathVariable long groupId, @RequestBody List<Long> userIds)
      throws IOException {
    Group group = getGroup(groupId);
    for (Long userId : userIds) {
      User user = userService.get(userId);
      if (user == null) {
        throw new RestException("User " + userId + " does not exist", Status.BAD_REQUEST);
      }
      if (group.getUsers().stream().noneMatch(u -> u.getId() == userId.longValue())) {
        group.getUsers().add(user);
      }
    }
    return updateGroupMembers(group);
  }

  @PostMapping("/{groupId}/users/remove")
  public @ResponseBody GroupDto removeMembers(@PathVariable long groupId, @RequestBody List<Long> userIds)
      throws IOException {
    Group group = getGroup(groupId);
    for (Long userId : userIds) {
      group.getUsers().removeIf(u -> u.getId() == userId.longValue());
    }
    return updateGroupMembers(group);
  }

  private Group getGroup(long groupId) throws IOException {
    Group group = groupService.get(groupId);
    if (group == null) {
      throw new RestException("Group not found", Status.NOT_FOUND);
    }
    return group;
  }

  private GroupDto updateGroupMembers(Group group) throws IOException {
    groupService.updateMembers(group);
    Group saved = groupService.get(group.getId());
    return Dtos.asDto(saved);
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Group", ids, groupService);
  }

}

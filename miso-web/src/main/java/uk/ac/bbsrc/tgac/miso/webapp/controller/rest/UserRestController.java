package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.UserService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.UserDto;

@Controller
@RequestMapping("/rest/users")
public class UserRestController extends RestController {

  @Autowired
  private UserService userService;
  @Autowired
  private SecurityManager securityManager;
  @Autowired(required = false)
  private PasswordEncoder passwordEncoder; // Will be null if using LDAP/AD authentication
  @Autowired
  private AuthorizationManager authorizationManager;

  @PostMapping
  public @ResponseBody UserDto create(@RequestBody UserDto dto) throws IOException {
    if (!securityManager.canCreateNewUser()) {
      throw new RestException("Cannot create new users in MISO directly.", Status.BAD_REQUEST);
    }
    return RestUtils.createObject("User", dto, d -> {
      User user = Dtos.to(d);
      user.setPassword(passwordEncoder.encode(d.getPassword()));
      return user;
    }, userService, Dtos::asDto);
  }

  @PutMapping("/{userId}")
  public @ResponseBody UserDto update(@PathVariable long userId, @RequestBody UserDto dto) throws IOException {
    if (!securityManager.canCreateNewUser()) {
      throw new RestException("Cannot modify users in MISO directly.", Status.BAD_REQUEST);
    }
    return RestUtils.updateObject("User", userId, dto, d -> {
      User user = Dtos.to(d);
      user.setPassword(null); // null password has the effect of keeping the current password in DefaultUserService
      return user;
    }, userService, Dtos::asDto);
  }

  public static class PasswordChangeDto {

    private String oldPassword;
    private String newPassword;

    public String getOldPassword() {
      return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
      this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
      return newPassword;
    }

    public void setNewPassword(String newPassword) {
      this.newPassword = newPassword;
    }

  }

  @PostMapping("/{userId}/password")
  public @ResponseBody UserDto changePassword(@PathVariable long userId, @RequestBody PasswordChangeDto dto) throws IOException {
    if (!securityManager.isPasswordMutable()) {
      throw new RestException("Cannot change password in MISO directly.", Status.BAD_REQUEST);
    }
    User user = userService.get(userId);
    if (user == null) {
      throw new RestException("User not found", Status.NOT_FOUND);
    }
    if (authorizationManager.getCurrentUser().getId() == userId
        && !passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
      throw new ValidationException(new ValidationError("oldPassword", "Existing password does not match"));
    }
    user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    userService.update(user);
    return Dtos.asDto(userService.get(userId));
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("User", ids, userService);
  }

}

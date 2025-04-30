package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationException;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.UserService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.NotFoundException;

@Controller
public class EditUserController {
  protected static final Logger log = LoggerFactory.getLogger(EditUserController.class);

  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private UserService userService;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private ObjectMapper mapper;

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  @ModelAttribute("usersEditable")
  public boolean populateUsersEditable() {
    return securityManager.canCreateNewUser();
  }

  @ModelAttribute("mutablePassword")
  public boolean populateMutablePassword() {
    return securityManager.isPasswordMutable();
  }

  @GetMapping(value = "/user/{userId}")
  public ModelAndView userForm(@PathVariable long userId, ModelMap model, HttpServletRequest request)
      throws SecurityException, IOException {
    User user = authorizationManager.getCurrentUser();
    if (userId != user.getId()) {
      throw new AuthorizationException("You can only edit your own user details.");
    }
    return setupForm(user, model);
  }

  @GetMapping(value = "/admin/user/new")
  public ModelAndView newSetupForm(ModelMap model, HttpServletRequest request) throws IOException {
    authorizationManager.throwIfNonAdmin();
    if (!securityManager.canCreateNewUser()) {
      throw new IOException("Cannot add users through the MISO interface.");
    }
    return setupForm(new UserImpl(), model);
  }

  @GetMapping(value = "/admin/user/{userId}")
  public ModelAndView adminSetupForm(@PathVariable long userId, ModelMap model) throws IOException {
    authorizationManager.throwIfNonAdmin();
    User user = userService.get(userId);
    if (user == null) {
      throw new NotFoundException("No user found for ID " + userId);
    }
    return setupForm(user, model);
  }

  private ModelAndView setupForm(User user, ModelMap model) throws IOException {
    model.put("title", user.isSaved() ? ("User " + user.getId()) : "New User");
    model.put("user", user);
    model.put("userDto", mapper.writeValueAsString(Dtos.asDto(user)));

    if (user.isSaved()) {
      model.put("groups", user.getGroups().stream().map(Dtos::asDto).collect(Collectors.toList()));
    }

    return new ModelAndView("/WEB-INF/pages/editUser.jsp", model);
  }

}

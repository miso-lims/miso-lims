package uk.ac.bbsrc.tgac.miso.webapp.context;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.ldap.userdetails.InetOrgPerson;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.security.util.LimsSecurityUtils;
import uk.ac.bbsrc.tgac.miso.core.service.UserService;

public class UserSyncHandler extends SavedRequestAwareAuthenticationSuccessHandler {
  
  @Autowired
  private UserService userService;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {
    try {
      InetOrgPerson p = (InetOrgPerson) authentication.getPrincipal();
      // map the LDAP user details to a MISO User
      User u = LimsSecurityUtils.fromLdapUser(p);
      // check if a user exists in the database with this username
      User dbu = userService.getByLoginName(u.getLoginName());
      if (dbu == null || !dbu.equals(u)) {
        userService.create(u);
      } else {
        // update user data from LDAP (password, roles, etc.)
        LimsSecurityUtils.updateFromLdapUser(dbu, p);
        userService.update(dbu);
      }
    } catch (Exception e) {
      authentication.setAuthenticated(false);
      throw e;
    }
    super.onAuthenticationSuccess(request, response, authentication);
  }

}

package uk.ac.bbsrc.tgac.miso.core.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.security.util.LimsSecurityUtils;
import uk.ac.bbsrc.tgac.miso.core.service.UserService;

public class LDAPSecurityManager implements SecurityManager {

  @Autowired
  private UserService userService;

  @Override
  public boolean canCreateNewUser() {
    return false;
  }

  @Override
  public boolean isPasswordMutable() {
    return false;
  }

  @Override
  public void syncUser(UserDetails userDetails) throws IOException {
    User u = LimsSecurityUtils.fromLdapUser(userDetails);
    User dbu = userService.getByLoginName(u.getLoginName());
    if (dbu == null || !dbu.equals(u)) {
      userService.create(u);
    } else {
      LimsSecurityUtils.updateFromLdapUser(dbu, userDetails);
      userService.update(dbu);
    }
  }
}

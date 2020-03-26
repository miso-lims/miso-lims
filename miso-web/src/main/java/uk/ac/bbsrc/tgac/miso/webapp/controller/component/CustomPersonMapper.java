package uk.ac.bbsrc.tgac.miso.webapp.controller.component;

import java.util.Collection;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.InetOrgPerson;
import org.springframework.security.ldap.userdetails.InetOrgPersonContextMapper;

public class CustomPersonMapper extends InetOrgPersonContextMapper {

  private String emailAttribute;

  public String getEmailAttribute() {
    return emailAttribute;
  }

  public void setEmailAttribute(String emailAttribute) {
    this.emailAttribute = emailAttribute;
  }

  @Override
  public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
    Essence p = new Essence(ctx);
    p.setUsername(username);
    p.setAuthorities(authorities);
    return p.createUserDetails();
  }

  public static class Essence extends InetOrgPerson.Essence {

    public Essence(DirContextOperations ctx) {
      super(ctx);
      setMail(ctx.getStringAttribute("mail"));
    }

  }

}

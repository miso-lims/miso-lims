package uk.ac.bbsrc.tgac.miso.core.security;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

/**
 * LDAP roles may have a prefix to clarify their scope. You may wish to use roles such as MISO_ROLE_ADMIN rather than simply ROLE_ADMIN,
 * for example. This class will map these prefixed roles to the actual MISO roles if necessary.
 */
public class PrefixStrippingAuthoritiesMapper implements GrantedAuthoritiesMapper {

  @Value("${security.ldap.stripRolePrefix}")
  private String rolePrefix = "";

  public void setRolePrefix(String rolePrefix) {
    this.rolePrefix = rolePrefix;
  }

  @Override
  public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
    Set<MisoAuthority> roles = EnumSet.noneOf(MisoAuthority.class);

    for (GrantedAuthority a : authorities) {
      if ((rolePrefix + MisoAuthority.ROLE_ADMIN.name()).equals(a.getAuthority())) {
        roles.add(MisoAuthority.ROLE_ADMIN);
      } else if ((rolePrefix + MisoAuthority.ROLE_INTERNAL.name()).equals(a.getAuthority())) {
        roles.add(MisoAuthority.ROLE_INTERNAL);
      }
    }

    return roles;
  }

}

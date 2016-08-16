package uk.ac.bbsrc.tgac.miso.core.security.ad;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

import uk.ac.bbsrc.tgac.miso.core.security.MisoAuthority;

/**
 * When using active directory some roles may have a prefix required by the AD administrator. The class will map these prefixed roles to the
 * actual MISO roles.
 */
public class ActiveDirectoryAuthoritiesMapper implements GrantedAuthoritiesMapper {

  @Value("${security.ad.stripRolePrefix}")
  private String rolePrefix = "";

  @Override
  public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
    Set<MisoAuthority> roles = EnumSet.noneOf(MisoAuthority.class);

    for (GrantedAuthority a : authorities) {
      if ((rolePrefix + MisoAuthority.ROLE_ADMIN).equals(a.getAuthority())) {
        roles.add(MisoAuthority.ROLE_ADMIN);
      } else if ((rolePrefix + MisoAuthority.ROLE_INTERNAL).equals(a.getAuthority())) {
        roles.add(MisoAuthority.ROLE_INTERNAL);
      } else if ((rolePrefix + MisoAuthority.ROLE_EXTERNAL).equals(a.getAuthority())) {
        roles.add(MisoAuthority.ROLE_EXTERNAL);
      }
    }

    return roles;
  }

}

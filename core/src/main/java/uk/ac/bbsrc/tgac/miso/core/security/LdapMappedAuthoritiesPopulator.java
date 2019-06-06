package uk.ac.bbsrc.tgac.miso.core.security;

import java.util.HashSet;
import java.util.Set;

import org.springframework.ldap.core.ContextSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;

public class LdapMappedAuthoritiesPopulator extends DefaultLdapAuthoritiesPopulator {

  private GrantedAuthoritiesMapper grantedAuthoritiesMapper;

  public LdapMappedAuthoritiesPopulator(ContextSource contextSource, String groupSearchBase) {
    super(contextSource, groupSearchBase);
  }

  public void setGrantedAuthoritiesMapper(GrantedAuthoritiesMapper grantedAuthoritiesMapper) {
    this.grantedAuthoritiesMapper = grantedAuthoritiesMapper;
  }

  @Override
  public Set<GrantedAuthority> getGroupMembershipRoles(String userDn, String username) {
    return new HashSet<>(grantedAuthoritiesMapper.mapAuthorities(super.getGroupMembershipRoles(userDn, username)));
  }

}

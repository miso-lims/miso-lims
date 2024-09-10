package uk.ac.bbsrc.tgac.miso.webapp.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.search.LdapUserSearch;
import org.springframework.security.ldap.userdetails.InetOrgPersonContextMapper;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapUserDetailsService;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.session.SimpleRedirectInvalidSessionStrategy;

import uk.ac.bbsrc.tgac.miso.core.security.LDAPSecurityManager;
import uk.ac.bbsrc.tgac.miso.core.security.LdapMappedAuthoritiesPopulator;
import uk.ac.bbsrc.tgac.miso.core.security.PrefixStrippingAuthoritiesMapper;
import uk.ac.bbsrc.tgac.miso.core.security.util.LimsSecurityUtils;
import uk.ac.bbsrc.tgac.miso.webapp.context.SecurityMethods.LdapOrAdSecurityEnabled;
import uk.ac.bbsrc.tgac.miso.webapp.context.SecurityMethods.LdapSecurityEnabled;

@Configuration
@Conditional(LdapOrAdSecurityEnabled.class)
public class LdapSecurityConfig {

  @Bean
  public LDAPSecurityManager securityManager() {
    return new LDAPSecurityManager();
  }

  @Bean
  public InvalidSessionStrategy invalidSessionStrategy() {
    SimpleRedirectInvalidSessionStrategy strategy = new SimpleRedirectInvalidSessionStrategy("/login");
    strategy.setCreateNewSession(false);
    return strategy;
  }

  @Bean
  public LdapContextSource securityContextSource(@Value("${security.ldap.url}") String ldapUrl,
      @Value("${security.ldap.userDn}") String userDn, @Value("${security.ldap.password}") String password) {
    DefaultSpringSecurityContextSource source = new DefaultSpringSecurityContextSource(ldapUrl);
    source.setUserDn(userDn);
    source.setPassword(password);
    return source;
  }

  /**
   * Retrieve user as InetOrgPerson as this has details needed to populate db User table. Otherwise
   * will be sparse LdapUserDetail.
   * 
   * @return InetOrgPersonContextMapper
   */
  @Bean
  public UserDetailsContextMapper userDetailsMapper() {
    return new InetOrgPersonContextMapper();
  }

  @Bean
  public LdapUserSearch ldapUserSearch(@Value("${security.ldap.searchBase}") String searchBase,
      @Value("${security.ldap.searchFilter}") String searchFilter, LdapContextSource contextSource) {
    return new FilterBasedLdapUserSearch(searchBase, searchFilter, contextSource);
  }

  @Bean
  public LdapAuthenticator ldapAuthenticator(LdapContextSource contextSource, LdapUserSearch ldapUserSearch) {
    BindAuthenticator authenticator = new BindAuthenticator(contextSource);
    authenticator.setUserSearch(ldapUserSearch);
    return authenticator;
  }

  @Bean
  public GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
    return new PrefixStrippingAuthoritiesMapper();
  }

  @Bean
  public LdapAuthoritiesPopulator ldapMappedAuthoritiesPopulator(LdapContextSource contextSource,
      GrantedAuthoritiesMapper grantedAuthoritiesMapper,
      @Value("${security.ldap.groupSearchBase}") String groupSearchBase,
      @Value("${security.ldap.groupRoleAttribute}") String groupRoleAttribute,
      @Value("${security.ldap.groupSearchFilter}") String groupSearchFilter,
      @Value("${security.ldap.rolePrefix}") String rolePrefix) {
    LdapMappedAuthoritiesPopulator populator = new LdapMappedAuthoritiesPopulator(contextSource, groupSearchBase);
    populator.setGroupRoleAttribute(groupRoleAttribute);
    populator.setGroupSearchFilter(groupSearchFilter);
    populator.setRolePrefix(rolePrefix);
    populator.setSearchSubtree(true);
    populator.setConvertToUpperCase(true);
    populator.setGrantedAuthoritiesMapper(grantedAuthoritiesMapper);
    return populator;
  }

  @Bean
  public UserDetailsService userAuthService(LdapUserSearch ldapUserSearch,
      LdapAuthoritiesPopulator ldapAuthoritiesPopulator, UserDetailsContextMapper userDetailsContextMapper) {
    LdapUserDetailsService service = new LdapUserDetailsService(ldapUserSearch, ldapAuthoritiesPopulator);
    service.setUserDetailsMapper(userDetailsContextMapper);
    return service;
  }

  @Bean
  @Conditional(LdapSecurityEnabled.class)
  public AuthenticationProvider authenticationProvider(LdapAuthenticator ldapAuthenticator,
      LdapAuthoritiesPopulator ldapAuthoritiesPopulator, UserDetailsContextMapper userDetailsContextMapper) {
    LdapAuthenticationProvider provider = new LdapAuthenticationProvider(ldapAuthenticator, ldapAuthoritiesPopulator);
    provider.setUserDetailsContextMapper(userDetailsContextMapper);
    return provider;
  }

  @Bean
  @Conditional(LdapSecurityEnabled.class)
  public AuthenticationManager authenticationManager(ObjectPostProcessor<Object> postProcessor,
      AuthenticationProvider authenticationProvider) throws Exception {
    return new AuthenticationManagerBuilder(postProcessor)
        .authenticationProvider(authenticationProvider)
        .build();
  }

  @Bean
  public LimsSecurityUtils limsSecurityUtils(@Value("${security.ldap.stripRolePrefix}") String stripRolePrefix) {
    // This is load as bean just to inject the role prefix. Used to removes prefix when populating
    // database.
    LimsSecurityUtils utils = new LimsSecurityUtils();
    utils.setRolePrefix(stripRolePrefix);
    return utils;
  }

}

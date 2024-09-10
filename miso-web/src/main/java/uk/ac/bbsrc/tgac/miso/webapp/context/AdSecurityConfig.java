package uk.ac.bbsrc.tgac.miso.webapp.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import uk.ac.bbsrc.tgac.miso.webapp.context.SecurityMethods.AdSecurityEnabled;

@Configuration
@Conditional(AdSecurityEnabled.class)
public class AdSecurityConfig {

  @Bean
  public AuthenticationProvider authenticationProvider(
      @Value("${security.ad.emailDomain}") String emailDomain,
      @Value("${security.ad.url}") String url,
      @Value("${security.ad.domainDn}") String domainDn, GrantedAuthoritiesMapper grantedAuthoritiesMapper,
      UserDetailsContextMapper userDetailsContextMapper) {
    ActiveDirectoryLdapAuthenticationProvider provider =
        new ActiveDirectoryLdapAuthenticationProvider(emailDomain, url, domainDn);
    provider.setAuthoritiesMapper(grantedAuthoritiesMapper);
    provider.setConvertSubErrorCodesToExceptions(true);
    provider.setUserDetailsContextMapper(userDetailsContextMapper);
    return provider;
  }

  @Bean
  public AuthenticationManager authenticationManager(ObjectPostProcessor<Object> postProcessor,
      AuthenticationProvider authenticationProvider) throws Exception {
    return new AuthenticationManagerBuilder(postProcessor)
        .authenticationProvider(authenticationProvider)
        .build();
  }

}

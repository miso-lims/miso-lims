package uk.ac.bbsrc.tgac.miso.webapp.context;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.eaglegenomics.simlims.core.manager.LocalSecurityManager;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.webapp.context.SecurityMethods.JdbcSecurityEnabled;

@Configuration
@Conditional(JdbcSecurityEnabled.class)
public class JdbcSecurityConfig {

  @Bean
  public SecurityManager securityManager() {
    return new LocalSecurityManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public UserDetailsService userDetailsService(JdbcTemplate jdbcTemplate) {
    MisoJdbcUserDetailsManager manager = new MisoJdbcUserDetailsManager();
    manager.setUsersByUsernameQuery(
        "SELECT loginName AS username, password AS password, active AS enabled FROM User WHERE loginName=?");
    manager.setAuthoritiesByUsernameQuery(
        "SELECT loginName AS username, roles AS authority, admin, internal FROM User WHERE loginName=?");
    manager.setEnableAuthorities(true);
    manager.setJdbcTemplate(jdbcTemplate);
    return manager;
  }

  @Bean
  public AuthenticationManager authenticationManager(ObjectPostProcessor<Object> postProcessor,
      UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) throws Exception {
    return new AuthenticationManagerBuilder(postProcessor)
        .userDetailsService(userDetailsService)
        .passwordEncoder(passwordEncoder)
        .and()
        .build();
  }

}

package uk.ac.bbsrc.tgac.miso.webapp.context;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;

import jakarta.servlet.DispatcherType;
import uk.ac.bbsrc.tgac.miso.core.service.ApiKeyService;
import uk.ac.bbsrc.tgac.miso.webapp.context.SecurityMethods.NonSamlSecurityEnabled;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public AuthenticationSuccessHandler successHandler() {
    SavedRequestAwareAuthenticationSuccessHandler handler = new SavedRequestAwareAuthenticationSuccessHandler();
    handler.setDefaultTargetUrl("/mainMenu");
    return handler;
  }

  @Bean
  public AuthenticationFailureHandler failureHandler() {
    return new SimpleUrlAuthenticationFailureHandler("/login?login_error=1");
  }

  @Bean
  @Conditional(NonSamlSecurityEnabled.class)
  public AuthenticationEntryPoint loginUrlEntryPoint() {
    return new LoginUrlAuthenticationEntryPoint("/login");
  }

  @Bean
  @Conditional(NonSamlSecurityEnabled.class)
  public PersistentTokenRepository tokenRepository(DataSource dataSource) {
    JdbcTokenRepositoryImpl repository = new JdbcTokenRepositoryImpl();
    repository.setDataSource(dataSource);
    return repository;
  }

  @Bean
  @Conditional(NonSamlSecurityEnabled.class)
  public RememberMeServices rememberMeServices(UserDetailsService userDetailsService,
      PersistentTokenRepository tokenRepository) {
    PersistentTokenBasedRememberMeServices services = new PersistentTokenBasedRememberMeServices("miso",
        userDetailsService, tokenRepository);
    services.setParameter("_spring_security_remember_me");
    return services;
  }

  @Bean
  @Conditional(NonSamlSecurityEnabled.class)
  public MisoLoginFilter loginFilter(AuthenticationManager authenticationManager,
      AuthenticationSuccessHandler successHandler, AuthenticationFailureHandler failureHandler,
      RememberMeServices rememberMeServices) {
    MisoLoginFilter filter = new MisoLoginFilter();
    filter.setAuthenticationManager(authenticationManager);
    filter.setAuthenticationSuccessHandler(successHandler);
    filter.setAuthenticationFailureHandler(failureHandler);
    filter.setRememberMeServices(rememberMeServices);
    return filter;
  }

  @Bean
  public ApiKeyAuthenticationFilter apiKeyFilter(ApiKeyService apiKeyService) {
    return new ApiKeyAuthenticationFilter(apiKeyService);
  }

  @Bean
  public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
  }

  @Bean
  @Conditional(NonSamlSecurityEnabled.class)
  public SecurityFilterChain securityFilterChain(HttpSecurity http, MisoLoginFilter loginFilter,
      ApiKeyAuthenticationFilter apiKeyFilter, RememberMeServices rememberMeServices) throws Exception {
    return setupCommon(http, apiKeyFilter)
        .addFilter(loginFilter)
        .formLogin(formLogin -> formLogin
            .loginPage("/login").permitAll())
        .rememberMe(rememberMe -> rememberMe.rememberMeServices(rememberMeServices))
        .logout(logout -> logout.logoutSuccessUrl("/login"))
        .build();
  }

  public static HttpSecurity setupCommon(HttpSecurity http, ApiKeyAuthenticationFilter apiKeyFilter)
      throws Exception {
    return http
        .authorizeHttpRequests(authorizeRequests -> authorizeRequests
            .dispatcherTypeMatchers(DispatcherType.FORWARD)
            .permitAll()
            .requestMatchers(
                "/favicon.ico",
                "/styles/**",
                "/scripts/**",
                "/metrics",
                "/login",
                "/login/saml2/**",
                "/saml2/**",
                "/accessDenied",
                "/error")
            .permitAll()
            .requestMatchers("/api/**")
            .hasRole("APIUSER")
            .requestMatchers("/admin/**")
            .hasRole("ADMIN")
            .anyRequest()
            .hasRole("INTERNAL"))
        .addFilterBefore(apiKeyFilter, MisoLoginFilter.class)
        .csrf(csrf -> csrf.disable())
        .exceptionHandling(handling -> handling.accessDeniedPage("/accessDenied"))
        .securityContext(securityContext -> securityContext
            .requireExplicitSave(true)
            .securityContextRepository(new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository(),
                new HttpSessionSecurityContextRepository())));
  }

}

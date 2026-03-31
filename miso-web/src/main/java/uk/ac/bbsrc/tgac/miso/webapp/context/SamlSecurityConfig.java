package uk.ac.bbsrc.tgac.miso.webapp.context;

import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrations;
import org.springframework.security.saml2.provider.service.registration.Saml2MessageBinding;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.security.SamlSecurityManager;
import uk.ac.bbsrc.tgac.miso.webapp.context.SecurityMethods.SamlSecurityEnabled;

@Configuration
@Conditional(SamlSecurityEnabled.class)
public class SamlSecurityConfig {

  @Bean
  public SecurityManager securityManager() {
    return new SamlSecurityManager();
  }

  @Bean
  public RelyingPartyRegistrationRepository relyingPartyRegistrationRepository(
      @Value("${security.saml.registrationId:miso}") String registrationId,
      @Value("${security.saml.idp.metadataUrl}") String metadataUrl,
      @Value("${security.saml.sp.entityId:{baseUrl}/saml2/service-provider-metadata/{registrationId}}") String entityId,
      @Value("${security.saml.sp.privateKey}") Resource privateKey,
      @Value("${security.saml.sp.certificate}") Resource certificate) {

    RelyingPartyRegistration.Builder builder = RelyingPartyRegistrations.fromMetadataLocation(metadataUrl)
        .registrationId(registrationId)
        .entityId(entityId)
        .assertionConsumerServiceLocation("{baseUrl}/login/saml2/sso/{registrationId}")
        .singleLogoutServiceLocation("{baseUrl}/logout/saml2/slo/{registrationId}")
        .singleLogoutServiceResponseLocation("{baseUrl}/logout/saml2/slo/{registrationId}")
        .singleLogoutServiceBinding(Saml2MessageBinding.REDIRECT);

    builder.signingX509Credentials(c -> c.add(loadSigningCredential(privateKey, certificate)));

    return new InMemoryRelyingPartyRegistrationRepository(builder.build());
  }

  @Bean
  public OpenSaml4AuthenticationProvider samlAuthenticationProvider(
      SecurityManager securityManager,
      @Value("${security.saml.usernameAttribute}") String usernameAttribute,
      @Value("${security.saml.firstNameAttribute}") String firstNameAttribute,
      @Value("${security.saml.lastNameAttribute}") String lastNameAttribute,
      @Value("${security.saml.emailAttribute}") String emailAttribute,
      @Value("${security.saml.rolesAttribute}") String rolesAttribute,
      @Value("${security.saml.stripRolePrefix:}") String stripRolePrefix) {

    OpenSaml4AuthenticationProvider provider = new OpenSaml4AuthenticationProvider();
    provider.setResponseAuthenticationConverter(token -> {
      Saml2Authentication auth =
          OpenSaml4AuthenticationProvider.createDefaultResponseAuthenticationConverter().convert(token);

      Saml2AuthenticatedPrincipal principal = (Saml2AuthenticatedPrincipal) auth.getPrincipal();
      List<GrantedAuthority> authorities = mapAuthorities(principal, rolesAttribute, stripRolePrefix);

      if (authorities.stream().noneMatch(a -> a.getAuthority().equals("ROLE_INTERNAL"))) {
        throw new InsufficientAuthenticationException("User is not authorized for MISO login");
      }

      SamlUserDetails userDetails = new SamlUserDetails(
          required(principal, usernameAttribute).toLowerCase(Locale.ROOT),
          required(principal, firstNameAttribute) + " " + required(principal, lastNameAttribute),
          required(principal, emailAttribute),
          authorities,
          principal);

      try {
        securityManager.syncUser(userDetails);
      } catch (Exception e) {
        throw new InternalAuthenticationServiceException("User sync failed", e);
      }

      return new Saml2Authentication(userDetails, auth.getSaml2Response(), authorities);
    });

    return provider;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      ApiKeyAuthenticationFilter apiKeyFilter,
      AuthenticationSuccessHandler successHandler,
      AuthenticationFailureHandler failureHandler,
      OpenSaml4AuthenticationProvider samlAuthenticationProvider) throws Exception {

    return SecurityConfig.setupCommon(http, apiKeyFilter)
        .saml2Login(saml -> saml
            .loginPage("/login")
            .successHandler(successHandler)
            .failureHandler(failureHandler)
            .authenticationManager(new ProviderManager(samlAuthenticationProvider)))
        .saml2Logout(Customizer.withDefaults())
        .saml2Metadata(Customizer.withDefaults())
        .build();
  }

  private static String required(Saml2AuthenticatedPrincipal principal, String attribute) {
    String value = principal.getFirstAttribute(attribute);
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("Missing SAML attribute: " + attribute);
    }
    return value;
  }

  private static List<GrantedAuthority> mapAuthorities(
      Saml2AuthenticatedPrincipal principal, String rolesAttribute, String stripRolePrefix) {
    List<Object> roles = principal.getAttribute(rolesAttribute);
    List<GrantedAuthority> result = new ArrayList<>();
    if (roles == null)
      return result;

    for (Object role : roles) {
      String value = role.toString();
      if (!stripRolePrefix.isEmpty() && value.startsWith(stripRolePrefix)) {
        value = value.substring(stripRolePrefix.length());
      }
      value = value.toUpperCase(Locale.ROOT);
      if (!value.startsWith("ROLE_")) {
        value = "ROLE_" + value;
      }
      result.add(new SimpleGrantedAuthority(value));
    }
    return result;
  }

  private static Saml2X509Credential loadSigningCredential(Resource key, Resource cert) {
    try (InputStream keyStream = key.getInputStream(); InputStream certStream = cert.getInputStream()) {
      RSAPrivateKey privateKey = RsaKeyConverters.pkcs8().convert(keyStream);
      X509Certificate certificate =
          (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(certStream);
      return Saml2X509Credential.signing(privateKey, certificate);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to load SAML signing credential", e);
    }
  }

}


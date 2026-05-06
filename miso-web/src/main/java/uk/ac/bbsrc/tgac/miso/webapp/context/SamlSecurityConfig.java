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
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
import org.springframework.util.StringUtils;

import com.eaglegenomics.simlims.core.manager.SecurityManager;

import jakarta.servlet.http.HttpSession;
import uk.ac.bbsrc.tgac.miso.core.security.MisoAuthority;
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
      @Value("${security.saml.sp.registrationId:miso}") String registrationId,
      @Value("${security.saml.idp.metadataUrl}") String metadataUrl,
      @Value("${security.saml.sp.privateKey:#{null}}") String privateKeyLocation,
      @Value("${security.saml.sp.certificate:#{null}}") String certificateLocation) {

    RelyingPartyRegistration.Builder builder = RelyingPartyRegistrations.fromMetadataLocation(metadataUrl)
        .registrationId(registrationId)
        .entityId("{baseUrl}/saml2/service-provider-metadata/{registrationId}")
        .assertionConsumerServiceLocation("{baseUrl}/login/saml2/sso/{registrationId}")
        .singleLogoutServiceLocation("{baseUrl}/logout/saml2/slo/{registrationId}")
        .singleLogoutServiceResponseLocation("{baseUrl}/logout/saml2/slo/{registrationId}")
        .singleLogoutServiceBinding(Saml2MessageBinding.POST);

    if (StringUtils.hasText(privateKeyLocation) || StringUtils.hasText(certificateLocation)) {
      if (!StringUtils.hasText(privateKeyLocation) || !StringUtils.hasText(certificateLocation)) {
        throw new IllegalStateException(
            "Both security.saml.sp.privateKey and security.saml.sp.certificate must be set together");
      }
      DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
      Resource privateKey = resourceLoader.getResource(normalizeResourceLocation(privateKeyLocation));
      Resource certificate = resourceLoader.getResource(normalizeResourceLocation(certificateLocation));
      builder.signingX509Credentials(c -> c.add(loadSigningCredential(privateKey, certificate)));
    } else {
      builder.authnRequestsSigned(false)
          .assertingPartyDetails(party -> party.wantAuthnRequestsSigned(false));
    }

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
      @Value("${security.saml.internalRoleName}") String internalRoleName,
      @Value("${security.saml.adminRoleName}") String adminRoleName) {

    if (!StringUtils.hasText(internalRoleName) || !StringUtils.hasText(adminRoleName)) {
      throw new IllegalArgumentException(
          "Both security.saml.internalRoleName and security.saml.adminRoleName must be set");
    }

    OpenSaml4AuthenticationProvider provider = new OpenSaml4AuthenticationProvider();
    provider.setResponseAuthenticationConverter(token -> {
      Saml2Authentication auth =
          OpenSaml4AuthenticationProvider.createDefaultResponseAuthenticationConverter().convert(token);

      Saml2AuthenticatedPrincipal principal = (Saml2AuthenticatedPrincipal) auth.getPrincipal();
      List<GrantedAuthority> authorities = mapAuthorities(principal, rolesAttribute, internalRoleName, adminRoleName);
      SamlUserDetails userDetails = new SamlUserDetails(
          getRequiredAttribute(principal, usernameAttribute).toLowerCase(Locale.ROOT),
          getRequiredAttribute(principal, firstNameAttribute) + " "
              + getRequiredAttribute(principal, lastNameAttribute),
          getRequiredAttribute(principal, emailAttribute),
          authorities,
          principal);

      if (hasMisoLoginAuthority(authorities)) {
        try {
          securityManager.syncUser(userDetails);
        } catch (Exception e) {
          throw new InternalAuthenticationServiceException("User sync failed", e);
        }
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
      OpenSaml4AuthenticationProvider samlAuthenticationProvider,
      @Value("${security.saml.sp.registrationId:miso}") String registrationId) throws Exception {

    String sloUrl = "/logout/saml2/slo/" + registrationId;
    AuthenticationFailureHandler samlFailureHandler = (request, response, exception) -> {
      SecurityContextHolder.clearContext();
      HttpSession session = request.getSession(false);
      if (session != null) {
        session.invalidate();
      }
      response.sendRedirect(request.getContextPath() + "/login?login_error=1");
    };
    AuthenticationSuccessHandler samlSuccessHandler = (request, response, authentication) -> {
      if (!hasMisoLoginAuthority(authentication.getAuthorities())) {
        response.sendRedirect(request.getContextPath() + "/login?login_error=1");
        return;
      }
      successHandler.onAuthenticationSuccess(request, response, authentication);
    };

    return SecurityConfig.setupCommon(http, apiKeyFilter)
        .saml2Login(saml -> saml
            .loginPage("/login")
            .successHandler(samlSuccessHandler)
            .failureHandler(samlFailureHandler)
            .authenticationManager(new ProviderManager(samlAuthenticationProvider)))
        .saml2Logout(saml -> saml
            .logoutRequest(logout -> logout.logoutUrl(sloUrl))
            .logoutResponse(logout -> logout.logoutUrl(sloUrl)))
        .saml2Metadata(Customizer.withDefaults())
        .build();
  }

  private static String getRequiredAttribute(Saml2AuthenticatedPrincipal principal, String attribute) {
    String value = principal.getFirstAttribute(attribute);
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("Missing SAML attribute: " + attribute);
    }
    return value;
  }

  private static List<GrantedAuthority> mapAuthorities(
      Saml2AuthenticatedPrincipal principal, String rolesAttribute, String internalRoleName, String adminRoleName) {
    List<Object> roles = principal.getAttribute(rolesAttribute);
    List<GrantedAuthority> result = new ArrayList<>();
    if (roles == null)
      return result;

    for (Object role : roles) {
      String value = role.toString();
      if (value.equals(internalRoleName)) {
        result.add(MisoAuthority.ROLE_INTERNAL);
      }
      if (value.equals(adminRoleName)) {
        result.add(MisoAuthority.ROLE_ADMIN);
      }
    }
    return result;
  }

  private static boolean hasMisoLoginAuthority(Iterable<? extends GrantedAuthority> authorities) {
    for (GrantedAuthority authority : authorities) {
      if (authority.getAuthority().equals(MisoAuthority.ROLE_INTERNAL.getAuthority())) {
        return true;
      }
    }
    return false;
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

  private static String normalizeResourceLocation(String location) {
    return location.contains(":") ? location : "file:" + location;
  }

}

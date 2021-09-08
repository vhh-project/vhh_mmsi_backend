package eu.vhhproject.mmsi.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.WebFilter;

import eu.vhhproject.mmsi.gateway.GatewayProperties.GatewayOauth2Properties;
import reactor.core.publisher.Mono;

@RestController
@SpringBootApplication
public class GatewayApplication {

  /* Properties */

  @Autowired
  private GatewayOauth2Properties oauth2Properties;

  @Autowired
  private OAuth2ClientProperties oauth2ClientProps;

  @GetMapping("/")
  public Object index(
      Model model,
      @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient,
      @AuthenticationPrincipal OAuth2User oauth2User)
  {
    authorizedClient.getClientRegistration();
    // further potential useful information can be found here
    //   authorizedClient.getClientRegistration().getClientName()
    return oauth2User;
  }

  @GetMapping("/test-api/1")
  public String testApi1()
  {
    return "{ \"Test API Endpoint\" : 1 }";
  }

  @GetMapping("/test-api/2")
  public String testApi2()
  {
    return "{ \"Test API Endpoint\" : 2 }";
  }

  @Autowired
  ReactiveClientRegistrationRepository clientRegistrationRepository;

  private OidcClientInitiatedServerLogoutSuccessHandler oidcLogoutSuccessHandler()
  {
    OidcClientInitiatedServerLogoutSuccessHandler successHandler = new OidcClientInitiatedServerLogoutSuccessHandler(
        clientRegistrationRepository);

    successHandler.setPostLogoutRedirectUri(oauth2Properties.getLogoutRedirectUri());

    return successHandler;
  }

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http)
  {
    // Enable OAuth2
    http.oauth2Login();

    // Require that all requests are authenticated 
    http.authorizeExchange(exchanges -> exchanges.anyExchange().authenticated());

    // Logout and logout handling
    http.logout(logout -> logout.logoutSuccessHandler(oidcLogoutSuccessHandler()));

    // Enable CSRF Token Repository to provide the token via a Cookie to the frontend
    // see: https://docs.spring.io/spring-security/site/docs/5.2.4.RELEASE/reference/htmlsingle/#webflux-csrf-configure-custom-repository
    //
    http.csrf().csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse());

    return http.build();
  }

  /**
   * Add dummy subscription to CSRF token, as it would not be included in
   * responses per default. Which is actually the behavior for the Servlet stack,
   * but apparently not for the Reactive stack.
   * 
   * <p>
   * See the github issue for more information and context:
   * 
   * <li>https://github.com/spring-projects/spring-security/issues/5766
   */
  @Bean
  WebFilter addCsrfToken()
  {
    // The solution is based on a comment in above issue:
    // https://github.com/spring-projects/spring-security/issues/5766#issuecomment-418380289

    // @formatter:off
    return (exchange, next) -> exchange.<Mono<CsrfToken>>getAttribute(CsrfToken.class.getName())
        .doOnSuccess(token -> { }) // do nothing, just subscribe :/
        .then(next.filter(exchange));
    // @formatter:on
  }

  public static void main(String[] args)
  {
    SpringApplication.run(GatewayApplication.class, args);
  }
}

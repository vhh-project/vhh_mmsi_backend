package eu.vhhproject.mmsi.gateway.filters;

import java.nio.charset.Charset;
import java.util.Base64;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CAAuthGatewayFilterFactory extends AbstractGatewayFilterFactory<CAAuthGatewayFilterFactory.Config> {

  public CAAuthGatewayFilterFactory() {
    super(Config.class);
  }

  @Override
  public GatewayFilter apply(Config config)
  {

    // @formatter:off
    return (exchange, chain) -> exchange.getPrincipal()
        .filter(principal -> principal instanceof OAuth2AuthenticationToken)
        .cast(OAuth2AuthenticationToken.class)
        .flatMap(authentication -> { 
          // @formatter:on
          ServerHttpRequest.Builder builder = exchange.getRequest().mutate();

          String userName = authentication.getPrincipal().getAttribute("preferred_username");

          String credentials = userName + ":secret";
          byte[] encodedAuth = Base64.getEncoder().encode(credentials.getBytes(Charset.forName("US-ASCII")));

          builder.header("Authorization", "Basic " + new String(encodedAuth));

          log.debug("Setting Authorization header for CA authentication");

          exchange.mutate().request(builder.build()).build();
          return Mono.just(exchange);
        // @formatter:off
        })
        .defaultIfEmpty(exchange).flatMap(chain::filter);
  }

  public static class Config {
    // No config necessary for now
  }

}

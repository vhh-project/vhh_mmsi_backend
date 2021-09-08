package eu.vhhproject.mmsi.gateway;

import java.net.URI;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;

@Validated
@ConfigurationProperties(prefix = "gateway")
@EnableConfigurationProperties
public class GatewayProperties {

  @Component
  @Validated
  @ConfigurationProperties(prefix = "gateway.oauth2")
  @Getter
  @Setter
  public static class GatewayOauth2Properties {

    @NotNull
    private URI logoutRedirectUri;

  }

}

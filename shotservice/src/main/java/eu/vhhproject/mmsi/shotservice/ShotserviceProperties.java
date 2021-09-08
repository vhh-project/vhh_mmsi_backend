package eu.vhhproject.mmsi.shotservice;

import javax.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;

@Validated
@ConfigurationProperties(prefix = "shotservice")
@EnableConfigurationProperties
public class ShotserviceProperties {

  @Component
  @Validated
  @ConfigurationProperties(prefix = "shotservice.error")
  @Getter
  @Setter
  public static class ShotServiceErrorProperties {

    /**
     * Flag for including the exception in error responses to clients.
     */
    private boolean includeException = false;

    /**
     * Flag for including the stack trace in error responses to clients.
     */
    private boolean includeStackTrace = false;

  }

  @Component
  @Validated
  @ConfigurationProperties(prefix = "shotservice.ca")
  @Getter
  @Setter
  public static class ShotServiceCAProperties {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String serviceUrl;

    @NotBlank
    private String mediaUrl;
  }

  @Component
  @Validated
  @ConfigurationProperties(prefix = "shotservice.swagger")
  @Getter
  @Setter
  public static class ShotServiceSwaggerProperties {

    @NotBlank
    private String applicationBasePath;

  }

}

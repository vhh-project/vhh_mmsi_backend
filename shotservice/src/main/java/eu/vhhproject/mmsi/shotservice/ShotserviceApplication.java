package eu.vhhproject.mmsi.shotservice;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletPath;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import eu.vhhproject.mmsi.shotservice.error.ShotServiceErrorController;
import eu.vhhproject.mmsi.shotservice.error.ShotServiceErrorPageRegistration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableAutoConfiguration(
    exclude = {
        // Disable Spring Boot Error MVC auto configuration.
        // The application uses custom error handling where all 
        // exceptions and errors are returned as json responses.
        ErrorMvcAutoConfiguration.class })
public class ShotserviceApplication {

  @Autowired
  private BuildProperties buildProperties;

  public static void main(String[] args)
  {

    SpringApplication.run(ShotserviceApplication.class, args);
  }

  /* ***** Beans for error handling ***** */

  @Bean
  ShotServiceErrorController errorController()
  {
    Instant buildTime = buildProperties.getTime();
    String buildTimeFmtted = "not available";

    if (buildTime != null)
    {
      buildTimeFmtted = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("Europe/Vienna")).format(buildTime);
    }

    log.info("");
    log.info("Build information");
    log.info("");
    log.info("Version: " + buildProperties.getVersion());
    log.info("Build Time: " + buildTimeFmtted);
    log.info("");

    return new ShotServiceErrorController();
  }

  @Bean
  public ShotServiceErrorPageRegistration errorPageRegistration(DispatcherServletPath dispatcherServletPath)
  {
    return new ShotServiceErrorPageRegistration(dispatcherServletPath);
  }

  /* ***** Utility beans ***** */

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder)
  {
    return builder.build();
  }
}

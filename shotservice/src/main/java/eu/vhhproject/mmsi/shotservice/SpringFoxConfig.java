package eu.vhhproject.mmsi.shotservice;

import java.util.Collections;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import eu.vhhproject.mmsi.shotservice.ShotserviceProperties.ShotServiceSwaggerProperties;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.DocExpansion;
import springfox.documentation.swagger.web.ModelRendering;
import springfox.documentation.swagger.web.OperationsSorter;
import springfox.documentation.swagger.web.TagsSorter;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class SpringFoxConfig {

  @Autowired
  private ServletContext servletContext;

  @Autowired
  private ShotServiceSwaggerProperties swaggerProperties;

  @Bean
  public Docket apiDocket()
  {
    // @formatter:off
    return new Docket(DocumentationType.SWAGGER_2)
        .pathProvider(new RelativePathProvider(servletContext) {
            @Override
            public String getApplicationBasePath() {
              return swaggerProperties.getApplicationBasePath();
            }
            @Override
            public String getDocumentationPath() {
              return swaggerProperties.getApplicationBasePath();
            }
          })
        .select()
        .apis(RequestHandlerSelectors.basePackage("eu.vhhproject.mmsi.shotservice.api"))
        .paths(PathSelectors.ant("/videos/**"))
        .build()
        .useDefaultResponseMessages(false)
        .apiInfo(apiInfo());
    // @formatter:on
  }

  private ApiInfo apiInfo()
  {
    // @formatter:off
    return new ApiInfo(
        "VHH-MMSI Shot Service API", 
        """
        <p>
        The shot service API currently supports searching for videos and retrieval / storage of automatically created shots.
        </p>
        <p>
        The API is completely json based, consuming and producing messages of content type <i>application/json</i>.
        </p>
        </span>
        <details>
        <summary>General Error Handling</summary>
        
        In case of a general error, the API returns a json object with the following fields:
        </p>
        <pre>
          {
            "timestamp": "2020-03-19T08:22:25.524048Z",
            "statusCode": 500,
            "statusReason": "Internal Server Error",
            "exception": {
              "handledBy": "ErrorController",
              "name": "java.lang.Exception",
              "message": "Checked Exception"
              "stacktrace" : [
                  "java.lang.Exception: Checked Exception",
                  "    at eu.vhhproject.mmsi.shotservice.api.ShotController.testCheckedException(ShotController.java:93)",
                  ....
              ]
            }
          }
        </pre>
        <p>
        The HTTP status code and reason are returned in the fields <b>statusCode</b> and <b>statusReason</b>.
        </p>
        <p>
        The <b>exception</b> object provides further information on the exception that caused the error. 
        This information is irrelevant for clients and can only be used to diagnose server-side problems.
        </p>
        </details>

        <details>
        <summary>Specific Error Codes</summary>
        
        Besides the general error response with the HTTP status codes, the API might return more specific errors.
        The following additional fields are then included in the response:
          * **errorCode**: the error code, e.g. *malformedJson*, *constraintViolation*, etc.
          * **errorDetails**: further details on the error that provide additional information for the client. Different error codes have different error details. 
        
        Below is an example of an error response with a specific error code:
        <pre>
          {
            "timestamp": "2020-03-19T08:30:10.090759Z",
            "statusCode": 400,
            "statusReason": "Bad Request",
            "errorCode": "malformedJson",
            "errorDetails": {
              "message": "Unexpected character ('.' (code 46)): was expecting double-quote to start field name",
              "line": 1,
              "column": 2
            },
            "exception": {
              ...
            }
          }
        </pre>
        <p>
        Information on which error details are returned for which error code can be found at each API endpoint that returns the error code and in the Models Section below.
        </p>
        <p>
        <b>Note</b> that an API endpoint might return different error codes although the HTTP status code remains the same. Typically, a HTTP status code 400 will be returned 
        to indicate a client error. The error code then provides further details. The API documentation only allows to document a single response per HTTP status code. 
        Therefore, when API endpoint returns multiple error codes under HTTP status code 400, additional error codes are documented under status codes 460, 461, etc.
        </p>
        </details>
        """,
        null,
        null,
        null,
        null,
        null, 
        Collections.emptyList());
    // @formatter:on
  }

  @Bean
  public UiConfiguration swaggerUiConfig()
  {
    // @formatter:off
      return UiConfigurationBuilder.builder()
        .deepLinking(true)
        .displayOperationId(false)
        .defaultModelsExpandDepth(1)
        .defaultModelExpandDepth(0)
        .defaultModelRendering(ModelRendering.MODEL)
        .displayRequestDuration(false)
        .docExpansion(DocExpansion.LIST)
        .filter(false)
        .maxDisplayedTags(null)
        .operationsSorter(OperationsSorter.ALPHA)
        .showExtensions(false)
        .tagsSorter(TagsSorter.ALPHA)
        .supportedSubmitMethods(UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS)
        .validatorUrl(null)
        .build();
    // @formatter:on
  }
}

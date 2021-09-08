package eu.vhhproject.mmsi.shotservice.api;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@Transactional
public class ShotControllerTest {

  private static final String RESOURCE_BASE_PATH = "/api/shots";

  @Autowired
  private MockMvc mockMvc;

  @Test
  void postShots2xx() throws Exception
  {
    RequestBuilder requestBuilder = createJsonRequestFromFile("/videos/1/shots/auto",
        RESOURCE_BASE_PATH + "/shots-auto-create-1.json");
    mockMvc.perform(get("/videos/1/shots/auto")).andExpect(status().is(404));

    ResultActions result = mockMvc.perform(requestBuilder).andExpect(status().isOk());

    // @formatter:off
    result.andExpect(jsonPath("$.[3].inPoint", equalTo(301)))
          .andExpect(jsonPath("$.[3].outPoint", equalTo(400)))
          .andExpect(jsonPath("$.[3].shotType", equalTo("MS")))
          .andExpect(jsonPath("$.[3].cameraMovement", equalTo("TILT")));
    // @formatter:on
  }

  @Test
  void postShots4xxMissingInAndOutPoint() throws Exception
  {
    RequestBuilder requestBuilder = createJsonRequestFromFile("/videos/1/shots/auto",
        RESOURCE_BASE_PATH + "/shots-create-4xx-missing-in-and-out-point.json");

    ResultActions result = mockMvc.perform(requestBuilder).andExpect(status().is(400));

    // @formatter:off
    result.andExpect(jsonPath("$.errorCode", equalTo("constraintViolation")))
          .andExpect(jsonPath("$.errorDetails.length()", equalTo(4)))
          .andExpect(jsonPath("$.errorDetails[0].property", equalTo("[0].inPoint")))
          .andExpect(jsonPath("$.errorDetails[1].property", equalTo("[0].outPoint")));
    // @formatter:on
  }

  @Test
  void postShots4xxInvalidCameraMovementAndShotType() throws Exception
  {
    RequestBuilder requestBuilder = createJsonRequestFromFile("/videos/1/shots/auto",
        RESOURCE_BASE_PATH + "/shots-create-4xx-invalid-camera-movement-and-shot-type.json");

    ResultActions result = mockMvc.perform(requestBuilder).andExpect(status().is(400));

    // @formatter:off
    result.andExpect(jsonPath("$.errorCode", equalTo("malformedJson")))
          .andExpect(jsonPath("$.errorDetails.length()", equalTo(3)))
          .andExpect(jsonPath("$.errorDetails.message", startsWith("Cannot deserialize")))
          .andExpect(jsonPath("$.errorDetails.line", equalTo(5)))
          .andExpect(jsonPath("$.errorDetails.column", equalTo(24)));
    // @formatter:on
  }

  /* ***** Private Methods ***** */

  private RequestBuilder createJsonRequestFromFile(String apiPath, String fileNameRequestBody) throws Exception
  {
    MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.request(HttpMethod.valueOf("POST"), apiPath);

    requestBuilder.accept(MediaType.APPLICATION_JSON);

    if (fileNameRequestBody != null)
    {
      URL url = getClass().getResource(fileNameRequestBody);

      if (url == null)
      {
        throw new FileNotFoundException("File not found: " + fileNameRequestBody);
      }

      URI uri = url.toURI();
      Path path = Path.of(uri);

      byte[] fileContent = Files.readAllBytes(path);

      requestBuilder.content(new String(fileContent, StandardCharsets.UTF_8));
      requestBuilder.contentType(MediaType.APPLICATION_JSON);
    }

    return requestBuilder;
  }
}

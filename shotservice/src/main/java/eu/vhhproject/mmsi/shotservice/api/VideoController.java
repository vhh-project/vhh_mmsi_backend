package eu.vhhproject.mmsi.shotservice.api;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import eu.vhhproject.mmsi.shotservice.ShotserviceProperties.ShotServiceCAProperties;
import eu.vhhproject.mmsi.shotservice.api.tos.CAObjectRepresentationSearchResultsTO;
import eu.vhhproject.mmsi.shotservice.api.tos.CAObjectRepresentationTO;
import eu.vhhproject.mmsi.shotservice.api.tos.VideoTO;
import eu.vhhproject.mmsi.shotservice.error.InternalServiceException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Api(tags = VideoController.API_NAME_VIDEOS, description = "_")
public class VideoController {

  public static final String API_NAME_VIDEOS = "Videos";
  public static final String BASE_PATH = "/videos";

  /* Properties */

  @Autowired
  private ShotServiceCAProperties caProps;

  /* Misc */

  @Autowired
  private RestTemplate restTemplate;

  /* ***** REST endpoints ***** */

  @ApiOperation(value = "Search for videos", notes = """
      <p>
      Get videos that are stored in the MMSI. The returned information is primarily targeted at the requirements for
      the automatic processing at TUW-CV.
      </p>
      <p>
      Videos can be filtered by the processing status via the processed query parameter. Paging is not supported.
      </p>
      """)
  @ApiResponses(value = { @ApiResponse(code = 200, message = "The list of all matching videos is returned") })
  @GetMapping(path = BASE_PATH + "/search")
  public List<VideoTO> searchVideos(
      @ApiParam(name = "processed", value = "filter by processing status of TUW-CV") @RequestParam(
          name = "processed",
          required = false) Boolean processed)
      throws InternalServiceException
  {
    HttpHeaders headers = new HttpHeaders();
    String credentials = caProps.getUsername() + ":" + caProps.getPassword();

    byte[] encodedAuth = Base64.getEncoder().encode(credentials.getBytes(Charset.forName("US-ASCII")));
    headers.add("Authorization", "Basic " + new String(encodedAuth));

    headers.setContentType(MediaType.APPLICATION_JSON);

    // specify fields that should be returned
    @SuppressWarnings("preview")
    String searchRequest = """
        {
          "bundles" : {
            "ca_object_representations.vhh_mmsi_processed" : true,
            "ca_object_representations.original_filename" : true,
            "ca_object_representations.media.original" : { "returnURL" : true}
          }
        }
        """;

    HttpEntity<String> req = new HttpEntity<>(searchRequest, headers);
    ResponseEntity<CAObjectRepresentationSearchResultsTO> response = null;

    // It seems that somewhere a cache is disturbing the correct response of searches.
    // See: MMSI-487
    // For the time being, the cacheinvalidator parameter seems to be sufficient to 
    // bypass the cache and return the up-to-date state of entities.
    String query = "?cacheinvalidator=" + System.currentTimeMillis() + "&q=mimetype:\"video/mp4\"";

    if (processed != null)
    {
      query = query + " AND ";
      if (processed)
      {
        query += "ca_object_representations.vhh_mmsi_processed:1";
      }
      else
      {
        // Search for all object representations whose processed field is empty 
        // or contains the text "0" -> both are used to indicate that an object 
        // representation has not yet been processed
        query += "(ca_object_representations.vhh_mmsi_processed:\"[BLANK]\" OR ca_object_representations.vhh_mmsi_processed:0)";
      }
    }

    try
    {
      response = restTemplate.exchange(caProps.getServiceUrl() + "/find/ca_object_representations" + query,
          HttpMethod.POST, req, CAObjectRepresentationSearchResultsTO.class);
    }
    catch (RestClientException e)
    {
      throw new InternalServiceException("CA", e);
    }

    List<VideoTO> videoTOs = new ArrayList<>();

    if (response != null && HttpStatus.OK.equals(response.getStatusCode()) && response.hasBody())
    {
      for (CAObjectRepresentationTO caObjectRepresentationTO : response.getBody().getResults())
      {
        VideoTO videoTO = new VideoTO();

        videoTO.setId(caObjectRepresentationTO.getRepresentationId());
        videoTO.setOriginalFileName(caObjectRepresentationTO.getOriginalFilename());
        videoTO.setProcessed(caObjectRepresentationTO.isProcessed());

        if (caObjectRepresentationTO.getUrl() != null)
        {
          videoTO.setUrl(caObjectRepresentationTO.getUrl().replaceFirst("^http.*\\/providence\\/media\\/",
              caProps.getMediaUrl() + "/"));
        }

        videoTOs.add(videoTO);
      }
    }

    return videoTOs;
  }
}
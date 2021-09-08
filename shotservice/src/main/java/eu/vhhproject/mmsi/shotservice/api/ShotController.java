package eu.vhhproject.mmsi.shotservice.api;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import eu.vhhproject.mmsi.shotservice.ShotserviceProperties.ShotServiceCAProperties;
import eu.vhhproject.mmsi.shotservice.api.tos.CAObjectRepresentationPUTTO;
import eu.vhhproject.mmsi.shotservice.api.tos.ShotTO;
import eu.vhhproject.mmsi.shotservice.error.EntityNotFoundException;
import eu.vhhproject.mmsi.shotservice.error.ErrorCode;
import eu.vhhproject.mmsi.shotservice.error.ErrorDetailsConstraintViolation;
import eu.vhhproject.mmsi.shotservice.error.ErrorDetailsMalformedJson;
import eu.vhhproject.mmsi.shotservice.error.InternalServiceException;
import eu.vhhproject.mmsi.shotservice.mapping.ShotMapper;
import eu.vhhproject.mmsi.shotservice.model.Shot;
import eu.vhhproject.mmsi.shotservice.repository.ShotRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

@Validated
@RestController
@Transactional
@Slf4j
@Api(tags = "Shots Auto", description = "_")
public class ShotController {

  public static final String BASE_PATH = "/videos/{videoId}/shots/auto";

  /* Repositories */

  @Autowired
  private ShotRepository shotRepository;

  /* Mappers */

  @Autowired
  private ShotMapper shotMapper;

  /* Properties */

  @Autowired
  private ShotServiceCAProperties caProps;

  /* Misc */

  @Autowired
  private RestTemplate restTemplate;

  /* ***** REST endpoints ***** */

  @ApiResponses(value = { @ApiResponse(code = 200, message = "The list is returned") })
  @ApiOperation(value = "Get automatically created shots", notes = """
      <p>
      Get the list of automatically created shots for this video. Per default sorted by inPoint ascending.
      </p>
      """)
  //
  // The sort parameter was causing issues with springfox-bean-validator 2.8.0 resulting 
  // in an exception and not creating the whole API documentation
  // Upgraded to 2.9.2 which auto-generated documentation for sorted and unsorted query params?
  // Going with an explicit solution that was taken from a Stackoverflow suggestion:
  //
  // * https://stackoverflow.com/a/35427093
  //
  @ApiImplicitParams({ @ApiImplicitParam(
      name = "sort",
      allowMultiple = true,
      dataType = "string",
      paramType = "query",
      value = "Sorting criteria in the format: property(,asc|desc). " + "Default sort order is ascending. "
          + "Multiple sort criteria are supported.") })
  @Transactional(readOnly = true)
  @GetMapping(path = BASE_PATH)
  public List<ShotTO> getShots(@PathVariable Long videoId, @ApiIgnore @SortDefault(sort = "inPoint") Sort sort)
      throws EntityNotFoundException
  {
    if (!shotRepository.existsByVideoId(videoId))
    {
      throw new EntityNotFoundException("video", videoId);
    }

    return shotMapper.shotsToShotTOs(shotRepository.findByVideoId(videoId, sort));
  }

  @ApiOperation(value = "Add automatically created shots", notes = """
      <p>
      Add a list of automatically created shots.
      </p>
      <p>
      This list completely replaces all existing automatically created shots
      that are stored for this video.
      </p>
      """)
  @ApiResponses(value = {
     // @formatter:off
     @ApiResponse(code = 200,
                  message = "The list was successfully stored and is returned as response"),
     @ApiResponse(code = 400,
                  message = "Error Code '" + ErrorCode.MALFORMED_JSON + "': the json sent by the client is malformed",
                  response = ErrorDetailsMalformedJson.class),
     @ApiResponse(code = 460,
                  message = "Error Code '" + ErrorCode.CONSTRAINT_VIOLATION + "': objects in the json body are not valid wrt. the model",
                  response = ErrorDetailsConstraintViolation.class, responseContainer = "List")
     // @formatter:on
  })
  @PostMapping(path = BASE_PATH)
  public List<ShotTO> postShots(
      @PathVariable Long videoId,
      @ApiParam(name = "shots", value = "list of automatically created shots") @RequestBody List<@Valid ShotTO> shotTOs)
      throws InternalServiceException
  {
    List<Shot> shots = shotMapper.shotTOsToShots(shotTOs);

    log.info("Received new auto shots for video '{}'", videoId);

    for (Shot shot : shots)
    {
      shot.setVideoId(videoId);
    }

    shotRepository.deleteByVideoId(videoId);

    List<ShotTO> shotTos = shotMapper.shotsToShotTOs(shotRepository.saveAll(shots));

    HttpHeaders headers = new HttpHeaders();
    String credentials = caProps.getUsername() + ":" + caProps.getPassword();

    byte[] encodedAuth = Base64.getEncoder().encode(credentials.getBytes(Charset.forName("US-ASCII")));
    headers.add("Authorization", "Basic " + new String(encodedAuth));

    headers.setContentType(MediaType.APPLICATION_JSON);

    // specify fields that should be returned
    @SuppressWarnings("preview")
    String updateRequest = """
        {
          "remove_attributes": [
            "vhh_mmsi_processed"
          ],
          "attributes": {
            "vhh_mmsi_processed": [
              {
                "vhh_mmsi_processed": 1
              }
            ]
          }
        }
        """;

    HttpEntity<String> req = new HttpEntity<>(updateRequest, headers);
    ResponseEntity<CAObjectRepresentationPUTTO> response = null;

    try
    {
      log.info("Updating processed status of object representation '{}' in CA", videoId);

      response = restTemplate.exchange(caProps.getServiceUrl() + "/item/ca_object_representations/id/" + videoId,
          HttpMethod.PUT, req, CAObjectRepresentationPUTTO.class);

      log.info("Received response from CA for object representation '{}': '{}'", videoId,
          response.getStatusCodeValue());
    }
    catch (RestClientException e)
    {
      log.error("Failed to update object representation '{}' in CA: '{}'", videoId, e.getMessage());
      throw new InternalServiceException("CA", e);
    }

    return shotTos;
  }
}

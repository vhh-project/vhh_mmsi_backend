package eu.vhhproject.mmsi.shotservice.api.tos;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CAObjectRepresentationPUTTO {
  private boolean ok;

  @JsonProperty("representation_id")
  private Long representationId;
}

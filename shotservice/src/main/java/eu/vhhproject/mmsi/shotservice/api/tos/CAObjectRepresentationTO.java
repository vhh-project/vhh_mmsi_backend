package eu.vhhproject.mmsi.shotservice.api.tos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import eu.vhhproject.mmsi.shotservice.support.NumericBooleanDeserializer;
import lombok.Data;

@Data
public class CAObjectRepresentationTO {

  @JsonProperty("representation_id")
  private Long representationId;

  @JsonProperty("ca_object_representations.original_filename")
  private String originalFilename;

  @JsonProperty("ca_object_representations.vhh_mmsi_processed")
  @JsonDeserialize(using = NumericBooleanDeserializer.class)
  private boolean processed;

  @JsonProperty("ca_object_representations.media.original")
  private String url;

}

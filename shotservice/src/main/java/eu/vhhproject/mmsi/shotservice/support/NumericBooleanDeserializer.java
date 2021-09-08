package eu.vhhproject.mmsi.shotservice.support;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class NumericBooleanDeserializer extends JsonDeserializer<Boolean> {

  @Override
  public Boolean deserialize(JsonParser parser, DeserializationContext context)
      throws IOException, JsonProcessingException
  {
    // empty string and 0 should be converted to "false", all other (non-null) values to "true"
    return parser.getText().length() == 0 || "0".equals(parser.getText()) ? false : true;
  }

  @Override
  public Boolean getNullValue(DeserializationContext ctxt)
  {
    // null should be interpreted as "false"
    return Boolean.FALSE;
  }

}

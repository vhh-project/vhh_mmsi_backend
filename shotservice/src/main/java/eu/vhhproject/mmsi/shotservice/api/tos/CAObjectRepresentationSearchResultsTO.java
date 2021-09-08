package eu.vhhproject.mmsi.shotservice.api.tos;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class CAObjectRepresentationSearchResultsTO {
  private boolean ok;
  
  private int total;

  private List<CAObjectRepresentationTO> results = new ArrayList<>();

}

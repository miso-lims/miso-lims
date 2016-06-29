package uk.ac.bbsrc.tgac.miso.dto;

import java.net.URI;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonTypeName(value = SampleTissueProcessing.CATEGORY_NAME)
public class SampleTissueProcessingDto extends SampleTissueDto {

  @Override
  public void writeUrls(URI baseUri) {
    super.writeUrls(baseUri);
    setUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/tree/sample/{id}").buildAndExpand(getId()).toUriString());
  }

}

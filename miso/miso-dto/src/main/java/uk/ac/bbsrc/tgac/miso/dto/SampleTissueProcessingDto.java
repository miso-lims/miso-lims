package uk.ac.bbsrc.tgac.miso.dto;

import java.net.URI;

import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;

import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName(value = SampleTissueProcessing.CATEGORY_NAME)
public class SampleTissueProcessingDto extends SampleTissueDto {

  @Override
  public void writeUrls(URI baseUri) {
    super.writeUrls(baseUri);
    setUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/tree/sample/{id}").buildAndExpand(getId()).toUriString());
  }

}

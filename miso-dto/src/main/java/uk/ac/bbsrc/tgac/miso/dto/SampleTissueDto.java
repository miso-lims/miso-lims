package uk.ac.bbsrc.tgac.miso.dto;

import java.net.URI;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonTypeName(value = SampleTissue.CATEGORY_NAME)
public class SampleTissueDto extends SampleIdentityDto {

  private Integer cellularity;

  @Override
  public void writeUrls(URI baseUri) {
    super.writeUrls(baseUri);
    setUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/sample/tissue/{id}").buildAndExpand(getId()).toUriString());
  }

  public Integer getCellularity() {
    return cellularity;
  }

  public void setCellularity(Integer cellularity) {
    this.cellularity = cellularity;
  }

  @Override
  public String toString() {
    return "SampleTissueDto [cellularity=" + cellularity + "]";
  }

}

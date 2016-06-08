package uk.ac.bbsrc.tgac.miso.dto;

import java.net.URI;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import uk.ac.bbsrc.tgac.miso.core.data.Identity;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonTypeName(value = Identity.CATEGORY_NAME)
public class SampleIdentityDto extends SampleAdditionalInfoDto {

  private String internalName;
  private String externalName;
  private String donorSex;

  @Override
  public void writeUrls(URI baseUri) {
    super.writeUrls(baseUri);
  }

  public String getInternalName() {
    return internalName;
  }

  public void setInternalName(String internalName) {
    this.internalName = internalName;
  }

  public String getExternalName() {
    return externalName;
  }

  public void setExternalName(String externalName) {
    this.externalName = externalName;
  }

  public String getDonorSex() {
    return donorSex;
  }

  public void setDonorSex(String donorSex) {
    this.donorSex = donorSex;
  }

}

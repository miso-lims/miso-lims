package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;

import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName(value = SampleIdentity.CATEGORY_NAME)
public class SampleIdentityDto extends DetailedSampleDto {

  private String externalName;
  private String donorSex;
  private String consentLevel;

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

  public String getConsentLevel() {
    return consentLevel;
  }

  public void setConsentLevel(String consentLevel) {
    this.consentLevel = consentLevel;
  }

  @Override
  public String toString() {
    return "SampleIdentityDto [externalName=" + externalName + ", donorSex=" + donorSex
        + ", super=" + super.toString() + "]";
  }

}

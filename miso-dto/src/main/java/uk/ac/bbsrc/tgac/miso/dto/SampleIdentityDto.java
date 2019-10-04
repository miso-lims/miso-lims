package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;

import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;

@JsonTypeName(value = SampleIdentity.CATEGORY_NAME)
public class SampleIdentityDto extends DetailedSampleDto {

  private String donorSex;
  private String consentLevel;
  private String externalName;

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
    return "SampleIdentityDto [externalName=" + getExternalName() + ", donorSex=" + donorSex
        + ", super=" + super.toString() + "]";
  }

}

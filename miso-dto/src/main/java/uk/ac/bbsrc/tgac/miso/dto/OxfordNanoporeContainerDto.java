package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(value = "OxfordNanoporeContainer")
public class OxfordNanoporeContainerDto extends ContainerDto {

  private Long poreVersionId;
  private String receivedDate;
  private String returnedDate;

  public Long getPoreVersionId() {
    return poreVersionId;
  }

  public void setPoreVersionId(Long poreVersionId) {
    this.poreVersionId = poreVersionId;
  }

  public String getReceivedDate() {
    return receivedDate;
  }

  public void setReceivedDate(String receivedDate) {
    this.receivedDate = receivedDate;
  }

  public String getReturnedDate() {
    return returnedDate;
  }

  public void setReturnedDate(String returnedDate) {
    this.returnedDate = returnedDate;
  }

}

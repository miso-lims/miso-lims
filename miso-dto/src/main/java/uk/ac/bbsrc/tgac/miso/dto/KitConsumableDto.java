package uk.ac.bbsrc.tgac.miso.dto;

public class KitConsumableDto {
  private String date;
  private KitDescriptorDto descriptor;
  private Long id;
  private String lotNumber;

  public String getDate() {
    return date;
  }

  public KitDescriptorDto getDescriptor() {
    return descriptor;
  }

  public Long getId() {
    return id;
  }

  public String getLotNumber() {
    return lotNumber;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public void setDescriptor(KitDescriptorDto descriptor) {
    this.descriptor = descriptor;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setLotNumber(String lotNumber) {
    this.lotNumber = lotNumber;
  }

}

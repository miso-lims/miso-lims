package uk.ac.bbsrc.tgac.miso.dto;

public class TissuePieceTypeDto {

  private String abbreviation;
  private String v2NamingCode;
  private Boolean archived;
  private Long id;
  private String name;

  public String getAbbreviation() {
    return abbreviation;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Boolean isArchived() {
    return archived;
  }

  public void setAbbreviation(String abbreviation) {
    this.abbreviation = abbreviation;
  }

  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getV2NamingCode() {
    return v2NamingCode;
  }

  public void setV2NamingCode(String v2NamingCode) {
    this.v2NamingCode = v2NamingCode;
  }

}

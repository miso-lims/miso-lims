package uk.ac.bbsrc.tgac.miso.dto;

public class ReferenceGenomeDto {

  private Long id;
  private String alias;
  private String defaultScientificName;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getDefaultScientificName() {
    return defaultScientificName;
  }

  public void setDefaultScientificName(String defaultScientificName) {
    this.defaultScientificName = defaultScientificName;
  }

}

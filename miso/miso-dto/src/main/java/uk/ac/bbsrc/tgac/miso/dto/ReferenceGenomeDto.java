package uk.ac.bbsrc.tgac.miso.dto;

public class ReferenceGenomeDto {
  private String alias;

  private Long id;

  public String getAlias() {
    return alias;
  }

  public Long getId() {
    return id;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public void setId(Long id) {
    this.id = id;
  }

}

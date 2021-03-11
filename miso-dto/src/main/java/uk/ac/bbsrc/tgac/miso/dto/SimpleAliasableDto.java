package uk.ac.bbsrc.tgac.miso.dto;

/**
 * May represent any object that only has an ID and alias
 */
public class SimpleAliasableDto {

  private Long id;

  private String alias;

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

}

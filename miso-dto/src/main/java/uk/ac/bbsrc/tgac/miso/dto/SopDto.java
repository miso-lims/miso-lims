package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

public class SopDto {

  private long id;
  private String alias;
  private String version;
  private String category;
  private String url;
  private boolean archived;
  private List<SopFieldDto> fields;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public boolean isArchived() {
    return archived;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  public List<SopFieldDto> getFields() {
    return fields;
  }

  public void setFields(List<SopFieldDto> fields) {
    this.fields = fields;
  }

}

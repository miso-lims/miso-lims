package uk.ac.bbsrc.tgac.miso.dto;

import java.util.Collections;
import java.util.List;

/**
 * SOP DTO.
 *
 * <p>
 * {@code fields} is a display-only string summary used by list/datatable views. {@code sopFields}
 * contains full editable field definitions.
 * </p>
 */
public class SopDto {

  private long id;
  private String alias;
  private String version;
  private String category;
  private String url;
  private boolean archived;
  private String fields;

  /**
   * Full editable field definitions (ordered).
   */
  private List<SopFieldDto> sopFields = Collections.emptyList();

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

  public String getFields() {
    return fields;
  }

  public void setFields(String fields) {
    this.fields = fields;
  }

  public List<SopFieldDto> getSopFields() {
    return sopFields;
  }

  public void setSopFields(List<SopFieldDto> sopFields) {
    this.sopFields = sopFields == null ? Collections.emptyList() : sopFields;
  }
}

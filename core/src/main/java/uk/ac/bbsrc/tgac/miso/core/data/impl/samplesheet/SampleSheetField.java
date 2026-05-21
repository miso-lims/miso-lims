package uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet;

import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

public class SampleSheetField {

  private String name;

  @JdbcTypeCode(SqlTypes.JSON_ARRAY)
  private List<SampleSheetFieldSource> sources;

  private Boolean omitIfEmpty;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<SampleSheetFieldSource> getSources() {
    return sources;
  }

  public void setSources(List<SampleSheetFieldSource> sources) {
    this.sources = sources;
  }

  public Boolean getOmitIfEmpty() {
    return omitIfEmpty;
  }

  public void setOmitIfEmpty(Boolean omitIfEmpty) {
    this.omitIfEmpty = omitIfEmpty;
  }

}

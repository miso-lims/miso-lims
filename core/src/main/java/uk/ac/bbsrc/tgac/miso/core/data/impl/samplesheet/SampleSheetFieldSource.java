package uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class SampleSheetFieldSource {

  public enum Aggregation {
    JOIN_DISTINCT, MAX_LENGTH
  }

  private String value;
  private String source;
  private String sourceProperty;

  @Enumerated(EnumType.STRING)
  private Aggregation aggregation;

  private String separator;
  private String dateFormat;

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getSourceProperty() {
    return sourceProperty;
  }

  public void setSourceProperty(String sourceProperty) {
    this.sourceProperty = sourceProperty;
  }

  public Aggregation getAggregation() {
    return aggregation;
  }

  public void setAggregation(Aggregation aggregation) {
    this.aggregation = aggregation;
  }

  public String getSeparator() {
    return separator;
  }

  public void setSeparator(String separator) {
    this.separator = separator;
  }

  public String getDateFormat() {
    return dateFormat;
  }

  public void setDateFormat(String dateFormat) {
    this.dateFormat = dateFormat;
  }

}

package uk.ac.bbsrc.tgac.miso.dto;

import static uk.ac.bbsrc.tgac.miso.dto.Dtos.*;

import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheetFieldSource;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheetFieldSource.Aggregation;

public class SampleSheetFieldSourceDto {

  private String value;
  private String source;
  private String sourceProperty;
  private String dateFormat;
  private String aggregation;
  private String separator;

  public static SampleSheetFieldSourceDto from(SampleSheetFieldSource from) {
    SampleSheetFieldSourceDto to = new SampleSheetFieldSourceDto();
    setString(to::setSource, from.getSource());
    setString(to::setSourceProperty, from.getSourceProperty());
    setString(to::setDateFormat, from.getDateFormat());
    setString(to::setAggregation, maybeGetProperty(from.getAggregation(), Aggregation::name));
    // Value and separator should not be trimmed
    to.setValue(from.getValue());
    to.setSeparator(from.getSeparator());
    return to;
  }

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

  public String getDateFormat() {
    return dateFormat;
  }

  public void setDateFormat(String dateFormat) {
    this.dateFormat = dateFormat;
  }

  public String getAggregation() {
    return aggregation;
  }

  public void setAggregation(String aggregation) {
    this.aggregation = aggregation;
  }

  public String getSeparator() {
    return separator;
  }

  public void setSeparator(String separator) {
    this.separator = separator;
  }

  public SampleSheetFieldSource to() {
    SampleSheetFieldSource to = new SampleSheetFieldSource();
    setString(to::setSource, getSource());
    setString(to::setSourceProperty, getSourceProperty());
    setString(to::setDateFormat, getDateFormat());
    setObject(to::setAggregation, getAggregation(), Aggregation::valueOf);
    // Value and separator should not be trimmed
    to.setValue(getValue());
    to.setSeparator(getSeparator());
    return to;
  }

}

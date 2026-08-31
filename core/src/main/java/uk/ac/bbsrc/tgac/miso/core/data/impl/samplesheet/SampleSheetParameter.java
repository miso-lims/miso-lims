package uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import tools.jackson.databind.node.ArrayNode;

public class SampleSheetParameter {

  public enum ParameterType {
    TEXT, INT, DECIMAL, DATE, DROPDOWN
  }

  public enum MultivalueType {
    INSTRUMENT_POSITION
  }

  private String name;

  @Enumerated(EnumType.STRING)
  private ParameterType type;

  @JdbcTypeCode(SqlTypes.JSON_ARRAY)
  private ArrayNode source;

  @Enumerated(EnumType.STRING)
  private MultivalueType multivalue;

  private String defaultValue;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ParameterType getType() {
    return type;
  }

  public void setType(ParameterType type) {
    this.type = type;
  }

  public ArrayNode getSource() {
    return source;
  }

  public void setSource(ArrayNode source) {
    this.source = source;
  }

  public MultivalueType getMultivalue() {
    return multivalue;
  }

  public void setMultivalue(MultivalueType multiValue) {
    this.multivalue = multiValue;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

}

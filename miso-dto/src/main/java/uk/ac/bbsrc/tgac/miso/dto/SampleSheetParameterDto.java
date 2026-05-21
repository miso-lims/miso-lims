package uk.ac.bbsrc.tgac.miso.dto;

import static uk.ac.bbsrc.tgac.miso.dto.Dtos.*;

import com.fasterxml.jackson.databind.node.ArrayNode;

import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheetParameter;

public class SampleSheetParameterDto {

  private String name;
  private String type;
  private ArrayNode source;
  private String multivalue;
  private String defaultValue;

  public static SampleSheetParameterDto from(SampleSheetParameter from) {
    SampleSheetParameterDto to = new SampleSheetParameterDto();
    setString(to::setName, from.getName());
    setString(to::setType, maybeGetProperty(from.getType(), SampleSheetParameter.ParameterType::name));
    to.setSource(from.getSource());
    setString(to::setMultivalue, maybeGetProperty(from.getMultivalue(), SampleSheetParameter.MultivalueType::name));
    setString(to::setDefaultValue, from.getDefaultValue());
    return to;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public ArrayNode getSource() {
    return source;
  }

  public void setSource(ArrayNode source) {
    this.source = source;
  }

  public String getMultivalue() {
    return multivalue;
  }

  public void setMultivalue(String multivalue) {
    this.multivalue = multivalue;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

}

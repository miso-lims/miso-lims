package uk.ac.bbsrc.tgac.miso.dto;

import static uk.ac.bbsrc.tgac.miso.dto.Dtos.*;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheetSection;

public class SampleSheetSectionDto {

  private String name;
  private Boolean optional;
  private String format;
  private String multivalue;
  private Boolean invadeHeader;
  private Boolean omitFieldNames;
  private List<SampleSheetFieldDto> fields;

  public static SampleSheetSectionDto from(SampleSheetSection from) {
    SampleSheetSectionDto to = new SampleSheetSectionDto();
    setString(to::setName, from.getName());
    setBoolean(to::setOptional, from.getOptional(), true);
    setString(to::setFormat, maybeGetProperty(from.getFormat(), SampleSheetSection.Format::name));
    setString(to::setMultivalue, maybeGetProperty(from.getMultivalue(), SampleSheetSection.MultivalueType::name));
    setBoolean(to::setInvadeHeader, from.getInvadeHeader(), true);
    setBoolean(to::setOmitFieldNames, from.getOmitFieldNames(), true);
    setObject(to::setFields, from.getFields(), list -> list.stream().map(SampleSheetFieldDto::from).toList());
    return to;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Boolean getOptional() {
    return optional;
  }

  public void setOptional(Boolean optional) {
    this.optional = optional;
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public String getMultivalue() {
    return multivalue;
  }

  public void setMultivalue(String multivalue) {
    this.multivalue = multivalue;
  }

  public Boolean getInvadeHeader() {
    return invadeHeader;
  }

  public void setInvadeHeader(Boolean invadeHeader) {
    this.invadeHeader = invadeHeader;
  }

  public Boolean getOmitFieldNames() {
    return omitFieldNames;
  }

  public void setOmitFieldNames(Boolean omitFieldNames) {
    this.omitFieldNames = omitFieldNames;
  }

  public List<SampleSheetFieldDto> getFields() {
    return fields;
  }

  public void setFields(List<SampleSheetFieldDto> fields) {
    this.fields = fields;
  }

  public SampleSheetSection to() {
    SampleSheetSection to = new SampleSheetSection();
    setString(to::setName, getName());
    setBoolean(to::setOptional, getOptional(), true);
    setObject(to::setFormat, getFormat(), SampleSheetSection.Format::valueOf);
    setObject(to::setMultivalue, getMultivalue(), SampleSheetSection.MultivalueType::valueOf);
    setBoolean(to::setInvadeHeader, getInvadeHeader(), true);
    setBoolean(to::setOmitFieldNames, getOmitFieldNames(), true);
    setObject(to::setFields, getFields(), list -> list.stream().map(SampleSheetFieldDto::to).toList());
    return to;
  }

}

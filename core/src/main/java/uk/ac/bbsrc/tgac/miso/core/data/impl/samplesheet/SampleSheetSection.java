package uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet;

import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class SampleSheetSection {

  public enum Format {
    ROWS, COLUMNS
  }

  public enum MultivalueType {
    INSTRUMENT_POSITIONS, LIBRARY_ALIQUOTS, DISTINCT_LIBRARY_ALIQUOTS
  }

  private String name;
  private Boolean optional;

  @Enumerated(EnumType.STRING)
  private Format format;

  @Enumerated(EnumType.STRING)
  private MultivalueType multivalue;

  private Boolean invadeHeader;
  private Boolean omitFieldNames;

  @JdbcTypeCode(SqlTypes.JSON_ARRAY)
  private List<SampleSheetField> fields;

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

  public Format getFormat() {
    return format;
  }

  public void setFormat(Format format) {
    this.format = format;
  }

  public MultivalueType getMultivalue() {
    return multivalue;
  }

  public void setMultivalue(MultivalueType multivalue) {
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

  public List<SampleSheetField> getFields() {
    return fields;
  }

  public void setFields(List<SampleSheetField> fields) {
    this.fields = fields;
  }

}

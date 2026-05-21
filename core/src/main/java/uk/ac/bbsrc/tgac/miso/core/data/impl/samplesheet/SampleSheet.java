package uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet;

import java.io.Serializable;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

@Entity
public class SampleSheet implements Deletable, Serializable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long sampleSheetId = UNSAVED_ID;

  private String name;

  @Enumerated(EnumType.STRING)
  private PlatformType platformType;

  @JdbcTypeCode(SqlTypes.JSON)
  private List<SampleSheetParameter> parameters;

  @JdbcTypeCode(SqlTypes.JSON)
  private List<SampleSheetSection> sections;

  @Override
  public long getId() {
    return sampleSheetId;
  }

  @Override
  public void setId(long id) {
    this.sampleSheetId = id;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public PlatformType getPlatformType() {
    return platformType;
  }

  public void setPlatformType(PlatformType platformType) {
    this.platformType = platformType;
  }

  public List<SampleSheetParameter> getParameters() {
    return parameters;
  }

  public void setParameters(List<SampleSheetParameter> parameters) {
    this.parameters = parameters;
  }

  public List<SampleSheetSection> getSections() {
    return sections;
  }

  public void setSections(List<SampleSheetSection> sections) {
    this.sections = sections;
  }

  @Override
  public String getDeleteType() {
    return "Sample sheet";
  }

  @Override
  public String getDeleteDescription() {
    return getName();
  }

}

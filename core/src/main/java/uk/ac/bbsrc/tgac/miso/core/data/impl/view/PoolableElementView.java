package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;

@Entity
@Table(name = "PoolableElementView")
public class PoolableElementView {

  @Id
  private Long dilutionId;

  private String dilutionName;

  private Double dilutionConcentration;

  @Column(nullable = false)
  private boolean lowQualityLibrary = false;

  private String projectShortName;

  private String projectAlias;

  public Long getDilutionId() {
    return dilutionId;
  }

  public void setDilutionId(Long dilutionId) {
    this.dilutionId = dilutionId;
  }

  public String getDilutionName() {
    return dilutionName;
  }

  public void setDilutionName(String dilutionName) {
    this.dilutionName = dilutionName;
  }

  public Double getDilutionConcentration() {
    return dilutionConcentration;
  }

  public void setDilutionConcentration(Double dilutionConcentration) {
    this.dilutionConcentration = dilutionConcentration;
  }

  public boolean isLowQualityLibrary() {
    return lowQualityLibrary;
  }

  public void setLowQualityLibrary(boolean lowQualityLibrary) {
    this.lowQualityLibrary = lowQualityLibrary;
  }

  public String getProjectShortName() {
    return projectShortName;
  }

  public void setProjectShortName(String projectShortName) {
    this.projectShortName = projectShortName;
  }

  public String getProjectAlias() {
    return projectAlias;
  }

  public void setProjectAlias(String projectAlias) {
    this.projectAlias = projectAlias;
  }

  public static String getUnits() {
    return LibraryDilution.UNITS;
  }

}

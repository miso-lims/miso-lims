package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

@Entity
@Table(name = "PoolableElementView")
public class PoolableElementView implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private Long dilutionId;

  private String dilutionName;

  private Double dilutionConcentration;

  private String dilutionBarcode;

  @Temporal(TemporalType.DATE)
  private Date lastModified;

  @Temporal(TemporalType.DATE)
  private Date created;

  private Long libraryId;

  private String libraryName;

  private String libraryAlias;

  private String libraryDescription;

  @Column(nullable = false)
  private boolean lowQualityLibrary = false;

  private Long sampleId;

  private String sampleName;

  private String sampleAlias;

  private Long projectId;

  private String projectShortName;

  private String projectAlias;

  private String lastModifierName;

  private String creatorName;

  @Enumerated(EnumType.STRING)
  private PlatformType platformType;

  @OneToMany(targetEntity = Index.class)
  @JoinTable(name = "Library_Index", joinColumns = {
      @JoinColumn(name = "library_libraryId", nullable = false, referencedColumnName = "libraryId") }, inverseJoinColumns = {
          @JoinColumn(name = "index_indexId", nullable = false) })
  private final List<Index> indices = new ArrayList<>();

  public List<Index> getIndices() {
    return indices;
  }

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

  public Long getProjectId() {
    return projectId;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  public String getDilutionBarcode() {
    return dilutionBarcode;
  }

  public void setDilutionBarcode(String dilutionBarcode) {
    this.dilutionBarcode = dilutionBarcode;
  }

  public Long getLibraryId() {
    return libraryId;
  }

  public void setLibraryId(Long libraryId) {
    this.libraryId = libraryId;
  }

  public String getLibraryName() {
    return libraryName;
  }

  public void setLibraryName(String libraryName) {
    this.libraryName = libraryName;
  }

  public String getLibraryAlias() {
    return libraryAlias;
  }

  public void setLibraryAlias(String libraryAlias) {
    this.libraryAlias = libraryAlias;
  }

  public Long getSampleId() {
    return sampleId;
  }

  public void setSampleId(Long sampleId) {
    this.sampleId = sampleId;
  }

  public String getSampleName() {
    return sampleName;
  }

  public void setSampleName(String sampleName) {
    this.sampleName = sampleName;
  }

  public String getSampleAlias() {
    return sampleAlias;
  }

  public void setSampleAlias(String sampleAlias) {
    this.sampleAlias = sampleAlias;
  }

  public String getLibraryDescription() {
    return libraryDescription;
  }

  public void setLibraryDescription(String libraryDescription) {
    this.libraryDescription = libraryDescription;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  public PlatformType getPlatformType() {
    return platformType;
  }

  public void setPlatformType(PlatformType platformType) {
    this.platformType = platformType;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public String getLastModifierName() {
    return lastModifierName;
  }

  public void setLastModifierName(String lastModifierName) {
    this.lastModifierName = lastModifierName;
  }

  public String getCreatorName() {
    return creatorName;
  }

  public void setCreatorName(String creatorName) {
    this.creatorName = creatorName;
  }

}

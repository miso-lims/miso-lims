package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.IndexedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.type.ConsentLevel;

@Entity
@Immutable
@Table(name = "ListPoolView_Element")
public class ListPoolViewElement implements IndexedLibrary, Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private long aliquotId;

  private String name;
  private String alias;
  private long libraryId;
  private long projectId;
  private boolean lowQuality;
  private Long dnaSize;

  @ManyToOne
  @JoinColumn(name = "index1Id")
  private Index index1;

  @ManyToOne
  @JoinColumn(name = "index2Id")
  private Index index2;

  private String subprojectAlias;
  private Boolean subprojectPriority = false;

  @Enumerated(EnumType.STRING)
  private ConsentLevel consentLevel;

  public long getAliquotId() {
    return aliquotId;
  }

  public void setAliquotId(long aliquotId) {
    this.aliquotId = aliquotId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public long getLibraryId() {
    return libraryId;
  }

  public void setLibraryId(long libraryId) {
    this.libraryId = libraryId;
  }

  public long getProjectId() {
    return projectId;
  }

  public void setProjectId(long projectId) {
    this.projectId = projectId;
  }

  public boolean isLowQuality() {
    return lowQuality;
  }

  public void setLowQuality(boolean lowQuality) {
    this.lowQuality = lowQuality;
  }

  public Long getDnaSize() {
    return dnaSize;
  }

  public void setDnaSize(Long dnaSize) {
    this.dnaSize = dnaSize;
  }

  @Override
  public Index getIndex1() {
    return index1;
  }

  @Override
  public void setIndex1(Index index1) {
    this.index1 = index1;
  }

  @Override
  public Index getIndex2() {
    return index2;
  }

  @Override
  public void setIndex2(Index index2) {
    this.index2 = index2;
  }

  public String getSubprojectAlias() {
    return subprojectAlias;
  }

  public void setSubprojectAlias(String subprojectAlias) {
    this.subprojectAlias = subprojectAlias;
  }

  public Boolean isSubprojectPriority() {
    return subprojectPriority;
  }

  public void setSubprojectPriority(Boolean subprojectPriority) {
    this.subprojectPriority = subprojectPriority;
  }

  public ConsentLevel getConsentLevel() {
    return consentLevel;
  }

  public void setConsentLevel(ConsentLevel consentLevel) {
    this.consentLevel = consentLevel;
  }

}

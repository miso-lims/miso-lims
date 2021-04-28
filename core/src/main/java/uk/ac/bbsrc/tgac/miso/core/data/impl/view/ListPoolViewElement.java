package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.type.ConsentLevel;

@Entity
@Immutable
@Table(name = "ListPoolView_Element")
public class ListPoolViewElement implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private long aliquotId;

  private String name;
  private String alias;
  private long libraryId;
  private long projectId;
  private boolean lowQuality;
  private Long dnaSize;

  @ManyToMany(targetEntity = Index.class)
  @JoinTable(name = "Library_Index", joinColumns = {
      @JoinColumn(name = "library_libraryId", nullable = false, referencedColumnName = "libraryId") }, inverseJoinColumns = {
          @JoinColumn(name = "index_indexId", nullable = false) })
  private final List<Index> indices = new ArrayList<>();

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

  public List<Index> getIndices() {
    return indices;
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

package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

import uk.ac.bbsrc.tgac.miso.core.data.Aliasable;
import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
public class Assay implements Aliasable, Deletable, Serializable {

  private static final long serialVersionUID = 1L;
  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long assayId;

  private String alias;

  @Column(updatable = false)
  private String version;

  private String description;
  private boolean archived = false;

  private Integer caseTargetDays;
  private Integer receiptTargetDays;
  private Integer extractionTargetDays;
  private Integer libraryPreparationTargetDays;
  private Integer libraryQualificationTargetDays;
  private Integer fullDepthSequencingTargetDays;
  private Integer analysisReviewTargetDays;
  private Integer releaseApprovalTargetDays;
  private Integer releaseTargetDays;

  @OneToMany
  @JoinTable(name = "Assay_AssayTest", joinColumns = {@JoinColumn(name = "assayId")},
      inverseJoinColumns = {@JoinColumn(name = "testId")})
  private Set<AssayTest> assayTests;


  @OneToMany(mappedBy = "assay", cascade = CascadeType.ALL)
  private Set<AssayMetric> assayMetrics;

  @Override
  public long getId() {
    return assayId;
  }

  @Override
  public void setId(long id) {
    this.assayId = id;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "Assay";
  }

  @Override
  public String getDeleteDescription() {
    return getAlias();
  }

  @Override
  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isArchived() {
    return archived;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  public Integer getCaseTargetDays() {
    return caseTargetDays;
  }

  public void setCaseTargetDays(Integer caseTargetDays) {
    this.caseTargetDays = caseTargetDays;
  }

  public Integer getReceiptTargetDays() {
    return receiptTargetDays;
  }

  public void setReceiptTargetDays(Integer receiptTargetDays) {
    this.receiptTargetDays = receiptTargetDays;
  }

  public Integer getExtractionTargetDays() {
    return extractionTargetDays;
  }

  public void setExtractionTargetDays(Integer extractionTargetDays) {
    this.extractionTargetDays = extractionTargetDays;
  }

  public Integer getLibraryPreparationTargetDays() {
    return libraryPreparationTargetDays;
  }

  public void setLibraryPreparationTargetDays(Integer libraryPreparationTargetDays) {
    this.libraryPreparationTargetDays = libraryPreparationTargetDays;
  }

  public Integer getLibraryQualificationTargetDays() {
    return libraryQualificationTargetDays;
  }

  public void setLibraryQualificationTargetDays(Integer libraryQualificationTargetDays) {
    this.libraryQualificationTargetDays = libraryQualificationTargetDays;
  }

  public Integer getFullDepthSequencingTargetDays() {
    return fullDepthSequencingTargetDays;
  }

  public void setFullDepthSequencingTargetDays(Integer fullDepthSequencingTargetDays) {
    this.fullDepthSequencingTargetDays = fullDepthSequencingTargetDays;
  }

  public Integer getAnalysisReviewTargetDays() {
    return analysisReviewTargetDays;
  }

  public void setAnalysisReviewTargetDays(Integer analysisReviewTargetDays) {
    this.analysisReviewTargetDays = analysisReviewTargetDays;
  }

  public Integer getReleaseApprovalTargetDays() {
    return releaseApprovalTargetDays;
  }

  public void setReleaseApprovalTargetDays(Integer releaseApprovalTargetDays) {
    this.releaseApprovalTargetDays = releaseApprovalTargetDays;
  }

  public Integer getReleaseTargetDays() {
    return releaseTargetDays;
  }

  public void setReleaseTargetDays(Integer releaseTargetDays) {
    this.releaseTargetDays = releaseTargetDays;
  }

  public Set<AssayTest> getAssayTests() {
    if (assayTests == null) {
      assayTests = new HashSet<>();
    }
    return assayTests;
  }

  public Set<AssayMetric> getAssayMetrics() {
    if (assayMetrics == null) {
      assayMetrics = new HashSet<>();
    }
    return assayMetrics;
  }

  @Override
  public int hashCode() {
    return LimsUtils.hashCodeByIdFirst(this, alias, version, description, archived);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equalsByIdFirst(this, obj,
        Assay::getAlias,
        Assay::getVersion,
        Assay::getDescription,
        Assay::isArchived);
  }

}

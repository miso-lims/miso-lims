package uk.ac.bbsrc.tgac.miso.dto;

import static uk.ac.bbsrc.tgac.miso.dto.Dtos.*;

import java.util.List;
import java.util.stream.Collectors;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay;

public class AssayDto {

  private Long id;
  private String alias;
  private String version;
  private String description;
  private boolean archived;
  private List<AssayTestDto> tests;
  private List<AssayMetricDto> metrics;

  private Integer caseTargetDays;
  private Integer receiptTargetDays;
  private Integer extractionTargetDays;
  private Integer libraryPreparationTargetDays;
  private Integer libraryQualificationTargetDays;
  private Integer fullDepthSequencingTargetDays;
  private Integer analysisReviewTargetDays;
  private Integer releaseApprovalTargetDays;
  private Integer releaseTargetDays;

  public static AssayDto from(Assay from) {
    AssayDto to = new AssayDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setAlias, from.getAlias());
    setString(to::setVersion, from.getVersion());
    setString(to::setDescription, from.getDescription());
    setBoolean(to::setArchived, from.isArchived(), false);
    to.setTests(from.getAssayTests().stream().map(AssayTestDto::from).collect(Collectors.toList()));
    to.setMetrics(from.getAssayMetrics().stream().map(AssayMetricDto::from).collect(Collectors.toList()));
    setInteger(to::setCaseTargetDays, from.getCaseTargetDays(), true);
    setInteger(to::setReceiptTargetDays, from.getReceiptTargetDays(), true);
    setInteger(to::setExtractionTargetDays, from.getExtractionTargetDays(), true);
    setInteger(to::setLibraryPreparationTargetDays, from.getLibraryPreparationTargetDays(), true);
    setInteger(to::setLibraryQualificationTargetDays, from.getLibraryQualificationTargetDays(), true);
    setInteger(to::setFullDepthSequencingTargetDays, from.getFullDepthSequencingTargetDays(), true);
    setInteger(to::setAnalysisReviewTargetDays, from.getAnalysisReviewTargetDays(), true);
    setInteger(to::setReleaseApprovalTargetDays, from.getReleaseApprovalTargetDays(), true);
    setInteger(to::setReleaseTargetDays, from.getReleaseTargetDays(), true);
    return to;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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

  public List<AssayTestDto> getTests() {
    return tests;
  }

  public void setTests(List<AssayTestDto> tests) {
    this.tests = tests;
  }

  public List<AssayMetricDto> getMetrics() {
    return metrics;
  }

  public void setMetrics(List<AssayMetricDto> metrics) {
    this.metrics = metrics;
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

  public Assay to() {
    Assay to = new Assay();
    setLong(to::setId, getId(), false);
    setString(to::setAlias, getAlias());
    setString(to::setVersion, getVersion());
    setString(to::setDescription, getDescription());
    setBoolean(to::setArchived, isArchived(), false);
    if (getTests() != null) {
      getTests().stream().map(AssayTestDto::to).forEach(x -> to.getAssayTests().add(x));
    }
    if (getMetrics() != null) {
      getMetrics().stream().map(AssayMetricDto::to).forEach(x -> to.getAssayMetrics().add(x));
    }
    setInteger(to::setCaseTargetDays, getCaseTargetDays(), true);
    setInteger(to::setReceiptTargetDays, getReceiptTargetDays(), true);
    setInteger(to::setExtractionTargetDays, getExtractionTargetDays(), true);
    setInteger(to::setLibraryPreparationTargetDays, getLibraryPreparationTargetDays(), true);
    setInteger(to::setLibraryQualificationTargetDays, getLibraryQualificationTargetDays(), true);
    setInteger(to::setFullDepthSequencingTargetDays, getFullDepthSequencingTargetDays(), true);
    setInteger(to::setAnalysisReviewTargetDays, getAnalysisReviewTargetDays(), true);
    setInteger(to::setReleaseApprovalTargetDays, getReleaseApprovalTargetDays(), true);
    setInteger(to::setReleaseTargetDays, getReleaseTargetDays(), true);
    return to;
  }

}

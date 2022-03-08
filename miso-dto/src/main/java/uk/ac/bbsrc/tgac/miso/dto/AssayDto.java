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

  public static AssayDto from(Assay from) {
    AssayDto to = new AssayDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setAlias, from.getAlias());
    setString(to::setVersion, from.getVersion());
    setString(to::setDescription, from.getDescription());
    setBoolean(to::setArchived, from.isArchived(), false);
    to.setTests(from.getAssayTests().stream().map(AssayTestDto::from).collect(Collectors.toList()));
    to.setMetrics(from.getAssayMetrics().stream().map(AssayMetricDto::from).collect(Collectors.toList()));
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
    return to;
  }

}

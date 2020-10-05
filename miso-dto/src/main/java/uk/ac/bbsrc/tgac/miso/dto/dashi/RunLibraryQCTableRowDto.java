package uk.ac.bbsrc.tgac.miso.dto.dashi;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.dto.LibraryAliquotDto;

public class RunLibraryQCTableRowDto {

  private LibraryAliquotDto libraryAliquot;
  private List<RunLibraryQcTableRowMetricDto> metrics;
  private List<RunLibraryQcTableQcNodeDto> qcNodes;

  public LibraryAliquotDto getLibraryAliquot() {
    return libraryAliquot;
  }

  public void setLibraryAliquot(LibraryAliquotDto libraryAliquot) {
    this.libraryAliquot = libraryAliquot;
  }

  public List<RunLibraryQcTableRowMetricDto> getMetrics() {
    return metrics;
  }

  public void setMetrics(List<RunLibraryQcTableRowMetricDto> metrics) {
    this.metrics = metrics;
  }

  public List<RunLibraryQcTableQcNodeDto> getQcNodes() {
    return qcNodes;
  }

  public void setQcNodes(List<RunLibraryQcTableQcNodeDto> qcNodes) {
    this.qcNodes = qcNodes;
  }

}

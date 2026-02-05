package uk.ac.bbsrc.tgac.miso.dto.dashi;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.dto.LibraryAliquotDto;
import uk.ac.bbsrc.tgac.miso.dto.QcNodeDto;

public class RunItemQCTableRowDto {

  private LibraryAliquotDto libraryAliquot;
  private List<RunItemQcTableRowMetricDto> metrics;
  private List<QcNodeDto> qcNodes;

  public LibraryAliquotDto getLibraryAliquot() {
    return libraryAliquot;
  }

  public void setLibraryAliquot(LibraryAliquotDto libraryAliquot) {
    this.libraryAliquot = libraryAliquot;
  }

  public List<RunItemQcTableRowMetricDto> getMetrics() {
    return metrics;
  }

  public void setMetrics(List<RunItemQcTableRowMetricDto> metrics) {
    this.metrics = metrics;
  }

  public List<QcNodeDto> getQcNodes() {
    return qcNodes;
  }

  public void setQcNodes(List<QcNodeDto> qcNodes) {
    this.qcNodes = qcNodes;
  }

}

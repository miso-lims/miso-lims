package uk.ac.bbsrc.tgac.miso.dto.dashi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RunItemQcTableRequestDto {

  private String report;
  private List<RunItemQcTableRequestLibraryDto> libraryAliquots;

  public String getReport() {
    return report;
  }

  public void setReport(String report) {
    this.report = report;
  }

  @JsonProperty("library_aliquots")
  public List<RunItemQcTableRequestLibraryDto> getLibraryAliquots() {
    return libraryAliquots;
  }

  public void setLibraryAliquots(List<RunItemQcTableRequestLibraryDto> libraryAliquots) {
    this.libraryAliquots = libraryAliquots;
  }

}

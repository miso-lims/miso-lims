package uk.ac.bbsrc.tgac.miso.dto.dashi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RunLibraryQcTableRequestDto {

  private String report;
  private List<RunLibraryQcTableRequestLibraryDto> libraryAliquots;

  public String getReport() {
    return report;
  }

  public void setReport(String report) {
    this.report = report;
  }

  @JsonProperty("library_aliquots")
  public List<RunLibraryQcTableRequestLibraryDto> getLibraryAliquots() {
    return libraryAliquots;
  }

  public void setLibraryAliquots(List<RunLibraryQcTableRequestLibraryDto> libraryAliquots) {
    this.libraryAliquots = libraryAliquots;
  }

}

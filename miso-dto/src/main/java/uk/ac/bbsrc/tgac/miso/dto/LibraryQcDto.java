package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LibraryQcDto extends QcDto {

  private Double results;
  private Long libraryId;

  public Double getResults() {
    return results;
  }

  public void setResults(Double results) {
    this.results = results;
  }

  public Long getLibraryId() {
    return libraryId;
  }

  public void setLibraryId(Long libraryId) {
    this.libraryId = libraryId;
  }
}

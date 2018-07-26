package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

public class SampleSpreadSheetDto extends SpreadsheetDto {
  private List<String> allowedClasses;

  public List<String> getAllowedClasses() {
    return allowedClasses;
  }

  public void setAllowedClasses(List<String> allowedClasses) {
    this.allowedClasses = allowedClasses;
  }

}

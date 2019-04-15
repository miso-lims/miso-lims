package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

public class SpreadsheetRequest {
  private String format;
  private List<Long> ids;
  private String sheet;

  public String getFormat() {
    return format;
  }

  public List<Long> getIds() {
    return ids;
  }

  public String getSheet() {
    return sheet;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public void setIds(List<Long> ids) {
    this.ids = ids;
  }

  public void setSheet(String sheet) {
    this.sheet = sheet;
  }
}

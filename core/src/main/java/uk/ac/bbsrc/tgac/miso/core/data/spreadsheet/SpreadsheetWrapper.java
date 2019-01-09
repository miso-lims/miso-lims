package uk.ac.bbsrc.tgac.miso.core.data.spreadsheet;

public interface SpreadsheetWrapper {

  public int getRowCount();

  public int getColumnCount();

  public String getCellValue(int row, int col);

}

package uk.ac.bbsrc.tgac.miso.core.data;

public class BoxSize {
  private long id;
  private int rows;
  private int columns;
  private boolean scannable;

  public int getColumns() {
    return columns;
  }

  public long getId() {
    return id;
  }

  public int getRows() {
    return rows;
  }

  public void setColumns(int columns) {
    this.columns = columns;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setRows(int rows) {
    this.rows = rows;
  }
  
  /**
   * Creates a String of number of rows x number of columns. 
   * 
   * @return String getRowsByColumns
   */
  public String getRowsByColumns() {
    return Integer.toString(rows) + " x " + Integer.toString(columns);
  }
  
  /**
   * Returns whether the box is able to be scanned by the bulk scanner
   */
  public boolean getScannable() {
    return scannable;
  }
  
  /**
   * Sets whether the box is able to be scanned by the bulk scanner
   */
  public void setScannable(boolean scannable) {
    this.scannable = scannable;
  }
}

package uk.ac.bbsrc.tgac.miso.integration.dp5mirage;

public class DP5MirageScanPosition {
  /*
   "tubeBarcodes": [
    {
      "decodeStatus": "SUCCESS",
      "y": 0,
      "column": 0,
      "x": 0,
      "barcode": "string",
      "row": 0
    },
    ...
  ],
   */
  private String decodeStatus;
  private String barcode;
  private int y;
  private int x;
  private int row;
  private int column;

// Getters
  public String getDecodeStatus() {
    return decodeStatus;
  }

  public String getBarcode() {
    return barcode;
  }

  public int getY() {
    return y;
  }

  public int getX() {
    return x;
  }

  public int getRow() {
    return row;
  }

  public int getColumn() {
    return column;
  }

  public String toString() {
    return "row:" + row
        + ", column:" + column
        + ", barcode:" + barcode
        + ", decodeStatus:" + decodeStatus
        + ", x:" + x
        + ", y" + y;
  }

  // Setters
  public void setBarcode(String barcode) {
    this.barcode = barcode;
  }

  public void setY(int y) {
    this.y = y;
  }

  public void setX(int x) {
    this.x = x;
  }

  public void setRow(int row) {
    this.row = row;
  }

  public void setColumn(int column) {
    this.column = column;
  }
}
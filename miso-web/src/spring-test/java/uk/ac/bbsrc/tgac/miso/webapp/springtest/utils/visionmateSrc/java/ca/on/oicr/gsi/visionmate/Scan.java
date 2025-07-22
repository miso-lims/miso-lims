package uk.ac.bbsrc.tgac.miso.webapp.springtest.utils.visionmateSrc.ca.on.oicr.gsi.visionmate;

/**
 * Immutable object class to represent the data scanned from a rack
 */
public class Scan {

  private final String[][] barcodes;
  private final String noTube;
  private final String noRead;
  private final int failedReads;
  private final int emptyPositions;

  /**
   * Constructs a new Scan based on configuration options and a String of barcodes
   * 
   * @param rack the current product. Must accomodate the exact number of barcodes provided
   * @param data a String of barcodes, delimited by whatever is set as the delimiter in config
   * @param config server configuration used when creating the data String. This is used check the
   *        delimiter and labels for No Tube (empty position) and No Read (failure to scan a tube)
   */
  public Scan(RackType rack, String data, ServerConfig config) {
    if (rack == null)
      throw new NullPointerException("Rack type cannot be null");
    if (data == null)
      throw new NullPointerException("Data cannot be null");
    if (data.isEmpty())
      throw new IllegalArgumentException("Data cannot be empty");
    if (config == null)
      throw new NullPointerException("Server config cannot be null");

    int rows = rack.getRows();
    int columns = rack.getColumns();
    int expectedReads = rows * columns;

    String[] list = data.split(config.getDelimiter());
    if (list.length != expectedReads)
      throw new IllegalArgumentException("Expected " + expectedReads + " reads for this rack type, but " +
          "only " + list.length + " barcodes were found in the data");
    this.barcodes = new String[rows][columns];

    this.noTube = config.getNoTubeLabel();
    this.noRead = config.getNoReadLabel();
    int noTubes = 0;
    int noReads = 0;

    switch (config.getSortOrder()) {
      case COLUMNS:
        for (int col = 0, i = 0; col < columns; col++) {
          for (int row = 0; row < rows; row++, i++) {
            this.barcodes[row][col] = list[i];
            if (noRead.equals(list[i]))
              noReads++;
            if (noTube.equals(list[i]))
              noTubes++;
          }
        }
        break;
      case ROWS:
        for (int row = 0, i = 0; row < rows; row++) {
          for (int col = 0; row < columns; col++, i++) {
            this.barcodes[row][col] = list[i];
            if (noRead.equals(list[i]))
              noReads++;
            if (noTube.equals(list[i]))
              noTubes++;
          }
        }
        break;
    }

    this.failedReads = noReads;
    this.emptyPositions = noTubes;
  }

  /**
   * @return the label that represents an empty position
   */
  public String getNoTubeLabel() {
    return noTube;
  }

  /**
   * @return the label that represents a position where there is a tube, but the barcode could not be
   *         read
   */
  public String getNoReadLabel() {
    return noRead;
  }

  public int getRowCount() {
    return barcodes.length;
  }

  public int getColumnCount() {
    return barcodes[0].length;
  }

  public String[][] getBarcodes() {
    String[][] copy = new String[barcodes.length][];
    for (int i = 0; i < barcodes.length; i++) {
      copy[i] = barcodes[i].clone();
    }
    return copy;
  }

  /**
   * Gets a single barcode from the scan. Positions are 1-based, so the top-left position would be row
   * 1, column 1
   * 
   * @param row
   * @param column
   * @return the barcode at the specified position
   */
  public String getBarcode(int row, int column) {
    return barcodes[row - 1][column - 1];
  }

  /**
   * Gets a single barcode from the scan. Column number is 1-based, so the top-left position is row A,
   * column 1
   * 
   * @param row
   * @param column
   * @return the barcode at the specified position
   */
  public String getBarcode(char row, int column) {
    return barcodes[getNumberForChar(row) - 1][column - 1];
  }

  /**
   * @return the number of positions that contain a tube from which the barcode could not be read
   */
  public int getNoReadCount() {
    return failedReads;
  }

  /**
   * @return the number of empty positions
   */
  public int getNoTubeCount() {
    return emptyPositions;
  }

  /**
   * @return true if every position has a tube in it; false otherwise. Note: a return value of true
   *         does not mean that all barcodes were read successfully. See also
   *         {@link #getNoReadCount()}
   */
  public boolean isFull() {
    return emptyPositions == 0;
  }

  /**
   * @return true if every position in this scan is empty; false otherwise
   */
  public boolean isEmpty() {
    return emptyPositions == barcodes.length * barcodes[0].length;
  }

  /**
   * Converts an integer into its equivalent character for referencing a Scan row
   * 
   * @param num
   * @return the row reference character - an uppercase letter between A and X
   * @throws IllegalArgumentException if num is outside of the range 1-24
   */
  public static char getCharForNumber(int num) {
    if (num < 1 || num > 24)
      throw new IllegalArgumentException("Row number must be between 1 and 24");
    return (char) (num + 'A' - 1);
  }

  /**
   * Converts a row letter into the equivalent row number for referencing a Scan row. Note that row
   * numbers are 1-based, so A=1.
   * 
   * @param letter
   * @return the row number, between 1 and 24
   * @throws IllegalArgumentException if letter is outside of the ranges a-x and A-X
   */
  public static int getNumberForChar(char letter) {
    if (letter >= 'a' && letter <= 'x')
      letter = Character.toUpperCase(letter);
    if (letter < 'A' || letter > 'X')
      throw new IllegalArgumentException("Row letter must be between A and X");
    return letter - 'A' + 1;
  }

}

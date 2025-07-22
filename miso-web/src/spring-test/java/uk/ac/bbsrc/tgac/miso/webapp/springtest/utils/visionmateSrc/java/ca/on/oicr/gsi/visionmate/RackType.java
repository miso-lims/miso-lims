package uk.ac.bbsrc.tgac.miso.webapp.springtest.utils.visionmateSrc.ca.on.oicr.gsi.visionmate;

/**
 * Immutable object class to represent possible rack products to be scanned
 */
public class RackType {

  public static enum Manufacturer {
    ABGENE('A'), MATRIX('M'), NUNC('A'), OTHER('O');

    private final char code;

    private Manufacturer(char code) {
      this.code = code;
    }

    public char getCode() {
      return code;
    }

    public static Manufacturer getByCode(char code) {
      for (Manufacturer value : Manufacturer.values()) {
        if (value.getCode() == code) {
          return value;
        }
      }
      return null;
    }
  };

  private final Manufacturer manufacturer;
  private final int rows;
  private final int columns;

  /**
   * Constructs a new RackType from the String representation returned from the VisionMate server's
   * Get Current Product command. This String must be in the expected format, which is ARRCC, where A
   * is the manufacturer type (A for Abgene, M for Matrix, or N for Nunc), RR is the number of rows,
   * and CC is the number of columns. Number of rows and columns must be 2 digits each, so preceded by
   * a zero if necessary
   * 
   * @param productString
   * @throws NullPointerException if productString is null
   * @throws IllegalArgumentException if productString isn't in the correct format
   */
  public RackType(String productString) {
    if (productString == null)
      throw new NullPointerException("Product string must not be null");
    if (!productString.matches("^[AMNO]\\d{4}$"))
      throw new IllegalArgumentException("Invalid product string");

    this.manufacturer = Manufacturer.getByCode(productString.charAt(0));
    if (this.manufacturer == null) {
      throw new IllegalArgumentException("Invalid manufacturer initial. Must be A, M, or N");
    }

    this.rows = Integer.parseInt(productString.substring(1, 3));
    this.columns = Integer.parseInt(productString.substring(3, 5));
    if (rows < 1 || rows > 24 || columns < 0 || columns > 24)
      throw new IllegalArgumentException(rows + " rows or " + columns +
          " columns is invalid. Both must be between 1 and 24");
  }

  /**
   * Constructs a new RackType for a specific manufacturer and number of rows and columns. If the
   * manufacturer is unknown, Matrix is recommended as a default
   * 
   * @param manufacturer
   * @param rows number of rows in the rack (1-24)
   * @param columns number of columns in the rack (1-24)
   * @throws NullPointerException if manufacturer is null
   * @throws IllegalArgumentException if rows or columns is out of range 1-24
   */
  public RackType(Manufacturer manufacturer, int rows, int columns) {
    if (manufacturer == null)
      throw new NullPointerException("Manufacturer must not be null");
    if (rows < 1 || rows > 24 || columns < 0 || columns > 24)
      throw new IllegalArgumentException(rows + " rows or " + columns +
          " columns is invalid. Both must be between 1 and 24");
    this.manufacturer = manufacturer;
    this.rows = rows;
    this.columns = columns;
  }

  @Override
  public String toString() {
    return manufacturer.toString() + "-type, " + rows + " x " + columns;
  }

  public Manufacturer getManufacturer() {
    return manufacturer;
  }

  /**
   * @return the number of rows in this rack
   */
  public int getRows() {
    return rows;
  }

  /**
   * @return the number of columns in this rack
   */
  public int getColumns() {
    return columns;
  }

  /**
   * @return the string representation that should be sent to the VisionMate server when setting the
   *         current product, formatted ARRCC, where A is the manufacturer code, RR is the number of
   *         rows, and CC is the number of columns. Number of rows and columns must be 2 digits each,
   *         so preceded by a zero if necessary
   */
  public String getStringRepresentation() {
    StringBuilder sb = new StringBuilder(5);
    sb.append(manufacturer.getCode());
    if (rows < 10)
      sb.append("0");
    sb.append(rows);
    if (columns < 10)
      sb.append("0");
    sb.append(columns);
    return sb.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + columns;
    result = prime * result
        + ((manufacturer == null) ? 0 : manufacturer.hashCode());
    result = prime * result + rows;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    RackType other = (RackType) obj;
    if (columns != other.columns)
      return false;
    if (manufacturer != other.manufacturer)
      return false;
    if (rows != other.rows)
      return false;
    return true;
  }

}

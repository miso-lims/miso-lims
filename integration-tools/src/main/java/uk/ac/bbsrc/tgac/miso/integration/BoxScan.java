package uk.ac.bbsrc.tgac.miso.integration;

import java.util.List;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;

/**
 * Immutable object class to encapsulate barcode data for a scanned box of tubes
 */
public interface BoxScan {
  
  /**
   * Get the barcode from a position in the box. Implementors should use methods in {@link BoxUtils} to convert between position 
   * reference representations if necessary
   * 
   * @param position the box position, specified in a String containing row letter and two-digit column number (e.g. "A01" for top-left)
   * @return Null if there is no tube in the position, or an empty String if the barcode could not be read; otherwise, the barcode of 
   * the tube in the specified position
   */
  public String getBarcode(String position);
  
  /**
   * Get the barcode from a position in the box. Implementors should use methods in {@link BoxUtils} to convert between position 
   * reference representations if necessary. Note that column is zero-based.
   * 
   * @param row row letter of the position to examine, where 'A' is the first row
   * @param column column number of the position to examine, where 0 is the first column
   * @return Null if there is no tube in the position, or an empty String if the barcode could not be read; otherwise, the barcode of 
   * the tube in the specified position
   */
  public String getBarcode(char row, int column);
  
  /**
   * Get the barcode from a position in the box. Implementors should use methods in {@link BoxUtils} to convert between position 
   * reference representations if necessary. Note that row and column are zero-based.
   * 
   * @param row row number of the position to examine, where 0 is the first row
   * @param column column number of the position to examine, where 0 is the first column
   * @return Null if there is no tube in the position, or an empty String if the barcode could not be read; otherwise, the barcode of 
   * the tube in the specified position
   */
  public String getBarcode(int row, int column);

  /**
   * Gets all of the positions and associated barcodes, no tube indicators ("No Tube") and unreadable tube indicators ("No Read") in map 
   * form. Implementors should return defensive copies if necessary to enforce immutability
   * 
   * @return a Map containing all of the scanned barcodes. The keys are position names containing the row letter and two-digit column 
   * number (e.g. "A01" for top-left), and the values are the barcodes, "No Tube" or "No Read". Methods in {@link BoxUtils} may be used to 
   * convert to this position reference representations if necessary
   */
  public Map<String,String> getBarcodesMap();
  
  /**
   * @return true if every position in the box contains a tube; false otherwise. A return value of true does not indicate success in 
   * reading the barcode. The "barcode" for a failed read will be an empty String. See {@link #hasReadErrors()}
   */
  public boolean isFull();
  
  /**
   * @return true if there are no tubes in this box; false otherwise
   */
  public boolean isEmpty();
  
  /**
   * @return the maximum number of tubes that this box can accommodate
   */
  public int getMaximumTubeCount();
  
  /**
   * @return the number of tubes currently in the box. This count may include tubes with barcodes that were successfully read, as well 
   * as tubes with barcodes that the scanner failed to read. See {@link #hasReadErrors()}
   */
  public int getTubeCount();
  
  /**
   * @return true if the scanner failed to read the barcode on any tube(s) in the box; false otherwise. An empty position (no tube) does 
   * not count as a read error; there must actually be a tube in the position to have a read error
   */
  public boolean hasReadErrors();
  
  /**
   * @return a list of each position where there is a tube, but the scanner failed to read its barcode. Positions are represented by 
   * row letter and two-digit column number (e.g. "A01" for top-left)
   */
  public List<String> getReadErrorPositions();
  
  /**
   * @return the number of rows in the scanned box
   */
  public int getRowCount();
  
  /**
   * @return the number of columns in the scanned box
   */
  public int getColumnCount();
  
  /**
   * @return the String label for a single unreadable tube
   */
  public String getNoReadLabel();
  
  /**
   * @return the String label for the absence of a tube
   */
  public String getNoTubeLabel();
}

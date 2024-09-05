package uk.ac.bbsrc.tgac.miso.integration.dp5mirage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import uk.ac.bbsrc.tgac.miso.integration.BoxScan;

public class DP5MirageScan implements BoxScan {

  private final JsonNode scan;

  /**
   * Constructs a new DP5MirageScan to wrap scan data from a DP5MirageScanner
   *
   * @param scan scan data retrieved using a {@link DP5MirageScanner}
   */
  public DP5MirageScan(JsonNode scan) {
    this.scan = scan;
  }

  /**
   * Get the barcode from a position in the box. Implementors should use methods in {@link BoxUtils} to convert between position
   * reference representations if necessary
   *
   * @param position the box position, specified in a String containing row letter and two-digit column number (e.g. "A01" for top-left)
   * @return Null if there is no tube in the position, or an empty String if the barcode could not be read; otherwise, the barcode of
   * the tube in the specified position
   */
  @Override
  public String getBarcode(String position) {
    return getBarcode(BoxUtils.getRowNumber(position) +1,
        BoxUtils.getColumnNumber(position));
  }

  /**
   * Get the barcode from a position in the box. Implementors should use methods in {@link BoxUtils} to convert between position
   * reference representations if necessary. Note that column is zero-based.
   *
   * @param row row letter of the position to examine, where 'A' is the first row
   * @param column column number of the position to examine, where 0 is the first column
   * @return Null if there is no tube in the position, or an empty String if the barcode could not be read; otherwise, the barcode of
   * the tube in the specified position
   */
  @Override
  public String getBarcode(char row, int column) {
    // We need to subtract 1 from column because of zero-based index
    String[][] barcodes = getBarcodesArray();
    return barcodes[BoxUtils.fromRowChar(row)][column-1];
  }

  /**
   * Get the barcode from a position in the box. Implementors should use methods in {@link BoxUtils} to convert between position
   * reference representations if necessary. Note that row and column are zero-based.
   *
   * @param row row number of the position to examine, where 0 is the first row
   * @param column column number of the position to examine, where 0 is the first column
   * @return Null if there is no tube in the position, or an empty String if the barcode could not be read; otherwise, the barcode of
   * the tube in the specified position
   */
  @Override
  public String getBarcode(int row, int column) {
    String[][] barcodes = getBarcodesArray();
    return barcodes[row -1][column -1];
  }

  /**
   * Gets all the barcodes in array form. Implementors should return defensive copies if necessary to enforce immutability
   *
   * @return a 2D array containing all the scanned barcodes. The outer array contains the rows, and the inner array contains columns
   * (e.g. position "B10" = result[1,9])
   */
  @Override
  public String[][] getBarcodesArray() {
    // scan is immutable and already returns a defensive copy, so returning this does not break
    // immutability
    String[][] barcodes =
        new String[scan.get("tubeBarcode").get(scan.get("tubeBarcode").size()-1).get(
        "row").asInt()][scan.get("tubeBarcode").get(scan.get("tubeBarcode").size()-1).get("column").asInt()];

    // Populate barcodes array
    for(int i = 0; i < scan.get("tubeBarcode").size(); i++) {
      barcodes[scan.get("tubeBarcode").get(i).get("row").asInt()-1][scan.get("tubeBarcode").get(i).get("column").asInt()-1] = String.valueOf(
          scan.get("tubeBarcode").get(i).get("barcode"));
    }
    return barcodes;
  }

  /**
   * Gets all the positions and associated barcodes, no tube indicators ("No Tube") and unreadable tube indicators ("No Read") in map
   * form. Implementors should return defensive copies if necessary to enforce immutability
   *
   * @return a Map containing all the scanned barcodes. The keys are position names containing the row letter and two-digit column
   * number (e.g. "A01" for top-left), and the values are the barcodes, "No Tube" or "No Read". Methods in {@link BoxUtils} may be used to
   * convert to this position reference representations if necessary
   */
  @Override
  public Map<String, String> getBarcodesMap() {
    String[][] array = getBarcodesArray();
    Map<String, String> map = new HashMap<>();
    for (int row = 0; row < array.length; row++) {
      for (int col = 0; col < array[row].length; col++) {
        map.put(BoxUtils.getPositionString(row, col), array[row][col]);
      }
    }
    return map;
  }

  /**
   * @return true if every position in the box contains a tube; false otherwise. A return value of true does not indicate success in
   * reading the barcode. The "barcode" for a failed read will be an empty String. See {@link #hasReadErrors()}
   */
  @Override
  public boolean isFull() {
    for(DP5MirageScanPosition position: getFormattedScan()) {
      if(position.getDecodeStatus().equals("EMPTY")) {
        return false;
      }
    }
    return true;
  }

  /**
   * @return true if there are no tubes in this box; false otherwise
   */
  @Override
  public boolean isEmpty() {
    for(DP5MirageScanPosition position: getFormattedScan()) {
      if(position.getDecodeStatus().equals("SUCCESS")) {
        return false;
      }
    }
    return true;
  }

  /**
   * @return the maximum number of tubes that this box can accommodate
   */
  @Override
  public int getMaximumTubeCount() {
    return scan.get("tubeBarcode").size();
  }

  /**
   * @return the number of tubes currently in the box. This count may include tubes with barcodes that were successfully read, as well
   * as tubes with barcodes that the scanner failed to read. See {@link #hasReadErrors()}
   */
  @Override
  public int getTubeCount() {
    int tubeCounter = 0;
    for(DP5MirageScanPosition position: getFormattedScan()) {
      if(!position.getDecodeStatus().equals("EMPTY")) {
        tubeCounter++;
      }
    }
    return tubeCounter;
  }

  /**
   * @return true if the scanner failed to read the barcode on any tube(s) in the box; false otherwise. An empty position (no tube) does
   * not count as a read error; there must actually be a tube in the position to have a read error
   */
  @Override
  public boolean hasReadErrors() {
    for(DP5MirageScanPosition position: getFormattedScan()) {
      if(position.getDecodeStatus().equals("ERROR") && position.getBarcode().equals("null")) {
        return true;
      }
    }
    return false;
  }

  /**
   * @return a list of each position where there is a tube, but the scanner failed to read its barcode. Positions are represented by
   * row letter and two-digit column number (e.g. "A01" for top-left)
   */
  @Override
  public List<String> getReadErrorPositions() {
    final String[][] barcodes = getBarcodesArray();
    final String noRead = getNoReadLabel();
    List<String> positions = new ArrayList<>();

    for (int row = 0; row < barcodes.length; row++) {
      for (int col = 0; col < barcodes[row].length; col++) {
        if (noRead.equals(barcodes[row][col])) positions.add(BoxUtils.getPositionString(row, col));
      }
    }
    return positions;
  }

  /**
   * @return the number of rows in the scanned box
   */
  @Override
  public int getRowCount() {
    return scan.get("tubeBarcode").get(scan.get("tubeBarcode").size() -1).get("row").asInt();
  }

  /**
   * @return the number of columns in the scanned box
   */
  @Override
  public int getColumnCount() {
    return scan.get("tubeBarcode").get(scan.get("tubeBarcode").size() -1).get("column").asInt();
  }

  /**
   * @return the String label for a single unreadable tube
   */
  @Override
  public String getNoReadLabel() {
    return "\"null\"";
  }

  /**
   * @return the String label for the absence of a tube
   */
  @Override
  public String getNoTubeLabel() {
    return "EMPTY";
  }

  /**
   * @return array of DP5MirageScanPosition objects
   */
  public DP5MirageScanPosition[] getFormattedScan() {
    ObjectMapper objectMapper = new ObjectMapper();
    List<DP5MirageScanPosition> positions = objectMapper.convertValue(scan.get("tubeBarcode"),
        new TypeReference<>() {
        });

    return positions.toArray(new DP5MirageScanPosition[scan.get("tubeBarcode").size()]);
  }
}
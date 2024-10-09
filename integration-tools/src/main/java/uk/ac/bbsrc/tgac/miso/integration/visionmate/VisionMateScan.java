package uk.ac.bbsrc.tgac.miso.integration.visionmate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.on.oicr.gsi.visionmate.Scan;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import uk.ac.bbsrc.tgac.miso.integration.BoxScan;

/**
 * This class is an implementation of BoxScan that wraps (already immutable) scan data retrieved using a {@link VisionMateScanner}
 */
public class VisionMateScan implements BoxScan {
  
  private final Scan scan;
  
  /**
   * Constructs a new VisionMateScan to wrap scan data from a VisionMateScanner
   * 
   * @param scan scan data retrieved using a {@link VisionMateScanner}
   */
  public VisionMateScan(Scan scan) {
    this.scan = scan;
  }

  /**
   * {@link VisionMateScanner} expects a one-based row number, but BoxUtils.getRowNumber converts it to zero-based. This method
   * increments by one to account for this.
   * 
   * @param position String like "A02"
   */
  @Override
  public String getBarcode(String position) {
    return getBarcode(BoxUtils.getRowNumber(position) +1, BoxUtils.getColumnNumber(position));
  }

  @Override
  public String getBarcode(char row, int column) {
    return scan.getBarcode(row, column);
  }

  @Override
  public String getBarcode(int row, int column) {
    return scan.getBarcode(row, column);
  }

  @Override
  public Map<String, String> getBarcodesMap() {
    String[][] array = scan.getBarcodes();
    Map<String, String> map = new HashMap<>();
    for (int row = 0; row < array.length; row++) {
      for (int col = 0; col < array[row].length; col++) {
        map.put(BoxUtils.getPositionString(row, col), array[row][col]);
      }
    }
    return map;
  }

  @Override
  public boolean isFull() {
    return scan.isFull();
  }

  @Override
  public boolean isEmpty() {
    return scan.isEmpty();
  }

  @Override
  public int getMaximumTubeCount() {
    return scan.getRowCount() * scan.getColumnCount();
  }

  @Override
  public int getTubeCount() {
    return getMaximumTubeCount() - scan.getNoTubeCount();
  }

  @Override
  public boolean hasReadErrors() {
    return scan.getNoReadCount() != 0;
  }

  @Override
  public List<String> getReadErrorPositions() {
    final String[][] barcodes = scan.getBarcodes();
    final String noRead = scan.getNoReadLabel();
    List<String> positions = new ArrayList<>();
    
    for (int row = 0; row < barcodes.length; row++) {
      for (int col = 0; col < barcodes[row].length; col++) {
        if (noRead.equals(barcodes[row][col])) positions.add(BoxUtils.getPositionString(row, col));
      }
    }
    
    return positions;
  }

  @Override
  public int getRowCount() {
    return scan.getRowCount();
  }

  @Override
  public int getColumnCount() {
    return scan.getColumnCount();
  }
  
  @Override
  public String getNoReadLabel() {
    return scan.getNoReadLabel();
  }
  
  @Override
  public String getNoTubeLabel() {
    return scan.getNoTubeLabel();
  }

}

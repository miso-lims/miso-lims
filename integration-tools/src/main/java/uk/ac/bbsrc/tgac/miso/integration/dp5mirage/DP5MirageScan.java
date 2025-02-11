package uk.ac.bbsrc.tgac.miso.integration.dp5mirage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.SortedSet;
import java.util.TreeSet;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import uk.ac.bbsrc.tgac.miso.integration.BoxScan;
import uk.ac.bbsrc.tgac.miso.integration.dp5mirage.DP5MirageScanner.DP5MirageScanPosition;

public class DP5MirageScan implements BoxScan {
  private final Map<String, String> barcodesMap;

  /**
   * Constructs a new DP5MirageScan to wrap scan data from a DP5MirageScanner
   *
   * @param scanData scan data retrieved using a {@link DP5MirageScanner}
   */
  public DP5MirageScan(List<DP5MirageScanPosition> scanData) {
    this.barcodesMap = buildBarcodesMap(scanData);
  }

  @Override
  public String getBarcode(String position) {
    return barcodesMap.get(position);
  }

  @Override
  public String getBarcode(char row, int column) {
    // Conversion from char row is already a zero-based index
    // We need to subtract one from column to account for zero-based index
    return barcodesMap.get(BoxUtils.getPositionString(BoxUtils.fromRowChar(row), column -1));
  }

  @Override
  public String getBarcode(int row, int column) {
    // We need to subtract one from row and column to account for zero-based index
    return barcodesMap.get(BoxUtils.getPositionString(row -1, column -1));
  }

  @Override
  public Map<String, String> getBarcodesMap() {
    return barcodesMap;
  }

  @Override
  public boolean isFull() {
    for(Map.Entry<String, String> barcode : barcodesMap.entrySet())
    {
      if(barcode.getValue().equals(getNoTubeLabel()))
      {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean isEmpty() {
    for(Map.Entry<String, String> barcode : barcodesMap.entrySet())
    {
      if(!barcode.getValue().equals(getNoTubeLabel()))
      {
        return false;
      }
    }
    return true;
  }

  @Override
  public int getMaximumTubeCount() {
    return barcodesMap.size();
  }

  @Override
  public int getTubeCount() {
    int tubeCounter = 0;
    for(Map.Entry<String, String> barcode : barcodesMap.entrySet())
    {
      if(!barcode.getValue().equals(getNoTubeLabel()))
      {
        tubeCounter++;
      }
    }
    return tubeCounter;
  }

  @Override
  public boolean hasReadErrors() {
    for(Map.Entry<String, String> barcode : barcodesMap.entrySet())
    {
      if(barcode.getValue().equals(getNoReadLabel()))
      {
        return true;
      }
    }
    return false;
  }

  @Override
  public List<String> getReadErrorPositions() {
    final String noRead = getNoReadLabel();
    List<String> positions = new ArrayList<>();

    for (Map.Entry<String, String> entry : barcodesMap.entrySet()) {
      if (noRead.equals(entry.getValue())) positions.add(entry.getKey());
    }
    return positions;
  }

  @Override
  public int getRowCount() {
    SortedSet<String> keys = new TreeSet<>(barcodesMap.keySet());
    return BoxUtils.getRowNumber(keys.last()) + 1;
  }

  @Override
  public int getColumnCount() {
    SortedSet<String> keys = new TreeSet<>(barcodesMap.keySet());
    return BoxUtils.getColumnNumber(keys.last()) + 1;
  }

  @Override
  public String getNoReadLabel() { return "No Read"; }

  @Override
  public String getNoTubeLabel() { return "No Tube"; }

  /**
   * @return a map containing box positions and barcode values
   */
  private Map<String, String> buildBarcodesMap(List<DP5MirageScanPosition> scanData) {
    Map<String, String> barcodesMap = new HashMap<>();
    for(DP5MirageScanPosition position: scanData) {
      // Subtract one for zero-based index box position
      String key = BoxUtils.getPositionString(position.row() -1, position.column() -1);

      // Replace null barcode value with either "No Read" or "No Tube" else put barcode value in map
      if (position.decodeStatus().equals("EMPTY") && position.barcode() == null) {
        barcodesMap.put(key, getNoTubeLabel());
      }
      else if (position.decodeStatus().equals("ERROR") && position.barcode() == null) {
        barcodesMap.put(key, getNoReadLabel());
      }
      else {
        barcodesMap.put(key, position.barcode());
      }
    }
    return Collections.unmodifiableMap(barcodesMap);
  }
}
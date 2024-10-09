package uk.ac.bbsrc.tgac.miso.integration.dp5mirage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import uk.ac.bbsrc.tgac.miso.integration.BoxScan;
import uk.ac.bbsrc.tgac.miso.integration.dp5mirage.DP5MirageScanner.DP5MirageScanPosition;

public class DP5MirageScan implements BoxScan {
  private final List<DP5MirageScanPosition> scanData;
  private final Map<String, String> barcodesMap;

  /**
   * Constructs a new DP5MirageScan to wrap scan data from a DP5MirageScanner
   *
   * @param scanData scan data retrieved using a {@link DP5MirageScanner}
   */
  public DP5MirageScan(List<DP5MirageScanPosition> scanData) {
    this.scanData = scanData;
    this.barcodesMap = getBarcodesMap();
  }

  @Override
  public String getBarcode(String position) {
    return getBarcode(BoxUtils.getRowNumber(position) +1,
        BoxUtils.getColumnNumber(position));
  }

  @Override
  public String getBarcode(char row, int column) {
    // We need to subtract 1 from column because of zero-based index
    return barcodesMap.get(BoxUtils.getPositionString(BoxUtils.fromRowChar(row), column -1));
  }

  @Override
  public String getBarcode(int row, int column) {
    return barcodesMap.get(BoxUtils.getPositionString(row -1, column -1));
  }

  @Override
  public Map<String, String> getBarcodesMap() {
    Map<String, String> barcodesMap = new HashMap<>();
    for(DP5MirageScanPosition position: scanData) {
      // -1 for zero indexed box positions
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

  @Override
  public boolean isFull() {
    for(DP5MirageScanPosition position: scanData) {
      if(position.decodeStatus().equals("EMPTY")) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean isEmpty() {
    for(DP5MirageScanPosition position: scanData) {
      if(position.decodeStatus().equals("SUCCESS") || position.decodeStatus().equals("ERROR")) {
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
    for(DP5MirageScanPosition position: scanData) {
      if(!position.decodeStatus().equals("EMPTY")) {
        tubeCounter++;
      }
    }
    return tubeCounter;
  }

  @Override
  public boolean hasReadErrors() {
    for(DP5MirageScanPosition position: scanData) {
      if(position.decodeStatus().equals("ERROR")) {
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
    return  scanData.get(scanData.size() -1).row();
  }

  @Override
  public int getColumnCount() {
    return  scanData.get(scanData.size() -1).column();
  }

  @Override
  public String getNoReadLabel() { return "No Read"; }

  @Override
  public String getNoTubeLabel() { return "No Tube"; }
}
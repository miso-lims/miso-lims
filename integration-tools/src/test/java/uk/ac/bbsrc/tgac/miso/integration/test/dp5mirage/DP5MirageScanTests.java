package uk.ac.bbsrc.tgac.miso.integration.test.dp5mirage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import uk.ac.bbsrc.tgac.miso.integration.dp5mirage.DP5MirageScan;
import uk.ac.bbsrc.tgac.miso.integration.dp5mirage.DP5MirageScanner.DP5MirageScanPosition;
import uk.ac.bbsrc.tgac.miso.integration.test.BoxScanTests;

public class DP5MirageScanTests extends BoxScanTests<DP5MirageScan> {

  /**
   * Returns a new BoxScan, representing a scan of a full box of tubes. Implementations must return
   * a BoxScan implementation object containing 2 rows, 2 columns, and the following barcodes:
   * <OL>
   * <LI>A01: 11111</LI>
   * <LI>B01: 22222</LI>
   * <LI>A02: 33333</LI>
   * <LI>B02: 44444</LI>
   * </OL>
   */
  @Override
  protected DP5MirageScan getFullScan() {
    List<DP5MirageScanPosition> records = new ArrayList<>();
    records.add(new DP5MirageScanPosition("11111", "SUCCESS", 1, 1, 1, 1));
    records.add(new DP5MirageScanPosition("33333", "SUCCESS", 1, 2, 1, 2));
    records.add(new DP5MirageScanPosition("22222", "SUCCESS", 2, 1, 2, 1));
    records.add(new DP5MirageScanPosition("44444", "SUCCESS", 2, 2, 2, 2));

    return new DP5MirageScan(records);
  }

  /**
   * Returns a new BoxScan, representing a scan of an empty box of tubes. Implementations must
   * return a BoxScan implementation object containing 2 rows, 2 columns, and no tubes.
   */
  @Override
  protected DP5MirageScan getEmptyScan() {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode emptyWrappedScan = null;
    List<DP5MirageScanPosition> records = new ArrayList<>();
    records.add(new DP5MirageScanPosition("null", "EMPTY", 1, 1, 1, 1));
    records.add(new DP5MirageScanPosition("null", "EMPTY", 1, 2, 1, 2));
    records.add(new DP5MirageScanPosition("null", "EMPTY", 2, 1, 2, 1));
    records.add(new DP5MirageScanPosition("null", "EMPTY", 2, 2, 2, 2));

    return new DP5MirageScan(records);
  }

  /**
   * Returns a new BoxScan, representing a scan of a full box of tubes with failed reads.
   * Implementations must return a BoxScan implementation object containing 2 rows, 2 columns, and
   * the following barcodes:
   * <OL>
   * <LI>A01: 11111</LI>
   * <LI>B01: (failed read)</LI>
   * <LI>A02: (failed read)</LI>
   * <LI>B02: 44444</LI>
   * </OL>
   */
  @Override
  protected DP5MirageScan getErredScan() {
    List<DP5MirageScanPosition> records = new ArrayList<>();
    records.add(new DP5MirageScanPosition("11111", "SUCCESS", 1, 1, 1, 1));
    records.add(new DP5MirageScanPosition("No Read", "ERROR", 1, 2, 1, 2));
    records.add(new DP5MirageScanPosition("No Read", "ERROR", 2, 1, 2, 1));
    records.add(new DP5MirageScanPosition("44444", "SUCCESS", 2, 2, 2, 2));

    return new DP5MirageScan(records);
  }
}

package uk.ac.bbsrc.tgac.miso.integration.test.dp5mirage;

import java.util.ArrayList;
import java.util.List;
import uk.ac.bbsrc.tgac.miso.integration.dp5mirage.DP5MirageScan;
import uk.ac.bbsrc.tgac.miso.integration.dp5mirage.DP5MirageScanner.DP5MirageScanPosition;
import uk.ac.bbsrc.tgac.miso.integration.test.BoxScanTests;

public class DP5MirageScanTests extends BoxScanTests<DP5MirageScan> {

  @Override
  protected DP5MirageScan getFullScan() {
    List<DP5MirageScanPosition> records = new ArrayList<>();
    records.add(new DP5MirageScanPosition("11111", "SUCCESS", 1, 1, 1, 1));
    records.add(new DP5MirageScanPosition("33333", "SUCCESS", 1, 2, 1, 2));
    records.add(new DP5MirageScanPosition("22222", "SUCCESS", 2, 1, 2, 1));
    records.add(new DP5MirageScanPosition("44444", "SUCCESS", 2, 2, 2, 2));

    return new DP5MirageScan(records);
  }

  @Override
  protected DP5MirageScan getEmptyScan() {
    List<DP5MirageScanPosition> records = new ArrayList<>();
    records.add(new DP5MirageScanPosition("No Tube", "EMPTY", 1, 1, 1, 1));
    records.add(new DP5MirageScanPosition("No Tube", "EMPTY", 1, 2, 1, 2));
    records.add(new DP5MirageScanPosition("No Tube", "EMPTY", 2, 1, 2, 1));
    records.add(new DP5MirageScanPosition("No Tube", "EMPTY", 2, 2, 2, 2));

    return new DP5MirageScan(records);
  }

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
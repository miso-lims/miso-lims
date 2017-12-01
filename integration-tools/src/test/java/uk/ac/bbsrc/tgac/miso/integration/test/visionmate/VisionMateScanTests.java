package uk.ac.bbsrc.tgac.miso.integration.test.visionmate;

import uk.ac.bbsrc.tgac.miso.integration.test.BoxScanTests;
import uk.ac.bbsrc.tgac.miso.integration.visionmate.VisionMateScan;
import ca.on.oicr.gsi.visionmate.RackType;
import ca.on.oicr.gsi.visionmate.RackType.Manufacturer;
import ca.on.oicr.gsi.visionmate.Scan;
import ca.on.oicr.gsi.visionmate.ServerConfig;

public class VisionMateScanTests extends BoxScanTests<VisionMateScan> {
  
  @Override
  protected VisionMateScan getFullScan() {
    RackType rack = new RackType(Manufacturer.MATRIX, 2, 2);
    ServerConfig config = new ServerConfig();
    String data = "11111,22222,33333,44444,";
    Scan wrappedScan = new Scan(rack, data, config);
    return new VisionMateScan(wrappedScan);
  }
  
  @Override
  protected VisionMateScan getEmptyScan() {
    RackType rack = new RackType(Manufacturer.MATRIX, 2, 2);
    ServerConfig config = new ServerConfig();
    String noTube = config.getNoTubeLabel();
    StringBuilder sb = new StringBuilder();
    sb.append(noTube).append(",")
        .append(noTube).append(",")
        .append(noTube).append(",")
        .append(noTube).append(",");
    String data = sb.toString();
    Scan wrappedScan = new Scan(rack, data, config);
    return new VisionMateScan(wrappedScan);
  }
  
  @Override
  protected VisionMateScan getErredScan() {
    RackType rack = new RackType(Manufacturer.MATRIX, 2, 2);
    ServerConfig config = new ServerConfig();
    String noRead = config.getNoReadLabel();
    StringBuilder sb = new StringBuilder();
    sb.append("11111").append(",")
        .append(noRead).append(",")
        .append(noRead).append(",")
        .append("44444");
    String data = sb.toString();
    Scan wrappedScan = new Scan(rack, data, config);
    return new VisionMateScan(wrappedScan);
  }

}

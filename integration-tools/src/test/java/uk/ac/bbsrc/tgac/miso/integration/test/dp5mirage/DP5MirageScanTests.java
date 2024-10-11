package uk.ac.bbsrc.tgac.miso.integration.test.dp5mirage;

import com.fasterxml.jackson.core.JsonProcessingException;
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
    ObjectMapper mapper = new ObjectMapper();
    JsonNode fullWrappedScan = null;
    List<DP5MirageScanPosition> records = new ArrayList<>();
    records.add(new DP5MirageScanPosition("11111", "SUCCESS", 1, 1, 1, 1));
    records.add(new DP5MirageScanPosition("33333", "SUCCESS", 1, 2, 1, 2));
    records.add(new DP5MirageScanPosition("22222", "SUCCESS", 2, 1, 2, 1));
    records.add(new DP5MirageScanPosition("44444", "SUCCESS", 2, 2, 2, 2));

    //TODO not used
    String tubeBarcode = "[{\"row\":1,\"column\":1,\"barcode\":11111, "
        + "\"decodeStatus\":\"SUCCESS"
        + "\",\"x\":1,\"y\":1}, "
        + "{\"row\":1,\"column\":2,\"barcode\":33333, "
        + "\"decodeStatus\":\"SUCCESS\",\"x\":1,\"y\":2}, "
        + "{\"row\":2,\"column\":1,"
        + "\"barcode\":22222, \"decodeStatus\":\"SUCCESS\",\"x\":2,\"y\":1}, {\"row\":2,"
        + "\"column\":2,\"barcode\":44444, \"decodeStatus\":\"SUCCESS\",\"x\":2,\"y\":2}]";

    //TODO not used
    try {
      fullWrappedScan = mapper.readTree(
          "{\"scanId\":\"185eea49-0a7a-4c53-9e46-19929234d792\", \"scanTime\":1725379428062, "
              + "\"containerBarcode\":\"#container-barcode\", "
              + "\"scanTimeAnswers\":\"null\", \"containerName\":\"96 SBS rack\", "
              + "\"containerUid\":\"mirage96sbs\", \"demoImage\":\"null\", "
              + "\"containerGuid\":\"1e20491b-b8ba-4c35-991e-2012542f6a5e\", \"rawImage\":\"null\", \"annotatedImage\":\"null\", "
              + "\"linearReaderImage\":\"null\", \"tubeBarcode\":" + tubeBarcode + ", \"orientationBarcode\":[]}"
      );
    } catch (JsonProcessingException e) {
    }

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

    //TODO not used
    String tubeBarcode = "[{\"row\":1,\"column\":1,\"barcode\":\"null\", "
        + "\"decodeStatus\":\"EMPTY"
        + "\",\"x\":1,\"y\":1}, {\"row\":1,\"column\":2,\"barcode\":\"null\", "
        + "\"decodeStatus\":\"EMPTY\",\"x\":1,\"y\":2}, {\"row\":2,\"column\":1,"
        + "\"barcode\":\"null\", \"decodeStatus\":\"EMPTY\",\"x\":2,\"y\":1}, {\"row\":2,"
        + "\"column\":2,\"barcode\":\"null\", \"decodeStatus\":\"EMPTY\",\"x\":2,\"y\":2}]";

    //TODO not used
    try {
      emptyWrappedScan = mapper.readTree(
          "{\"scanId\":\"185eea49-0a7a-4c53-9e46-19929234d792\", \"scanTime\":1725379428062, "
              + "\"containerBarcode\":\"#container-barcode\", "
              + "\"scanTimeAnswers\":\"null\", \"containerName\":\"96 SBS rack\", "
              + "\"containerUid\":\"mirage96sbs\", \"demoImage\":\"null\", "
              + "\"containerGuid\":\"1e20491b-b8ba-4c35-991e-2012542f6a5e\", \"rawImage\":\"null\", \"annotatedImage\":\"null\", "
              + "\"linearReaderImage\":\"null\", \"tubeBarcode\":" + tubeBarcode + ", \"orientationBarcode\":[]}"
      );
    } catch (JsonProcessingException e) {
    }

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
    ObjectMapper mapper = new ObjectMapper();
    JsonNode errorWrappedScan = null;
    List<DP5MirageScanPosition> records = new ArrayList<>();
    records.add(new DP5MirageScanPosition("11111", "ERROR", 1, 1, 1, 1));
    records.add(new DP5MirageScanPosition("No Read", "ERROR", 1, 2, 1, 2));
    records.add(new DP5MirageScanPosition("No Read", "ERROR", 2, 1, 2, 1));
    records.add(new DP5MirageScanPosition("44444", "ERROR", 2, 2, 2, 2));

    String tubeBarcode = "[{\"row\":1,\"column\":1,\"barcode\":\"11111\", "
        + "\"decodeStatus\":\"SUCCESS"
        + "\",\"x\":1,\"y\":1}, {\"row\":1,\"column\":2,\"barcode\":\"No Read\", "
        + "\"decodeStatus\":\"ERROR\",\"x\":1,\"y\":2}, {\"row\":2,\"column\":1,"
        + "\"barcode\":\"No Read\", \"decodeStatus\":\"ERROR\",\"x\":2,\"y\":1}, {\"row\":2,"
        + "\"column\":2,\"barcode\":\"44444\", \"decodeStatus\":\"SUCCESS\",\"x\":2,\"y\":2}]";

    try {
      errorWrappedScan = mapper.readTree(
          "{\"scanId\":\"185eea49-0a7a-4c53-9e46-19929234d792\", \"scanTime\":1725379428062, "
              + "\"containerBarcode\":\"#container-barcode\", "
              + "\"scanTimeAnswers\":\"null\", \"containerName\":\"96 SBS rack\", "
              + "\"containerUid\":\"mirage96sbs\", \"demoImage\":\"null\", "
              + "\"containerGuid\":\"1e20491b-b8ba-4c35-991e-2012542f6a5e\", \"rawImage\":\"null\", \"annotatedImage\":\"null\", "
              + "\"linearReaderImage\":\"null\", \"tubeBarcode\":" + tubeBarcode + ", \"orientationBarcode\":[]}"
      );
    } catch (JsonProcessingException e) {
    }

    return new DP5MirageScan(records);
  }
}

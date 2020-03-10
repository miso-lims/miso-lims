package uk.ac.bbsrc.tgac.miso.core.service.printing;

import java.nio.charset.StandardCharsets;

/**
 * Zebra label language
 *
 * https://www.zebra.com/content/dam/zebra/manuals/printers/common/programming/zpl-zbi2-pm-en.pdf
 */
public class ZebraLabelGenerator extends LabelCanvas {
  private final int dotsPerMM;
  private final StringBuilder sb = new StringBuilder();

  public ZebraLabelGenerator(int dotsPerMM, double width, double height) {
    this.dotsPerMM = dotsPerMM;
    // Reset delimiters
    sb.append("CT~~CD,~CC^~CT~\r\n");
    // Reset label positions, encodings, and such
    sb.append("^XA~TA000~JSN^LT0^MNW^MTT^PON^PMN^LH0,0^JMA^PR2,2~SD30^JUS^LRN^CI28^XZ\r\n");
    // Start format
    sb.append("^XA\r\n");
    // Tear-off mode
    sb.append("^MMT\r\n");
    // Print Width
    sb.append("^PW").append(dots(width)).append("\r\n");
    // Label Length
    sb.append("^LL").append(dots(height)).append("\r\n");
    // Label offset
    sb.append("^LS0\r\n");
  }

  @Override
  public void barcode1d(double x, double y, double height, double moduleWidth, String contents) {
    if (contents == null) return;
    // Set origin for barcode
    sb.append("^FT").append(dots(x)).append(",").append(dots(y)).append(",0\r\n");
    // Code 128 barcode in normal orientation
    sb.append("^BCN,").append(dots(height)).append("\r\n");
    writeData(contents);
  }

  @Override
  public void barcode2d(double x, double y, double moduleSize, String contents) {
    if (contents == null) return;
    // Set origin for barcode
    sb.append("^FT").append(dots(x)).append(",").append(dots(y)).append(",0\r\n");
    // Datamatrix barcode in normal orientation, maximum quality
    sb.append("^BXN,").append(dots(moduleSize)).append(",200\r\n");
    writeData(contents);
  }

  private int dots(double mm) {
    return Math.max((int) (mm * dotsPerMM), 1);
  }

  @Override
  public String finish(int copies) {
    sb.append("^PQ").append(copies).append("\r\n");
    sb.append("^XZ\r\n");
    return sb.toString();
  }

  @Override
  public void text(double x, double y, double height, TextDirection direction, FontStyle style, Justification justification, String text) {
    if (text == null) {
      return;
    }
    sb.append("^FT").append(dots(x)).append(",").append(dots(y)).append(",")
        .append(justification == Justification.RIGHT ? "1" : "0");
    // Font 0 (scalable font)
    sb.append("^A0");
    switch (direction) {
    case UPSIDEDOWN:
      sb.append("I");
      break;
    case VERTICAL_DOWN:
      sb.append("R");
      break;
    case VERTICAL_UP:
      sb.append("B");
      break;
    default:
      sb.append("N");
      break;
    }
    sb.append(",").append(dots(height));
    writeData(text);
  }

  private void writeData(String contents) {
    sb.append("^FH_^FD");
    for (final byte by : contents.getBytes(StandardCharsets.UTF_8)) {
      sb.append("_").append(String.format("%02X", by));
    }
    sb.append("^FS\r\n");
  }

}

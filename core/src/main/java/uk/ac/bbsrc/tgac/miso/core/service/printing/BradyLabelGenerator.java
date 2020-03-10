package uk.ac.bbsrc.tgac.miso.core.service.printing;

/**
 * Brady printers
 * 
 * Brady printers are programmed in Yabasic:
 * https://www.cab.de/en/support/support-downloads/?suchtyp=bereich&bereich=45&produktgruppe=48&produkt=166
 */
public class BradyLabelGenerator extends LabelCanvas {
  private final StringBuilder sb = new StringBuilder();

  public BradyLabelGenerator(double width, double height, double gap) {
    sb.append("m m\n"); // Set to mm units
    sb.append("J\n"); // Start job
    sb.append("S 5,0,").append(height).append(",").append(height + gap).append(",").append(width).append("\n");
  }

  @Override
  public String finish(int copies) {
    sb.append("A ").append(copies).append("\n");
    return sb.toString();
  }

  @Override
  public void barcode2d(double x, double y, double moduleSize, String contents) {
    if (contents != null)
      sb.append("B ").append(x).append(",").append(y).append(",0,DATAMATRIX,").append(moduleSize).append(";")
          .append(contents).append("\n");
  }

  @Override
  public void barcode1d(double x, double y, double height, double moduleWidth, String contents) {
    if (contents != null)
      sb.append("B ").append(x).append(",").append(y).append(",0,CODE128,").append(height).append(";").append(contents).append("\n");
  }

  @Override
  public void text(double x, double y, double height, TextDirection direction, FontStyle style, Justification justification, String text) {
    if (text == null) {
      return;
    }
    int rotation;
    switch (direction) {
    case UPSIDEDOWN:
      rotation = 180;
      break;
    case VERTICAL_DOWN:
      rotation = 270;
      break;
    case VERTICAL_UP:
      rotation = 90;
      break;
    default:
      rotation = 0;
      break;

    }
    sb.append("T ").append(x).append(",").append(y).append(",").append(rotation).append(",").append(style == FontStyle.BOLD ? "5" : "3")
        .append(",").append(height).append(";");
    text.codePoints().forEachOrdered(codePoint -> {
      if (codePoint > 31 && codePoint < 128) {
        sb.appendCodePoint(codePoint);
      } else {
        sb.append("[U:$").append(String.format("%04X", codePoint)).append("]");
      }
    });
    sb.append("\n");
  }

}

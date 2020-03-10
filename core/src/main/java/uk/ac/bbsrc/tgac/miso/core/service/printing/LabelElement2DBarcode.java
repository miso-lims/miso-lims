package uk.ac.bbsrc.tgac.miso.core.service.printing;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;

public class LabelElement2DBarcode extends LabelElement {

  private PrintableText contents = PrintableText.NULL;
  private double moduleSize;

  private double x;

  private double y;

  @Override
  public void draw(LabelCanvas canvas, Barcodable barcodable) {
    canvas.barcode2d(x, y, moduleSize, contents.text(barcodable));

  }

  public PrintableText getContents() {
    return contents;
  }

  public double getModuleSize() {
    return moduleSize;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public void setContents(PrintableText contents) {
    this.contents = contents;
  }

  public void setModuleSize(double moduleSize) {
    this.moduleSize = moduleSize;
  }

  public void setX(double x) {
    this.x = x;
  }

  public void setY(double y) {
    this.y = y;
  }

}

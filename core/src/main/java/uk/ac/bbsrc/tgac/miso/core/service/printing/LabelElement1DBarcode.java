package uk.ac.bbsrc.tgac.miso.core.service.printing;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;

public class LabelElement1DBarcode extends LabelElement {

  private PrintableText contents = PrintableText.NULL;
  private double height;
  private double moduleWidth;
  private double x;
  private double y;

  @Override
  public void draw(LabelCanvas canvas, Barcodable barcodable) {
    canvas.barcode1d(x, y, height, moduleWidth, contents.text(barcodable));

  }

  public PrintableText getContents() {
    return contents;
  }

  public double getHeight() {
    return height;
  }

  public double getModuleWidth() {
    return moduleWidth;
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

  public void setHeight(double height) {
    this.height = height;
  }

  public void setModuleWidth(double moduleWidth) {
    this.moduleWidth = moduleWidth;
  }

  public void setX(double x) {
    this.x = x;
  }

  public void setY(double y) {
    this.y = y;
  }

}

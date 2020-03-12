package uk.ac.bbsrc.tgac.miso.core.service.printing;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.service.printing.LabelCanvas.TextDirection;

public class LabelElementTextBlock extends LabelElement {

  private PrintableText contents = PrintableText.NULL;

  private TextDirection direction = TextDirection.NORMAL;

  private double height;


  private int lineLimit;

  private int rowLimit;

  private double x;

  private double y;

  @Override
  public void draw(LabelCanvas canvas, Barcodable barcodable) {
    canvas.multilineText(x, y, height, lineLimit, rowLimit, direction, contents.lines(barcodable));
  }

  public PrintableText getContents() {
    return contents;
  }

  public TextDirection getDirection() {
    return direction;
  }

  public double getHeight() {
    return height;
  }


  public int getLineLimit() {
    return lineLimit;
  }

  public int getRowLimit() {
    return rowLimit;
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

  public void setDirection(TextDirection direction) {
    this.direction = direction;
  }

  public void setHeight(double height) {
    this.height = height;
  }

  public void setLineLimit(int lineLimit) {
    this.lineLimit = lineLimit;
  }

  public void setRowLimit(int rowLimit) {
    this.rowLimit = rowLimit;
  }

  public void setX(double x) {
    this.x = x;
  }

  public void setY(double y) {
    this.y = y;
  }

}

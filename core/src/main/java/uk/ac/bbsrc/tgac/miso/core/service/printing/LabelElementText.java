package uk.ac.bbsrc.tgac.miso.core.service.printing;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.service.printing.LabelCanvas.FontStyle;
import uk.ac.bbsrc.tgac.miso.core.service.printing.LabelCanvas.Justification;
import uk.ac.bbsrc.tgac.miso.core.service.printing.LabelCanvas.TextDirection;

public class LabelElementText extends LabelElement {

  private PrintableText contents = PrintableText.NULL;

  private TextDirection direction = TextDirection.NORMAL;

  private double height;

  private Justification justification = Justification.LEFT;

  private int lineLimit;

  private FontStyle style = FontStyle.REGULAR;

  private double x;

  private double y;

  @Override
  public void draw(LabelCanvas canvas, Barcodable barcodable) {
    if (lineLimit < 1) {
      canvas.text(x, y, height, direction, style, justification, contents.text(barcodable));
    } else {
      canvas.textClipped(x, y, height, lineLimit, direction, style, justification, contents.text(barcodable));
    }
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

  public Justification getJustification() {
    return justification;
  }

  public int getLineLimit() {
    return lineLimit;
  }

  public FontStyle getStyle() {
    return style;
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

  public void setJustification(Justification justification) {
    this.justification = justification;
  }

  public void setLineLimit(int limit) {
    this.lineLimit = limit;
  }

  public void setStyle(FontStyle style) {
    this.style = style;
  }

  public void setX(double x) {
    this.x = x;
  }

  public void setY(double y) {
    this.y = y;
  }

}

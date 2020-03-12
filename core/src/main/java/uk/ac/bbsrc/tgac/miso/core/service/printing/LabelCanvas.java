package uk.ac.bbsrc.tgac.miso.core.service.printing;

import java.util.stream.Stream;

import uk.ac.bbsrc.tgac.miso.core.data.Pair;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * A canvas for drawing a printer label
 */
public abstract class LabelCanvas {
  public enum FontStyle {
    BOLD, REGULAR
  }

  public enum Justification {
    LEFT, RIGHT
  }

  public enum TextDirection {
    NORMAL(0, 1), UPSIDEDOWN(0, -1), VERTICAL_DOWN(-1, 0), VERTICAL_UP(1, 0);
    private final int xMultiplier;
    private final int yMultiplier;

    private TextDirection(int xMultiplier, int yMultiplier) {
      this.xMultiplier = xMultiplier;
      this.yMultiplier = yMultiplier;
    }

  }

  /**
   * Draw a CODE 128 barcode
   *
   * @param x the x-position of the top-left corner of the barcode in mm
   * @param y the y-position of the top-left corner of the barcode in mm
   * @param height the height of the barcode in mm
   * @param moduleWidth the width of the narrowest bar in the barcode in mm
   * @param contents the data in the barcode
   */
  public abstract void barcode1d(double x, double y, double height, double moduleWidth, String contents);

  /**
   * Draw a DataMatrix barcode
   *
   * @param x the x-position of the top-left corner of the barcode in mm
   * @param y the y-position of the top-left corner of the barcode in mm
   * @param moduleSize the height/width of the little blocks in the barcode in mm
   * @param contents the data in the barcode
   */
  public abstract void barcode2d(double x, double y, double moduleSize, String contents);

  /**
   * Create a string with the printer commands in it
   *
   * @param copies the number of copies of the label to print
   */
  public abstract String finish(int copies);

  /**
   * Create a multi-line text block in normal orientation
   *
   * @param x the x coordinator the top-left position of the text block in mm
   * @param y the y coordinator the top-left position of the text block in mm
   * @param height the height of each line of the text block in mm
   * @param numCharsPerLine the maximum number of characters on each line
   * @param maxLines the maximum number of lines
   * @param lines the lines to draw; each line may have its own font and will be wrapped until the maximum number of lines is reached. Lines
   *          which are null or blank are skipped.
   */
  public final void multilineText(double x, double y, double height, int numCharsPerLine, int maxLines,
      Stream<Pair<FontStyle, String>> lines) {
    multilineText(x, y, height, numCharsPerLine, maxLines, TextDirection.NORMAL, lines);
  }

  /**
   * Create a multi-line text block
   *
   * @param x the x coordinator the top-left position of the text block in mm
   * @param y the y coordinator the top-left position of the text block in mm
   * @param height the height of each line of the text block in mm
   * @param numCharsPerLine the maximum number of characters on each line
   * @param maxLines the maximum number of lines
   * @param direction the direction of text in the block
   * @param lines the lines to draw; each line may have its own font and will be wrapped until the maximum number of lines is reached. Lines
   *          which are null or blank are skipped.
   */
  public final void multilineText(double x, double y, double height, int numCharsPerLine, int maxLines, TextDirection direction,
      Stream<Pair<FontStyle, String>> lines) {
    lines//
        .filter(input -> !LimsUtils.isStringBlankOrNull(input.getValue()))//
        .reduce(0, (offset, input) -> {
          // This block is full. This line of text gets ignored. So sad.
          if (offset >= maxLines) {
            return offset;
          }
          int line;
          // Print this text over as many lines as it needs and/or the block can hold
          for (line = 0; line + offset < maxLines && input.getValue().length() > line * numCharsPerLine; line++) {
            String str;
            // If we are on our last line and this string needs more than one, then add some dots at the end of this line to stand in for
            // the additional line(s).
            if (line + offset == maxLines - 1 && input.getValue().length() > (line + 1) * numCharsPerLine) {
              str = input.getValue().substring(line * numCharsPerLine, (line + 1) * numCharsPerLine - 2) + "...";
            } else {
              str = input.getValue().substring(line * numCharsPerLine, Math.min(input.getValue().length(), (line + 1) * numCharsPerLine));
            }
            text(x + direction.xMultiplier * 1.1 * height * (line + offset), y + direction.yMultiplier * 1.1 * height * (line + offset),
                height, direction,
                input.getKey(), Justification.LEFT,
                str);
          }
          return line + offset;
        }, (a, b) -> a + b);
  }

  /**
   * Draw left-justified text in the default font
   *
   * @param x the x coordinator the top-corner position of the text in mm
   * @param y the y coordinator the top-corner position of the text in mm
   * @param height the height of each line of the text block in mm
   * @param text the text to draw; must not be null
   */
  public final void text(double x, double y, double height,
      String text) {
    text(x, y, height, TextDirection.NORMAL, FontStyle.REGULAR, Justification.LEFT, text);
  }

  /**
   * Draw text
   *
   * @param x the x coordinator the top-corner position of the text in mm
   * @param y the y coordinator the top-corner position of the text in mm
   * @param height the height of each line of the text block in mm
   * @param direction the direction of text in the block
   * @param style the font style
   * @param justification which top-corner of the text the coordinates refer to
   * @param text the text to draw; must not be null
   */
  public abstract void text(double x, double y, double height, TextDirection direction, FontStyle style, Justification justification,
      String text);

  /**
   * Draw text, truncating with an ellipsis if too long
   *
   * @param x the x coordinator the top-corner position of the text in mm
   * @param y the y coordinator the top-corner position of the text in mm
   * @param height the height of each line of the text block in mm
   * @param numCharsPerLine the maximum number of characters on each line
   * @param text the text to draw; must not be null
   */
  public final void textClipped(double x, double y, double height, int numCharsPerLine, String text) {
    textClipped(x, y, height, numCharsPerLine, TextDirection.NORMAL, FontStyle.REGULAR, Justification.LEFT, text);
  }

  /**
   * Draw text, truncating with an ellipsis if too long
   *
   * @param x the x coordinator the top-corner position of the text in mm
   * @param y the y coordinator the top-corner position of the text in mm
   * @param height the height of each line of the text block in mm
   * @param numCharsPerLine the maximum number of characters on each line
   * @param direction the direction of text in the block
   * @param style the font style
   * @param justification which top-corner of the text the coordinates refer to
   * @param text the text to draw; must not be null
   */
  public final void textClipped(double x, double y, double height, int numCharsPerLine, TextDirection direction, FontStyle style,
      Justification justification, String text) {
    if (text != null)
      text(x, y, height, direction, style, justification,
          text.length() > numCharsPerLine ? text.substring(0, numCharsPerLine - 2) + "..." : text);
  }

}

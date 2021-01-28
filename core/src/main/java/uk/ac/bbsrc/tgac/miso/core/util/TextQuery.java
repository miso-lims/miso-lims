package uk.ac.bbsrc.tgac.miso.core.util;

public class TextQuery {

  private static final String WILDCARD = "*";

  private final String text;
  private final boolean exactStart;
  private final boolean exactEnd;

  public static TextQuery matchAnywhere(String text) {
    return new TextQuery(text, false);
  }

  public TextQuery(String input) {
    if (LimsUtils.isStringEmptyOrNull(input)) {
      text = null;
      exactStart = false;
      exactEnd = false;
    } else {
      exactStart = !input.startsWith(WILDCARD);
      exactEnd = !input.endsWith(WILDCARD);
      if ("*".equals(input)) {
        text = "";
      } else {
        text = input.substring(exactStart ? 0 : 1, exactEnd ? input.length() : input.length() - 1);
      }
    }
  }

  private TextQuery(String text, boolean exact) {
    this.text = text;
    this.exactStart = false;
    this.exactEnd = false;
  }

  public String getText() {
    return text;
  }

  public boolean isExactStart() {
    return exactStart;
  }

  public boolean isExactEnd() {
    return exactEnd;
  }

}
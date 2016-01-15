package uk.ac.bbsrc.tgac.miso.core.util;

public class TableUtils {

  // hyperLinkify(String, String) returns the hyperlink-ed (in HTML) version of
  // text given a path.
  public static String hyperLinkify(String path, String text) {
    return "<a href=\"" + path + "\">" + text + "</a>";
  }

  public static String hyperLinkify(String path, String text, Boolean image) {
    String img = image ? "<span style=\"display: inline-block; width: 16px;\"" + "class=\"ui-icon ui-icon-pencil\"></span>" : "";
    String hyp = "<div>" + img + "<span> <a href=\"" + path + "\">" + text + "</a> </span>" + "</div>";
    return hyp;
  }
}

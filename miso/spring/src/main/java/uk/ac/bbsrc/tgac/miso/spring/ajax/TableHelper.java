package uk.ac.bbsrc.tgac.miso.spring.ajax;

public class TableHelper {

  /**
   * Constructs an HTML <a> tag with relative path and display text
   * 
   * @param path String relative URL path
   * @param text String text to be hyperlinked
   */
  public static String hyperLinkify(String path, String text) {
    return "<a href=\"" + path + "\">" + text + "</a>";
  } 
  
  /**
   * Constructs an HTML <a> tag with relative path, display text, and optional hyperlinked pencil icon
   * 
   * @param path String relative URL path
   * @param text String text to be hyperlinked
   * @param image Boolean for whether to also display a hyperlinked pencil icon beside hyperlinked text
   */

  public static String hyperLinkify(String path, String text, Boolean image) {
    String img = image ? "<span style=\"display: inline-block; width: 16px;\""
                       + "class=\"ui-icon ui-icon-pencil\"></span>" : "";
    String hyp = "<div>"
             + img
             + "<span> <a href=\"" + path + "\">" + text + "</a> </span>"
             + "</div>";
    return hyp;
  }	
}

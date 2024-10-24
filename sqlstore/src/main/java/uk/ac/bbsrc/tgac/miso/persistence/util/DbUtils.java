package uk.ac.bbsrc.tgac.miso.persistence.util;

import java.util.regex.Matcher;

public class DbUtils {

  /**
   * Escapes MySQL wildcard characters "_" and "%"
   * 
   * @param original the String to format
   * @return the String with MySQL wildcard characters escaped
   */
  public static String sanitizeQueryString(String original) {
    // escape MySQL LIKE wildcard characters
    return original.trim()
        .replaceAll("_", Matcher.quoteReplacement("\\_"))
        .replaceAll("%", Matcher.quoteReplacement("\\%"));
  }

  /**
   * Checks whether a String starts and ends with non-escaped double quotes ("), indicating that an
   * exact match is wanted
   * 
   * @param query the String to check
   * @return true if the String is quoted; false otherwise
   */
  public static boolean isQuoted(String query) {
    return query.matches("\"(.*[^\\\\])?\"");
  }

  /**
   * Checks whether a String contains any MISO wildcards (*), indicating that partial matching is
   * wanted
   * 
   * @param query the String to check
   * @return true if the String contains one or more wildcards; false otherwise
   */
  public static boolean containsWildcards(String query) {
    return query.matches(".*(^|[^\\\\])\\*.*");
  }

  /**
   * Removes double quotes (") from the start and end of a String, then removes any escape characters
   * from inner double quotes and MISO wildcards (*)
   * 
   * @param original the String to format
   * @return the String with quotes and escapes removed
   */
  public static String removeQuotes(String original) {
    return removeEscapes(original.replaceFirst("^\"(.*)\"$", "$1"));
  }

  /**
   * Replaces non-escaped MISO wildcards (*) with MySQL wildcards (%)
   * 
   * @param original the String to format
   * @return the formatted String
   */
  public static String replaceWildcards(String original) {
    return removeEscapes(original.replaceAll("(^|[^\\\\])\\*", "$1%"));
  }

  /**
   * Removes escape characters from double quotes (") and MISO wildcards (*)
   * 
   * @param original the String to format
   * @return the formatted String
   */
  public static String removeEscapes(String original) {
    return original
        .replaceAll("\\\\\"", "\"")
        .replaceAll("\\\\\\*", "*");
  }

}

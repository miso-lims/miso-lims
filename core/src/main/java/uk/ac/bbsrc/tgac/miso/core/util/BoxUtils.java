package uk.ac.bbsrc.tgac.miso.core.util;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibraryAliquotView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListPoolView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.box.BoxableView;

/**
 * Utility class to provide helpful functions for Box-related methods in MISO
 *
 * Note: We define "position" to mean a position in a Box. A position is a combination of a single
 * Alphabet character followed by a 2-digit integer. For example the very first position (in the
 * top-left-most part of the box) is A01.
 * 
 * @author Dillan Cooke and Kyle Verhoog
 */
public class BoxUtils {
  /**
   * Return the character representation of an integer. This corresponds to the labeling of row on
   * boxes
   *
   * 0 -> A, 1 -> B, ... , 25 -> Z
   *
   * @param integer representation of the row
   * @return character representation of the row
   * @throws IllegalArgumentException if an invalid integer representation is given. ie. greater than
   *         25, less than 0.
   */

  public static char toRowChar(int row) throws IllegalArgumentException {
    if (row < 0 || row > 25)
      throw new IllegalArgumentException("Row number must be between 0 and 25");
    return (char) (row + 'A');
  }

  /**
   * Return the integer representation of a character which represents a row of a Box.
   *
   * A -> 0, B -> 1, ... , Z -> 25
   *
   * @param character representation of the row
   * @return integer representation of the row
   * @throws IllegalArgumentException if the given character is not in the Alphabet
   */

  public static int fromRowChar(char letter) {
    return normalizeLetter(letter) - 'A';
  }

  // If parse fails, return -1, else return parsed integer
  public static int tryParseInt(String s) {
    try {
      return Integer.parseInt(s);
    } catch (NumberFormatException e) {
      return -1;
    }
  }

  /**
   * Return a String of the position, given the position as two integers representing the row and
   * column. Examples: assertTrue(getPositionString(0, 0).equals("A01"))
   * assertTrue(getPositionString(25, 0).equals("Z01")) assertTrue(getPositionString(0,
   * 11).equals("A12"))
   *
   * @param integer representations of the row and column where 0 <= row <= 25 and 0 <= column <= 98
   * @return String representation of the row and column
   * @throws IllegalArgumentException if the row and column do not meet the following conditions: 0 <=
   *         row <= 25, 0 <= column <= 98
   */
  public static String getPositionString(int row, int column) {
    return getPositionString(toRowChar(row), column);
  }

  /**
   * Return a String of the position, given the position a character and an integer representation.
   * Examples: assertTrue(getPositionString('A', 0).equals("A01")) assertTrue(getPositionString('Z',
   * 0).equals("Z01")) assertTrue(getPositionString('H', 11).equals("H12"))
   *
   * @param character representation of the row and integer representation of the column where: 'A' <=
   *        row <= 'Z' and 0 <= column <= 98
   * @return String representation of the row and column
   * @throws IllegalArgumentException if the row and column do not meet the following conditions: 'A'
   *         <= row <= 'Z', 0 <= column <= 98
   */
  public static String getPositionString(char row, int column) {
    if (column < 0 || column > 98)
      throw new IllegalArgumentException("column must be between 0 and 98");
    return String.format("%c%02d", row, column + 1); // pad col with zeros
  }

  /**
   * Extracts the column number from a box position reference in String form ("A01")
   * 
   * @param position the position reference
   * @return the column number, between 0 and 25 inclusive
   * @throws IllegalArgumentException if this is not a valid String reference to a box position
   */
  public static int getColumnNumber(String position) {
    validateReference(position);
    // Need to subtract 1 to account for zero-indexed column number.
    return Integer.parseInt(position.substring(1, position.length())) - 1;
  }

  /**
   * Extracts the row number from a box position reference in String form ("A01")
   * 
   * @param position the position reference
   * @return the row number, between 0 and 25 inclusive
   * @throws IllegalArgumentException if this is not a valid String reference to a box position
   */
  public static int getRowNumber(String position) {
    validateReference(position);
    return fromRowChar(position.charAt(0));
  }

  /**
   * Extracts the row character from a box position reference in String form ("A01")
   * 
   * @param position the position reference
   * @return an alphabetic letter, taken from the first character of the position reference, and made
   *         uppercase if neccessary
   * @throws IllegalArgumentException if this is not a valid String reference to a box position
   */
  public static char getRowChar(String position) {
    validateReference(position);
    return normalizeLetter(position.charAt(0));
  }

  private static void validateReference(String position) {
    if (!isValidBoxPosition(position)) {
      throw new IllegalArgumentException(position + " is not a valid box position reference");
    }
  }

  /**
   * Checks whether a box position reference is in proper form, consisting of one capital alphabetic
   * character followed by 2 digits forming a number between 1 and 26 (e.g. "A01")
   * 
   * @param position the position reference
   * @return true if the position reference is valid; false otherwise
   */
  public static boolean isValidBoxPosition(String position) {
    return position.matches("[A-Z]\\d{2}") && Integer.parseInt(position.substring(1, position.length())) <= 26;
  }

  /**
   * Converts a letter to uppercase if necessary
   * 
   * @param letter
   * @return uppercase form of letter, regardless of its initial case
   * @throws IllegalArgumentException if letter is not in the alphabet
   */
  private static char normalizeLetter(char letter) {
    if (letter >= 'a' && letter <= 'z')
      letter = Character.toUpperCase(letter);
    if (letter < 'A' || letter > 'Z')
      throw new IllegalArgumentException("Row letter must be between A and Z");
    return letter;
  }

  public static String makeLocationLabel(BoxableView boxable) {
    return makeLocationLabel(boxable.isDiscarded(),
        boxable.isDistributed(),
        boxable.getLocationBarcode(),
        boxable.getBoxAlias(),
        boxable.getBoxPosition(),
        boxable.getBoxLocationBarcode(),
        true);
  }

  public static String makeLocationLabel(ListLibraryAliquotView boxable) {
    return makeLocationLabel(boxable.isDiscarded(),
        boxable.isDistributed(),
        null,
        boxable.getBox() == null ? null : boxable.getBox().getAlias(),
        boxable.getBoxPosition(),
        boxable.getBox() == null ? null : boxable.getBox().getLocationBarcode(),
        true);
  }

  public static String makeLocationLabel(ListPoolView boxable) {
    return makeLocationLabel(boxable.isDiscarded(),
        boxable.isDistributed(),
        null,
        boxable.getBoxAlias(),
        boxable.getBoxPosition(),
        boxable.getBoxLocationBarcode(),
        true);
  }

  public static String makeLocationLabel(Boxable boxable) {
    return makeLocationLabel(boxable, true);
  }

  public static String makeBoxLabel(Boxable boxable) {
    return makeLocationLabel(boxable, false);
  }

  private static String makeLocationLabel(Boxable boxable, boolean includePosition) {
    return makeLocationLabel(boxable.isDiscarded(),
        boxable.getDistributionTransfer() != null,
        boxable.getLocationBarcode(),
        boxable.getBox() == null ? null : boxable.getBox().getAlias(),
        boxable.getBoxPosition(),
        boxable.getBox() == null ? null : boxable.getBox().getLocationBarcode(),
        includePosition);
  }

  private static String makeLocationLabel(boolean discarded, boolean distributed, String locationBarcode,
      String boxAlias, String position, String boxLocationBarcode, boolean includePosition) {
    if (boxAlias != null) {
      if (position != null && includePosition) {
        return (isStringEmptyOrNull(locationBarcode) ? "" : locationBarcode + ", ")
            + boxAlias + " - " + position
            + (isStringEmptyOrNull(boxLocationBarcode) ? "" : " (" + boxLocationBarcode + ")");
      } else {
        return (isStringEmptyOrNull(locationBarcode) ? "" : locationBarcode + ", ")
            + boxAlias
            + (isStringEmptyOrNull(boxLocationBarcode) ? "" : " (" + boxLocationBarcode + ")");
      }
    } else if (distributed) {
      return "DISTRIBUTED";
    } else if (discarded) {
      return "EMPTY";
    } else if (locationBarcode != null) {
      return locationBarcode;
    } else {
      return "Unknown";
    }
  }

  /**
   * Returns a string like "Box_1 A01"
   * 
   * @param boxAlias
   * @param position
   * @return
   */
  public static String makeBoxPositionLabel(String boxAlias, String position) {
    if (boxAlias != null && position != null) {
      return boxAlias + " " + position;
    } else {
      return "Unknown";
    }
  }
}

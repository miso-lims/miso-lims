package uk.ac.bbsrc.tgac.miso.webapp.springtest.utils.visionmateSrc.ca.on.oicr.gsi.visionmate;

/**
 * This class encapsulates the server configuration options that affect the client. It is imperative
 * that the settings provided to a VisionMateClient match the actual settings on the server. If they
 * do not, the VisionMateClient's behaviour is unspecified. Depending on the discrepencies, likely
 * symptoms include timeouts, failed requests, and misrepresented data (e.g. "No Tube" may not be
 * recognized as a missing tube, instead being understood as a barcode String).
 * <p>
 * Note that the default constructor sets all options to their default values EXCEPT the Suffix
 * Character, which is set to control character 3 (end of text character) rather than the server's
 * default of null. This suffix character is required for the VisionMateClient to detect end of line
 * in the server's responses.
 * <p>
 * It is recommended to keep with all default settings EXCEPT for the suffix character, which must
 * be set to something. A non-printable control character is ideal because such characters cannot be
 * found in barcodes
 */
public class ServerConfig {

  public enum SortOrder {
    ROWS, COLUMNS
  };

  private static final String CMD_NOT_NULL = "Command must not be null";
  private static final String CMD_NOT_EMPTY = "Command must not be empty";

  private String acknowledge = "OK";
  private Character prefixChar = null;
  private Character suffixChar = (char) 3; // Note: default is nothing, but we need something to tell us when we've
                                           // reached EOL
  private String delimiter = ",";
  private SortOrder sortOrder = SortOrder.COLUMNS;

  private String noTubeLabel = "No Tube";
  private String noReadLabel = "No Read";

  private String getStatusCommand = "L";
  private String getDataCommand = "D";
  private String getLastErrorCommand = "E";
  private String getProductCommand = "C";
  private String setProductCommand = "P";
  private String resetStatusCommand = "Q";

  /**
   * Constructs a new ServerConfig with all default server settings EXCEPT for the suffix character,
   * which is set to control character 3 (End of text character)
   */
  public ServerConfig() {
    // Auto-generated constructor stub
  }

  /**
   * Combines the start of response pattern, which is
   * {@code "^[prefixChar]<acknowledge>\??(<command>)?"}
   * 
   * @param command the command issued
   * @return the pattern that the response should begin with
   */
  public String getResponseStartPattern(String command) {
    StringBuilder sb = new StringBuilder("^");
    if (prefixChar != null)
      sb.append(prefixChar);
    if (acknowledge != null)
      sb.append(acknowledge);
    sb.append("\\??"); // for unknown command
    sb.append("(");
    sb.append(command);
    sb.append(")?");
    return sb.toString();
  }

  /**
   * gets the current setting for static text the server sends to acknowledge every command
   * 
   * @return the current setting
   */
  public String getAcknowledge() {
    return acknowledge;
  }

  /**
   * sets the static text the server sends to acknowledge every command
   * 
   * @param acknowledge acknowledgement text
   */
  public void setAcknowledge(String acknowledge) {
    this.acknowledge = acknowledge;
  }

  /**
   * gets the current setting of a character that precedes every response from the server
   * 
   * @return the current setting
   */
  public Character getPrefixChar() {
    return prefixChar;
  }

  /**
   * sets a character that precedes every response from the server
   * 
   * @param prefixChar the character
   */
  public void setPrefixChar(Character prefixChar) {
    this.prefixChar = prefixChar;
  }

  /**
   * gets the current setting of a character that is appended to the end of every response from the
   * server
   * 
   * @return the current setting
   */
  public Character getSuffixChar() {
    return suffixChar;
  }

  /**
   * sets a character that is appended to the end of every response from the server. This character is
   * required for detecting the end of output
   * 
   * @param suffixChar the character
   */
  public void setSuffixChar(Character suffixChar) {
    if (suffixChar == null)
      throw new NullPointerException("Suffix character must not be null");
    this.suffixChar = suffixChar;
  }

  /**
   * gets the current delimiter setting. This is the character that separates barcodes returned from
   * the Get Data command
   * 
   * @return the current setting
   */
  public String getDelimiter() {
    return delimiter;
  }

  /**
   * sets the delimiter used to separate barcodes returned from the Get Data command
   * 
   * @param delimiter
   */
  public void setDelimiter(String delimiter) {
    if (delimiter == null)
      throw new NullPointerException("Delimiter must not be null");
    if (delimiter.isEmpty())
      throw new IllegalArgumentException("Delimiter must not be empty");
    this.delimiter = delimiter;
  }

  /**
   * gets the text command for the Get Status operation
   * 
   * @return the command
   */
  public String getGetStatusCommand() {
    return getStatusCommand;
  }

  /**
   * sets the text command for the Get Status operation
   * 
   * @param command
   */
  public void setGetStatusCommand(String command) {
    if (command == null)
      throw new NullPointerException(CMD_NOT_NULL);
    if (command.isEmpty())
      throw new IllegalArgumentException(CMD_NOT_EMPTY);
    this.getStatusCommand = command;
  }

  /**
   * gets the text command for the Get Data operation, used to retrieve scan data
   * 
   * @return the command
   */
  public String getGetDataCommand() {
    return getDataCommand;
  }

  /**
   * sets the text command for the Get Data operation, used to retrieve scan data
   * 
   * @param command
   */
  public void setGetDataCommand(String command) {
    if (command == null)
      throw new NullPointerException(CMD_NOT_NULL);
    if (command.isEmpty())
      throw new IllegalArgumentException(CMD_NOT_EMPTY);
    this.getDataCommand = command;
  }

  /**
   * gets the text command for the Get Last Error operation, used to retrieve error messages
   * 
   * @return the command
   */
  public String getGetLastErrorCommand() {
    return getLastErrorCommand;
  }

  /**
   * sets the text command for the Get Last Error operation, used to retrieve error messages
   * 
   * @param command
   */
  public void setGetLastErrorCommand(String command) {
    if (command == null)
      throw new NullPointerException(CMD_NOT_NULL);
    if (command.isEmpty())
      throw new IllegalArgumentException(CMD_NOT_EMPTY);
    this.getLastErrorCommand = command;
  }

  /**
   * gets the text command for the Get Current Product command, used to check the currently configured
   * rack type
   * 
   * @return the command
   */
  public String getGetProductCommand() {
    return getProductCommand;
  }

  /**
   * sets the text command for the Get Current Product command, used to check the currently configured
   * rack type
   * 
   * @param command
   */
  public void setGetProductCommand(String command) {
    if (command == null)
      throw new NullPointerException(CMD_NOT_NULL);
    if (command.isEmpty())
      throw new IllegalArgumentException(CMD_NOT_EMPTY);
    this.getProductCommand = command;
  }

  /**
   * gets the text command for the Set Current Product operation, used to configure the rack type to
   * scan. The rack type information must be appended to this
   * 
   * @return the command
   */
  public String getSetProductCommand() {
    return setProductCommand;
  }

  /**
   * gets the text command for the Set Current Product operation, used to configure the rack type to
   * scan
   * 
   * @param product the rack type to configure
   * @return the command, including rack type information
   */
  public String getSetProductCommand(RackType product) {
    if (product == null)
      throw new NullPointerException("Product must not be null");
    return setProductCommand + product.getStringRepresentation();
  }

  /**
   * sets the text command for the Set Current Product operation, used to configure the rack type to
   * scan
   * 
   * @param command base text command, not including rack type information
   */
  public void setSetProductCommand(String command) {
    if (command == null)
      throw new NullPointerException(CMD_NOT_NULL);
    if (command.isEmpty())
      throw new IllegalArgumentException(CMD_NOT_EMPTY);
    this.setProductCommand = command;
  }

  /**
   * gets the text command for the Reset Scanner operation, used to reset the scanner's status bits
   * 
   * @return the command
   */
  public String getResetStatusCommand() {
    return resetStatusCommand;
  }

  /**
   * sets the text command for the Reset Scanner operation, used to reset the scanner's status bits
   * 
   * @param command
   */
  public void setResetStatusCommand(String command) {
    if (command == null)
      throw new NullPointerException(CMD_NOT_NULL);
    if (command.isEmpty())
      throw new IllegalArgumentException(CMD_NOT_EMPTY);
    this.resetStatusCommand = command;
  }

  /**
   * gets the String that will be used in place of a barcode when there is no tube present in the
   * position
   * 
   * @return the String
   */
  public String getNoTubeLabel() {
    return noTubeLabel;
  }

  /**
   * sets the String that will be used in place of a barcode when there is no tube present in the
   * position
   * 
   * @param noTubeLabel
   */
  public void setNoTubeLabel(String noTubeLabel) {
    if (noTubeLabel == null)
      throw new NullPointerException("Label must not be null");
    if (noTubeLabel.equals(this.noReadLabel))
      throw new IllegalArgumentException("No Read and No Tube conditions cannot share the " +
          "same label");
    this.noTubeLabel = noTubeLabel;
  }

  /**
   * gets the String that will be used in place of a barcode when there is a tube present in the
   * position, but the scanner failed to read its barcode
   * 
   * @return the String
   */
  public String getNoReadLabel() {
    return noReadLabel;
  }

  /**
   * sets the String that will be used in place of a barcode when there is a tube present in the
   * position, but the scanner failed to read its barcode
   * 
   * @param noReadLabel
   */
  public void setNoReadLabel(String noReadLabel) {
    if (noReadLabel == null)
      throw new NullPointerException("Label must not be null");
    if (noReadLabel.equals(this.noTubeLabel))
      throw new IllegalArgumentException("No Read and No Tube conditions cannot share the " +
          "same label");
    this.noReadLabel = noReadLabel;
  }

  /**
   * gets the currently configured sort order, which may be by row (A1, A2, B1, B2) or column (A1, B1,
   * A2, B2)
   * 
   * @return the order
   */
  public SortOrder getSortOrder() {
    return sortOrder;
  }

  /**
   * sets the sort order, which may be by row (A1, A2, B1, B2) or column (A1, B1, A2, B2)
   * 
   * @param sortOrder
   */
  public void setSortOrder(SortOrder sortOrder) {
    if (sortOrder == null)
      throw new NullPointerException("Sort order cannot be null");
    this.sortOrder = sortOrder;
  }

}

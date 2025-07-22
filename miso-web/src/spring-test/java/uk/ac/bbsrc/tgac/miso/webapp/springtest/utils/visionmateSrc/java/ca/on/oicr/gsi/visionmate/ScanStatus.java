package uk.ac.bbsrc.tgac.miso.webapp.springtest.utils.visionmateSrc.ca.on.oicr.gsi.visionmate;


/**
 * Immutable object class to translate the scanner's status bits into a more usable format. A
 * ScanStatus is a snapshot of the scanner's status and cannot be updated as status changes occur
 */
public class ScanStatus {

  private final boolean initialized;
  private final boolean scanning;
  private final boolean finishedScan;
  private final boolean dataReady;
  private final boolean dataSent;
  private final boolean rack96;
  private final boolean error;

  /**
   * Constructs a ScanStatus from the byte representation of the 8 status bits. Note that Java bytes
   * are signed, so int 128 = byte -127, int 255 = byte -1, etc. Neither the integer nor the whole
   * byte value need be maintained, as the byte is read as 8 separate bits for the purpose of
   * translation
   * 
   * @param status
   */
  public ScanStatus(byte status) {
    this.initialized = getBit(status, 0);
    this.scanning = getBit(status, 1);
    this.finishedScan = getBit(status, 2);
    this.dataReady = getBit(status, 3);
    this.dataSent = getBit(status, 4);
    this.rack96 = getBit(status, 5);
    this.error = getBit(status, 7);
  }

  /**
   * Constructs a ScanStatus from the integer (0-255) representation of the 8 status bits
   * 
   * @param status
   */
  public ScanStatus(int status) {
    this((byte) status);
  }

  /**
   * Gets the value of one bit from the byte
   * 
   * @param source the whole byte
   * @param position the position within the byte (0-7)
   * @return the bit value
   */
  private boolean getBit(byte source, int position) {
    if (position < 0 || position > 7)
      throw new IllegalArgumentException("Bit position " + position + "is out of invalid. "
          + "Must be between 0 and 7 inclusive");
    return ((source >> position) & 0x01) == 1;
  }

  @Override
  public String toString() {
    return "Scan status: initialized=" + initialized + ", scanning=" + scanning + ", finishedScan=" + finishedScan +
        ", dataReady=" + dataReady + ", dataSent=" + dataSent + ", rack96=" + rack96 + ", error=" + error;
  }

  /**
   * @return the int representation of this ScanStatus, which is the format the server sends it in
   */
  public int toInt() {
    int i = 0;
    if (initialized)
      i += 1;
    if (scanning)
      i += 2;
    if (finishedScan)
      i += 4;
    if (dataReady)
      i += 8;
    if (dataSent)
      i += 16;
    if (rack96)
      i += 32;
    if (error)
      i += 128;
    return i;
  }

  /**
   * @return true if this ScanStatus indicates the scanner is initialized; false otherwise
   */
  public boolean isInitialized() {
    return initialized;
  }

  /**
   * @return true if this ScanStatus indicates the scanner is currently scanning; false otherwise
   */
  public boolean isScanning() {
    return scanning;
  }

  /**
   * @return true if this ScanStatus indicates the scanner has completed a scan; false otherwise
   */
  public boolean isFinishedScan() {
    return finishedScan;
  }

  /**
   * @return true if this ScanStatus indicates the scanner has scan data ready for retrieval; false
   *         otherwise
   */
  public boolean isDataReady() {
    return dataReady;
  }

  /**
   * @return true if this ScanStatus indicates the scanner's current scan data has been retrieved from
   *         the server at least once
   */
  public boolean isDataSent() {
    return dataSent;
  }

  /**
   * @return true if this ScanStatus indicates the scanner's current configured product has 96
   *         positions; false otherwise. This is only an indication of the scanner's configuration,
   *         and not the physical presence of such a rack
   */
  public boolean isRack96() {
    return rack96;
  }

  /**
   * @return true if this ScanStatus indicates an error has been raised; false otherwise
   */
  public boolean isError() {
    return error;
  }

}

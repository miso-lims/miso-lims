package uk.ac.bbsrc.tgac.miso.migration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Extension of java.util.Properties that provides a few convenience methods for getting type-specific
 * and strictly-required properties
 */
public class MigrationProperties extends Properties {
  
  private static final long serialVersionUID = 1L;

  /**
   * Create a new MigrationProperties using the property list in a file
   * 
   * @param filePath path of properties file
   * @throws FileNotFoundException if the file does not exist, is a directory rather than a regular file,
   * or for some other reason cannot be opened for reading.
   * @throws IOException if an error occurred while attempting to read the file
   */
  public MigrationProperties(String filePath) throws FileNotFoundException, IOException {
    super();
    try (InputStream is = new FileInputStream(filePath)) {
      load(is);
    }
  }
  
  /**
   * Retrieves a String value from the Properties
   * 
   * @param property key of the property to retrieve
   * @return the (trimmed) String property value, or null if it was missing or empty
   */
  public String getStringOrNull(String property) {
    String prop = super.getProperty(property);
    if (prop != null) {
      prop = prop.trim();
      if (prop.isEmpty()) prop = null;
    }
    return prop;
  }
  
  /**
   * Retrieves a String value from the Properties or throws an exception if it is missing
   * 
   * @param property key of the property to retrieve
   * @return the (trimmed) String property value
   * @throws IllegalArgumentException if the property is missing or empty
   */
  public String getRequiredString(String property) {
    String prop = getStringOrNull(property);
    if (prop == null) {
      throw new IllegalArgumentException("Required property " + property + " missing");
    }
    return prop;
  }
  
  /**
   * Retrieves an int value from the Properties or throws an exception if it is missing
   * 
   * @param property key of the property to retrieve
   * @return the int property value
   * @throws IllegalArgumentException if the property is missing or empty
   */
  public int getRequiredInt(String property) {
    String prop = getRequiredString(property);
    try {
      return Integer.parseInt(prop);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Property " + property + " expected type: int");
    }
  }
  
  /**
   * Retrieves a long value from the Properties or throws an exception if it is missing
   * 
   * @param property key of the property to retrieve
   * @return the long property value
   * @throws IllegalArgumentException if the property is missing or empty
   */
  public long getRequiredLong(String property) {
    String prop = getRequiredString(property);
    try {
      return Long.parseLong(prop);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Property " + property + " expected type: long");
    }
  }
  
  /**
   * Retrieves a boolean value from the Properties
   * 
   * @param property key of the property to retrieve
   * @param defaultValue value to return if the property is not found
   * @return the boolean property value if found; defaultValue otherwise
   */
  public boolean getBoolean(String property, boolean defaultValue) {
    String prop = getStringOrNull(property);
    if (prop == null) return defaultValue;
    return Boolean.valueOf(prop);
  }
  
}

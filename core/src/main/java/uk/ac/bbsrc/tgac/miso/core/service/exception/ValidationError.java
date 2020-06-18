package uk.ac.bbsrc.tgac.miso.core.service.exception;

import java.io.Serializable;

import uk.ac.bbsrc.tgac.miso.core.data.Deletable;

public class ValidationError implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final String GENERAL_PROPERTY = "GENERAL";
  private static final String DEFAULT_PROPERTY_MESSAGE = "This value is invalid";
  private static final String DEFAULT_GENERAL_MESSAGE = "This data is invalid";

  private final String property;
  private final String message;

  /**
   * Constructs a ValidationError to describe one of possibly several reasons that an object is invalid
   * 
   * @param property name of the invalid property; set to null if it applies to no particular property
   * @param message user-friendly description of invalidity
   */
  public ValidationError(String property, String message) {
    this.property = property == null ? GENERAL_PROPERTY : property;
    if (message != null) {
      this.message = message;
    } else {
      this.message = property == null ? DEFAULT_GENERAL_MESSAGE : DEFAULT_PROPERTY_MESSAGE;
    }
  }

  public static ValidationError forDeletionUsage(Deletable object, long usage, String pluralizedReferrer) {
    return new ValidationError(
        String.format("%s '%s' is used by %d %s", object.getDeleteType(), object.getDeleteDescription(), usage, pluralizedReferrer));
  }

  public static ValidationError forDuplicate(String typeLabel, String property) {
    return forDuplicate(typeLabel, property, property);
  }

  public static ValidationError forDuplicate(String typeLabel, String property, String propertyLabel) {
    return new ValidationError(property, String.format("There is already a(n) %s with this %s", typeLabel, propertyLabel));
  }

  /**
   * Constructs a ValidationError to describe one of possibly several reasons that an object is invalid. If the error applies to a specific
   * field, a different constructor should be used
   * 
   * @param message user-friendly description of invalidity
   */
  public ValidationError(String message) {
    this(null, message);
  }

  /**
   * @return name of invalid property, or "GENERAL" if the error applies to no particular property
   */
  public String getProperty() {
    return property;
  }

  public String getMessage() {
    return message;
  }

}

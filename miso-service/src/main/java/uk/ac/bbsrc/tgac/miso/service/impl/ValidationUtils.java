package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationException;

public class ValidationUtils {

  private ValidationUtils() {
    throw new IllegalStateException("Static util class not intended for instantiation");
  }

  public static Map<String, Integer> adjustNameLength(Map<String, Integer> input, NamingScheme scheme) {
    return adjustLength(input, "name", scheme.nameLengthAdjustment());
  }

  public static Map<String, Integer> adjustLength(Map<String, Integer> input, String name, Integer validationLength) {
    if (validationLength != null) {
      input.put(name, input.containsKey(name) ? Math.min(input.get(name), validationLength) : validationLength);
    }
    return input;
  }

  public static <T extends Barcodable> void validateBarcodeUniqueness(T barcodable, T beforeChange,
      WhineyFunction<String, T> lookupByBarcode,
      Collection<ValidationError> errors, String typeLabel) throws IOException {
    if (barcodable.getIdentificationBarcode() != null
        && (beforeChange == null || !barcodable.getIdentificationBarcode().equals(beforeChange.getIdentificationBarcode()))
        && lookupByBarcode.apply(barcodable.getIdentificationBarcode()) != null) {
      errors.add(new ValidationError("identificationBarcode", String.format("There is already a %s with this barcode", typeLabel)));
    }
  }

  public static void validateConcentrationUnits(BigDecimal concentration, ConcentrationUnit units, String field, String label,
      Collection<ValidationError> errors) {
    if (concentration != null && units == null) {
      errors.add(new ValidationError(field, label + " units must be specified"));
    }
  }

  public static void validateConcentrationUnits(Double concentration, ConcentrationUnit units, Collection<ValidationError> errors) {
    if (concentration != null && concentration > 0D && units == null) {
      errors.add(new ValidationError("concentrationUnits", "Concentration units must be specified"));
    }
  }

  public static void validateVolumeUnits(Double volume, VolumeUnit units, Collection<ValidationError> errors) {
    if (volume != null && volume > 0D && units == null) {
      errors.add(new ValidationError("volumeUnits", "Volume units must be specified"));
    }
  }

  public static void validateDistributionFields(boolean isDistributed, Date distributionDate, String distributionRecipient, Box box,
      Collection<ValidationError> errors) {
    if (isDistributed) {
      if (distributionDate == null)
        errors.add(new ValidationError("distributionDate", "Distribution date must be recorded for distributed item"));
      if (isStringEmptyOrNull(distributionRecipient))
        errors.add(new ValidationError("distributionRecipient", "Distribution recipient must be recorded for distributed item"));
    } else {
      if (distributionDate != null)
        errors.add(new ValidationError("distributionDate", "Distribution date should be empty since item is not distributed"));
      if (!isStringEmptyOrNull(distributionRecipient))
        errors.add(new ValidationError("distributionRecipient", "Distribution recipient should be empty since item is not distributed"));
    }
  }
  
  public static void validateUnboxableFields(boolean discarded, boolean distributed, Box box, Collection<ValidationError> errors) {
    if (discarded && box != null) errors.add(new ValidationError("box", "Discarded item cannot be added to a box"));
    if (distributed && box != null) errors.add(new ValidationError("box", "Distributed item cannot be added to a box"));
  }

  public static void validateUrl(String fieldName, String maybeUrl, boolean allowEmptyUrl, Collection<ValidationError> errors) {
    if (isStringEmptyOrNull(maybeUrl) && allowEmptyUrl) return;
    URL url = parseUrl(maybeUrl);
    if (url == null) {
      errors.add(new ValidationError(fieldName, INVALID_URL));
      return;
    }
    if (!allowedUrlSchemes.contains(url.getProtocol())) {
      errors.add(new ValidationError(fieldName, INVALID_URL));
    }
  }

  private static URL parseUrl(String maybeUrl) {
    try {
      return new URL(maybeUrl);
    } catch (MalformedURLException e) {
      return null;
    }
  }

  private static Set<String> allowedUrlSchemes = new HashSet<>();
  static {
    allowedUrlSchemes.add("http");
    allowedUrlSchemes.add("https");
  }
  private static final String INVALID_URL = "URL is not valid";

  public static void validateNameOrThrow(Nameable object, NamingScheme namingScheme) {
    ValidationResult val = namingScheme.validateName(object.getName());
    if (!val.isValid()) {
      throw new ValidationException(new ValidationError("name", val.getMessage()));
    }
  }

  public static <T, R> boolean isSetAndChanged(Function<T, R> getter, T newItem, T beforeChange) {
    R after = getter.apply(newItem);
    if (after == null) {
      return false;
    } else if (beforeChange == null) {
      return true;
    }
    R before = getter.apply(beforeChange);
    return !after.equals(before);
  }

}

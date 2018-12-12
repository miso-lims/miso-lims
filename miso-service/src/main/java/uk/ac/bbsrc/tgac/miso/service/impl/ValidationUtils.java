package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationError;

public class ValidationUtils {

  private ValidationUtils() {
    throw new IllegalStateException("Static util class not intended for instantiation");
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

}

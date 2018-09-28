package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
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

}

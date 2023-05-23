package uk.ac.bbsrc.tgac.miso.core.data;

import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

/**
 * This interface simply describes an object that can be barcoded to denote its identity, i.e. have
 * an identification String that represents a scannable barcode. For physical barcode printing
 * purposes, Barcodable objects can be assigned names and label text fields which can be made up of
 * existing object fields to aid with barcode label generation.
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface Barcodable extends Identifiable {
  public enum EntityType {
    LIBRARY_ALIQUOT, POOL, SAMPLE, LIBRARY, BOX, CONTAINER, CONTAINER_MODEL, KIT, WORKSTATION, INSTRUMENT;
  }

  /**
   * Returns the label text of this Barcodable object.
   * 
   * @return String labelText.
   */
  public String getLabelText();

  /**
   * Returns the identificationBarcode of this Barcodable object.
   * 
   * @return String identificationBarcode.
   */
  public String getIdentificationBarcode();

  /**
   * The date, if any, that should appear on the label.
   */
  public LocalDate getBarcodeDate();

  /**
   * Sets the identificationBarcode of this Barcodable object.
   * 
   * @param identificationBarcode identificationBarcode.
   */
  public void setIdentificationBarcode(String identificationBarcode);

  public static <T extends Barcodable> Set<String> extractLabels(Iterable<T> items) {
    Set<String> labels = new TreeSet<>();
    for (T item : items) {
      labels.add(item.getLabelText());
    }
    return labels;
  }

  public <T> T visit(BarcodableVisitor<T> visitor);

}

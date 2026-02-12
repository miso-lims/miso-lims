package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.GroupIdentifiable;
import uk.ac.bbsrc.tgac.miso.core.data.HierarchyEntity;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Probe;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Probe.ProbeFeatureType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Probe.Read;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BarcodableReference;
import uk.ac.bbsrc.tgac.miso.core.data.qc.DetailedQcItem;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.BarcodableReferenceService;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public class ValidationUtils {

  private ValidationUtils() {
    throw new IllegalStateException("Static util class not intended for instantiation");
  }

  public static <T extends Barcodable> void validateBarcodeUniqueness(T barcodable, T beforeChange,
      BarcodableReferenceService service,
      Collection<ValidationError> errors) throws IOException {
    if (barcodable.getIdentificationBarcode() != null
        && (beforeChange == null
            || !barcodable.getIdentificationBarcode().equals(beforeChange.getIdentificationBarcode()))) {
      BarcodableReference ref = service.checkForExisting(barcodable.getIdentificationBarcode());
      if (ref != null) {
        errors.add(new ValidationError("identificationBarcode",
            String.format("%s '%s' already has this barcode", ref.getEntityType(), ref.getFullLabel())));
      }
    }
  }

  public static void validateConcentrationUnits(BigDecimal concentration, ConcentrationUnit units, String field,
      String label,
      Collection<ValidationError> errors) {
    if (concentration != null && units == null) {
      errors.add(new ValidationError(field, label + " units must be specified"));
    }
  }

  public static void validateConcentrationUnits(BigDecimal concentration, ConcentrationUnit units,
      Collection<ValidationError> errors) {
    if (concentration != null && concentration.compareTo(BigDecimal.ZERO) > 0 && units == null) {
      errors.add(new ValidationError("concentrationUnits", "Concentration units must be specified"));
    }
  }

  public static void validateVolume(BigDecimal initialVolume, BigDecimal volume, Collection<ValidationError> errors) {
    if (initialVolume != null && volume == null) {
      errors.add(new ValidationError("volume", "Volume is required when initial volume is set"));
    }
  }

  public static void validateVolumeUnits(BigDecimal volume, VolumeUnit units, Collection<ValidationError> errors) {
    if (volume != null && volume.compareTo(BigDecimal.ZERO) > 0 && units == null) {
      errors.add(new ValidationError("volumeUnits", "Volume units must be specified"));
    }
  }

  public static void validateUnboxableFields(Boxable item, Collection<ValidationError> errors) {
    if (item.getBox() != null) {
      if (item.isDiscarded())
        errors.add(new ValidationError("Discarded item cannot be added to a box"));
      if (item.getDistributionTransfer() != null)
        errors.add(new ValidationError("Distributed item cannot be added to a box"));
    }
  }

  public static void validateDetailedQcStatus(HierarchyEntity item, Collection<ValidationError> errors) {
    if (item.getDetailedQcStatus() != null && item.getDetailedQcStatus().getNoteRequired()
        && LimsUtils.isStringEmptyOrNull(item.getDetailedQcStatusNote())) {
      errors.add(new ValidationError("detailedQcStatusNote", "QC Note must be specified for the selected status"));
    }
    validateQcUser(item.getDetailedQcStatus(), item.getQcUser(), errors);
  }

  public static void validateQcUser(Object qcStatus, User qcUser, Collection<ValidationError> errors) {
    validateQcUser(qcStatus, qcUser, errors, "QC status", "QC user");
  }

  public static void validateQcUser(Object qcStatus, User qcUser, Collection<ValidationError> errors,
      String qcFieldLabel,
      String qcUserLabel) {
    if (qcStatus == null && qcUser != null) {
      errors.add(
          new ValidationError(String.format("%s cannot be set when %s is not specified", qcUserLabel, qcFieldLabel)));
    } else if (qcStatus != null && qcUser == null) {
      errors.add(new ValidationError(String.format("%s must be set when %s is specified", qcUserLabel, qcFieldLabel)));
    }
  }

  public static void validateGroupDescription(GroupIdentifiable object, Collection<ValidationError> errors) {
    if (object.getGroupDescription() != null && object.getGroupId() == null) {
      errors.add(new ValidationError("groupDescription", "Cannot set group description without group ID"));
    }
  }

  public static void validateUrl(String fieldName, String maybeUrl, boolean allowEmptyUrl,
      Collection<ValidationError> errors) {
    if (isStringEmptyOrNull(maybeUrl) && allowEmptyUrl)
      return;
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

  private static final String PROBE_PATTERN_REGEX = "^(?:5P|\\^)?[NACGT]*\\(BC\\)[NACGT]*(?:3P|\\$)?$";
  private static final String SEQUENCE_PATTERN_REGEX = "^[ACGT]+$";

  /**
   * Checks that probes are valid both individually and as a set. This method does not handle null or
   * empty sets, so those conditions should be checked first, and this method should only be called if
   * probes are present. All errors are reported on the "probes" field
   * 
   * @param probes collection containing one or more probes
   * @param errors list to collect errors
   */
  public static void validateProbes(Collection<? extends Probe> probes, List<ValidationError> errors) {
    if (probes == null || probes.isEmpty()) {
      throw new IllegalArgumentException(
          "The method should only be called after ensuring that there are probes in the set");
    }

    Set<String> identifiers = new HashSet<>();
    Set<String> names = new HashSet<>();
    Set<String> sequences = new HashSet<>();

    Probe firstProbe = probes.iterator().next();
    Read read = firstProbe.getRead();
    String pattern = firstProbe.getPattern();
    ProbeFeatureType featureType = firstProbe.getFeatureType();

    // We only want to report each of these once
    boolean multipleReads = false;
    boolean multiplePatterns = false;
    boolean multipleFeatureTypes = false;
    boolean badPattern = false;
    boolean badSequence = false;
    boolean targetGeneMissing = false;
    boolean targetGeneInvalid = false;

    for (Probe probe : probes) {
      multipleReads |= probe.getRead() != read;
      multiplePatterns |= !Objects.equals(probe.getPattern(), pattern);
      multipleFeatureTypes |= probe.getFeatureType() != featureType;


      if (identifiers.contains(probe.getIdentifier())) {
        errors.add(new ValidationError("probes", "Duplicate identifier: " + probe.getIdentifier()));
      }
      identifiers.add(probe.getIdentifier());

      if (names.contains(probe.getName())) {
        errors.add(new ValidationError("probes", "Duplicate name: " + probe.getName()));
      }
      names.add(probe.getName());

      if (sequences.contains(probe.getSequence())) {
        errors.add(new ValidationError("probes", "Duplicate sequence: " + probe.getSequence()));
      }
      sequences.add(probe.getSequence());

      badPattern |= !probe.getPattern().matches(PROBE_PATTERN_REGEX);
      badSequence |= !probe.getSequence().matches(SEQUENCE_PATTERN_REGEX);
      if (probe.getFeatureType() == ProbeFeatureType.CRISPR) {
        targetGeneMissing |= probe.getTargetGeneId() == null || probe.getTargetGeneName() == null;
      } else {
        targetGeneInvalid |= probe.getTargetGeneId() != null || probe.getTargetGeneName() != null;
      }
    }

    if (multipleReads) {
      errors.add(new ValidationError("probes", "All probes must have the same read"));
    }
    if (multiplePatterns) {
      errors.add(new ValidationError("probes", "All probes must have the same pattern"));
    }
    if (multipleFeatureTypes) {
      errors.add(new ValidationError("probes", "All probes must have the same feature type"));
    }

    // All below errors should be caught by front end validation, which provides better, row-specific
    // help.
    if (badPattern) {
      errors.add(new ValidationError("probes", "Invalid pattern."));
    }
    if (badSequence) {
      errors.add(new ValidationError("probes", "Invalid sequence."));
    }
    if (targetGeneMissing) {
      errors.add(new ValidationError("probes",
          "Target Gene ID and name are required for %s probes".formatted(ProbeFeatureType.CRISPR.getLabel())));
    }
    if (targetGeneInvalid) {
      errors.add(new ValidationError("probes",
          "Target Gene ID and name are only valid for %s probes".formatted(ProbeFeatureType.CRISPR.getLabel())));
    }
  }

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

  public static <T, R> boolean isChanged(Function<T, R> getter, T newItem, T beforeChange) {
    if (beforeChange == null) {
      return true;
    }
    R after = getter.apply(newItem);
    R before = getter.apply(beforeChange);
    if (after == null) {
      return before != null;
    } else {
      return !after.equals(before);
    }
  }

  /**
   * Loads data from the backend through the given parameters, and checks if it is a valid item to
   * attempt to load. If it is valid, it will load and the passed setter method will accept the item
   * as its function parameters. If it is invalid, it will throw a ValidationException.
   * 
   * @param <T>
   * @param setter
   * @param childEntity
   * @param service
   * @param property Used to place the error message on the front end. As such, correct naming
   *        convention is required in order for the error message to be accurate. For simple fields,
   *        the property name should match the name of the field in the DTO. For collections, a
   *        sensible collection name should be used, and you need to add a div to the code in the JSP
   *        file that loads the DTO in order for the error message to display.
   * @throws IOException
   * 
   */
  public static <T extends Identifiable> void loadChildEntity(Consumer<T> setter, T childEntity,
      ProviderService<T> service,
      String property)
      throws IOException {
    if (childEntity != null) {
      T item = service.get(childEntity.getId());
      if (item == null) {
        throw new ValidationException(new ValidationError(property, "Invalid item ID: " + childEntity.getId()));
      }
      setter.accept(item);
    }
  }

  public static <T extends Identifiable> void applySetChanges(Set<T> to, Set<T> from) {
    to.removeIf(toItem -> from.stream().noneMatch(fromItem -> fromItem.getId() == toItem.getId()));
    from.forEach(fromItem -> {
      if (to.stream().noneMatch(toItem -> toItem.getId() == fromItem.getId())) {
        to.add(fromItem);
      }
    });
  }

  public static void updateDetailedQcStatusDetails(DetailedQcItem object, DetailedQcItem beforeChange,
      AuthorizationManager authorizationManager) throws IOException {
    updateQcDetails(object, beforeChange, DetailedQcItem::getDetailedQcStatus, DetailedQcItem::getQcUser,
        DetailedQcItem::setQcUser,
        authorizationManager, DetailedQcItem::getQcDate, DetailedQcItem::setQcDate);
  }

  public static <T> void updateQcDetails(T object, T beforeChange, Function<T, Object> getStatus,
      Function<T, User> getUser,
      BiConsumer<T, User> setUser, AuthorizationManager authorizationManager, Function<T, LocalDate> getDate,
      BiConsumer<T, LocalDate> setDate)
      throws IOException {
    if (isChanged(getStatus, object, beforeChange)) {
      if (getStatus.apply(object) == null) {
        setUser.accept(object, null);
        setDate.accept(object, null);
      } else {
        setUser.accept(object, authorizationManager.getCurrentUser());
        setDate.accept(object, LocalDate.now(ZoneId.systemDefault()));
      }
    } else if (beforeChange != null) {
      setUser.accept(object, getUser.apply(beforeChange));
      setDate.accept(object, getDate.apply(beforeChange));
    }
  }

  public static ValidationException rewriteParentErrors(ValidationException original) {
    return new ValidationException(original.getErrors().stream()
        .map(err -> new ValidationError(String.format("Parent %s: %s", err.getProperty(), err.getMessage())))
        .collect(Collectors.toList()));
  }

  public static ValidationError makeNoNullError(String property) {
    return new ValidationError(property, "Must be specified");
  }

}

package uk.ac.bbsrc.tgac.miso.core.util;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaglegenomics.simlims.core.User;
import com.google.common.annotations.VisibleForTesting;

import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.HierarchyEntity;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.IlluminaRun;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.IndexedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.LS454Run;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.OxfordNanoporeRun;
import uk.ac.bbsrc.tgac.miso.core.data.PacBioRun;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquotSingleCell;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSingleCell;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSlide;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStockRna;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStockSingleCell;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissuePiece;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SolidRun;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.OxfordNanoporeContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;

/**
 * Utility class to provde helpful functions to MISO
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public class LimsUtils {

  private static final BigDecimal MAX_VOLUME = new BigDecimal("999999.9999999999");
  private static final BigDecimal MIN_VOLUME = new BigDecimal("-999999.9999999999");

  private static final Logger log = LoggerFactory.getLogger(LimsUtils.class);

  public static boolean isStringEmptyOrNull(String s) {
    return "".equals(s) || s == null;
  }

  public static boolean isStringBlankOrNull(String s) {
    return s == null || "".equals(s.trim());
  }

  public static String nullifyStringIfBlank(String s) {
    return (isStringBlankOrNull(s) ? null : s);
  }

  public static String toNiceString(BigDecimal num) {
    if (num == null) {
      return null;
    }
    String nice = StringUtils.strip(num.toPlainString(), "0");
    if (nice.startsWith(".")) {
      nice = "0" + nice;
    }
    if (nice.endsWith(".")) {
      nice += "0";
    }
    return nice;
  }

  public static String zeroPad(int number, int minLength) {
    String string = Integer.toString(number);
    while (string.length() < minLength) {
      string = "0" + string;
    }
    return string;
  }

  public static List<Long> parseIds(String idString) {
    String[] split = idString.split(",");
    List<Long> ids = new ArrayList<>();
    for (int i = 0; i < split.length; i++) {
      ids.add(Long.parseLong(split[i]));
    }
    return ids;
  }

  /**
   * Checks that a directory exists. This method will attempt to create the directory if it doesn't
   * exist and if the attemptMkdir flag is true
   * 
   * @param path of type File
   * @param attemptMkdir of type boolean
   * @return boolean true if the directory exists/was created, false if not
   * @throws IOException when the directory exist check/creation could not be completed
   */
  public static boolean checkDirectory(File path, boolean attemptMkdir) throws IOException {
    boolean storageOk;

    if (attemptMkdir) {
      storageOk = path.exists() || path.mkdirs();
    } else {
      storageOk = path.exists();
    }

    if (!storageOk) {
      StringBuilder sb = new StringBuilder("The directory [" + path.toString() + "] doesn't exist");
      if (attemptMkdir) {
        sb.append(" or is not creatable");
      }
      sb.append(". Please create this directory and ensure that it is writable.");
      throw new IOException(sb.toString());
    } else {
      if (attemptMkdir) {
        log.info("Directory (" + path + ") exists.");
      } else {
        log.info("Directory (" + path + ") OK.");
      }
    }
    return storageOk;
  }

  public static String getCurrentDateAsString(DateFormat df) {
    return df.format(new Date());
  }

  public static String getCurrentDateAsString() {
    return getCurrentDateAsString(new SimpleDateFormat("yyyyMMdd"));
  }

  public static String formatDate(Date date) {
    return date == null ? null : getDateFormat().format(date);
  }

  public static String formatDate(LocalDate date) {
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
    return date == null ? null : date.format(dateFormatter);
  }

  public static Date parseDate(String dateString) {
    if (isStringEmptyOrNull(dateString)) {
      return null;
    }
    try {
      return getDateFormat().parse(dateString);
    } catch (ParseException e) {
      throw new IllegalArgumentException("Invalid date string");
    }
  }

  public static LocalDate parseLocalDate(String dateString) {
    if (isStringEmptyOrNull(dateString)) {
      return null;
    }
    try {
      return LocalDate.parse(dateString);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid date string");
    }
  }

  public static String formatDateTime(Date date) {
    return date == null ? null : getDateTimeFormat().format(date);
  }

  public static Date parseDateTime(String dateString) {
    if (isStringEmptyOrNull(dateString)) {
      return null;
    }
    try {
      return getDateTimeFormat().parse(dateString);
    } catch (ParseException e) {
      throw new IllegalArgumentException("Invalid datetime string");
    }
  }

  public static DateFormat getDateFormat() {
    return new SimpleDateFormat("yyyy-MM-dd");
  }

  public static DateFormat getDateTimeFormat() {
    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  }

  public static boolean isDetailedSample(Sample sample) {
    return sample instanceof DetailedSample;
  }

  public static boolean isPlainSample(Sample sample) {
    return !isDetailedSample(sample);
  }

  private static boolean safeCategoryCheck(Sample sample, String category) {
    DetailedSample detailedSample = (DetailedSample) sample;
    if (detailedSample.getSampleClass() == null)
      return false;
    return category.equals(detailedSample.getSampleClass().getSampleCategory());
  }

  public static boolean isIdentitySample(Sample sample) {
    if (!isDetailedSample(sample))
      return false;
    return sample instanceof SampleIdentity || safeCategoryCheck(sample, SampleIdentity.CATEGORY_NAME);
  }

  public static boolean isTissueSample(Sample sample) {
    if (!isDetailedSample(sample))
      return false;
    return sample instanceof SampleTissue || safeCategoryCheck(sample, SampleTissue.CATEGORY_NAME);
  }

  public static boolean isTissueProcessingSample(Sample sample) {
    if (!isDetailedSample(sample))
      return false;
    return sample instanceof SampleTissueProcessing || safeCategoryCheck(sample, SampleTissueProcessing.CATEGORY_NAME);
  }

  public static boolean isTissuePieceSample(Sample sample) {
    if (!isDetailedSample(sample))
      return false;
    return sample instanceof SampleTissuePiece;
  }

  public static boolean isProcessingSingleCellSample(Sample sample) {
    if (!isDetailedSample(sample))
      return false;
    return sample instanceof SampleSingleCell;
  }

  public static boolean isStockSample(Sample sample) {
    if (!isDetailedSample(sample))
      return false;
    return sample instanceof SampleStock || safeCategoryCheck(sample, SampleStock.CATEGORY_NAME);
  }

  public static boolean isStockSingleCellSample(Sample sample) {
    if (!isDetailedSample(sample))
      return false;
    return sample instanceof SampleStockSingleCell;
  }

  public static boolean isStockRnaSample(Sample sample) {
    if (!isDetailedSample(sample))
      return false;
    return sample instanceof SampleStockRna;
  }

  public static boolean isAliquotSample(Sample sample) {
    return sample instanceof SampleAliquot || safeCategoryCheck(sample, SampleAliquot.CATEGORY_NAME);
  }

  public static boolean isAliquotSingleCellSample(Sample sample) {
    if (!isDetailedSample(sample))
      return false;
    return sample instanceof SampleAliquotSingleCell;
  }

  public static boolean isSampleSlide(Sample sample) {
    if (!isDetailedSample(sample))
      return false;
    return sample instanceof SampleSlide
        || SampleSlide.SUBCATEGORY_NAME.equals(((DetailedSample) sample).getSampleClass().getSampleSubcategory());
  }

  public static boolean isDetailedLibrary(Library library) {
    return library instanceof DetailedLibrary;
  }

  public static boolean isDetailedLibraryAliquot(LibraryAliquot aliquot) {
    return aliquot instanceof DetailedLibraryAliquot;
  }

  public static boolean isIlluminaRun(Run run) {
    return run instanceof IlluminaRun;
  }

  public static boolean isPacBioRun(Run run) {
    return run instanceof PacBioRun;
  }

  public static boolean isLS454Run(Run run) {
    return run instanceof LS454Run;
  }

  public static boolean isSolidRun(Run run) {
    return run instanceof SolidRun;
  }

  public static boolean isOxfordNanoporeRun(Run run) {
    return run instanceof OxfordNanoporeRun;
  }

  public static boolean isOxfordNanoporeContainer(SequencerPartitionContainer container) {
    return container instanceof OxfordNanoporeContainer;
  }

  public static <T extends DetailedSample> T getParent(Class<T> targetParentClass, DetailedSample start) {
    for (DetailedSample current = deproxify(start.getParent()); current != null; current =
        deproxify(current.getParent())) {
      if (targetParentClass.isInstance(current)) {
        return targetParentClass.cast(current);
      }
    }
    return null;
  }

  public static <T extends DetailedSample> T getParentOrSelf(Class<T> targetParentClass, DetailedSample start) {
    for (DetailedSample current = deproxify(start); current != null; current = deproxify(current.getParent())) {
      if (targetParentClass.isInstance(current)) {
        return targetParentClass.cast(current);
      }
    }
    return null;
  }

  public static Requisition getParentRequisition(Sample sample) {
    if (!isDetailedSample(sample)) {
      return null;
    }
    DetailedSample requisitionSample = ((DetailedSample) sample).getParent();
    while (requisitionSample != null && requisitionSample.getRequisition() == null) {
      requisitionSample = requisitionSample.getParent();
    }
    return requisitionSample == null ? null : requisitionSample.getRequisition();
  }

  /**
   * universal temporary name prefix. TODO: these same methods are in sqlstore DbUtils; use those when
   * refactoring away the ProjectService.
   */
  static final private String TEMPORARY_NAME_PREFIX = "TEMPORARY_";

  /**
   * Generate a temporary name using a UUID.
   * 
   * @return Temporary name
   */
  static public String generateTemporaryName() {
    return TEMPORARY_NAME_PREFIX + UUID.randomUUID();
  }

  /**
   * Check if the nameable object has a temporary name.
   * 
   * @param nameable Nameable object
   * @return
   */
  static public boolean hasTemporaryName(Nameable nameable) {
    return nameable != null && nameable.getName() != null && nameable.getName().startsWith(TEMPORARY_NAME_PREFIX);
  }

  /**
   * Check if the Boxable item has a temporary name
   * 
   * @param boxable Boxable item
   * @return
   */
  static public boolean hasTemporaryAlias(Boxable boxable) {
    return boxable != null && boxable.getAlias() != null && boxable.getAlias().startsWith(TEMPORARY_NAME_PREFIX);
  }

  /**
   * Generates a unique barcode for a Nameable entity, and sets the identificationBarcode property for
   * Boxables and LibraryAliquots.
   * 
   * @param nameable Nameable object
   * @throws IOException
   */
  public static void generateAndSetIdBarcode(Nameable nameable) throws IOException {
    String barcode = null;
    if (nameable instanceof LibraryAliquot && nameable.getName() != null) {
      barcode = nameable.getName();
      if (((LibraryAliquot) nameable).getLibrary() != null
          && ((LibraryAliquot) nameable).getLibrary().getAlias() != null) {
        barcode += "::" + ((LibraryAliquot) nameable).getLibrary().getAlias();
      }
      ((LibraryAliquot) nameable).setIdentificationBarcode(barcode);
    } else if (nameable instanceof Boxable && nameable.getName() != null) {
      barcode = nameable.getName();
      if (((Boxable) nameable).getAlias() != null) {
        barcode += "::" + ((Boxable) nameable).getAlias();
      }
      ((Boxable) nameable).setIdentificationBarcode(barcode);
    } else {
      throw new IOException("Error generating barcode");
    }
  }

  public static void appendSet(StringBuilder target, Collection<String> items, String prefix) {
    if (items.isEmpty())
      return;
    target.append(" ");
    target.append(prefix);
    target.append(": ");
    boolean first = true;
    for (String item : items) {
      if (first) {
        first = false;
      } else {
        target.append(", ");
      }
      target.append(item);
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T deproxify(T from) {
    if (from instanceof HibernateProxy) {
      HibernateProxy proxy = (HibernateProxy) from;
      from = (T) proxy.getHibernateLazyInitializer().getImplementation();
    }
    return from;
  }

  public static String[] prefix(String prefix, String... suffixes) {
    String[] results = new String[suffixes.length];
    for (int i = 0; i < suffixes.length; i++) {
      results[i] = prefix + suffixes[i];
    }
    return results;
  }

  /**
   * Switches from a representation of date without time or a time zone to one that contains
   * milliseconds from epoch as interpreted from some place with a time zone. No good will come of
   * this. Turn back now.
   * 
   * @param localDate A date without time.
   * @param timezone A time zone. We'll calculate milliseconds from the epoch from this particular
   *        time zone.
   * @return Milliseconds from the epoch.
   */
  public static Date toBadDate(LocalDate localDate, ZoneId timezone) {
    return localDate == null ? null : Date.from(localDate.atStartOfDay(timezone).toInstant());
  }

  public static Date toBadDate(LocalDate localDate) {
    return toBadDate(localDate, ZoneId.systemDefault());
  }

  public static Date toBadDate(LocalDateTime localDate) {
    return localDate == null ? null : toBadDate(localDate.toLocalDate());
  }

  public static Date toBadDate(Instant instant) {
    return instant == null ? null : toBadDate(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate());
  }

  public static <T> Predicate<T> rejectUntil(Predicate<T> check) {
    return new Predicate<>() {
      private boolean state = false;

      @Override
      public boolean test(T t) {
        if (state) {
          return true;
        }
        state = check.test(t);
        return false;
      }

    };
  }

  @SafeVarargs
  public static Set<String> concatSets(Set<String>... sets) {
    return Arrays.stream(sets).flatMap(Collection::stream).collect(Collectors.toSet());
  }

  public static String makeConcentrationLabel(BigDecimal concentration, ConcentrationUnit concentrationUnits) {
    String strConcentrationUnits = (concentrationUnits == null ? "" : concentrationUnits.getUnits());
    if (concentration != null && concentration.compareTo(BigDecimal.ZERO) != 0) {
      return String.format(makeFormatString(concentration), concentration, strConcentrationUnits);
    }
    return null;
  }

  @VisibleForTesting
  protected static String makeFormatString(BigDecimal value) {
    return "%." + getDecimalPlacesToDisplay(value) + "f%s";
  }

  @VisibleForTesting
  protected static int getDecimalPlacesToDisplay(BigDecimal value) {
    if (value == null) {
      return 0;
    }
    return value.abs().compareTo(BigDecimal.TEN) >= 0 ? 0 : 1;
  }

  public static String joinWithConjunction(Collection<String> words, String conjunction) {
    switch (words.size()) {
      case 0:
        throw new IllegalArgumentException("No words provided");
      case 1:
        return words.iterator().next();
      case 2:
        Iterator<String> iterator = words.iterator();
        return iterator.next() + " " + conjunction + " " + iterator.next();
      default:
        List<String> wordsCopy = new ArrayList<>(words);
        wordsCopy.set(wordsCopy.size() - 1, conjunction + " " + wordsCopy.get(wordsCopy.size() - 1));
        return String.join(", ", wordsCopy);
    }
  }

  /**
   * Given a bunch of strings, find the long substring that matches all of them that doesn't end in
   * numbers or underscores.
   */
  public static String findCommonPrefix(String[] str) {
    StringBuilder commonPrefix = new StringBuilder();

    while (commonPrefix.length() < str[0].length()) {
      char current = str[0].charAt(commonPrefix.length());
      boolean matches = true;
      for (int i = 1; matches && i < str.length; i++) {
        if (str[i].charAt(commonPrefix.length()) != current) {
          matches = false;
        }
      }
      if (matches) {
        commonPrefix.append(current);
      } else {
        break;
      }
    }
    // Chew back any digits at the end
    while (commonPrefix.length() > 0 && Character.isDigit(commonPrefix.charAt(commonPrefix.length() - 1))) {
      commonPrefix.setLength(commonPrefix.length() - 1);
    }
    if (commonPrefix.length() > 0 && commonPrefix.charAt(commonPrefix.length() - 1) == '_') {
      commonPrefix.setLength(commonPrefix.length() - 1);
    }
    return (commonPrefix.length() > 0) ? commonPrefix.toString() : null;

  }

  /**
   * Update the volume of the entity's parent based on the entity's volumeUsed property
   * 
   * @param entity the child entity
   * @param beforeChange the child entity before the current change; null if the child entity is just
   *        being created
   */
  public static void updateParentVolume(HierarchyEntity entity, HierarchyEntity beforeChange, User changeUser) {
    HierarchyEntity parent = entity.getParent();
    if (parent == null || parent.getVolume() == null) {
      return;
    }
    if (beforeChange == null) {
      if (entity.getVolumeUsed() != null) {
        updateParentVolume(parent, parent.getVolume().subtract(entity.getVolumeUsed()), changeUser);
      }
    } else {
      if (entity.getVolumeUsed() != null && beforeChange.getVolumeUsed() != null) {
        if (entity.getVolumeUsed().compareTo(beforeChange.getVolumeUsed()) != 0) {
          updateParentVolume(parent,
              parent.getVolume().add(beforeChange.getVolumeUsed()).subtract(entity.getVolumeUsed()), changeUser);
        }
      } else if (beforeChange.getVolumeUsed() != null) {
        updateParentVolume(parent, parent.getVolume().add(beforeChange.getVolumeUsed()), changeUser);
      } else if (entity.getVolumeUsed() != null) {
        updateParentVolume(parent, parent.getVolume().subtract(entity.getVolumeUsed()), changeUser);
      }
    }
  }

  private static void updateParentVolume(HierarchyEntity parent, BigDecimal value, User changeUser) {
    parent.setChangeDetails(changeUser);
    parent.setVolume(value.min(MAX_VOLUME).max(MIN_VOLUME));
  }

  @SafeVarargs
  public static <T> boolean equals(T item, Object other, Function<T, Object>... getters) {
    if (item == other)
      return true;
    if (other == null)
      return false;
    if (item.getClass() != other.getClass())
      return false;

    @SuppressWarnings("unchecked")
    T castedOther = (T) other;

    return allEquals(item, castedOther, getters);
  }

  @SafeVarargs
  public static <T extends Identifiable> boolean equalsByIdFirst(T item, Object other, Function<T, Object>... getters) {
    if (item == other)
      return true;
    if (other == null)
      return false;
    if (item.getClass() != other.getClass())
      return false;

    @SuppressWarnings("unchecked")
    T castedOther = (T) other;

    if (item.isSaved()) {
      return item.getId() == castedOther.getId();
    }
    return allEquals(item, castedOther, getters);
  }

  @SafeVarargs
  private static <T> boolean allEquals(T item, T other, Function<T, Object>... getters) {
    for (Function<T, Object> getter : getters) {
      if (!Objects.equals(getter.apply(item), getter.apply(other))) {
        return false;
      }
    }
    return true;
  }

  public static <T extends Identifiable> int hashCodeByIdFirst(T item, Object... fields) {
    if (item.isSaved()) {
      return (int) item.getId();
    } else {
      return Objects.hash(fields);
    }
  }

  public static String getLongestIndex(Collection<? extends IndexedLibrary> libraries) {
    int index1Max = getLongestIndex(libraries, IndexedLibrary::getIndex1);
    int index2Max = getLongestIndex(libraries, IndexedLibrary::getIndex2);
    if (index2Max > 0) {
      return index1Max + "," + index2Max;
    } else {
      return String.valueOf(index1Max);
    }
  }

  public static int getLongestIndex(Collection<? extends IndexedLibrary> libraries,
      Function<IndexedLibrary, Index> getIndex) {
    return libraries.stream()
        .map(getIndex)
        .filter(Objects::nonNull)
        .mapToInt(index -> index.getSequence().length())
        .max()
        .orElse(0);
  }

}

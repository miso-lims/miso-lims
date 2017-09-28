/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.util;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.function.Predicate;

import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaglegenomics.simlims.core.SecurityProfile;

import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.IlluminaRun;
import uk.ac.bbsrc.tgac.miso.core.data.LS454Run;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.PacBioRun;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.SolidRun;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;

/**
 * Utility class to provde helpful functions to MISO
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public class LimsUtils {

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

  /**
   * Checks that a directory exists. This method will attempt to create the directory if it doesn't exist and if the attemptMkdir flag is
   * true
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

  public static void inheritUsersAndGroups(SecurableByProfile child, SecurityProfile parentProfile) {
    SecurityProfile childProfile = child.getSecurityProfile();
    childProfile.setReadGroups(parentProfile.getReadGroups());
    childProfile.setWriteGroups(parentProfile.getWriteGroups());
    childProfile.setReadUsers(parentProfile.getReadUsers());
    childProfile.setWriteUsers(parentProfile.getWriteUsers());
  }

  public static boolean isDetailedSample(Sample sample) {
    return sample instanceof DetailedSample;
  }

  public static boolean isPlainSample(Sample sample) {
    return !isDetailedSample(sample);
  }

  private static boolean safeCategoryCheck(Sample sample, String category) {
    DetailedSample detailedSample = (DetailedSample) sample;
    if (detailedSample.getSampleClass() == null) return false;
    return category.equals(detailedSample.getSampleClass().getSampleCategory());
  }

  public static boolean isIdentitySample(Sample sample) {
    if (!isDetailedSample(sample)) return false;
    return sample instanceof SampleIdentity || safeCategoryCheck(sample, SampleIdentity.CATEGORY_NAME);
  }

  public static boolean isTissueSample(Sample sample) {
    if (!isDetailedSample(sample)) return false;
    return sample instanceof SampleTissue || safeCategoryCheck(sample, SampleTissue.CATEGORY_NAME);
  }

  public static boolean isTissueProcessingSample(Sample sample) {
    if (!isDetailedSample(sample)) return false;
    return sample instanceof SampleTissueProcessing || safeCategoryCheck(sample, SampleTissueProcessing.CATEGORY_NAME);
  }

  public static boolean isStockSample(Sample sample) {
    return sample instanceof SampleStock || safeCategoryCheck(sample, SampleStock.CATEGORY_NAME);
  }

  public static boolean isAliquotSample(Sample sample) {
    return sample instanceof SampleAliquot || safeCategoryCheck(sample, SampleAliquot.CATEGORY_NAME);
  }

  public static boolean isDetailedLibrary(Library library) {
    return library instanceof DetailedLibrary;
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

  public static <T extends DetailedSample> T getParent(Class<T> targetParentClass, DetailedSample start) {
    for (DetailedSample current = deproxify(start.getParent()); current != null; current = deproxify(current.getParent())) {
      if (targetParentClass.isInstance(current)) {
        @SuppressWarnings("unchecked")
        T result = (T) current;
        return result;
      }
    }
    return null;
  }

  public static void validateNameOrThrow(Nameable object, NamingScheme namingScheme) throws IOException {
    ValidationResult val = namingScheme.validateName(object.getName());
    if (!val.isValid()) throw new IOException("Save failed - invalid name:" + val.getMessage());
  }

  /**
   * universal temporary name prefix. TODO: these same methods are in sqlstore DbUtils;
   * use those when refactoring away the RequestManager.
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
   * Generates a unique barcode for a Nameable entity, and sets the identificationBarcode property for Boxables and LibraryDilutions.
   * 
   * @param nameable Nameable object
   * @throws IOException
   */
  public static void generateAndSetIdBarcode(Nameable nameable) throws IOException {
    String barcode = null;
    if (nameable instanceof LibraryDilution && nameable.getName() != null) {
      barcode = nameable.getName();
      if (((LibraryDilution) nameable).getLibrary() != null
          && ((LibraryDilution) nameable).getLibrary().getAlias() != null) {
        barcode += "::" + ((LibraryDilution) nameable).getLibrary().getAlias();
      }
      ((LibraryDilution) nameable).setIdentificationBarcode(barcode);
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
    if (items.isEmpty()) return;
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
   * Switches from a representation of date without time or a time zone to one that contains milliseconds from epoch as interpreted from
   * some place with a time zone. No good will come of this. Turn back now.
   * 
   * @param localDate A date without time.
   * @param timezone A time zone. We'll calculate milliseconds from the epoch from this particular time zone.
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

  public static <T> Predicate<T> rejectUntil(Predicate<T> check) {
    return new Predicate<T>() {
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
}

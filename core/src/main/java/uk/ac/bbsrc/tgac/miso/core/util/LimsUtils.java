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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

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
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;
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
  public static final long SYSTEM_USER_ID = 0;

  private static final Logger log = LoggerFactory.getLogger(LimsUtils.class);

  public static String unicodeify(String barcode) {
    log.debug("ORIGINAL :: " + barcode);
    StringBuilder b = new StringBuilder();
    int count = 0;
    for (Character c : barcode.toCharArray()) {
      if (Character.UnicodeBlock.of(c) != Character.UnicodeBlock.BASIC_LATIN) {
        int codePoint = Character.codePointAt(barcode, count);
        b.append("[U:$").append(String.format("%04x", codePoint).toUpperCase()).append("]");
      } else {
        b.append(c);
      }
      count++;
    }
    log.debug("UNICODED :: " + b.toString());
    return b.toString();
  }

  public static boolean isUrlValid(URL url) {
    try {
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("HEAD");
      int responseCode = connection.getResponseCode();
      return (responseCode == 200);
    } catch (IOException e) {
      log.error("is URL valid", e);
    }
    return false;
  }

  public static boolean isUrlValid(URI uri) {
    try {
      URL url = uri.toURL();
      return isUrlValid(url);
    } catch (Exception e) {
      log.error("is URL valid", e);
    }
    return false;
  }

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
   * Join a collection, akin to Perl's join(), using a given delimiter to produce a single String
   * 
   * @param s of type Collection
   * @param delimiter of type String
   * @return String
   * @throws IllegalArgumentException
   */
  public static String join(Iterable<?> s, String delimiter) throws IllegalArgumentException {
    if (s == null) {
      throw new IllegalArgumentException("Collection to join must not be null");
    }
    StringBuffer buffer = new StringBuffer();
    boolean first = true;
    for (Object o : s) {
      if (first) {
        first = false;
      } else {
        buffer.append(delimiter);
      }
      buffer.append(o);
    }
    return buffer.toString();
  }

  /**
   * Join an Array, akin to Perl's join(), using a given delimiter to produce a single String
   * 
   * @param s of type Object[]
   * @param delimiter of type String
   * @return String
   */
  public static String join(Object[] s, String delimiter) {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < s.length; i++) {
      buffer.append(s[i]);
      if (i < s.length - 1) {
        buffer.append(delimiter);
      }
    }
    return buffer.toString();
  }

  public static String findHyperlinks(String text) {
    if (!LimsUtils.isStringEmptyOrNull(text)) {
      Pattern p = Pattern.compile(
          "(?i)\\b((?:[a-z][\\w-]+:(?:/{1,3}|[a-z0-9%])|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:'\".,<>?«»“”‘’]))");
      Matcher m = p.matcher(text);

      StringBuffer sb = new StringBuffer();
      while (m.find()) {
        m.appendReplacement(sb, "<a href='$0'>$0</a>");
      }
      m.appendTail(sb);
      return sb.toString();
    } else {
      return "";
    }
  }

  public static String lookupLocation(String locationBarcode) {
    // TODO - proper lookup!
    /*
     * if (locationBarcode is valid) { retrieve text representation of location and return } else { return null; }
     */
    return locationBarcode;
  }

  public static boolean unzipFile(File source, File destination) {
    final int BUFFER = 2048;

    try {
      BufferedOutputStream dest = null;
      FileInputStream fis = new FileInputStream(source);
      ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        File outputFile = null;

        if (destination != null && destination.exists() && destination.isDirectory()) {
          outputFile = new File(destination, entry.getName());
        } else {
          outputFile = new File(entry.getName());
        }

        if (entry.isDirectory()) {
          log.info("Extracting directory: " + entry.getName());
          LimsUtils.checkDirectory(outputFile, true);
        } else {
          log.info("Extracting file: " + entry.getName());
          int count;
          byte data[] = new byte[BUFFER];
          FileOutputStream fos = new FileOutputStream(outputFile);
          dest = new BufferedOutputStream(fos, BUFFER);
          while ((count = zis.read(data, 0, BUFFER)) != -1) {
            dest.write(data, 0, count);
          }
          dest.flush();
          dest.close();
        }
      }
      zis.close();
    } catch (Exception e) {
      log.error("location lookup", e);
      return false;
    }
    return true;
  }

  public static void zipFiles(Set<File> files, File outpath) throws IOException {
    // Create a buffer for reading the files
    byte[] buf = new byte[1024];

    ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outpath));

    // Compress the files
    for (File f : files) {
      FileInputStream in = new FileInputStream(f);

      // Add ZIP entry to output stream.
      out.putNextEntry(new ZipEntry(f.getName()));

      // Transfer bytes from the file to the ZIP file
      int len;
      while ((len = in.read(buf)) > 0) {
        out.write(buf, 0, len);
      }

      // Complete the entry
      out.closeEntry();
      in.close();
    }

    // Complete the ZIP file
    out.close();
  }

  public static void writeFile(InputStream in, File path) throws IOException {
    OutputStream out = null;
    try {
      out = new FileOutputStream(path);
      try {
        byte[] buf = new byte[16884];
        int len;
        while ((len = in.read(buf)) > 0) {
          out.write(buf, 0, len);
        }
      } catch (IOException e) {
        log.error("Could not write file: " + path.getAbsolutePath(), e);
      } finally {
        try {
          in.close();
        } catch (IOException e) {
          // ignore
        }
      }
    } finally {
      if (out != null) {
        out.close();
      }
    }
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

  /**
   * Similar to checkDirectory, but for single files.
   * 
   * @param path of type File
   * @return boolean true if the file exists, false if not
   * @throws IOException when the file doesn't exist
   */
  public static boolean checkFile(File path) throws IOException {
    boolean storageOk = path.exists();
    if (!storageOk) {
      StringBuilder sb = new StringBuilder("The file [" + path.toString() + "] doesn't exist.");
      throw new IOException(sb.toString());
    } else {
      log.info("File (" + path + ") OK.");
    }
    return storageOk;
  }

  /**
   * Helper method to parse and store output from a given process' stdout and stderr
   * 
   * @param process of type Process
   * @return Map<String, String>
   * @throws IOException when
   */
  static Map<String, String> checkPipes(Process process) throws IOException {
    HashMap<String, String> r = new HashMap<>();
    String error = LimsUtils.processStdErr(process);
    if (isStringEmptyOrNull(error)) {
      String out = LimsUtils.processStdOut(process);
      log.debug(out);
      r.put("ok", out);
    } else {
      log.error(error);
      r.put("error", error);
    }
    return r;
  }

  /**
   * Reads the contents of an InputStream into a String
   * 
   * @param in of type InputStream
   * @return String
   * @throws IOException when
   */
  public static String inputStreamToString(InputStream in) throws IOException {
    StringBuilder sb = new StringBuilder();
    String line;
    BufferedReader br = new BufferedReader(new InputStreamReader(in));
    while ((line = br.readLine()) != null) {
      sb.append(line);
    }
    return sb.toString();
  }

  /**
   * Reads the contents of an File into a String
   * 
   * @param f of type File
   * @return String
   * @throws IOException when
   */
  public static String fileToString(File f) throws IOException {
    StringBuilder sb = new StringBuilder();
    String line;
    BufferedReader br = new BufferedReader(new FileReader(f));
    while ((line = br.readLine()) != null) {
      sb.append(line);
    }
    br.close();
    return sb.toString();
  }

  public static File stringToFile(String s, File f) throws IOException {
    PrintWriter p = new PrintWriter(f);
    p.println(s);
    safeClose(p);
    return f;
  }

  /**
   * Process stdout from a given Process and concat it to a single String
   * 
   * @param p of type Process
   * @return String
   * @throws IOException when
   */
  private static String processStdOut(Process p) throws IOException {
    return inputStreamToString(p.getInputStream());
  }

  /**
   * Process stderr from a given Process and concat it to a single String
   * 
   * @param p of type Process
   * @return String
   * @throws IOException when
   */
  private static String processStdErr(Process p) throws IOException {
    return inputStreamToString(p.getErrorStream());
  }

  public static String getCurrentDateAsString(DateFormat df) {
    return df.format(new Date());
  }

  public static String getCurrentDateAsString() {
    return getCurrentDateAsString(new SimpleDateFormat("yyyyMMdd"));
  }

  public static String getDateAsString(Date date) {
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    return df.format(date);
  }

  private static final Pattern linePattern = Pattern.compile(".*\r?\n");

  private static Matcher grep(CharBuffer cb, Pattern pattern) {
    Matcher lm = linePattern.matcher(cb); // Line matcher
    Matcher pm = null; // Pattern matcher
    while (lm.find()) {
      CharSequence cs = lm.group(); // The current line
      if (pm == null)
        pm = pattern.matcher(cs);
      else
        pm.reset(cs);
      if (pm.find()) {
        return pm;
      }
      if (lm.end() == cb.limit()) break;
    }
    return null;
  }

  public static Matcher tailGrep(File f, Pattern p, int lines) throws IOException, FileNotFoundException {
    // Open the file and then get a channel from the stream
    FileInputStream fis = new FileInputStream(f);
    FileChannel fc = fis.getChannel();
    try {
      // Get the file's size and then map it into memory
      int sz = (int) fc.size();
      MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, sz);

      long cnt = 0;
      long i = 0;
      for (i = sz - 1; i >= 0; i--) {
        if (bb.get((int) i) == '\n') {
          cnt++;
          if (cnt == lines + 1) break;
        }
      }

      fis.close();
      int offset = (int) i + 1;

      if (offset >= bb.limit()) throw new NoSuchElementException();
      ByteArrayOutputStream sb = new ByteArrayOutputStream();
      while (offset < bb.limit()) {
        for (; offset < bb.limit(); offset++) {
          sb.write(bb.get(offset));
        }
      }

      // Decode the file into a char buffer
      CharBuffer cb = CharBuffer.wrap(sb.toString());

      // Perform the search
      return grep(cb, p);
    } catch (IOException e) {
      throw e;
    } finally {
      // Close the channel and the stream
      safeClose(fc);
    }
  }

  // put this anywhere you like in your common code.
  public static void safeClose(Closeable c) {
    try {
      c.close();
    } catch (Throwable t) {
      log.error("safe close", t);
    }
  }

  public static String capitalise(String s) {
    return Character.toUpperCase(s.charAt(0)) + s.substring(1);
  }

  public static void inheritUsersAndGroups(SecurableByProfile child, SecurityProfile parentProfile) {
    SecurityProfile childProfile = child.getSecurityProfile();
    childProfile.setReadGroups(parentProfile.getReadGroups());
    childProfile.setWriteGroups(parentProfile.getWriteGroups());
    childProfile.setReadUsers(parentProfile.getReadUsers());
    childProfile.setWriteUsers(parentProfile.getWriteUsers());
  }

  public static String getSimpleCurrentDate() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    return sdf.format(new Date());
  }

  public static boolean isValidRelationship(Iterable<SampleValidRelationship> relations, Sample parent, Sample child) {
    if (parent == null && !isDetailedSample(child)) {
      return true; // Simple sample has no relationships.
    }
    if (!isDetailedSample(child) || !isDetailedSample(parent)) {
      return false;
    }
    return isValidRelationship(
        relations,
        ((DetailedSample) parent).getSampleClass(),
        ((DetailedSample) child).getSampleClass());
  }

  private static boolean isValidRelationship(Iterable<SampleValidRelationship> relations, SampleClass parent, SampleClass child) {
    for (SampleValidRelationship relation : relations) {
      if (relation.getParent().getId() == parent.getId() && relation.getChild().getId() == child.getId()) {
        return true;
      }
    }
    return false;
  }

  public static double round(double value, int numberOfDigitsAfterDecimalPoint) {
    BigDecimal bigDecimal = new BigDecimal(value);
    bigDecimal = bigDecimal.setScale(numberOfDigitsAfterDecimalPoint, BigDecimal.ROUND_HALF_UP);
    return bigDecimal.doubleValue();
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

  public static boolean hasStockParent(Long id, Iterable<SampleValidRelationship> relationships) {
    for (SampleValidRelationship relationship : relationships) {
      if (!relationship.getArchived() && relationship.getChild().getId() == id
          && relationship.getParent().getSampleCategory().equals(SampleStock.CATEGORY_NAME)) {
        return true;
      }
    }
    return false;
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

  public static void appendSet(StringBuilder target, Set<String> items, String prefix) {
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
    return Date.from(localDate.atStartOfDay(timezone).toInstant());
  }
  
  public static Date toBadDate(LocalDate localDate) {
    return toBadDate(localDate, ZoneId.systemDefault());
  }

}

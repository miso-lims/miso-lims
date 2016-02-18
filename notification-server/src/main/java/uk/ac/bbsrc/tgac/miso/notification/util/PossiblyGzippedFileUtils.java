package uk.ac.bbsrc.tgac.miso.notification.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;

import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.SubmissionUtils;

public class PossiblyGzippedFileUtils {

  private static final String GZIP_EXTENSION = ".gz";

  /**
   * Creates a File representing a gzipped version of the file with the name specified. The extension ".gz" is appended to the provided path
   * 
   * @param rootFile
   *          the file's parent directory
   * @param path
   *          file path relative to rootFile
   * @return a File representing the path rootFile/path + ".gz"
   */
  private static File getGzippedFile(File rootFile, String path) {
    return new File(rootFile, path + GZIP_EXTENSION);
  }

  /**
   * Checks a File's name for the .gz extension indicating Gzipped
   * 
   * @param file
   *          the file to check
   * @return true if the filename ends with ".gz"; false otherwise
   */
  public static boolean isGzipped(File file) {
    return file.getName().endsWith(GZIP_EXTENSION);
  }

  /**
   * Checks for the existance of a file either by the name specified, or the same name suffixed with ".gz"
   * 
   * @param rootFile
   *          the file's parent directory
   * @param path
   *          file path relative to rootFile
   * @return true if a file of the exact name specified, or the same name suffixed with ".gz" exists; false otherwise
   */
  public static boolean checkExists(File rootFile, String path) {
    return new File(rootFile, path).exists() || getGzippedFile(rootFile, path).exists();
  }

  /**
   * Checks for the existance and readability of a file either by the name specified, or the same name suffixed with ".gz"
   * 
   * @param rootFile
   *          the file's parent directory
   * @param path
   *          file path relative to rootFile
   * @return true if a file of the exact name specified, or the same name suffixed with ".gz" exists and is readable; false otherwise
   */
  public static boolean checkExistsReadable(File rootFile, String path) {
    File file = new File(rootFile, path);
    File gzipped = getGzippedFile(rootFile, path);
    return (file.exists() && file.canRead()) || (gzipped.exists() && gzipped.canRead());
  }

  /**
   * Checks whether a raw or gzipped XML file exists and is readable, and if so, creates a Document for reading it. This method only checks
   * the file it is given. To also check for an alternate raw/gzipped XML file, see the {@link #getXmlDocument(File, String)} method
   * 
   * @param file
   *          the file to read
   * @return the Document if the file is readable; null otherwise
   * @throws IOException
   * @throws ParserConfigurationException
   * @throws TransformerException
   */
  public static Document getXmlDocument(File file) throws IOException, ParserConfigurationException, TransformerException {
    if (isGzipped(file))
      return getXmlDocumentGzipped(file);
    else
      return getXmlDocumentUncompressed(file);
  }

  /**
   * Checks whether an XML file, or a gzipped file of the same name suffixed with ".gz" exists and is readable, and if so, creates a
   * Document for reading it.
   * 
   * @param file
   *          the file to read
   * @param checkGzipped
   *          if true, and the file to read does not exist, this method will look for a gzipped file of the same name plus the extension
   *          .gz, and read that file instead if it exists
   * @return The Document if the file is readable; null otherwise
   * @throws ParserConfigurationException
   * @throws TransformerException
   * @throws IOException
   */
  public static Document getXmlDocument(File rootFile, String path) throws ParserConfigurationException, TransformerException, IOException {
    File file = new File(rootFile, path);
    if (file.exists()) {
      if (!file.canRead())
        return null;
      else
        return getXmlDocumentUncompressed(file);
    }

    // Raw file doesn't exist. Check for Gzipped
    file = PossiblyGzippedFileUtils.getGzippedFile(rootFile, path);
    if (!file.exists() || !file.canRead())
      return null;
    else
      return getXmlDocumentGzipped(file);
  }

  private static Document getXmlDocumentUncompressed(File file) throws ParserConfigurationException, TransformerException, IOException {
    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    SubmissionUtils.transform(file, doc);
    return doc;
  }

  private static Document getXmlDocumentGzipped(File file) throws IOException, ParserConfigurationException, TransformerException {
    try (InputStream is = new FileInputStream(file); GZIPInputStream gs = new GZIPInputStream(is)) {

      Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
      SubmissionUtils.transform(gs, doc);
      return doc;
    }
  }

  /**
   * Greps the tail of a raw or gzipped text file, attempting to match a pattern. This method only checks the file it is given. To also
   * check for an alternate raw/gzipped file, see the {@link #tailGrep(File, String, Pattern, int)} method
   * 
   * @param file
   *          the file to grep through
   * @param pattern
   *          the pattern to match
   * @param lines
   *          number of lines at the end of the file to check
   * @return the matcher if a match is found; null otherwise
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static Matcher tailGrep(File file, Pattern pattern, int lines) throws FileNotFoundException, IOException {
    if (PossiblyGzippedFileUtils.isGzipped(file))
      return PossiblyGzippedFileUtils.tailGrepGzipped(file, pattern, lines);
    else
      return LimsUtils.tailGrep(file, pattern, lines);
  }

  /**
   * Greps the tail of a raw or gzipped text file, attempting to match a pattern.
   * 
   * @param rootFile
   *          the file's parent directory
   * @param path
   *          file path relative to rootFile
   * @param pattern
   *          the pattern to match
   * @param lines
   *          number of lines at the end of the file to check
   * @return the matcher if a match is found; null otherwise
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static Matcher tailGrep(File rootFile, String path, Pattern pattern, int lines) throws FileNotFoundException, IOException {
    File file = new File(rootFile, path);
    if (!path.endsWith(GZIP_EXTENSION)) {
      if (file.exists()) return LimsUtils.tailGrep(file, pattern, lines);
      // Uncompressed file not found
      file = getGzippedFile(rootFile, path);
    }
    return tailGrepGzipped(file, pattern, lines);
  }

  private static Matcher tailGrepGzipped(File file, Pattern pattern, int lines) throws FileNotFoundException, IOException {
    try (InputStream is = new FileInputStream(file);
        InputStream gis = new GZIPInputStream(is);
        Reader r = new InputStreamReader(gis);
        BufferedReader br = new BufferedReader(r)) {
      String[] lineText = new String[lines];

      // Find only the last lines
      int i = 0;
      boolean done = false;
      while (!done) {
        String text = br.readLine();
        if (text == null)
          done = true;
        else {
          lineText[i] = text;
          i++;
          if (i == lineText.length) i = 0;
        }
      }

      // Look for the first instance of the pattern in these lines
      int start = i;
      done = false;
      Matcher m = null;
      while (!done) {
        if (lineText[i] != null) { // may be less lines in file than requested
          if (m == null)
            m = pattern.matcher(lineText[i]);
          else
            m.reset(lineText[i]);
          if (m.find()) {
            return m;
          }
        }
        i++;
        if (i == lineText.length) i = 0;
        if (i == start) done = true;
      }
    }
    return null;
  }

}

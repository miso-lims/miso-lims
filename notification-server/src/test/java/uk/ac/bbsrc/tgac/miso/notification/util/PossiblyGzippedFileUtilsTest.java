package uk.ac.bbsrc.tgac.miso.notification.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.Test;
import org.w3c.dom.Document;

public class PossiblyGzippedFileUtilsTest {

  private static final String h1080_84_raw = "/runs/111110_h1080_0084_AC08UPACXX_raw";
  private static final String h1080_84_gzip = "/runs/111110_h1080_0084_AC08UPACXX_raw";

  private static final String runInfo = "RunInfo.xml";
  private static final String cycleTimes = "/Logs/CycleTimes.txt";

  private URI getResourcePath(String path) throws URISyntaxException {
    return this.getClass().getResource(path).toURI();
  }

  private File getResourceFile(String path) throws URISyntaxException {
    return new File(getResourcePath(path));
  }

  @Test
  public void testDirectoryExists() throws URISyntaxException {
    assertTrue(PossiblyGzippedFileUtils.checkExists(new File(getResourcePath("/")), h1080_84_raw));
  }

  @Test
  public void testRawFileExists() throws URISyntaxException {
    assertTrue(PossiblyGzippedFileUtils.checkExists(getResourceFile(h1080_84_raw), runInfo));
  }

  @Test
  public void testGzippedFileExists() throws URISyntaxException {
    assertTrue(PossiblyGzippedFileUtils.checkExists(getResourceFile(h1080_84_gzip), runInfo));
  }

  @Test
  public void testFileDoesntExist() throws URISyntaxException {
    assertFalse(PossiblyGzippedFileUtils.checkExists(null, "NotARealFile"));
  }

  @Test
  public void testRawFileReadable() throws URISyntaxException {
    assertTrue(PossiblyGzippedFileUtils.checkExistsReadable(new File(getResourcePath(h1080_84_raw)), runInfo));
  }

  @Test
  public void testGzippedFileReadable() throws URISyntaxException {
    assertTrue(PossiblyGzippedFileUtils.checkExistsReadable(new File(getResourcePath(h1080_84_gzip)), runInfo));
  }

  @Test
  public void testRawXmlRead() throws ParserConfigurationException, TransformerException, IOException, URISyntaxException {
    Document xml = PossiblyGzippedFileUtils.getXmlDocument(getResourceFile(h1080_84_raw), runInfo);
    assertTrue("SN1080".equals(xml.getElementsByTagName("Instrument").item(0).getTextContent()));
  }

  @Test
  public void testGzippedXmlRead() throws ParserConfigurationException, TransformerException, IOException, URISyntaxException {
    Document xml = PossiblyGzippedFileUtils.getXmlDocument(getResourceFile(h1080_84_gzip), runInfo);
    assertTrue("SN1080".equals(xml.getElementsByTagName("Instrument").item(0).getTextContent()));
  }

  @Test
  public void testRawTextTailGrep() throws FileNotFoundException, IOException, URISyntaxException {
    Pattern p = Pattern
        .compile("(\\d{1,2}\\/\\d{1,2}\\/\\d{4})\\s+(\\d{2}:\\d{2}:\\d{2})\\.\\d{3}\\s+[A-z0-9]+\\s+202\\s+End\\s{1}Imaging");
    Matcher m = PossiblyGzippedFileUtils.tailGrep(getResourceFile(h1080_84_raw), cycleTimes, p, 10);
    assertFalse(m == null);
    String cycleDateStr = m.group(1) + "," + m.group(2);
    assertTrue("11/19/2011,15:24:57".equals(cycleDateStr));
  }

  @Test
  public void testGzippedTextTailGrep() throws FileNotFoundException, IOException, URISyntaxException {
    Pattern p = Pattern
        .compile("(\\d{1,2}\\/\\d{1,2}\\/\\d{4})\\s+(\\d{2}:\\d{2}:\\d{2})\\.\\d{3}\\s+[A-z0-9]+\\s+202\\s+End\\s{1}Imaging");
    Matcher m = PossiblyGzippedFileUtils.tailGrep(getResourceFile(h1080_84_gzip), cycleTimes, p, 10);
    assertFalse(m == null);
    String cycleDateStr = m.group(1) + "," + m.group(2);
    assertTrue("11/19/2011,15:24:57".equals(cycleDateStr));
  }

}

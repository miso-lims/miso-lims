/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.util;

import com.opensymphony.util.XMLUtils;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

/**
 * Utility class to aid the submission of data to repositories
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public class SubmissionUtils {

  /**
   * Creates an empty {@link Document} for use with the other helper methods in this class.
   *
   * @return Document
   * @throws ParserConfigurationException when
   */
  public static Document emptyDocument() throws ParserConfigurationException {
    DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    return docBuilder.newDocument();
  }

  /**
   * Transforms a DOM Document into a file on disk
   *
   * @param fromDocument the Document to use as the source
   * @param toPath       the destination file path represented as a String
   * @throws java.io.IOException
   * @throws javax.xml.transform.TransformerException
   *
   */
  public static void transform(Document fromDocument, File toPath) throws TransformerException, IOException {
    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");

    DOMSource source = new DOMSource(fromDocument);
    StreamResult result = new StreamResult(new BufferedWriter(new FileWriter(toPath)));
    transformer.transform(source, result);
  }

  /**
   * Transforms a DOM Document into a file on disk
   *
   * @param fromDocument       the Document to use as the source
   * @param toPath             the destination file path represented as a String
   * @param omitXmlDeclaration will not write the XML header if true
   * @throws java.io.IOException
   * @throws javax.xml.transform.TransformerException
   *
   */
  public static void transform(Document fromDocument, File toPath, boolean omitXmlDeclaration) throws TransformerException, IOException {
    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    if (omitXmlDeclaration) transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

    DOMSource source = new DOMSource(fromDocument);
    StreamResult result = new StreamResult(new BufferedWriter(new FileWriter(toPath)));
    transformer.transform(source, result);
  }

  /**
   * Transforms a DOM Document into a String
   *
   * @param fromDocument the Document to use as the source
   * @return the XML as a String
   * @throws java.io.IOException
   * @throws javax.xml.transform.TransformerException
   *
   */
  public static String transform(Document fromDocument) throws TransformerException, IOException {
    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");

    StringWriter sw = new StringWriter();
    StreamResult result = new StreamResult(sw);
    DOMSource source = new DOMSource(fromDocument);
    transformer.transform(source, result);
    return sw.toString();
  }

  /**
   * Transforms a DOM Document into a String
   *
   * @param fromDocument       the Document to use as the source
   * @param omitXmlDeclaration will not write the XML header if true
   * @return the XML as a String
   * @throws java.io.IOException
   * @throws javax.xml.transform.TransformerException
   *
   */
  public static String transform(Document fromDocument, boolean omitXmlDeclaration) throws TransformerException, IOException {
    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    if (omitXmlDeclaration) transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

    StringWriter sw = new StringWriter();
    transformer.transform(new DOMSource(fromDocument), new StreamResult(sw));
    return sw.toString();
  }

  public static String transform(File fromPath) throws TransformerException, IOException {
    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");

    Reader reader = bomCheck(fromPath);

    StringWriter sw = new StringWriter();
    transformer.transform(new StreamSource(reader), new StreamResult(sw));
    return sw.toString();
  }

  /**
   * Transforms a File representing a DOM Document into a String
   *
   * @param fromPath           the File object to use as the source
   * @param omitXmlDeclaration will not write the XML header if true
   * @return the XML as a String
   * @throws java.io.IOException
   * @throws javax.xml.transform.TransformerException
   *
   */
  public static String transform(File fromPath, boolean omitXmlDeclaration) throws TransformerException, IOException {
    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    if (omitXmlDeclaration) transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

    Reader reader = bomCheck(fromPath);

    StringWriter sw = new StringWriter();
    transformer.transform(new StreamSource(reader), new StreamResult(sw));
    return sw.toString();
  }

  public static String xslTransform(String fromString, String xslString) throws TransformerException, IOException {
    return XMLUtils.transform(fromString, xslString);
  }

  /**
   * Transforms a file on disk into a DOM Document
   *
   * @param fromPath   the File object to use as the source
   * @param toDocument the destination Document
   * @throws javax.xml.transform.TransformerException
   * @throws java.io.IOException
   */
  public static void transform(File fromPath, Document toDocument) throws TransformerException, IOException {
    Reader reader = bomCheck(fromPath);
    TransformerFactory.newInstance().newTransformer().transform(new StreamSource(reader), new DOMResult(toDocument));
  }

  /**
   * Transforms a streamed XML representation into a DOM Document
   *
   * @param in         the XML fragment as an input stream to use as the source
   * @param toDocument the destination Document
   * @throws javax.xml.transform.TransformerException
   *
   */
  public static void transform(InputStream in, Document toDocument) throws TransformerException {
    Reader reader = new BufferedReader(new InputStreamReader(in));
    try {
      removeBOM(reader);
      TransformerFactory.newInstance().newTransformer().transform(new StreamSource(in), new DOMResult(toDocument));
    }
    catch (Exception e) {
      throw new TransformerException("Cannot remove byte-order-mark from XML file");
    }

  }

  /**
   * Transforms a streamed XML representation into a DOM Document
   *
   * @param reader     the XML fragment as an input stream to use as the source
   * @param toDocument the destination Document
   * @throws javax.xml.transform.TransformerException
   *
   */
  public static void transform(Reader reader, Document toDocument) throws TransformerException {
    try {
      removeBOM(reader);
      TransformerFactory.newInstance().newTransformer().transform(new StreamSource(reader), new DOMResult(toDocument));
    }
    catch (Exception e) {
      throw new TransformerException("Cannot remove byte-order-mark from XML file");
    }
  }

  /**
   * Transforms a string path representing a file on disk into a DOM Document
   *
   * @param fromPath   the file path represented as a String to use as the source
   * @param toDocument the destination Document
   * @throws javax.xml.transform.TransformerException
   * @throws java.io.IOException
   */
  public static void transform(String fromPath, Document toDocument) throws TransformerException, IOException {
    Reader reader = bomCheck(new File(fromPath));
    SubmissionUtils.transform(reader, toDocument);
  }

  /**
   * Parses a string path representing an XML file on disk into a StringWriter
   *
   * @param fromPath the file path represented as a String to use as the source
   * @param toWriter the destination StringWriter
   * @throws javax.xml.transform.TransformerException
   * @throws java.io.IOException
   */
  public static void transform(String fromPath, StringWriter toWriter) throws TransformerException, IOException {
    SubmissionUtils.transform(new File(fromPath), toWriter);
  }

  /**
   * Parses an XML file on disk into a Writer
   *
   * @param fromPath the file to use as the source
   * @param toWriter the destination Writer
   * @throws javax.xml.transform.TransformerException
   * @throws java.io.IOException
   */
  public static void transform(File fromPath, Writer toWriter) throws TransformerException, IOException {
    Reader reader = bomCheck(fromPath);
    TransformerFactory.newInstance().newTransformer().transform(new StreamSource(fromPath), new StreamResult(toWriter));
  }

  /**
   * Parses an XML document into a Writer
   *
   * @param fromDocument the Document to use as the source
   * @param toWriter     the destination Writer
   * @throws javax.xml.transform.TransformerException
   *
   */
  public static void transform(Document fromDocument, Writer toWriter) throws TransformerException {
    TransformerFactory.newInstance().newTransformer().transform(new DOMSource(fromDocument), new StreamResult(toWriter));
  }

  private static char[] UTF32BE = {0x00, 0x00, 0xFE, 0xFF};
  private static char[] UTF32LE = {0xFF, 0xFE, 0x00, 0x00};
  private static char[] UTF16BE = {0xFE, 0xFF};
  private static char[] UTF16LE = {0xFF, 0xFE};
  private static char[] UTF8 = {0xEF, 0xBB, 0xBF, '\uFEFF'};

  private static Reader bomCheck(File fromPath) throws IOException {
    StreamSource bomcheck = new StreamSource(new FileReader(fromPath));
    Reader reader = new BufferedReader(bomcheck.getReader());
    try {
      removeBOM(reader);
      return reader;
    }
    catch (Exception e) {
      throw new IOException("Cannot remove byte-order-mark from XML file");
    }
  }

  private static void removeBOM(Reader reader) throws Exception {
    if (removeBOM(reader, UTF32BE)) {
      return;
    }
    if (removeBOM(reader, UTF32LE)) {
      return;
    }
    if (removeBOM(reader, UTF16BE)) {
      return;
    }
    if (removeBOM(reader, UTF16LE)) {
      return;
    }
    if (removeBOM(reader, UTF8)) {
      return;
    }
  }

  private static boolean removeBOM(Reader reader, char[] bom) throws Exception {
    int bomLength = bom.length;
    reader.mark(bomLength);
    char[] possibleBOM = new char[bomLength];
    reader.read(possibleBOM);
    for (int x = 0; x < bomLength; x++) {
      if (bom[x] != possibleBOM[x]) {
        reader.reset();
        return false;
      }
    }
    return true;
  }
}

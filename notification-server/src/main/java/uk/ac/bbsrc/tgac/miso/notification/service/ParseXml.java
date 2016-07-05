package uk.ac.bbsrc.tgac.miso.notification.service;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

/**
 * Parse an XML document
 */
public class ParseXml extends RunTransform<InputStream, Document> {

  @Override
  protected Document convert(InputStream input, IlluminaRunMessage output) throws Exception {
    Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    TransformerFactory.newInstance().newTransformer().transform(new StreamSource(input), new DOMResult(document));
    return document;
  }

}

package uk.ac.bbsrc.tgac.miso.notification.service;

import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class WriteXml extends RunTransform<Document, String> {

  public static final WriteXml INSTANCE = new WriteXml();

  @Override
  protected String convert(Document input) throws Exception {
    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "no");

    StringWriter sw = new StringWriter();
    DOMSource source = new DOMSource(input);
    transformer.transform(source, new StreamResult(sw));
    return sw.toString();
  }

}

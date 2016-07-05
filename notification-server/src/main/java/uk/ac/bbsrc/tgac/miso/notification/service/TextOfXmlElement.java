package uk.ac.bbsrc.tgac.miso.notification.service;

import org.w3c.dom.Element;

/**
 * Convert the contents of an XML element to a string.
 */
public class TextOfXmlElement extends RunTransform<Element, String> {

  @Override
  protected String convert(Element input, IlluminaRunMessage output) throws Exception {
    return input.getTextContent();
  }

}

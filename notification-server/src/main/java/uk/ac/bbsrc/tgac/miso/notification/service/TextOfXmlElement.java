package uk.ac.bbsrc.tgac.miso.notification.service;

import org.w3c.dom.Element;

public class TextOfXmlElement extends RunTransform<Element, String> {

  @Override
  protected String convert(Element input) throws Exception {
    return input.getTextContent();
  }

}

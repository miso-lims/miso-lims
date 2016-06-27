package uk.ac.bbsrc.tgac.miso.notification.service;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class FindXmlElement extends RunTransform<Document, Element> {
  private final String[] elementNames;
  private final TextOfXmlElement text = new TextOfXmlElement();

  public FindXmlElement(String... elementNames) {
    this.elementNames = elementNames;
    add(text);
  }

  public TextOfXmlElement toText() {
    return text;
  }

  @Override
  public FindXmlElement attachTo(RunTransform<?, Document> parent) {
    parent.add(this);
    return this;
  }

  @Override
  protected Element convert(Document input) throws Exception {
    for (String name : elementNames) {
      NodeList nodes = input.getElementsByTagName(name);
      if (nodes.getLength() > 0) {
        return (Element) nodes.item(0);
      }
    }
    return null;
  }

}

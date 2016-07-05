package uk.ac.bbsrc.tgac.miso.notification.service;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Find XML element by tag name and process the first one found (if any).
 * 
 * If no nodes are found, processing stops.
 */
public class FindXmlElement extends RunTransform<Document, Element> {
  private final String[] elementNames;
  private final TextOfXmlElement text = new TextOfXmlElement();

  /**
   * Search for elements by name. Since the same data is found in different tags in different versions, multiple names can be tried.
   * 
   * @param elementNames
   *          The tag names to try, in sequence.
   */
  public FindXmlElement(String... elementNames) {
    this.elementNames = elementNames;
    add(text);
  }

  /**
   * Get the text content of the matched element as a string.
   */
  public TextOfXmlElement toText() {
    return text;
  }

  @Override
  public FindXmlElement attachTo(RunTransform<?, Document> parent) {
    parent.add(this);
    return this;
  }

  @Override
  protected Element convert(Document input, IlluminaRunMessage output) throws Exception {
    for (String name : elementNames) {
      NodeList nodes = input.getElementsByTagName(name);
      if (nodes.getLength() > 0) {
        return (Element) nodes.item(0);
      }
    }
    return null;
  }

}

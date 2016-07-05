package uk.ac.bbsrc.tgac.miso.notification.service;

import org.w3c.dom.Element;

/**
 * Get the contents of an attribute
 *
 */
public class GetAttribute extends RunTransform<Element, String> {
  private final String[] attributeNames;

  @Override
  protected String convert(Element input, IlluminaRunMessage output) throws Exception {
    for (String name : attributeNames) {
      if (input.hasAttribute(name)) return input.getAttribute(name);
    }
    return null;
  }

  public GetAttribute(String... attributeNames) {
    super();
    this.attributeNames = attributeNames;
  }

}

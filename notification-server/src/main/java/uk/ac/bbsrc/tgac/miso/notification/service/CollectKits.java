package uk.ac.bbsrc.tgac.miso.notification.service;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Write all the Illumina reagent kits to the output message
 */
class CollectKits extends RunSink<Element> {

  @Override
  public void process(Element input, IlluminaRunMessage output) throws Exception {
    Set<String> rlist = new HashSet<>();
    NodeList kits = input.getElementsByTagName("ID");
    for (int i = 0; i < kits.getLength(); i++) {
      Element e = (Element) kits.item(i);
      for (String r : e.getTextContent().split("[,;]")) {
        if (!isStringEmptyOrNull(r)) rlist.add(r.trim());
      }
    }
    output.setKits(rlist);
  }
}
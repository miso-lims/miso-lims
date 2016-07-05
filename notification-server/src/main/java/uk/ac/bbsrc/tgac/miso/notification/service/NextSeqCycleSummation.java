package uk.ac.bbsrc.tgac.miso.notification.service;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

final class NextSeqCycleSummation extends RunSink<Document> {
  private static final String[] FORMATS = new String[] { "CompletedRead%dCycles", "CompletedIndex%dReadCycles" };

  @Override
  public void process(Document input, IlluminaRunMessage output) throws Exception {
    if (output.getNumReads() == null) return;
    int cycles = 0;
    for (int read = 1; read <= output.getNumReads(); read++) {
      for (String format : FORMATS) {
        NodeList nodes = input.getElementsByTagName(String.format(format, read));
        for (int i = 0; i < nodes.getLength(); i++) {
          cycles += Integer.parseInt(((Element) nodes.item(i)).getTextContent());
        }
      }
    }
    int requiredCycles = output.getNumCycles();
    if (cycles == requiredCycles) {
      output.setSeenLastCycle(true);
    }
  }
}
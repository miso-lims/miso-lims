package uk.ac.bbsrc.tgac.miso.core.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * uk.ac.bbsrc.tgac.miso.core.util
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 29/12/11
 * @since 0.1.4
 */
public class TaxonomyUtils {

  private static final String NCBI_ENTREZ_UTILS_URL =
      "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=taxonomy&term={term}";

  private static final Map<String, String> taxonomyCache = new HashMap<>();

  public static String checkScientificNameAtNCBI(String scientificName) throws IOException {
    if (!taxonomyCache.containsKey(scientificName)) {
      String out = RestClient.create().get().uri(NCBI_ENTREZ_UTILS_URL, scientificName)
          .accept(MediaType.APPLICATION_XML)
          .retrieve()
          .body(String.class);
      try {
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document d = docBuilder.parse(new InputSource(new StringReader(out)));
        NodeList nl = d.getElementsByTagName("Id");
        if (nl.getLength() > 0) {
          taxonomyCache.put(scientificName, nl.item(0).getTextContent());
        }
      } catch (ParserConfigurationException | SAXException | TransformerFactoryConfigurationError e) {
        throw new IOException("Taxon lookup error", e);
      }
    }
    return taxonomyCache.get(scientificName);
  }
}

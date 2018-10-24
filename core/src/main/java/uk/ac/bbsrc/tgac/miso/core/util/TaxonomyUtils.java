/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.util;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

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

  private static final String NCBI_ENTREZ_UTILS_URL = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=taxonomy&term=";

  private static final Map<String, String> taxonomyCache = new HashMap<>();

  public static String checkScientificNameAtNCBI(String scientificName) throws IOException {
    if (!taxonomyCache.containsKey(scientificName)) {
      String query = NCBI_ENTREZ_UTILS_URL + URLEncoder.encode(scientificName, "UTF-8");
      final HttpClient httpclient = HttpClientBuilder.create().build();
      HttpGet httpget = new HttpGet(query);
      HttpResponse response = httpclient.execute(httpget);
      String out = parseEntity(response.getEntity());
      DocumentBuilder docBuilder;
      try {
        docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document d = docBuilder.newDocument();
        TransformerFactory.newInstance().newTransformer().transform(new StreamSource(new UnicodeReader(out)), new DOMResult(d));
        NodeList nl = d.getElementsByTagName("Id");
        if (nl.getLength() > 0) {
          taxonomyCache.put(scientificName, nl.item(0).getTextContent());
        }
      } catch (ParserConfigurationException | TransformerException | TransformerFactoryConfigurationError e) {
        throw new IOException("Taxon lookup error", e);
      }
    }
    return taxonomyCache.get(scientificName);
  }

  private static String parseEntity(HttpEntity entity) throws IOException {
    if (entity != null) {
      return EntityUtils.toString(entity, "UTF-8");
    } else {
      throw new IOException("Null entity in REST response");
    }
  }
}

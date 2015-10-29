/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
  protected static final Logger log = LoggerFactory.getLogger(TaxonomyUtils.class);

  private static final String ncbiEntrezUtilsURL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?";

  public static String checkScientificNameAtNCBI(String scientificName) {
    try {
      String query = ncbiEntrezUtilsURL + "db=taxonomy&term=" + URLEncoder.encode(scientificName, "UTF-8");
      final HttpClient httpclient = new DefaultHttpClient();
      HttpGet httpget = new HttpGet(query);
      try {
        HttpResponse response = httpclient.execute(httpget);
        String out = parseEntity(response.getEntity());
        log.info(out);
        try {
          Document d = SubmissionUtils.emptyDocument();
          SubmissionUtils.transform(new UnicodeReader(out), d);
          NodeList nl = d.getElementsByTagName("Id");
          for (int i = 0; i < nl.getLength(); i++) {
            Element e = (Element) nl.item(i);
            return e.getTextContent();
          }
        } catch (ParserConfigurationException e) {
          log.error("check scientific name at NCBI", e);
        } catch (TransformerException e) {
          log.error("check scientific name at NCBI", e);
        }
      } catch (ClientProtocolException e) {
        log.error("check scientific name at NCBI", e);
      } catch (IOException e) {
        log.error("check scientific name at NCBI", e);
      }
    } catch (UnsupportedEncodingException e) {
      log.error("check scientific name at NCBI", e);
    }
    return null;
  }

  private static String parseEntity(HttpEntity entity) throws IOException {
    if (entity != null) {
      return EntityUtils.toString(entity, "UTF-8");
    } else {
      throw new IOException("Null entity in REST response");
    }
  }
}

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

package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.View;

import com.eaglegenomics.simlims.core.ActivityData;
import com.eaglegenomics.simlims.core.DataReference;

public class InputDataXmlView implements View {
  protected static final Logger log = LoggerFactory.getLogger(InputDataXmlView.class);

  @Override
  public String getContentType() {
    return "text/xml";
  }

  @Override
  @SuppressWarnings("unchecked")
  public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
    Map<String, ActivityData> inputData = (Map<String, ActivityData>) model.get("inputData");
    if (inputData == null) {
      throw new IllegalArgumentException("Can only render Map<String,ActivityData> mapped to key 'inputData'.");
    }

    // Start document with "root" tag
    Document doc = new Document(new Element("inputData"));
    // Get the root tag
    Element rootEl = doc.getRootElement();

    // Add other tags
    for (Map.Entry<String, ActivityData> mapEntry : inputData.entrySet()) {
      String displayName = mapEntry.getKey();
      ActivityData data = mapEntry.getValue();
      // Add new xml element
      Element activityDataEl = new Element("activityData");
      activityDataEl.setAttribute(new Attribute("activityAlias", data.getActivityAlias()));
      activityDataEl.setAttribute(new Attribute("creationDate", data.getCreationDate().toString()));
      activityDataEl.setAttribute(new Attribute("priority", data.getPriority().toString()));
      activityDataEl.setAttribute(new Attribute("uniqueId", "" + data.getUniqueId()));
      activityDataEl.setAttribute(new Attribute("displayName", displayName));
      DataReference dataRef = data.getDataReference();
      Element dataRefEl = new Element("dataReference");
      dataRefEl.setAttribute(new Attribute("referenceClass", dataRef.getReferenceClass().getName()));
      dataRefEl.setAttribute(new Attribute("referenceId", "" + dataRef.getReferenceId()));
      activityDataEl.addContent(dataRefEl);
      for (Map.Entry<String, ActivityData.Entry> indexedEntry : data.getIndexedEntries().entrySet()) {
        String index = indexedEntry.getKey();
        ActivityData.Entry entry = indexedEntry.getValue();
        Element entryEl = new Element("entry");
        entryEl.setAttribute(new Attribute("index", index));
        entryEl.setAttribute(new Attribute("protocol", entry.getProtocol().getUniqueIdentifier()));
        entryEl.setAttribute(new Attribute("request", entry.getRequest().getName()));
        entryEl.setAttribute(new Attribute("executionCount", "" + entry.getExecutionCount()));
        activityDataEl.addContent(entryEl);
      }
      rootEl.addContent(activityDataEl);
    }

    // Set response type and write XML
    XMLOutputter outp = new XMLOutputter();
    outp.setFormat(Format.getPrettyFormat());
    String xmlAsString = outp.outputString(doc);

    if (log.isDebugEnabled()) {
      log.debug("Generating XML response: " + xmlAsString);
    }

    response.setContentType(getContentType());
    response.setContentLength(xmlAsString.length());

    PrintWriter out = new PrintWriter(response.getOutputStream());
    out.print(xmlAsString);
    out.flush();
    out.close();
  }

}

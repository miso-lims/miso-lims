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

package uk.ac.bbsrc.tgac.miso.core.data.impl.solid;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import uk.ac.bbsrc.tgac.miso.core.data.impl.StatusImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.util.SubmissionUtils;
import uk.ac.bbsrc.tgac.miso.core.util.UnicodeReader;


/**
 * uk.ac.bbsrc.tgac.miso.core.data.impl.solid
 * <p/>
 * TODO Info
 * 
 * @author Rob Davey
 * @since 0.0.3
 */
public class SolidStatus extends StatusImpl {
  protected static final Logger log = LoggerFactory.getLogger(SolidStatus.class);
  String statusXml = null;

  public SolidStatus() {
    setHealth(HealthType.Unknown);
  }

  public SolidStatus(String statusXml) {
    this.statusXml = statusXml;
    parseStatusXml(statusXml);
  }

  public void parseStatusXml(String statusXml) {
    try {
      Document statusDoc = SubmissionUtils.emptyDocument();
      SubmissionUtils.transform(new UnicodeReader(statusXml), statusDoc);
      String runDirRegex = "([A-Za-z0-9]+)_([0-9]{8})_(.*)";
      Pattern runRegex = Pattern.compile(runDirRegex);

      if (statusDoc.getDocumentElement().getTagName().equals("error")) {
        String runName = statusDoc.getElementsByTagName("RunName").item(0).getTextContent();
        Matcher m = runRegex.matcher(runName);
        if (m.matches()) {
          setStartDate(new SimpleDateFormat("yyyyMMdd").parse(m.group(2)));
          setInstrumentName(m.group(1));
        }
        setRunName(runName);
        setHealth(HealthType.Unknown);
      } else {
        String runName = statusDoc.getElementsByTagName("name").item(3).getTextContent();
        String runStarted = statusDoc.getElementsByTagName("dateStarted").item(0).getTextContent();
        DateFormat logDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        setStartDate(logDateFormat.parse(runStarted));

        if (statusDoc.getElementsByTagName("dateCompleted").getLength() != 0) {
          String runCompleted = statusDoc.getElementsByTagName("dateCompleted").item(0).getTextContent();
          setCompletionDate(logDateFormat.parse(runCompleted));
        }

        if (statusDoc.getElementsByTagName("name").getLength() != 0) {
          for (int i = 0; i < statusDoc.getElementsByTagName("name").getLength(); i++) {
            Element e = (Element) statusDoc.getElementsByTagName("name").item(i);
            Matcher m = runRegex.matcher(e.getTextContent());
            if (m.matches()) {
              runName = e.getTextContent();
              setInstrumentName(m.group(1));
            }
          }
        } else {
          Matcher m = runRegex.matcher(runName);
          if (m.matches()) {
            setInstrumentName(m.group(1));
          }
        }

        setRunName(runName);
        setHealth(HealthType.Unknown);
      }
      setXml(statusXml);
    } catch (ParserConfigurationException e) {
      log.error("parse status XML", e);
    } catch (TransformerException e) {
      log.error("parse status XML", e);
    } catch (ParseException e) {
      log.error("parse status XML", e);
    }
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(super.toString());
    if (statusXml != null) {
      sb.append(" : ");
      sb.append(statusXml);
    }
    return sb.toString();
  }
}

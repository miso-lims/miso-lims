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

package uk.ac.bbsrc.tgac.miso.core.data.impl.illumina;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import uk.ac.bbsrc.tgac.miso.core.data.impl.StatusImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.util.SubmissionUtils;
import uk.ac.bbsrc.tgac.miso.core.util.UnicodeReader;


/**
 * uk.ac.bbsrc.tgac.miso.core.data.impl.illumina
 * <p/>
 * TODO Info
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public class IlluminaStatus extends StatusImpl {
  protected static final Logger log = LoggerFactory.getLogger(IlluminaStatus.class);
  String statusXml = null;

  public IlluminaStatus() {
    setHealth(HealthType.Unknown);
  }

  public IlluminaStatus(String statusXml) {
    this.statusXml = statusXml;
    parseStatusXml(statusXml);
  }

  public void parseStatusXml(String statusXml) {
    try {
      Document statusDoc = SubmissionUtils.emptyDocument();
      SubmissionUtils.transform(new UnicodeReader(statusXml), statusDoc);

      if (statusDoc.getDocumentElement().getTagName().equals("error")) {
        String runName = statusDoc.getElementsByTagName("RunName").item(0).getTextContent();
        String runDirRegex = "(\\d{6})_([A-z0-9]+)_(\\d+)_[A-z0-9_]*";
        Matcher m = Pattern.compile(runDirRegex).matcher(runName);
        if (m.matches()) {
          setStartDate(new SimpleDateFormat("yyMMdd").parse(m.group(1)));
          setInstrumentName(m.group(2));
        }
        setRunName(runName);
        setHealth(HealthType.Unknown);
      } else {
        String runStarted = statusDoc.getElementsByTagName("RunStarted").item(0).getTextContent();
        setStartDate(new SimpleDateFormat("EEEE, MMMMM dd, yyyy h:mm aaa").parse(runStarted));
        setInstrumentName(statusDoc.getElementsByTagName("InstrumentName").item(0).getTextContent());
        setRunName(statusDoc.getElementsByTagName("RunName").item(0).getTextContent());
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

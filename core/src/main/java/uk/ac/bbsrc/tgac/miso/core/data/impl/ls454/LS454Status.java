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

package uk.ac.bbsrc.tgac.miso.core.data.impl.ls454;

import uk.ac.bbsrc.tgac.miso.core.data.impl.StatusImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;

/**
 * uk.ac.bbsrc.tgac.miso.core.data.impl.ls454
 * <p/>
 * TODO Info
 * 
 * @author Rob Davey
 * @since 0.1.4
 */
public class LS454Status extends StatusImpl {
  String runLog = null;

  public LS454Status() {
    setHealth(HealthType.Unknown);
  }

  public LS454Status(String runLog) {
    this.runLog = runLog;
    parseGsProcessorLog(runLog);
  }

  public void parseGsProcessorLog(String runLog) {
    /*
     * try {
     * 
     * String runStarted = statusDoc.getElementsByTagName("dateStarted").item(0).getTextContent(); setStartDate(new SimpleDateFormat(
     * "MM/dd/yyyy hh:mm aaa").parse(runStarted));
     * setInstrumentName(statusDoc.getElementsByTagName("instrumentName").item(0).getTextContent());
     * setRunName(statusDoc.getElementsByTagName("name").item(0).getTextContent());
     * 
     * setHealth(HealthType.Unknown); setXml(statusXml);
     * 
     * } catch (ParserConfigurationException e) { e.printStackTrace(); } catch (TransformerException e) { e.printStackTrace(); } catch
     * (ParseException e) { e.printStackTrace(); }
     */
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(super.toString());
    if (runLog != null) {
      sb.append(" : ");
      sb.append(runLog);
    }
    return sb.toString();
  }
}

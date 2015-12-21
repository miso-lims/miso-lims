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

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StatusImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
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
@Entity
@DiscriminatorValue("Illumina")
public class IlluminaRun extends RunImpl {
  protected static final Logger log = LoggerFactory.getLogger(IlluminaRun.class);

  public IlluminaRun() {
    setPlatformType(PlatformType.ILLUMINA);
    setStatus(new StatusImpl());
    setSecurityProfile(new SecurityProfile());
  }

  public IlluminaRun(String statusXml) {
    this(statusXml, null);
  }

  public IlluminaRun(String statusXml, User user) {
    try {
      Document statusDoc = SubmissionUtils.emptyDocument();
      if (!isStringEmptyOrNull(statusXml)) {
        SubmissionUtils.transform(new UnicodeReader(statusXml), statusDoc);

        String runName = (statusDoc.getElementsByTagName("RunName").item(0).getTextContent());
        setPairedEnd(false);

        if (!statusDoc.getDocumentElement().getTagName().equals("error")) {
          if (statusDoc.getElementsByTagName("IsPairedEndRun").getLength() > 0) {
            boolean paired = Boolean.parseBoolean(statusDoc.getElementsByTagName("IsPairedEndRun").item(0).getTextContent().toLowerCase());
            setPairedEnd(paired);
          }
        }

        String runDirRegex = "[\\d]+_([A-z0-9\\-])+_([\\d])+_([A-z0-9_\\+\\-]*)";
        Matcher m = Pattern.compile(runDirRegex).matcher(runName);
        if (m.matches()) {
          setPlatformRunId(Integer.parseInt(m.group(2)));
        }

        setAlias(runName);
        setFilePath(runName);
        setDescription(m.group(3));
        setPlatformType(PlatformType.ILLUMINA);
        setStatus(new IlluminaStatus(statusXml));
        if (user != null) {
          setSecurityProfile(new SecurityProfile(user));
        } else {
          setSecurityProfile(new SecurityProfile());
        }
      } else {
        log.error("No status XML for this run");
      }
    } catch (ParserConfigurationException e) {
      log.error("Cannot parse status", e);
    } catch (TransformerException e) {
      log.error("Cannot parse status: " + statusXml, e);
    }
  }

  public IlluminaRun(User user) {
    setPlatformType(PlatformType.ILLUMINA);
    setStatus(new StatusImpl());
    setSecurityProfile(new SecurityProfile(user));
  }

  @Override
  public void buildSubmission() {
    /*
     * try { DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder(); submissionDocument =
     * docBuilder.newDocument(); } catch (ParserConfigurationException e) { e.printStackTrace(); }
     * ERASubmissionFactory.generateFullRunSubmissionXML(submissionDocument, this);
     */
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(super.toString());
    /*
     * if (getFlowcells() != null) { sb.append(" : "); for(Flowcell f: getFlowcells()) { sb.append(f.toString()); } }
     */
    if (getSequencerPartitionContainers() != null) {
      sb.append(" : ");
      for (SequencerPartitionContainer f : getSequencerPartitionContainers()) {
        sb.append(f.toString());
      }
    }
    return sb.toString();
  }

  /**
   * Method buildReport ...
   */
  @Override
  public void buildReport() {

  }
}

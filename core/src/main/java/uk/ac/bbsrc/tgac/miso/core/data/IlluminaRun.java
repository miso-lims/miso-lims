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

package uk.ac.bbsrc.tgac.miso.core.data;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.util.SubmissionUtils;
import uk.ac.bbsrc.tgac.miso.core.util.UnicodeReader;

@Entity
@Table(name = "RunIllumina")
public class IlluminaRun extends Run {
  private static final Logger log = LoggerFactory.getLogger(IlluminaRun.class);
  private final static Pattern RUNNAME_PATTERN = Pattern.compile("(\\d{6})_([A-z0-9]+)_(\\d+)_[A-z0-9_]*");

  public IlluminaRun() {
    super();
  }

  public IlluminaRun(User user) {
    super(user);
  }

  public static IlluminaRun createRunFromXml(String statusXml, Function<String, SequencerReference> findSequencer) {
    try {
      Document statusDoc = SubmissionUtils.emptyDocument();
      if (!isStringEmptyOrNull(statusXml)) {
        SubmissionUtils.transform(new UnicodeReader(statusXml), statusDoc);

        String runName = (statusDoc.getElementsByTagName("RunName").item(0).getTextContent());
        IlluminaRun run = new IlluminaRun();
        run.setPairedEnd(false);

        Matcher m = RUNNAME_PATTERN.matcher(runName);

        if (!statusDoc.getDocumentElement().getTagName().equals("error")) {
          if (statusDoc.getElementsByTagName("IsPairedEndRun").getLength() > 0) {
            boolean paired = Boolean.parseBoolean(statusDoc.getElementsByTagName("IsPairedEndRun").item(0).getTextContent().toLowerCase());
            run.setPairedEnd(paired);
          }
          if (m.matches()) {
            run.setStartDate(new SimpleDateFormat("yyMMdd").parse(m.group(1)));
            run.setSequencerReference(findSequencer.apply(m.group(2)));
          }
          run.setName(runName);
          run.setHealth(HealthType.Unknown);

        } else {
          String runStarted = statusDoc.getElementsByTagName("RunStarted").item(0).getTextContent();
          run.setStartDate(new SimpleDateFormat("EEEE, MMMMM dd, yyyy h:mm aaa").parse(runStarted));
          run.setSequencerReference(findSequencer.apply(statusDoc.getElementsByTagName("InstrumentName").item(0).getTextContent()));
          run.setName(statusDoc.getElementsByTagName("RunName").item(0).getTextContent());
          run.setHealth(HealthType.Unknown);
        }

        run.setAlias(runName);
        run.setFilePath(runName);
        if (m.matches()) {
          run.setDescription(m.group(3));
        }
        run.setSecurityProfile(new SecurityProfile());
        return run;
      } else {
        log.error("No status XML for this run");
      }
    } catch (ParserConfigurationException | TransformerException | ParseException e) {
      log.error("Cannot parse status", e);
    }
    return null;
  }

  private Integer callCycle;
  private Integer imgCycle;
  private Integer numCycles;
  private Integer scoreCycle;

  public Integer getCallCycle() {
    return callCycle;
  }

  public Integer getImgCycle() {
    return imgCycle;
  }

  public Integer getNumCycles() {
    return numCycles;
  }

  public Integer getScoreCycle() {
    return scoreCycle;
  }

  public void setCallCycle(Integer callCycle) {
    this.callCycle = callCycle;
  }

  public void setImgCycle(Integer imgCycle) {
    this.imgCycle = imgCycle;
  }

  public void setNumCycles(Integer numCycles) {
    this.numCycles = numCycles;
  }

  public void setScoreCycle(Integer scoreCycle) {
    this.scoreCycle = scoreCycle;
  }
}

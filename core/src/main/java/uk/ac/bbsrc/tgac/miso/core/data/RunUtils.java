package uk.ac.bbsrc.tgac.miso.core.data;

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

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.google.common.base.Function;

import uk.ac.bbsrc.tgac.miso.core.data.impl.SolidRun;
import uk.ac.bbsrc.tgac.miso.core.util.SubmissionUtils;
import uk.ac.bbsrc.tgac.miso.core.util.UnicodeReader;

public class RunUtils {
  private static final Logger log = LoggerFactory.getLogger(RunUtils.class);

  private static final Pattern SOLID_RUN_REGEX = Pattern.compile("([A-z0-9\\-]+)_([0-9]{8})_(.*)");

  public static Run createFromSolidXml(String statusXml, Function<String, SequencerReference> findSequencer) {
    try {
      Document statusDoc = SubmissionUtils.emptyDocument();
      SubmissionUtils.transform(new UnicodeReader(statusXml), statusDoc);
  
      Run run = new Run();
      String runName;
      if (statusDoc.getDocumentElement().getTagName().equals("error")) {
        runName = (statusDoc.getElementsByTagName("RunName").item(0).getTextContent());
      } else {
        runName = (statusDoc.getElementsByTagName("name").item(0).getTextContent());
        if (statusDoc.getElementsByTagName("name").getLength() != 0) {
          for (int i = 0; i < statusDoc.getElementsByTagName("name").getLength(); i++) {
            Element e = (Element) statusDoc.getElementsByTagName("name").item(i);
            Matcher m = SOLID_RUN_REGEX.matcher(e.getTextContent());
            if (m.matches()) {
              runName = e.getTextContent();
            }
          }
        }
        String runStarted = statusDoc.getElementsByTagName("dateStarted").item(0).getTextContent();
        DateFormat logDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
  
        run.setStartDate(logDateFormat.parse(runStarted));
  
        if (statusDoc.getElementsByTagName("dateCompleted").getLength() != 0) {
          String runCompleted = statusDoc.getElementsByTagName("dateCompleted").item(0).getTextContent();
          run.setCompletionDate(logDateFormat.parse(runCompleted));
        }
      }
  
      run.setAlias(runName);
      run.setFilePath(runName);
      ((SolidRun) run).setPairedEnd(false);
  
      Matcher m = SOLID_RUN_REGEX.matcher(runName);
      String instrument = null;
      if (m.matches()) {
        instrument = m.group(1);
        run.setDescription(m.group(3));
        if (m.group(3).startsWith("MP") || m.group(3).startsWith("PE")) {
          ((SolidRun) run).setPairedEnd(true);
        }
      } else {
        run.setDescription(runName);
      }
      if (statusDoc.getElementsByTagName("name").getLength() != 0) {
        for (int i = 0; i < statusDoc.getElementsByTagName("name").getLength(); i++) {
          Element e = (Element) statusDoc.getElementsByTagName("name").item(i);
          Matcher me = SOLID_RUN_REGEX.matcher(e.getTextContent());
          if (m.matches()) {
            instrument = me.group(1);
          }
        }
      }
      run.setSequencerReference(findSequencer.apply(instrument));
      run.setSecurityProfile(new SecurityProfile());
      return run;
    } catch (ParserConfigurationException | TransformerException | ParseException e) {
      log.error("parse status XML", e);
    }
    return null;
  }

}

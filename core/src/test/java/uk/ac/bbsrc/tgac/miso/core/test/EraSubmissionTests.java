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

package uk.ac.bbsrc.tgac.miso.core.test;

import com.eaglegenomics.simlims.core.SecurityProfile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.decorator.submission.era.EraStudyDecorator;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.factory.TgacDataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.util.SubmissionUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Properties;

/**
 * uk.ac.bbsrc.tgac.miso.core.test
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 13-Sep-2011
 * @since 0.1.1
 */
public class EraSubmissionTests {
  protected static final Logger log = LoggerFactory.getLogger(EraSubmissionTests.class);
  private DataObjectFactory dataObjectFactory;

  @Before
  public void setUp() {
    dataObjectFactory = new TgacDataObjectFactory();
  }

  @Test
  public void testStudyXmlGeneration() {
      Project p = dataObjectFactory.getProject();
      p.setAlias("Submission Test Project");
      //creates a Study object and sets parameters
      Study s = dataObjectFactory.getStudy();
      s.setProject(p);
      s.setAlias("Submission Test Study");
      s.setDescription("A test of the Submission XML generation process");
      s.setSecurityProfile(new SecurityProfile());

      Document submissionDocument = null;
      try {
          DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
          submissionDocument = docBuilder.newDocument();
          Element set = submissionDocument.createElementNS(null, "STUDY_SET");
          submissionDocument.appendChild(set);
      } catch (Exception e) {
          log.debug("Error while attempting to build document");
      }

      new EraStudyDecorator(s, new Properties(), submissionDocument).buildSubmission();
      try{
      SubmissionUtils.transform(submissionDocument,
              new File("/tmp/testSubmission.xml"));
         //sb.append(SubmissionUtils.transform(submissionDocument, true));
      } catch(Exception e){
          log.debug("Error while attempting to write document to file");
      }

      log.info("Done testing Study XML generation");
  }

  @After
  public void tearDown() {
    dataObjectFactory = null;  
  }
}
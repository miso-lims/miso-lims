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

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.type.processor.SampleToLibraryTypeProcessor;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.exception.TypeProcessingException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.factory.TgacDataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.service.naming.DefaultLibraryNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.DefaultSampleNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.RequestManagerAwareNamingScheme;

/**
 * Test type conversion implementations
 *
 * @author Rob Davey
 * @date 27/11/14
 * @since 0.2.1
 */
public class TypeProcessorTests {
  protected static final Logger log = LoggerFactory.getLogger(TypeProcessorTests.class);
  private MockRequestManager mockRequestManager = new MockRequestManager();
  private DataObjectFactory dataObjectFactory;
  private RequestManagerAwareNamingScheme<Sample> sampleNamingScheme;
  private RequestManagerAwareNamingScheme<Library> libraryNamingScheme;
  private SampleToLibraryTypeProcessor sampleToLibraryTypeProcessor;

  @Before
  public void setUp() {
    dataObjectFactory = new TgacDataObjectFactory();
    sampleNamingScheme = new DefaultSampleNamingScheme();
    sampleNamingScheme.setRequestManager(mockRequestManager);
    libraryNamingScheme = new DefaultLibraryNamingScheme();
    libraryNamingScheme.setRequestManager(mockRequestManager);
    sampleToLibraryTypeProcessor = new SampleToLibraryTypeProcessor();
  }

  @Test
  public void testSampleToLibraryTypeProcessing() throws MisoNamingException, TypeProcessingException {
    Sample s = dataObjectFactory.getSample();
    s.setId(1L);
    String name = sampleNamingScheme.generateNameFor("name", s);
    s.setName(name);
    Assert.assertTrue(sampleNamingScheme.validateField("name", s.getName()));

    s.setAlias("RD_S1_Foo.bar");
    Assert.assertTrue(sampleNamingScheme.validateField("alias", s.getAlias()));

    log.info("Sample valid. Processing into new Library...");

    sampleToLibraryTypeProcessor.setNamingScheme(libraryNamingScheme);
    Library l = sampleToLibraryTypeProcessor.process(s);
    Assert.assertTrue(libraryNamingScheme.validateField("name", l.getName()));
    Assert.assertTrue(libraryNamingScheme.validateField("alias", l.getAlias()));
    log.info("Library valid: [ " +l.getName()+ " ("+l.getAlias()+") ]");
  }

  @After
  public void tearDown() {
    dataObjectFactory = null;
    sampleNamingScheme = null;
    libraryNamingScheme = null;
  }
}
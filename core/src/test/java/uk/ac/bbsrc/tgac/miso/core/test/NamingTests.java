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
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.factory.TgacDataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.service.naming.AllowAnythingEntityNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.DefaultLibraryNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.DefaultSampleNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;

/**
 * uk.ac.bbsrc.tgac.miso.core.test
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 26/09/11
 * @since 0.1.2
 */
public class NamingTests {
  protected static final Logger log = LoggerFactory.getLogger(NamingTests.class);
  private DataObjectFactory dataObjectFactory;
  private MisoNamingScheme<Sample> sampleNamingScheme;
  private MisoNamingScheme<Library> libraryNamingScheme;
  private MisoNamingScheme<Nameable> anythingNamingScheme;

  @Before
  public void setUp() {
    dataObjectFactory = new TgacDataObjectFactory();
    sampleNamingScheme = new DefaultSampleNamingScheme();
    libraryNamingScheme = new DefaultLibraryNamingScheme();
    anythingNamingScheme = new AllowAnythingEntityNamingScheme<>();
  }

  @Test
  public void testAllowAnything() throws MisoNamingException {
    Sample s = dataObjectFactory.getSample();
    s.setId(1L);
    s.setName(anythingNamingScheme.generateNameFor("name", s));
    Assert.assertTrue(anythingNamingScheme.validateField("name", s.getName()));

    Library l = dataObjectFactory.getLibrary();
    l.setId(2L);
    l.setName(anythingNamingScheme.generateNameFor("name", l));
    Assert.assertTrue(anythingNamingScheme.validateField("name", l.getName()));
  }

  @Test
  public void testSampleNaming() throws MisoNamingException {
    Sample s = dataObjectFactory.getSample();
    s.setId(1L);
    String name = sampleNamingScheme.generateNameFor("name", s);
    s.setName(name);
    Assert.assertTrue(sampleNamingScheme.validateField("name", s.getName()));

    s.setAlias("RD_S1_Foo.bar");
    Assert.assertTrue(sampleNamingScheme.validateField("alias", s.getAlias()));

    log.info("Sample naming scheme valid");
  }

  @Test
  public void testLibraryNaming() throws MisoNamingException {
    Library l = dataObjectFactory.getLibrary();
    l.setId(1L);
    String name = libraryNamingScheme.generateNameFor("name", l);
    l.setName(name);
    Assert.assertTrue(libraryNamingScheme.validateField("name", l.getName()));

    l.setAlias("RD_L1-1_Foo.bar");
    Assert.assertTrue(libraryNamingScheme.validateField("alias", l.getAlias()));

    log.info("Library naming scheme valid");
  }

  @After
  public void tearDown() {
    dataObjectFactory = null;
    sampleNamingScheme = null;
    libraryNamingScheme = null;
  }
}

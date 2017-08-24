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

package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedSampleException;
import uk.ac.bbsrc.tgac.miso.core.store.SampleStore;

public class HibernateSampleQcDaoTest extends AbstractDAOTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Autowired
  private SessionFactory sessionFactory;

  @Mock
  private SampleStore sampleDAO;

  @InjectMocks
  private HibernateSampleQcDao dao;

  @Before
  public void setup() throws IOException {
    MockitoAnnotations.initMocks(this);
    dao.setSessionFactory(sessionFactory);
  }

  @Test
  public void testGet() throws IOException {
    SampleQC sampleQC = dao.get(1L);
    assertNotNull(sampleQC);
    assertEquals(1L, sampleQC.getCreator().getUserId().longValue());
  }

  @Test
  public void testGetNone() throws IOException {
    assertNull(dao.get(9999L));
  }

  @Test
  public void testSaveEdit() throws IOException, MalformedSampleException {
    SampleQC sampleQC = dao.get(1L);
    sampleQC.setResults(5.0);

    assertEquals(1L, dao.save(sampleQC));
    SampleQC savedSampleQC = dao.get(1L);
    assertTrue(savedSampleQC.getResults() == 5.0);
  }

  @Test
  public void testSaveNew() throws IOException, MalformedSampleException {
    SampleQC newSampleQC = new SampleQC();
    newSampleQC.setSample(new SampleImpl());
    newSampleQC.getSample().setId(1L);
    newSampleQC.setType(new QcType());
    newSampleQC.getType().setQcTypeId(1L);
    newSampleQC.setCreator(new UserImpl());
    newSampleQC.getCreator().setUserId(1L);
    long id = dao.save(newSampleQC);

    SampleQC savedSampleQC = dao.get(id);
    assertEquals(newSampleQC, savedSampleQC);
    assertEquals(newSampleQC.getCreator(), savedSampleQC.getCreator());
  }

  @Test
  public void testSaveNull() throws IOException {
    exception.expect(NullPointerException.class);
    dao.save(null);
  }
}

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
import java.math.BigDecimal;
import java.util.Date;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.qc.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.persistence.SampleStore;

public class HibernateSampleQcDaoIT extends AbstractDAOTest {

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
    assertEquals(1L, sampleQC.getCreator().getId());
  }

  @Test
  public void testGetNone() throws IOException {
    assertNull(dao.get(9999L));
  }

  @Test
  public void testSaveEdit() throws IOException {
    SampleQC sampleQC = dao.get(1L);
    sampleQC.setResults(new BigDecimal("5.0"));

    assertEquals(1L, dao.save(sampleQC));
    SampleQC savedSampleQC = dao.get(1L);
    assertTrue(savedSampleQC.getResults().compareTo(new BigDecimal("5.0")) == 0);
  }

  @Test
  public void testSaveNew() throws IOException {
    SampleQC qc = new SampleQC();
    Sample sample = (Sample) currentSession().get(SampleImpl.class, 1L);
    QcType qcType = (QcType) currentSession().get(QcType.class, 1L);
    User user = (User) currentSession().get(UserImpl.class, 1L);
    qc.setSample(sample);
    qc.setType(qcType);
    qc.setResults(new BigDecimal("12"));
    qc.setCreator(user);
    qc.setCreationTime(new Date());
    qc.setLastModified(new Date());
    long id = dao.save(qc);

    clearSession();

    SampleQC saved = (SampleQC) currentSession().get(SampleQC.class, id);
    assertNotNull(saved);
    assertEquals(qc.getSample().getId(), saved.getSample().getId());
    assertEquals(qc.getType().getId(), saved.getType().getId());
    assertEquals(qc.getResults().compareTo(saved.getResults()), 0);
  }

  @Test
  public void testSaveNull() throws IOException {
    exception.expect(NullPointerException.class);
    dao.save(null);
  }
}

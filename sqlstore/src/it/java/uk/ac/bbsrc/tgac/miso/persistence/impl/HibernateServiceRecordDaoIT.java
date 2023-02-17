/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK MISO project contacts: Robert Davey @
 * TGAC *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MISO. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.persistence.InstrumentStore;

public class HibernateServiceRecordDaoIT extends AbstractDAOTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Autowired
  private SessionFactory sessionFactory;

  @Mock
  private MisoFilesManager misoFilesManager;
  @Mock
  private InstrumentStore instrumentDao;
  private final Instrument emptySR = new InstrumentImpl();

  @InjectMocks
  private HibernateServiceRecordDao dao;

  @Before
  public void setup() throws IOException {
    MockitoAnnotations.initMocks(this);
    dao.setSessionFactory(sessionFactory);

    emptySR.setId(2L);
    Mockito.when(instrumentDao.get(ArgumentMatchers.anyLong())).thenReturn(emptySR);
  }

  @Test
  public void testSaveNew() throws IOException {

    String title = "New Record 1";
    Long newId = dao.save(makeServiceRecord(title));

    ServiceRecord savedRec = dao.get(newId);
    assertEquals(title, savedRec.getTitle());
  }

  private ServiceRecord makeServiceRecord(String title) throws IOException {
    ServiceRecord rec = new ServiceRecord();
    rec.setTitle(title);
    rec.setServiceDate(new java.util.Date());
    rec.setServicedByName("Test Person");
    return rec;
  }

  @Test
  public void testSaveEdit() throws IOException {
    ServiceRecord rec = dao.get(1L);
    String newTitle = "ChangedTitle";
    rec.setTitle(newTitle);
    Instrument sr = Mockito.mock(Instrument.class);
    Mockito.when(sr.getId()).thenReturn(1L);

    assertEquals(1L, dao.save(rec));

    ServiceRecord saved = dao.get(1L);
    assertEquals(newTitle, saved.getTitle());
  }

  @Test
  public void testGet() throws IOException {
    ServiceRecord rec = dao.get(1L);
    assertNotNull(rec);
    assertEquals(1L, rec.getId());
  }

  @Test
  public void testGetNone() throws IOException {
    ServiceRecord rec = dao.get(100L);
    assertNull(rec);
  }

  @Test
  public void testListAll() throws IOException {
    List<ServiceRecord> list = dao.listAll();
    assertEquals(3, list.size());
  }

}

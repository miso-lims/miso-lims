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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerReferenceImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerServiceRecordImpl;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.store.SequencerReferenceStore;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSequencerServiceRecordDao;

public class HibernateSequencerServiceRecordDaoTest extends AbstractDAOTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();
  
  @Autowired
  private SessionFactory sessionFactory;

  @Autowired
  private JdbcTemplate jdbcTemplate;
  
  @Mock
  private MisoFilesManager misoFilesManager;
  @Mock
  private SequencerReferenceStore sequencerReferenceDao;
  private final SequencerReference emptySR = new SequencerReferenceImpl();
  
  @InjectMocks
  private HibernateSequencerServiceRecordDao dao;
  
  @Before
  public void setup() throws IOException {
    MockitoAnnotations.initMocks(this);
    dao.setTemplate(jdbcTemplate);
    dao.setSessionFactory(sessionFactory);

    emptySR.setId(2L);
    Mockito.when(sequencerReferenceDao.get(Matchers.anyLong())).thenReturn(emptySR);
  }
  
  @Test
  public void testSaveNew() throws IOException {
    String title = "New Record 1";
    Long newId = dao.save(makeServiceRecord(title));
    
    SequencerServiceRecord savedRec = dao.get(newId);
    assertEquals(title, savedRec.getTitle());
  }
  
  private SequencerServiceRecord makeServiceRecord(String title) {
    SequencerServiceRecord rec = new SequencerServiceRecordImpl();
    rec.setTitle(title);
    rec.setSequencerReference(emptySR);
    rec.setServiceDate(new java.util.Date());
    rec.setServicedByName("Test Person");
    return rec;
  }
  
  @Test
  public void testSaveEdit() throws IOException {
    SequencerServiceRecord rec = dao.get(1L);
    String newTitle = "ChangedTitle";
    rec.setTitle(newTitle);
    SequencerReference sr = Mockito.mock(SequencerReference.class);
    Mockito.when(sr.getId()).thenReturn(1L);
    rec.setSequencerReference(sr);
    
    assertEquals(1L, dao.save(rec));
    
    SequencerServiceRecord saved = dao.get(1L);
    assertEquals(newTitle, saved.getTitle());
  }
  
  @Test
  public void testSaveDecommissioned() throws IOException {
    SequencerServiceRecord newRec = makeServiceRecord("New Record 2");
    newRec.getSequencerReference().setDateDecommissioned(new Date());
    
    exception.expect(IOException.class);
    dao.save(newRec);
  }

  @Test
  public void testGet() throws IOException {
    SequencerServiceRecord rec = dao.get(1L);
    assertNotNull(rec);
    assertEquals(1L, rec.getId());
  }
  
  @Test
  public void testGetNone() throws IOException {
    SequencerServiceRecord rec = dao.get(100L);
    assertNull(rec);
  }

  @Test
  public void testListAll() throws IOException {
    List<SequencerServiceRecord> list = dao.listAll();
    assertEquals(3, list.size());
  }

  @Test
  public void testCount() throws IOException {
    int rows = dao.count();
    assertEquals(3, rows);
  }

  @Test
  public void testListBySequencerId() {
    List<SequencerServiceRecord> list = dao.listBySequencerId(1L);
    assertEquals(2, list.size());
  }
  
  @Test
  public void testListBySequencerIdNone() {
    List<SequencerServiceRecord> list = dao.listBySequencerId(100L);
    assertEquals(0, list.size());
  }

  @Test
  public void testRemove() throws IOException {
    SequencerServiceRecord rec = dao.get(1L);
    assertNotNull(rec);
    assertTrue(dao.remove(rec));
    assertNull(dao.get(1L));
  }
  
  @Test
  public void testRemoveNotExisting() throws IOException {
    assertFalse(dao.remove(new SequencerServiceRecordImpl()));
  }
  
  @Test
  public void testRemoveWithAttachments() throws IOException {
    SequencerServiceRecord rec = dao.get(1L);
    assertNotNull(rec);
    
    Mockito.when(misoFilesManager.getFileNames(SequencerServiceRecord.class, "1")).thenReturn(Arrays.asList("file"));
    
    assertTrue(dao.remove(rec));
    assertNull(dao.get(1L));
    Mockito.verify(misoFilesManager, Mockito.times(1)).deleteFile(SequencerServiceRecord.class, "1", "file");
  }

}

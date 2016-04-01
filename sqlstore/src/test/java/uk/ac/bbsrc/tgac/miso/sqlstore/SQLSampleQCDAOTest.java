/* Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
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

package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleQCImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedSampleException;
import uk.ac.bbsrc.tgac.miso.core.factory.TgacDataObjectFactory;

import uk.ac.bbsrc.tgac.miso.core.store.SampleStore;

public class SQLSampleQCDAOTest extends AbstractDAOTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Autowired
  @Spy
  private JdbcTemplate jdbcTemplate;

  @Mock
  private SampleStore sampleDAO;
  
  @InjectMocks
  private SQLSampleQCDAO dao;

  @Before
  public void setup() throws IOException {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setDataObjectFactory(new TgacDataObjectFactory());
  }
  
  @Test
  public void testListAll() throws IOException {
    Collection<SampleQC> sampleQCs = dao.listAll();
    assertNotNull(sampleQCs);
    assertEquals(15, sampleQCs.size());
  }
  
  @Test
  public void testSampleQcCount() throws IOException {
    assertEquals(15, dao.count());
  }
  
  @Test
  public void testListBySampleId() throws IOException {
    Collection<SampleQC> sampleQCs = dao.listBySampleId(14L);
    assertNotNull(sampleQCs);
    assertEquals(2, sampleQCs.size());
  }
  
  @Test
  public void testListBySampleIdNone() throws IOException {
    Collection<SampleQC> sampleQCs = dao.listBySampleId(9999L);
    assertEquals(0, sampleQCs.size());
  }
  
  @Test
  public void testListAllSampleQcTypes() throws IOException {
    Collection<QcType> qcTypes = dao.listAllSampleQcTypes();
    assertNotNull(qcTypes);
    assertEquals(2, qcTypes.size());
  }
  
  @Test
  public void testGetSampleQcTypeById() throws IOException {
    QcType qcType = dao.getSampleQcTypeById(3L);
    assertNotNull(qcType);
    assertEquals("Bioanalyser", qcType.getName());
  }
  
  @Test
  public void testGetSampleQcTypeByIdNone() throws IOException {
    QcType qcType = dao.getSampleQcTypeById(9999L);
    assertNull(qcType);
  }
  
  @Test
  public void testGetSampleQcTypeByName() throws IOException {
    QcType qcType = dao.getSampleQcTypeByName("Bioanalyser");
    assertNotNull(qcType);
  }
  
  @Test
  public void testGetSampleQcTypeByNameNone() throws IOException {
    QcType qcType = dao.getSampleQcTypeByName("Multiple Choice");
    assertNull(qcType);
  }
  
  @Test
  public void testGetSampleQcTypeByNameNull() throws IOException {
    QcType qcType = dao.getSampleQcTypeByName(null);
    assertNull(qcType);
  }
  
  @Test
  public void testGet() throws IOException {
    SampleQC sampleQC = dao.get(1L);
    assertNotNull(sampleQC);
    assertEquals("admin", sampleQC.getQcCreator());
  }
  
  @Test
  public void testGetNone() throws IOException {
    assertNull(dao.get(9999L));
  }
  
  @Test
  public void testLazyGet() throws IOException {
    SampleQC sampleQC = dao.lazyGet(1L);
    assertNotNull(sampleQC);
    assertEquals("admin", sampleQC.getQcCreator());
  }
  
  @Test
  public void testLazyGetNone() throws IOException {
    assertNull(dao.get(9999L));
  }
  
  @Test
  public void testSaveEdit() throws IOException, MalformedSampleException {
    SampleQC sampleQC = dao.get(1L);
    
    Sample sample = Mockito.mock(Sample.class);
    Mockito.when(sample.getId()).thenReturn(1L);
    sampleQC.setQcCreator("admin");
    sampleQC.setQcDate(new Date());
    sampleQC.setQcType(dao.getSampleQcTypeByName("QuBit"));
    sampleQC.setResults(5.0);
    sampleQC.setSample(sample);
    
    assertEquals(1L, dao.save(sampleQC));
    SampleQC savedSampleQC = dao.get(1L);
    assertNotSame(sampleQC, savedSampleQC);
    assertEquals(sampleQC.getId(), savedSampleQC.getId());
    assertEquals("admin", savedSampleQC.getQcCreator());
  }
  
  @Test
  public void testSaveNew() throws IOException, MalformedSampleException {
    SampleQC newSampleQC = new SampleQCImpl();
    newSampleQC.setSample(Mockito.mock(Sample.class));
    newSampleQC.setQcCreator("admin");
    newSampleQC.setQcType(Mockito.mock(QcType.class));
    long id = dao.save(newSampleQC);
    
    SampleQC savedSampleQC = dao.get(id);
    assertEquals(newSampleQC, savedSampleQC);
    assertEquals(newSampleQC.getQcCreator(), savedSampleQC.getQcCreator());
  }
  
  @Test
  public void testSaveNull() throws IOException {
    exception.expect(NullPointerException.class);
    dao.save(null);
  }
  
  @Test
  public void testRemove() throws IOException {
    SampleQC sampleQC = dao.get(1L);
    assertNotNull(sampleQC);
    dao.setCascadeType(CascadeType.REMOVE);
    dao.remove(sampleQC);
    assertNull(dao.get(1L));
  }
}

/* Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
 * * *********************************************************************
 * *
 * * This file is part of MISO.
 * *
 * * MISO is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * *
 * * MISO is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 * *
 * * *********************************************************************
 * */

package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.type.InstrumentType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public class HibernateInstrumentModelDaoIT extends AbstractDAOTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Autowired
  @Spy
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private SessionFactory sessionFactory;

  @InjectMocks
  private HibernateInstrumentModelDao dao;

  @Before
  public void setup() throws IOException {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setSessionFactory(sessionFactory);
  }

  @Test
  public void testList() throws IOException {
    List<InstrumentModel> models = dao.list();
    assertEquals(3, models.size());
  }

  @Test
  public void testGetByAlias() throws IOException {
    String alias = "Illumina HiSeq 2000";
    InstrumentModel model = dao.getByAlias(alias);
    assertNotNull(model);
    assertEquals(alias, model.getAlias());
  }

  @Test
  public void testGetByAliasNone() throws IOException {
    assertNull(dao.getByAlias("Coco Rocha"));
  }

  @Test
  public void testGet() throws IOException {
    long id = 16L;
    InstrumentModel model = dao.get(id);
    assertNotNull(model);
    assertEquals(id, model.getId());
  }

  @Test
  public void testGetNone() throws IOException {
    assertNull(dao.get(-9999L));
  }

  @Test
  public void testUpdate() throws IOException {
    long id = 16L;
    String alias = "Illumina HiSeq 2500";
    String description = "4-channel flow cell";
    InstrumentModel old = (InstrumentModel) currentSession().get(InstrumentModel.class, id);
    assertNotEquals(alias, old.getAlias());
    assertNotEquals(description, old.getDescription());
    old.setAlias(alias);
    old.setDescription(description);
    assertEquals(16L, dao.update(old));

    clearSession();

    InstrumentModel saved = (InstrumentModel) currentSession().get(InstrumentModel.class, id);
    assertNotNull(saved);
    assertEquals(old.getAlias(), saved.getAlias());
    assertEquals(old.getDescription(), saved.getDescription());
  }

  @Test
  public void testCreate() throws IOException {
    InstrumentModel newPlatform = makeInstrumentModel("PacBio", "Mystery container", 1);
    newPlatform.setInstrumentType(InstrumentType.SEQUENCER);
    long newId = dao.create(newPlatform);

    clearSession();

    InstrumentModel saved = (InstrumentModel) currentSession().get(InstrumentModel.class, newId);
    assertNotNull(saved);
    assertEquals(newPlatform.getAlias(), saved.getAlias());
    assertEquals(newPlatform.getDescription(), saved.getDescription());
  }

  InstrumentModel makeInstrumentModel(String instrumentModel, String description, Integer numContainers) {
    InstrumentModel platform = new InstrumentModel();
    platform.setDescription(description);
    platform.setAlias(instrumentModel);
    platform.setPlatformType(PlatformType.get("PacBio"));
    return platform;
  }

  @Test
  public void testListActivePlatformTypes() throws Exception {
    Set<PlatformType> platformTypes = dao.listActivePlatformTypes();
    assertEquals(1, platformTypes.size());
    assertFalse(platformTypes.contains(PlatformType.SOLID));
    assertTrue(platformTypes.contains(PlatformType.ILLUMINA));
  }

  @Test
  public void testGetUsage() throws Exception {
    InstrumentModel model = (InstrumentModel) currentSession().get(InstrumentModel.class, 16L);
    assertEquals(2L, dao.getUsage(model));
  }

  @Test
  public void testGetMaxContainersUsedSequencer() throws Exception {
    InstrumentModel sequencer = (InstrumentModel) currentSession().get(InstrumentModel.class, 16L);
    assertEquals(1, dao.getMaxContainersUsed(sequencer));
  }

  @Test
  public void testGetMaxContainersUsedArrayScanner() throws Exception {
    InstrumentModel scanner = (InstrumentModel) currentSession().get(InstrumentModel.class, 30L);
    assertEquals(0, dao.getMaxContainersUsed(scanner));
  }

}

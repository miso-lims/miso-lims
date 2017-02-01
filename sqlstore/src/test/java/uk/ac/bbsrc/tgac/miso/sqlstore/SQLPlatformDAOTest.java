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

package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

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
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PlatformImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernatePlatformDao;

public class SQLPlatformDAOTest extends AbstractDAOTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Autowired
  @Spy
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private SessionFactory sessionFactory;

  @InjectMocks
  private HibernatePlatformDao dao;

  @Before
  public void setup() throws IOException {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setSessionFactory(sessionFactory);
  }

  @Test
  public void testListAll() throws IOException {
    List<Platform> platforms = dao.listAll();
    assertTrue(platforms.size() > 0);
  }

  @Test
  public void testPlatformCount() throws IOException {
    assert (dao.count() == dao.listAll().size());
  }

  @Test
  public void testListDistinctPlatformNames() throws IOException {
    List<PlatformType> distinctPlatformNames = dao.listDistinctPlatformNames();
    assertTrue(distinctPlatformNames.size() > 0);
  }

  @Test
  public void testListbyName() throws IOException {
    List<Platform> platforms = dao.listByName("Illumina");
    assertTrue(platforms.size() > 0);
  }

  @Test
  public void testListbyNameNone() throws IOException {
    List<Platform> platforms = dao.listByName("Futurism");
    assertEquals(0, platforms.size());
  }

  @Test
  public void testListByNameEmpty() throws IOException {
    List<Platform> platforms = dao.listByName("");
    assertEquals(0, platforms.size());
  }

  @Test
  public void testListByNameNull() throws IOException {
    List<Platform> platforms = dao.listByName(null);
    assertEquals(0, platforms.size());
  }

  @Test
  public void testGetByModel() throws IOException {
    Platform platform = dao.getByModel("PacBio RS");
    assertNotNull(platform);
  }

  @Test
  public void testGetByModelNone() throws IOException {
    Platform platform = dao.getByModel("Coco Rocha");
    assertNull(platform);
  }

  @Test
  public void testGetByModelEmpty() throws IOException {
    Platform platform = dao.getByModel("");
    assertNull(platform);
  }

  @Test
  public void testGetByModelNull() throws IOException {
    Platform platform = dao.getByModel(null);
    assertNull(platform);
  }

  @Test
  public void testGet() throws IOException {
    Platform platform = dao.get(16L);
    assertNotNull(platform);
  }

  @Test
  public void testGetNone() throws IOException {
    Platform platform = dao.get(-9999L);
    assertNull(platform);
  }

  @Test
  public void testSaveEdit() throws IOException {
    Platform oldPlatform = dao.get(16L);

    oldPlatform.setPlatformType(oldPlatform.getPlatformType());
    oldPlatform.setInstrumentModel("Illumina HiSeq 2500");
    oldPlatform.setDescription("4-channel flow cell");
    oldPlatform.setNumContainers(1);

    assertEquals(16L, dao.save(oldPlatform));
    Platform savedPlatform = dao.get(16L);
    assertNotNull(savedPlatform);
    assertEquals(oldPlatform.getId(), savedPlatform.getId());
    assertEquals(oldPlatform.getInstrumentModel(), savedPlatform.getInstrumentModel());
    assertEquals(oldPlatform.getDescription(), savedPlatform.getDescription());
    assertEquals(oldPlatform.getPlatformType(), savedPlatform.getPlatformType());
  }

  @Test
  public void testSaveNew() throws IOException {
    Platform newPlatform = makePlatform("PacBio", "Mystery container", 1);
    Long newId = dao.save(newPlatform);
    assertNotNull(dao.get(newId));

    assertEquals(newPlatform.getDescription(), dao.get(newId).getDescription());
  }

  @Test
  public void testSaveNull() throws IOException {
    exception.expect(IllegalArgumentException.class);
    dao.save(null);
  }

  Platform makePlatform(String instrumentModel, String description, Integer numContainers) {
    Platform platform = new PlatformImpl();
    platform.setDescription(description);
    platform.setNumContainers(numContainers);
    platform.setInstrumentModel(instrumentModel);
    platform.setPlatformType(PlatformType.get("PacBio"));
    return platform;
  }
}

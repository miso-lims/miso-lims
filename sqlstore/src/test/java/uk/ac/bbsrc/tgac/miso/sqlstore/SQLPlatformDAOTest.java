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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PlatformImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.factory.TgacDataObjectFactory;

public class SQLPlatformDAOTest extends AbstractDAOTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Autowired
  @Spy
  private JdbcTemplate jdbcTemplate;

  @InjectMocks
  private SQLPlatformDAO dao;

  // Auto-increment sequence doesn't roll back with transactions, so must be tracked
  // There are 34 Platforms created during migrations, so this is the next id.
  // This will have to be changed if new Platforms are added during migrations.
  private static long nextAutoIncrementId = 35L;

  @Before
  public void setup() throws IOException {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setDataObjectFactory(new TgacDataObjectFactory());
  }

  @Test
  public void testListAll() {
    List<Platform> platforms = dao.listAll();
    assertEquals(3, platforms.size());
  }

  @Test
  public void testPlatformCount() throws IOException {
    assertEquals(3, dao.count());
  }

  @Test
  public void testListDistinctPlatformNames() {
    List<String> distinctPlatformNames = dao.listDistinctPlatformNames();
    assertEquals(2, distinctPlatformNames.size());
  }

  @Test
  public void testListbyName() {
    List<Platform> platforms = dao.listByName("Illumina");
    assertEquals(2, platforms.size());
  }

  @Test
  public void testListbyNameNone() {
    List<Platform> platforms = dao.listByName("Futurism");
    assertEquals(0, platforms.size());
  }

  @Test
  public void testListByNameEmpty() {
    List<Platform> platforms = dao.listByName("");
    assertEquals(0, platforms.size());
  }

  @Test
  public void testListByNameNull() {
    List<Platform> platforms = dao.listByName(null);
    assertEquals(0, platforms.size());
  }

  @Test
  public void testGetByModel() {
    Platform platform = dao.getByModel("PacBio RS");
    assertNotNull(platform);
  }

  @Test
  public void testGetByModelNone() {
    Platform platform = dao.getByModel("Coco Rocha");
    assertNull(platform);
  }

  @Test
  public void testGetByModelEmpty() {
    Platform platform = dao.getByModel("");
    assertNull(platform);
  }

  @Test
  public void testGetByModelNull() {
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
  public void testLazyGet() throws IOException {
    Platform platform = dao.lazyGet(16L);
    assertNotNull(platform);
  }

  @Test
  public void testSaveEdit() throws IOException {
    Platform platform = dao.get(16L);

    platform.setPlatformType(platform.getPlatformType());
    platform.setInstrumentModel("Illumina HiSeq 2500");
    platform.setDescription("4-channel flow cell");
    platform.setNumContainers(1);

    assertEquals(16L, dao.save(platform));
    Platform savedPlatform = dao.get(16L);
    assertNotSame(platform, savedPlatform);
    assertEquals(platform.getId(), savedPlatform.getId());
    assertEquals("Illumina HiSeq 2500", savedPlatform.getInstrumentModel());
    assertEquals("4-channel flow cell", savedPlatform.getDescription());
    assertEquals("Illumina", savedPlatform.getPlatformType().getKey());
  }

  @Test
  public void testSaveNew() throws IOException {
    long autoIncrementId = nextAutoIncrementId;
    Platform newPlatform = makePlatform("PacBio", "Mystery container", 1);
    mockAutoIncrement(autoIncrementId);
    assertEquals(autoIncrementId, dao.save(newPlatform));

    Platform savedPlatform = dao.get(autoIncrementId);
    assertEquals(newPlatform.getDescription(), savedPlatform.getDescription());
    nextAutoIncrementId += 1;
  }

  @Test
  public void testSaveNull() throws IOException {
    exception.expect(NullPointerException.class);
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

  private void mockAutoIncrement(long value) {
    Map<String, Object> rs = new HashMap<>();
    rs.put("auto_increment", value);
    Mockito.doReturn(rs).when(jdbcTemplate).queryForMap(Matchers.anyString());
  }
}

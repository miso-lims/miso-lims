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

package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.store.SecurityStore;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.factory.TgacDataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.ChangeLogStore;
import uk.ac.bbsrc.tgac.miso.core.store.RunQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.SequencerPartitionContainerStore;
import uk.ac.bbsrc.tgac.miso.core.store.SequencerReferenceStore;
import uk.ac.bbsrc.tgac.miso.core.store.StatusStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSampleDao;

public class SQLBoxDAOTest extends AbstractDAOTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Autowired
  @Spy
  private JdbcTemplate jdbcTemplate;

  @Mock
  private NamingScheme namingScheme;
  @Mock
  private SecurityStore securityDAO;
  @Mock
  private Store<SecurityProfile> securityProfileDAO;
  @Mock
  private SequencerReferenceStore sequencerReferenceDAO;
  @Mock
  private RunQcStore runQcDAO;
  @Mock
  private SequencerPartitionContainerStore sequencerPartitionContainerDAO;
  @Mock
  private StatusStore statusDAO;
  @Mock
  private ChangeLogStore changeLogDAO;
  @Mock
  private SQLLibraryDAO LibraryDAO;
  @Mock
  private HibernateSampleDao sampleDao;
  @Mock
  private SQLPoolDAO poolDao;

  @InjectMocks
  private SQLBoxDAO dao;

  // Auto-increment sequence doesn't roll back with transactions, so must be tracked
  private static long nextAutoIncrementId = 3L;

  @Before
  public void setup() throws IOException, MisoNamingException {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setDataObjectFactory(new TgacDataObjectFactory());

    SecurityProfile securityProfile = new SecurityProfile();
    when(securityProfileDAO.get(anyLong())).thenReturn(securityProfile);
    User user = new UserImpl();
    user.setUserId(1l);
    when(securityDAO.getUserById(anyLong())).thenReturn(user);

    when(namingScheme.validateName(anyString())).thenReturn(ValidationResult.success());

    CacheManager cacheManager = mock(CacheManager.class);
    Mockito.when(cacheManager.getCache(Matchers.anyString())).thenReturn(mock(Cache.class));
    dao.setCacheManager(cacheManager);
  }

  @Test
  public void testCount() throws IOException {
    int runs = dao.count();
    assertEquals(2, runs);
  }

  @Test
  public void testGetBoxById() throws IOException {
    Box box = dao.get(1);
    assertEquals("box1alias", box.getAlias());
    assertEquals("box1", box.getName());
    assertEquals(1, box.getId());
    assertEquals("barcode1", box.getIdentificationBarcode());
    assertEquals(4, box.getSize().getRows());
    assertEquals("boxuse1", box.getUse().getAlias());
  }

  @Test
  public void testBoxByAlias() throws Exception {
    Box box = dao.getBoxByAlias("box2alias");
    assertEquals(2, box.getId());
  }

  @Test
  public void testGetUseById() throws Exception {
    BoxUse boxUse = dao.getUseById(1);
    assertEquals("boxuse1", boxUse.getAlias());
  }

  @Test
  public void testLazyGet() throws Exception {
    Box box = dao.lazyGet(2);
    assertEquals("box2alias", box.getAlias());
    assertEquals("box2", box.getName());
    assertEquals(2, box.getId());
    assertEquals("barcode2", box.getIdentificationBarcode());
    assertEquals(4, box.getSize().getRows());
    assertEquals("boxuse2", box.getUse().getAlias());
  }

  @Test
  public void testListAll() throws Exception {
    Collection<Box> boxes = dao.listAll();
    assertTrue(boxes.size() == 2);

    Iterator<Box> iterator = boxes.iterator();

    assertEquals(1, iterator.next().getId());
    assertEquals(2, iterator.next().getId());
  }

  @Test
  public void testListAllBoxUses() throws IOException {
    Collection<BoxUse> boxUses = dao.listAllBoxUses();
    assertTrue(2 == boxUses.size());
  }

  @Test
  public void listALlBoxSizes() throws Exception {
    Collection<BoxSize> boxSizes = dao.listAllBoxSizes();
    assertTrue(boxSizes.size() == 1);

  }

  @Test
  public void testListAllBoxUsesStrings() throws Exception {
    List<String> strings = dao.listAllBoxUsesStrings();
    assertTrue(2 == strings.size());
    assertTrue(strings.contains("boxuse1"));
    assertTrue(strings.contains("boxuse2"));
  }

  @Test
  public void testListWithLimit() throws Exception {
    Collection<Box> boxes = dao.listWithLimit(1);
    assertTrue(boxes.size() == 1);
  }

  @Test
  public void testRemove() throws Exception {
    Box box = dao.get(1);
    boolean remove = dao.remove(box);
    assertTrue(remove);
    Collection<Box> boxes = dao.listAll();
    assertTrue(1 == boxes.size());
  }

  @Test
  public void testEmptyAllTubes() throws Exception {
    when(poolDao.getByPositionId(1)).thenReturn(new PoolImpl(new UserImpl()));
    when(poolDao.getByBarcode(null)).thenReturn(new PoolImpl(new UserImpl()));
    Box box = dao.get(1);

    assertTrue("precondition failed", box.getBoxables().values().size() > 0);
    dao.discardAllTubes(box);
    assertTrue(box.getBoxables().values().size() == 0);

  }

  @Test
  public void testEmptySingleTube() throws Exception {
    when(poolDao.getByPositionId(1)).thenReturn(new PoolImpl(new UserImpl()));
    when(poolDao.getByBarcode(null)).thenReturn(new PoolImpl(new UserImpl()));

    Box box = dao.get(1);

    assertTrue("precondition failed", box.getBoxables().values().size() > 0);
    dao.discardSingleTube(box, "B02");
    assertTrue(box.getBoxables().values().size() == 0);

  }

  @Test
  public void testRemoveBoxableFromBox() throws Exception {
    Pool pool = new PoolImpl(new UserImpl());
    pool.setBoxPosition("1");

    dao.removeBoxableFromBox(pool);
  }

  private void mockAutoIncrement(long value) {
    Map<String, Object> rs = new HashMap<>();
    rs.put("Auto_increment", value);
    Mockito.doReturn(rs).when(jdbcTemplate).queryForMap(Matchers.anyString());
  }

  @Test
  public void testSave() throws Exception {
    Box box = new BoxImpl(new UserImpl());
    UserImpl user = new UserImpl();
    user.setId(1l);
    box.setLastModifier(user);
    box.setDescription("newboxdescription");
    box.setAlias("newboxalias");
    box.setName("newbox");
    box.setLocationBarcode("newlocationbarcode");

    BoxSize boxSize = new BoxSize();
    boxSize.setColumns(2);
    boxSize.setRows(3);
    boxSize.setId(1l);
    box.setSize(boxSize);
    BoxUse boxuse = dao.getUseById(1);
    box.setUse(boxuse);

    long autoIncrementId = nextAutoIncrementId;
    mockAutoIncrement(autoIncrementId);

    when(namingScheme.generateNameFor(box)).thenReturn("newbox");
    long boxId = dao.save(box);

    Box retrieved = dao.get(boxId);

    assertEquals(box.getDescription(), retrieved.getDescription());
    assertEquals(box.getAlias(), retrieved.getAlias());
    assertEquals(box.getSize().getId(), retrieved.getSize().getId());
    assertEquals(box.getName(), retrieved.getName());
  }

  @Test
  public void testGetBoxColumnSizes() throws Exception {
    Map<String, Integer> boxColumnSizes = dao.getBoxColumnSizes();

    assertTrue(10 == boxColumnSizes.size());
  }
}

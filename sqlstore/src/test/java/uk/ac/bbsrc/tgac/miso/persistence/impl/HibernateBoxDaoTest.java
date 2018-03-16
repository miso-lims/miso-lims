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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
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

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView.BoxableId;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;

public class HibernateBoxDaoTest extends AbstractDAOTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Autowired
  @Spy
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private SessionFactory sessionFactory;

  @InjectMocks
  private HibernateBoxDao dao;

  @Before
  public void setup() throws IOException, MisoNamingException {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setSessionFactory(sessionFactory);
  }

  @Test
  public void testCount() throws IOException {
    int runs = dao.count();
    assertEquals(2, runs);
  }

  @Test
  public void testGetBoxById() throws IOException, InterruptedException {
    Box box = dao.get(1);
    assertEquals("box1alias", box.getAlias());
    assertEquals("box1", box.getName());
    assertEquals(1L, box.getId());
    assertEquals("identificationbarcode1", box.getIdentificationBarcode());
    assertEquals(4, box.getSize().getRows());
    assertEquals("boxuse1", box.getUse().getAlias());
    assertEquals(2, box.getTubeCount());
    // Should be able to get the tube count without initializing boxables
    assertFalse(Hibernate.isInitialized(box.getBoxables()));
    BoxableView a1 = box.getBoxable("A01");
    assertNotNull(a1);
    assertEquals("SAM15", a1.getName());
    BoxableView b2 = box.getBoxable("B02");
    assertNotNull(b2);
    assertEquals("SAM16", b2.getName());
    assertTrue(Hibernate.isInitialized(box.getBoxables()));
  }

  @Test
  public void testBoxByAlias() throws Exception {
    Box box = dao.getBoxByAlias("box2alias");
    assertEquals(2, box.getId());
  }

  @Test
  public void testGetBoxByBarcode() throws Exception {
    assertNull(dao.getByBarcode("this probably is not a barcode"));
    Box box = dao.getByBarcode("identificationbarcode1");
    assertNotNull(box);
    assertEquals(1L, box.getId());
  }

  @Test
  public void testGetUseById() throws Exception {
    BoxUse boxUse = dao.getUseById(1);
    assertEquals("boxuse1", boxUse.getAlias());
  }

  @Test
  public void testGetSizeById() throws Exception {
    BoxSize boxSize = dao.getSizeById(1);
    assertEquals(4, boxSize.getRows());
    assertEquals(4, boxSize.getColumns());
    assertFalse(boxSize.getScannable());
  }

  @Test
  public void testListAll() throws Exception {
    Collection<Box> boxes = dao.listAll();
    assertTrue(boxes.size() > 0);

    assertEquals(boxes.size(), dao.count());
  }

  @Test
  public void testListAllBoxUses() throws IOException {
    Collection<BoxUse> boxUses = dao.listAllBoxUses();
    assertTrue(2 == boxUses.size());
  }

  @Test
  public void testListAllBoxSizes() throws Exception {
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
  public void testEmptyAllTubes() throws Exception {
    Box box = dao.get(1);
    User user = (User) sessionFactory.getCurrentSession().get(UserImpl.class, 1L);
    assertTrue("precondition failed", box.getBoxables().size() > 0);
    dao.discardAllContents(box, user);
    assertTrue(box.getBoxables().size() == 0);

  }

  @Test
  public void testEmptySingleTube() throws Exception {

    Box box = dao.get(1);
    int count = box.getBoxables().size();
    User user = (User) sessionFactory.getCurrentSession().get(UserImpl.class, 1L);

    assertTrue("precondition failed", box.getBoxables().size() > 0);
    assertTrue(box.getBoxables().containsKey("B02"));
    dao.discardSingleItem(box, "B02", user);
    Box fetchedBox = dao.get(1);
    assertEquals(count - 1, fetchedBox.getBoxables().size());

  }

  @Test
  public void testRemoveBoxableFromBox() throws Exception {
    Sample s = (Sample) sessionFactory.getCurrentSession().get(SampleImpl.class, 15L);
    Box box = dao.get(1);
    BoxableView item = box.getBoxable("A01");
    assertNotNull(item);
    assertEquals(EntityType.SAMPLE, item.getId().getTargetType());
    assertEquals(s.getId(), item.getId().getTargetId());

    dao.removeBoxableFromBox(s);
    Box again = dao.get(1);
    assertFalse(again.getBoxables().containsValue(item));
  }

  @Test
  public void testRemoveBoxableViewFromBox() throws Exception {
    Sample s = (Sample) sessionFactory.getCurrentSession().get(SampleImpl.class, 15L);
    Box box = dao.get(1);
    BoxableView item = box.getBoxable("A01");
    assertNotNull(item);
    assertEquals(EntityType.SAMPLE, item.getId().getTargetType());
    assertEquals(s.getId(), item.getId().getTargetId());

    dao.removeBoxableFromBox(item);
    Box again = dao.get(1);
    assertFalse(again.getBoxables().containsValue(item));
  }

  @Test
  public void testSave() throws Exception {
    Box box = new BoxImpl(new UserImpl());
    UserImpl user = new UserImpl();
    user.setId(1l);
    Date now = new Date();
    box.setCreator(user);
    box.setCreationTime(now);
    box.setLastModifier(user);
    box.setLastModified(now);
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
    assertEquals(13, boxColumnSizes.size());
  }

  @Test
  public void testMoveWithinBox() throws Exception {
    // Note: move is a two-step process
    // 1. remove from previous position and save the box
    // 2. add to new position and save the box
    long boxId = 1L;
    String fromPos = "A01";
    String toPos = "A02";

    Box box = dao.get(boxId);
    BoxableView boxable = box.getBoxable(fromPos);
    assertNotNull(boxable);

    box.removeBoxable(fromPos);
    dao.save(box);

    box.setBoxable(toPos, boxable);
    dao.save(box);

    Box again = dao.get(boxId);
    assertNull(again.getBoxable(fromPos));
    assertNotNull(again.getBoxable(toPos));
  }

  @Test
  public void testMoveFromOtherBox() throws Exception {
    // Note: move is a two-step process
    // 1. remove from previous position and save the box
    // 2. add to new position and save the box
    long fromBoxId = 1L;
    String fromPos = "A01";
    long toBoxId = 2L;
    String toPos = "A01";

    Box fromBox = dao.get(fromBoxId);
    assertNotNull(fromBox);
    assertNotNull(fromBox.getBoxable(fromPos));
    BoxableView boxable = fromBox.getBoxable(fromPos);
    assertNotNull(boxable);

    fromBox.removeBoxable(fromPos);
    dao.save(fromBox);

    Box toBox = dao.get(toBoxId);
    assertNotNull(toBox);
    assertNull(toBox.getBoxable(toPos));
    toBox.setBoxable(toPos, boxable);
    dao.save(toBox);

    Box saved = dao.get(toBoxId);
    assertNotNull(saved);
    assertNotNull(saved.getBoxable(toPos));

    Box original = dao.get(fromBoxId);
    assertNotNull(original);
    assertNull(original.getBoxable(fromPos));
  }

  @Test
  public void testGetBoxableView() throws Exception {
    BoxableId badId = new BoxableId(EntityType.SAMPLE, -1L);
    assertNull(dao.getBoxableView(badId));

    BoxableId goodSampleId = new BoxableId(EntityType.SAMPLE, 1L);
    BoxableView sample = dao.getBoxableView(goodSampleId);
    assertNotNull(sample);
    assertEquals("SAM1", sample.getName());

    BoxableId goodLibraryId = new BoxableId(EntityType.LIBRARY, 1L);
    BoxableView library = dao.getBoxableView(goodLibraryId);
    assertNotNull(library);
    assertEquals("LIB1", library.getName());

    BoxableId goodDilutionId = new BoxableId(EntityType.DILUTION, 1L);
    BoxableView dilution = dao.getBoxableView(goodDilutionId);
    assertNotNull(dilution);
    assertEquals("LDI1", dilution.getName());

    BoxableId goodPoolId = new BoxableId(EntityType.POOL, 1L);
    BoxableView pool = dao.getBoxableView(goodPoolId);
    assertNotNull(pool);
    assertEquals("IPO1", pool.getName());
  }

  @Test
  public void testGetBoxableViewByPreMigrationId() throws Exception {
    assertNull(dao.getBoxableViewByPreMigrationId(32123L));

    BoxableView sample = dao.getBoxableViewByPreMigrationId(1L);
    assertNotNull(sample);
    assertEquals(new BoxableId(EntityType.SAMPLE, 17L), sample.getId());
  }

  @Test
  public void testGetBoxableViewsByBarcodeList() throws Exception {
    List<BoxableView> empty = dao.getBoxableViewsByBarcodeList(Collections.emptyList());
    assertNotNull(empty);
    assertTrue(empty.isEmpty());

    List<String> barcodes = Arrays.asList("SAM1::TEST_0001_Bn_P_nn_1-1_D_1", "LIB1::TEST_0001_Bn_P_PE_300_WG");
    List<BoxableView> list = dao.getBoxableViewsByBarcodeList(barcodes);
    assertNotNull(list);
    assertEquals(2, list.size());
  }

  @Test
  public void testGetBoxableViewsByIdList() throws Exception {
    List<BoxableView> empty = dao.getBoxableViewsByIdList(Collections.emptyList());
    assertNotNull(empty);
    assertTrue(empty.isEmpty());

    List<BoxableId> ids = Arrays.asList(new BoxableId(EntityType.DILUTION, 1L), new BoxableId(EntityType.POOL, 1L));
    List<BoxableView> list = dao.getBoxableViewsByIdList(ids);
    assertNotNull(list);
    assertEquals(2, list.size());
  }

  @Test
  public void testGetBoxableViewsBySearchBarcode() throws Exception {
    List<BoxableView> byBarcode = dao.getBoxableViewsBySearch("SAM1::TEST_0001_Bn_P_nn_1-1_D_1");
    assertEquals(1, byBarcode.size());
    assertEquals(new BoxableId(EntityType.SAMPLE, 1L), byBarcode.get(0).getId());
  }

  @Test
  public void testGetBoxableViewsBySearchName() throws Exception {
    List<BoxableView> byName = dao.getBoxableViewsBySearch("LIB3");
    assertEquals(1, byName.size());
    assertEquals(new BoxableId(EntityType.LIBRARY, 3L), byName.get(0).getId());
  }

  @Test
  public void testGetBoxableViewsBySearchAlias() throws Exception {
    List<BoxableView> byAlias = dao.getBoxableViewsBySearch("Pool 5");
    assertEquals(1, byAlias.size());
    assertEquals(new BoxableId(EntityType.POOL, 5L), byAlias.get(0).getId());
  }

}

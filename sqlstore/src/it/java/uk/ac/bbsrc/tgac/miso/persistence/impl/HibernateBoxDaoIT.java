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

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.BoxableId;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;

public class HibernateBoxDaoIT extends AbstractDAOTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Autowired
  private SessionFactory sessionFactory;

  @InjectMocks
  private HibernateBoxDao dao;

  @Before
  public void setup() throws IOException, MisoNamingException {
    MockitoAnnotations.initMocks(this);
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
    assertEquals(4, box.getSize().getRows().intValue());
    assertEquals("boxuse1", box.getUse().getAlias());
    assertEquals(2, box.getTubeCount());
    BoxPosition a1 = box.getBoxPositions().get("A01");
    assertNotNull(a1);
    assertEquals(new BoxableId(EntityType.SAMPLE, 15L), a1.getBoxableId());
    BoxPosition b2 = box.getBoxPositions().get("B02");
    assertNotNull(b2);
    assertEquals(new BoxableId(EntityType.SAMPLE, 16L), b2.getBoxableId());
  }

  @Test
  public void testBoxByAlias() throws Exception {
    Box box = dao.getBoxByAlias("box2alias");
    assertEquals(2, box.getId());
  }

  @Test
  public void testListAll() throws Exception {
    Collection<Box> boxes = dao.listAll();
    assertTrue(boxes.size() > 0);

    assertEquals(boxes.size(), dao.count());
  }

  @Test
  public void testRemoveBoxableFromBox() throws Exception {
    Sample s = (Sample) sessionFactory.getCurrentSession().get(SampleImpl.class, 15L);
    assertNotNull(s.getBox());
    assertEquals(1L, s.getBox().getId());
    assertEquals("A01", s.getBoxPosition());
    Box box = dao.get(1L);
    BoxPosition bp = box.getBoxPositions().get("A01");
    assertNotNull(bp);
    assertEquals(new BoxableId(s.getEntityType(), s.getId()), bp.getBoxableId());

    dao.removeBoxableFromBox(s);

    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

    Box savedBox = dao.get(1);
    assertFalse(savedBox.getBoxPositions().containsKey("A01"));
    Sample savedSample = (Sample) sessionFactory.getCurrentSession().get(SampleImpl.class, 15L);
    assertNull(savedSample.getBox());
    assertNull(savedSample.getBoxPosition());
  }

  @Test
  public void testRemoveBoxableViewFromBox() throws Exception {
    Sample s = (Sample) sessionFactory.getCurrentSession().get(SampleImpl.class, 15L);
    Box box = dao.get(1);
    BoxPosition bp = box.getBoxPositions().get("A01");
    assertNotNull(bp);
    assertEquals(new BoxableId(s.getEntityType(), s.getId()), bp.getBoxableId());
    BoxableView item = BoxableView.fromBoxable(s);
    dao.removeBoxableFromBox(item);

    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

    Box again = dao.get(1);
    assertFalse(again.getBoxPositions().containsKey("A01"));
  }

  @Test
  public void testSave() throws Exception {
    Box box = new BoxImpl();
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
    BoxUse boxuse = (BoxUse) sessionFactory.getCurrentSession().get(BoxUse.class, 1L);
    box.setUse(boxuse);

    long boxId = dao.save(box);

    Box retrieved = dao.get(boxId);

    assertEquals(box.getDescription(), retrieved.getDescription());
    assertEquals(box.getAlias(), retrieved.getAlias());
    assertEquals(box.getSize().getId(), retrieved.getSize().getId());
    assertEquals(box.getName(), retrieved.getName());
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
    BoxPosition fromBp = box.getBoxPositions().get(fromPos);
    assertNotNull(fromBp);

    box.getBoxPositions().remove(fromPos);
    dao.save(box);

    BoxPosition toBp = new BoxPosition(box, toPos, fromBp.getBoxableId());
    box.getBoxPositions().put(toPos, toBp);
    dao.save(box);

    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

    Box again = dao.get(boxId);
    assertNull(again.getBoxPositions().get(fromPos));
    assertNotNull(again.getBoxPositions().get(toPos));
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
    BoxPosition fromBp = fromBox.getBoxPositions().get(fromPos);
    assertNotNull(fromBp);

    fromBox.getBoxPositions().remove(fromPos);
    dao.save(fromBox);

    Box toBox = dao.get(toBoxId);
    assertNotNull(toBox);
    assertNull(toBox.getBoxPositions().get(toPos));

    BoxPosition toBp = new BoxPosition(toBox, toPos, fromBp.getBoxableId());
    toBox.getBoxPositions().put(toPos, toBp);
    dao.save(toBox);

    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

    Box saved = dao.get(toBoxId);
    assertNotNull(saved);
    assertNotNull(saved.getBoxPositions().get(toPos));

    Box original = dao.get(fromBoxId);
    assertNotNull(original);
    assertNull(original.getBoxPositions().get(fromPos));
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

    BoxableId goodLibraryAliquotId = new BoxableId(EntityType.LIBRARY_ALIQUOT, 1L);
    BoxableView libraryAliquot = dao.getBoxableView(goodLibraryAliquotId);
    assertNotNull(libraryAliquot);
    assertEquals("LDI1", libraryAliquot.getName());

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

    List<BoxableId> ids = Arrays.asList(new BoxableId(EntityType.LIBRARY_ALIQUOT, 1L), new BoxableId(EntityType.POOL, 1L));
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

  @Test
  public void testSearch() throws IOException {
    testSearch(PaginationFilter.query("BOX1"));
  }

  /**
   * Verifies Hibernate mappings by ensuring that no exception is thrown by a search
   * 
   * @param filter the search filter
   * @throws IOException
   */
  private void testSearch(PaginationFilter filter) throws IOException {
    // verify Hibernate mappings by ensuring that no exception is thrown
    assertNotNull(dao.list(err -> {
      throw new RuntimeException(err);
    }, 0, 10, true, "name", filter));
  }

  @Test
  public void testLoadBoxPositions() throws IOException {
    Box box = dao.get(1L);
    assertEquals(2, box.getBoxPositions().size());
  }

  @Test
  public void testAddItem() throws IOException {
    Box box = dao.get(1L);
    String insertPos = "A02";
    assertEquals(2, box.getBoxPositions().size());
    assertFalse(box.getBoxPositions().containsKey(insertPos));

    BoxPosition bp = new BoxPosition(box, insertPos, EntityType.LIBRARY, 1L);
    box.getBoxPositions().put(insertPos, bp);
    dao.save(box);

    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

    Box saved = dao.get(1L);
    assertEquals(3, saved.getBoxPositions().size());
    assertTrue(box.getBoxPositions().containsKey(insertPos));
  }

  @Test
  public void testRemoveItem() throws IOException {
    Box box = dao.get(1L);
    String removePos = "A01";
    assertEquals(2, box.getBoxPositions().size());
    assertTrue(box.getBoxPositions().containsKey(removePos));

    box.getBoxPositions().remove(removePos);
    dao.save(box);

    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

    Box saved = dao.get(1L);
    assertEquals(1, saved.getBoxPositions().size());
    assertFalse(box.getBoxPositions().containsKey(removePos));
  }

  @Test
  public void testGetBoxable() throws IOException {
    Boxable item = dao.getBoxable(new BoxableId(EntityType.SAMPLE, 15L));
    assertNotNull(item);
    assertEquals(EntityType.SAMPLE, item.getEntityType());
    assertEquals(15L, item.getId());
  }

  @Test
  public void testSaveBoxable() throws IOException {
    Boxable item = dao.getBoxable(new BoxableId(EntityType.SAMPLE, 15L));
    assertFalse(item.isDiscarded());
    item.setDiscarded(true);
    dao.saveBoxable(item);

    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

    Boxable saved = dao.getBoxable(new BoxableId(EntityType.SAMPLE, 15L));
    assertTrue(saved.isDiscarded());
  }

}

package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize.BoxType;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.BoxableId;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.box.BoxView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.box.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.box.SampleBoxablePositionView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.box.SampleBoxableView;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;

public class HibernateBoxDaoIT extends AbstractDAOTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @PersistenceContext
  private EntityManager entityManager;

  @InjectMocks
  private HibernateBoxDao dao;

  @Before
  public void setup() throws IOException, MisoNamingException {
    MockitoAnnotations.initMocks(this);
    dao.setEntityManager(entityManager);
  }

  @Test
  public void testGetBoxById() throws IOException, InterruptedException {
    Box box = dao.get(1);
    assertEquals("box1alias", box.getAlias());
    assertEquals("BOX1", box.getName());
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
  public void testListByIdList() throws Exception {
    List<Long> ids = Lists.newArrayList(2L, 3L);
    List<Box> boxes = dao.listByIdList(ids);
    assertNotNull(boxes);
    assertEquals(2, boxes.size());
    for (Long id : ids) {
      assertTrue(boxes.stream().anyMatch(x -> x.getId() == id.longValue()));
    }
  }

  @Test
  public void testListByIdListNull() throws Exception {
    List<Box> boxes = dao.listByIdList(null);
    assertNotNull(boxes);
    assertTrue(boxes.isEmpty());
  }

  @Test
  public void testListByIdListNone() throws Exception {
    List<Box> boxes = dao.listByIdList(Collections.emptyList());
    assertNotNull(boxes);
    assertTrue(boxes.isEmpty());
  }

  @Test
  public void testList() throws Exception {
    Collection<Box> boxes = dao.list();
    assertEquals(4, boxes.size());
  }

  @Test
  public void testRemoveBoxableViewFromBox() throws Exception {
    Sample s =
        (Sample) entityManager.unwrap(Session.class).get(SampleImpl.class, 15L);
    Box box = dao.get(1);
    BoxPosition bp = box.getBoxPositions().get("A01");
    assertNotNull(bp);
    assertEquals(new BoxableId(s.getEntityType(), s.getId()), bp.getBoxableId());
    BoxableView item = makeBoxableView(s);
    dao.removeBoxableFromBox(item);

    entityManager.unwrap(Session.class).flush();
    entityManager.unwrap(Session.class).clear();

    Box again = dao.get(1);
    assertFalse(again.getBoxPositions().containsKey("A01"));
  }

  @Test
  public void testRemoveBoxableViewUnneccessary() throws Exception {
    Sample before =
        (Sample) entityManager.unwrap(Session.class).get(SampleImpl.class, 1L);
    assertNull(before.getBox());
    BoxableView item = makeBoxableView(before);
    dao.removeBoxableFromBox(item);

    entityManager.unwrap(Session.class).flush();
    entityManager.unwrap(Session.class).clear();

    Sample after =
        (Sample) entityManager.unwrap(Session.class).get(SampleImpl.class, 1L);
    assertNull(after.getBox());
  }

  private static SampleBoxableView makeBoxableView(Sample boxable) {
    SampleBoxableView v = new SampleBoxableView();
    v.setId(boxable.getId());
    v.setName(boxable.getName());
    v.setAlias(boxable.getAlias());
    v.setIdentificationBarcode(boxable.getIdentificationBarcode());
    v.setLocationBarcode(boxable.getLocationBarcode());
    v.setVolume(boxable.getVolume());
    v.setDiscarded(boxable.isDiscarded());

    Box box = boxable.getBox();
    if (box != null) {
      BoxView toBox = new BoxView();
      toBox.setId(box.getId());
      toBox.setName(box.getName());
      toBox.setAlias(box.getAlias());
      toBox.setLocationBarcode(box.getLocationBarcode());

      SampleBoxablePositionView boxPosition = new SampleBoxablePositionView();
      boxPosition.setBox(toBox);
      boxPosition.setId(boxable.getId());
      boxPosition.setPosition(boxable.getBoxPosition());
      v.setBoxablePosition(boxPosition);
    }
    return v;
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
    BoxUse boxuse =
        (BoxUse) entityManager.unwrap(Session.class).get(BoxUse.class, 1L);
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

    entityManager.unwrap(Session.class).flush();
    entityManager.unwrap(Session.class).clear();

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

    entityManager.unwrap(Session.class).flush();
    entityManager.unwrap(Session.class).clear();

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
  public void testGetBoxableViewsByBarcodeList() throws Exception {
    List<String> barcodes = Arrays.asList("SAM1::TEST_0001_Bn_P_nn_1-1_D_1", "LIB1::TEST_0001_Bn_P_PE_300_WG");
    List<BoxableView> list = dao.getBoxableViewsByBarcodeList(barcodes);
    assertNotNull(list);
    assertEquals(2, list.size());
  }

  @Test
  public void testGetBoxableViewsByBarcodeListNull() throws Exception {
    List<BoxableView> empty = dao.getBoxableViewsByBarcodeList(null);
    assertNotNull(empty);
    assertTrue(empty.isEmpty());
  }

  @Test
  public void testGetBoxableViewsByBarcodeListNone() throws Exception {
    List<BoxableView> empty = dao.getBoxableViewsByBarcodeList(Collections.emptyList());
    assertNotNull(empty);
    assertTrue(empty.isEmpty());
  }

  @Test
  public void testGetBoxContents() throws Exception {
    List<BoxableView> contents = dao.getBoxContents(1L);
    assertNotNull(contents);
    assertEquals(2, contents.size());
    assertTrue(contents.stream().anyMatch(x -> x.getEntityType() == EntityType.SAMPLE && x.getId() == 15L));
    assertTrue(contents.stream().anyMatch(x -> x.getEntityType() == EntityType.SAMPLE && x.getId() == 16L));
  }

  @Test
  public void testGetBoxableViewsByIdList() throws Exception {
    List<BoxableView> empty = dao.getBoxableViewsByIdList(Collections.emptyList());
    assertNotNull(empty);
    assertTrue(empty.isEmpty());

    List<BoxableId> ids =
        Arrays.asList(new BoxableId(EntityType.LIBRARY_ALIQUOT, 1L), new BoxableId(EntityType.POOL, 1L));
    List<BoxableView> list = dao.getBoxableViewsByIdList(ids);
    assertNotNull(list);
    assertEquals(2, list.size());
  }

  @Test
  public void testGetBySearch() {
    List<Box> boxes = dao.getBySearch("alias");
    assertNotNull(boxes);
    assertEquals(3L, boxes.get(0).getId());
  }

  @Test
  public void testGetBySearchNull() {
    exception.expect(NullPointerException.class);
    dao.getBySearch(null);
  }

  @Test
  public void testGetByPartialSearchNull() {
    exception.expect(NullPointerException.class);
    dao.getByPartialSearch(null, true);
  }

  @Test
  public void testGetByPartialSearchStart() {
    List<Box> boxes = dao.getByPartialSearch("identificationbarcode", true);
    assertNotNull(boxes);
    assertEquals(2, boxes.size());
    assertTrue(boxes.stream().anyMatch(x -> x.getId() == 1L));
    assertTrue(boxes.stream().anyMatch(x -> x.getId() == 2L));
  }

  @Test
  public void testGetByPartialSearchAnywhere() {
    List<Box> boxes = dao.getByPartialSearch("test", false);
    assertNotNull(boxes);
    assertEquals(2, boxes.size());
    assertTrue(boxes.stream().anyMatch(x -> x.getId() == 3L));
    assertTrue(boxes.stream().anyMatch(x -> x.getId() == 4L));
  }

  @Test
  public void testGetBoxableViewsBySearchBarcode() throws Exception {
    List<BoxableView> byBarcode = dao.getBoxableViewsBySearch("SAM1::TEST_0001_Bn_P_nn_1-1_D_1");
    assertEquals(1, byBarcode.size());
    BoxableView result = byBarcode.get(0);
    assertEquals(EntityType.SAMPLE, result.getEntityType());
    assertEquals(1L, result.getId());
  }

  @Test
  public void testGetBoxableViewsBySearchName() throws Exception {
    List<BoxableView> byName = dao.getBoxableViewsBySearch("LIB3");
    assertEquals(1, byName.size());
    BoxableView result = byName.get(0);
    assertEquals(EntityType.LIBRARY, result.getEntityType());
    assertEquals(3L, result.getId());
  }

  @Test
  public void testGetBoxableViewsBySearchAlias() throws Exception {
    List<BoxableView> byAlias = dao.getBoxableViewsBySearch("Pool 5");
    assertEquals(1, byAlias.size());
    BoxableView result = byAlias.get(0);
    assertEquals(EntityType.POOL, result.getEntityType());
    assertEquals(5L, result.getId());
  }

  @Test
  public void testGetBoxableViewsBySearchNull() throws Exception {
    exception.expect(NullPointerException.class);
    dao.getBoxableViewsBySearch(null);
  }

  @Test
  public void testSearch() throws IOException {
    testSearch(PaginationFilter.query("BOX1"));
  }

  @Test
  public void testSearchByFreezer() throws IOException {
    testSearch(PaginationFilter.freezer("freezer"));
  }

  @Test
  public void testSearchByBoxUse() throws IOException {
    testSearch(PaginationFilter.boxUse(1L));
  }

  @Test
  public void testSearchByBoxType() throws IOException {
    testSearch(PaginationFilter.boxType(BoxType.PLATE));
  }

  @Test
  public void testSearchByCreator() throws IOException {
    testSearch(PaginationFilter.user("name", true));
  }

  @Test
  public void testSearchByLastModifier() throws IOException {
    testSearch(PaginationFilter.user("name", false));
  }

  @Test
  public void testSearchByEntered() throws IOException {
    testSearch(
        PaginationFilter.date(LimsUtils.parseDate("2021-02-24"), LimsUtils.parseDate("2021-02-24"), DateType.ENTERED));
  }

  @Test
  public void testSearchByLastModified() throws IOException {
    testSearch(
        PaginationFilter.date(LimsUtils.parseDate("2021-02-24"), LimsUtils.parseDate("2021-02-24"), DateType.UPDATE));
  }

  @Test
  public void testSearchByDistributedInvalid() throws IOException {
    exception.expect(RuntimeException.class);
    testSearch(PaginationFilter.date(LimsUtils.parseDate("2021-02-24"), LimsUtils.parseDate("2021-02-24"),
        DateType.DISTRIBUTED));
  }

  @Test
  public void testSearchByProjectInvalid() throws IOException {
    exception.expect(RuntimeException.class);
    testSearch(PaginationFilter.project(1L));
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

    entityManager.unwrap(Session.class).flush();
    entityManager.unwrap(Session.class).clear();

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

    entityManager.unwrap(Session.class).flush();
    entityManager.unwrap(Session.class).clear();

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

    entityManager.unwrap(Session.class).flush();
    entityManager.unwrap(Session.class).clear();

    Boxable saved = dao.getBoxable(new BoxableId(EntityType.SAMPLE, 15L));
    assertTrue(saved.isDiscarded());
  }

  @Test
  public void testSorts() throws Exception {
    String[] sorts = {"sizeId", "useId"};
    for (String sort : sorts) {
      assertNotNull(dao.list(0, 0, true, sort));
    }
  }

}

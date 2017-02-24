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

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.Identity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateBoxDao;

public class SQLBoxDAOTest extends AbstractDAOTest {

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
    Box box = dao.get(1);

    assertTrue("precondition failed", box.getBoxables().size() > 0);
    dao.discardAllTubes(box);
    assertTrue(box.getBoxables().size() == 0);

  }

  @Test
  public void testEmptySingleTube() throws Exception {

    Box box = dao.get(1);
    int count = box.getBoxables().size();

    assertTrue("precondition failed", box.getBoxables().size() > 0);
    assertTrue(box.getBoxables().containsKey("B02"));
    dao.discardSingleTube(box, "B02");
    Box fetchedBox = dao.get(1);
    assertEquals(count - 1, fetchedBox.getBoxables().size());

  }

  @Test
  public void testRemoveBoxableFromBox() throws Exception {
    Box box = dao.get(1);
    Boxable item = box.getBoxables().values().iterator().next();
    assertNotNull(item);

    dao.removeBoxableFromBox(item);
    Box again = dao.get(1);
    assertFalse(again.getBoxables().containsValue(item));
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

  @Test
  public void testAnyMetaDef() throws Exception {
    Box box = dao.get(1L);
    box.getBoxables();
    assertTrue(box.getBoxable("A01") instanceof Identity);
    assertTrue(box.getBoxable("B02") instanceof SampleTissue);
  }
}

package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.type.TissuePieceType;

public class HibernateTissuePieceTypeDaoIT extends AbstractDAOTest {

  private HibernateTissuePieceTypeDao sut;

  @Before
  public void setup() {
    sut = new HibernateTissuePieceTypeDao();
    sut.setEntityManager(getEntityManager());
  }

  @Test
  public void testGet() throws IOException {
    long id = 1L;
    TissuePieceType type = sut.get(id);
    assertNotNull(type);
    assertEquals(id, type.getId());
  }

  @Test
  public void testList() throws IOException {
    List<TissuePieceType> list = sut.list();
    assertNotNull(list);
    assertEquals(2, list.size());
  }

  @Test
  public void testCreate() throws IOException {
    String name = "New Tissue Piece Type";
    TissuePieceType type = new TissuePieceType();
    type.setName(name);
    type.setAbbreviation("TT");
    type.setV2NamingCode("TT");
    long savedId = sut.create(type);

    clearSession();

    TissuePieceType saved =
        (TissuePieceType) currentSession().get(TissuePieceType.class, savedId);
    assertEquals(name, saved.getName());
  }

  @Test
  public void testUpdate() throws IOException {
    long id = 1L;
    String name = "New Name";
    TissuePieceType type = (TissuePieceType) currentSession().get(TissuePieceType.class, id);
    assertNotEquals(name, type.getName());
    type.setName(name);
    sut.update(type);

    clearSession();

    TissuePieceType saved = (TissuePieceType) currentSession().get(TissuePieceType.class, id);
    assertEquals(name, saved.getName());
  }

  @Test
  public void testGetUsageByTissuePieces() throws IOException {
    TissuePieceType type = (TissuePieceType) currentSession().get(TissuePieceType.class, 1L);
    assertEquals("LCM Tube", type.getName());
    assertEquals(0L, sut.getUsage(type));
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(sut::listByIdList, Arrays.asList(1L, 2L));
  }

  @Test
  public void testListByIdListNone() throws Exception {
    testListByIdListNone(sut::listByIdList);
  }

}

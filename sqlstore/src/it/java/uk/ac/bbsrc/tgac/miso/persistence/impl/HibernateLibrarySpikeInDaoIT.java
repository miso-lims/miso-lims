package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.LibrarySpikeIn;

public class HibernateLibrarySpikeInDaoIT extends AbstractDAOTest {

  private HibernateLibrarySpikeInDao sut;

  @Before
  public void setup() {
    sut = new HibernateLibrarySpikeInDao();
    sut.setSessionFactory(getSessionFactory());
  }

  @Test
  public void testGet() throws IOException {
    long id = 1L;
    LibrarySpikeIn spikeIn = sut.get(id);
    assertNotNull(spikeIn);
    assertEquals(id, spikeIn.getId());
  }

  @Test
  public void testGetByAlias() throws IOException {
    String name = "ERCC Mix 2";
    LibrarySpikeIn spikeIn = sut.getByAlias(name);
    assertNotNull(spikeIn);
    assertEquals(name, spikeIn.getAlias());
  }

  @Test
  public void testList() throws IOException {
    List<LibrarySpikeIn> list = sut.list();
    assertNotNull(list);
    assertEquals(2, list.size());
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(sut::listByIdList, Arrays.asList(1L, 2L));
  }

  @Test
  public void testListByIdListNone() throws Exception {
    testListByIdListNone(sut::listByIdList);
  }

  @Test
  public void testCreate() throws IOException {
    String alias = "New LibrarySpikeIn";
    LibrarySpikeIn spikeIn = new LibrarySpikeIn();
    spikeIn.setAlias(alias);
    long savedId = sut.create(spikeIn);

    clearSession();

    LibrarySpikeIn saved = (LibrarySpikeIn) getSessionFactory().getCurrentSession().get(LibrarySpikeIn.class, savedId);
    assertEquals(alias, saved.getAlias());
  }

  @Test
  public void testUpdate() throws IOException {
    long id = 1L;
    String alias = "New Alias";
    LibrarySpikeIn spikeIn = (LibrarySpikeIn) getSessionFactory().getCurrentSession().get(LibrarySpikeIn.class, id);
    assertNotEquals(alias, spikeIn.getAlias());
    spikeIn.setAlias(alias);
    sut.update(spikeIn);

    clearSession();

    LibrarySpikeIn saved = (LibrarySpikeIn) getSessionFactory().getCurrentSession().get(LibrarySpikeIn.class, id);
    assertEquals(alias, saved.getAlias());
  }

  @Test
  public void testGetUsage() throws IOException {
    LibrarySpikeIn spikeIn = (LibrarySpikeIn) getSessionFactory().getCurrentSession().get(LibrarySpikeIn.class, 1L);
    assertEquals("ERCC Mix 1", spikeIn.getAlias());
    assertEquals(0L, sut.getUsage(spikeIn));
  }

}

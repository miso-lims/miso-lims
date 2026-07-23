package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

public class HibernateTargetedSequencingDaoIT extends AbstractDAOTest {

  private AutoCloseable mockito;

  @PersistenceContext
  private EntityManager entityManager;

  @InjectMocks
  private HibernateTargetedSequencingDao dao;

  @BeforeEach
  public void setUp() throws Exception {
    mockito = MockitoAnnotations.openMocks(this);
    dao.setEntityManager(entityManager);
  }

  @AfterEach
  public void cleanUp() throws Exception {
    mockito.close();
  }

  @Test
  public void testList() throws Exception {
    Collection<TargetedSequencing> targetedSequencingList = dao.list();
    assertEquals(2, targetedSequencingList.size());
  }

  @Test
  public void testGet() throws Exception {
    TargetedSequencing actual = dao.get(1L);
    assertNotNull(actual);
    assertEquals("HALO_IBP", actual.getAlias());
  }

  @Test
  public void testGetByAlias() throws Exception {
    String alias = "Thunderbolts";
    TargetedSequencing tarSeq = dao.getByAlias(alias);
    assertNotNull(tarSeq);
    assertEquals(alias, tarSeq.getAlias());
  }

  @Test
  public void testGetUsage() throws Exception {
    TargetedSequencing tarSeq = (TargetedSequencing) currentSession().find(TargetedSequencing.class, 1L);
    assertEquals("HALO_IBP", tarSeq.getAlias());
    assertEquals(1L, dao.getUsage(tarSeq));
  }

  @Test
  public void testCreate() throws Exception {
    TargetedSequencing tarSeq = new TargetedSequencing();
    tarSeq.setAlias("New_TarSeq");
    tarSeq.setDescription("For test");
    User user = (User) currentSession().find(UserImpl.class, 1L);
    assertNotNull(user);
    tarSeq.setChangeDetails(user);
    long savedId = dao.create(tarSeq);

    clearSession();

    TargetedSequencing saved = (TargetedSequencing) currentSession().find(TargetedSequencing.class, savedId);
    assertNotNull(saved);
    assertEquals(tarSeq.getAlias(), saved.getAlias());
    assertEquals(tarSeq.getDescription(), saved.getDescription());
  }

  @Test
  public void testUpdate() throws Exception {
    String description = "changed description";
    TargetedSequencing tarSeq = (TargetedSequencing) currentSession().find(TargetedSequencing.class, 2L);
    assertNotNull(tarSeq);
    assertNotEquals(description, tarSeq.getDescription());
    tarSeq.setDescription(description);
    dao.update(tarSeq);

    clearSession();

    TargetedSequencing saved = (TargetedSequencing) currentSession().find(TargetedSequencing.class, 2L);
    assertEquals(description, saved.getDescription());
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(dao::listByIdList, Arrays.asList(1L, 2L));
  }

  @Test
  public void testListByIdListNone() throws Exception {
    testListByIdListNone(dao::listByIdList);
  }

}

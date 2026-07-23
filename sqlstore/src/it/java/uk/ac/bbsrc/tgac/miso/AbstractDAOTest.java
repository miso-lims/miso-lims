package uk.ac.bbsrc.tgac.miso;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import org.hibernate.Session;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.util.ThrowingFunction;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("/db-it-context.xml")
@Transactional
public abstract class AbstractDAOTest {

  public static final TimeZone UTC = TimeZone.getTimeZone("UTC");

  @BeforeAll
  public static void setupAbstractClass() {
    TimeZone.setDefault(UTC);
  }

  @PersistenceContext
  private EntityManager entityManager;

  public Session currentSession() {
    return entityManager.unwrap(Session.class);
  }

  public EntityManager getEntityManager() {
    return entityManager;
  }

  protected void clearSession() {
    currentSession().flush();
    currentSession().clear();
  }

  protected <T extends Identifiable> void testListByIdList(
      ThrowingFunction<List<Long>, List<T>, IOException> listFunction, List<Long> ids)
      throws Exception {
    List<T> results = listFunction.apply(ids);
    assertNotNull(results);
    assertEquals(ids.size(), results.size());
    for (Long id : ids) {
      assertTrue(results.stream().anyMatch(x -> x.getId() == id));
    }
  }

  protected <T> void testListByIdListNone(ThrowingFunction<List<Long>, List<T>, IOException> listFunction)
      throws Exception {
    List<T> results = listFunction.apply(Collections.emptyList());
    assertNotNull(results);
    assertTrue(results.isEmpty());
  }

}

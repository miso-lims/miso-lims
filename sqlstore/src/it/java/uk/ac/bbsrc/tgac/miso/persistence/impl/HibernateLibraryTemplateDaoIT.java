package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryTemplate;

public class HibernateLibraryTemplateDaoIT extends AbstractDAOTest {

  @PersistenceContext
  private EntityManager entityManager;

  private HibernateLibraryTemplateDao sut;

  @Before
  public void setup() {
    sut = new HibernateLibraryTemplateDao();
    sut.setEntityManager(entityManager);
  }

  @Test
  public void testGet() throws Exception {
    long id = 3L;
    LibraryTemplate template = sut.get(id);
    assertNotNull(template);
    assertEquals(id, template.getId());
  }

  @Test
  public void testListLibraryTemplateForProject() {
    assertEquals(2, sut.listByProject(1L).size());
    assertEquals(1, sut.listByProject(2L).size());
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(sut::listByIdList, Arrays.asList(2L, 3L));
  }

  @Test
  public void testListByIdListNone() throws Exception {
    testListByIdListNone(sut::listByIdList);
  }

  @Test
  public void testCreate() throws Exception {
    String alias = "Test Template";
    LibraryTemplate template = new LibraryTemplate();
    template.setAlias(alias);
    long savedId = sut.create(template);

    clearSession();

    LibraryTemplate saved = (LibraryTemplate) currentSession().get(LibraryTemplate.class, savedId);
    assertNotNull(saved);
    assertEquals(alias, saved.getAlias());
  }

  @Test
  public void testUpdate() throws Exception {
    long id = 2L;
    String newAlias = "asdf";
    LibraryTemplate before = (LibraryTemplate) currentSession().get(LibraryTemplate.class, id);
    assertNotEquals(newAlias, before.getAlias());
    before.setAlias(newAlias);
    sut.update(before);

    clearSession();

    LibraryTemplate after = (LibraryTemplate) currentSession().get(LibraryTemplate.class, id);
    assertEquals(newAlias, after.getAlias());
  }

  @Test
  public void testList() throws Exception {
    List<LibraryTemplate> templates = sut.list();
    assertNotNull(templates);
    assertEquals(3, templates.size());
  }

}

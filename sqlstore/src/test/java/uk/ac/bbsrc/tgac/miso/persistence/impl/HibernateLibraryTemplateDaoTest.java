package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.assertEquals;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;

public class HibernateLibraryTemplateDaoTest extends AbstractDAOTest {

  @Autowired
  private SessionFactory sessionFactory;

  private HibernateLibraryTemplateDao sut;

  @Before
  public void setup() {
    sut = new HibernateLibraryTemplateDao();
    sut.setSessionFactory(sessionFactory);
  }

  @Test
  public void testListLibraryTemplateForProject() {
    assertEquals(1, sut.listLibraryTemplatesForProject(1L).size());
    assertEquals(0, sut.listLibraryTemplatesForProject(2L).size());
  }

}

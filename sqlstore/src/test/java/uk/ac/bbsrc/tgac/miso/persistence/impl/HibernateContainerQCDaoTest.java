package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.Date;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ContainerQC;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;

public class HibernateContainerQCDaoTest extends AbstractDAOTest {

  @Autowired
  private SessionFactory sessionFactory;

  private HibernateContainerQcDao sut;

  @Before
  public void setUp() throws Exception {
    sut = new HibernateContainerQcDao();
    sut.setSessionFactory(sessionFactory);
  }

  @Test
  public void testSave() throws Exception {
    ContainerQC qc = new ContainerQC();
    SequencerPartitionContainer container = new SequencerPartitionContainerImpl();
    container.setId(3L);
    qc.setContainer(container);
    qc.setType(new QcType());
    qc.getType().setId(1L);
    qc.setCreator(new UserImpl());
    qc.getCreator().setUserId(1L);
    qc.setCreationTime(new Date());
    qc.setLastModified(new Date());
    long id = sut.save(qc);

    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

    ContainerQC saved = (ContainerQC) sut.get(id);
    assertNotNull(saved);
    assertEquals(qc.getContainer().getId(), saved.getContainer().getId());
    assertEquals(qc.getType().getId(), saved.getType().getId());
    assertEquals(qc.getResults(), saved.getResults());
  }

}

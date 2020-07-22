package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Date;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.qc.ContainerQC;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;

public class HibernateContainerQCDaoIT extends AbstractDAOTest {

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
    SequencerPartitionContainer container = (SequencerPartitionContainer) currentSession().get(SequencerPartitionContainerImpl.class, 3L);
    QcType qcType = (QcType) currentSession().get(QcType.class, 1L);
    User user = (User) currentSession().get(UserImpl.class, 1L);
    qc.setContainer(container);
    qc.setType(qcType);
    qc.setResults(new BigDecimal("12"));
    qc.setCreator(user);
    qc.setCreationTime(new Date());
    qc.setLastModified(new Date());
    long id = sut.save(qc);

    clearSession();

    ContainerQC saved = (ContainerQC) currentSession().get(ContainerQC.class, id);
    assertNotNull(saved);
    assertEquals(qc.getContainer().getId(), saved.getContainer().getId());
    assertEquals(qc.getType().getId(), saved.getType().getId());
    assertEquals(qc.getResults().compareTo(saved.getResults()), 0);
  }

}

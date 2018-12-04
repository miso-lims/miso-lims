package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;

public class HibernateSequencingContainerModelDaoTest extends AbstractDAOTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Autowired
  private SessionFactory sessionFactory;

  @InjectMocks
  private HibernateSequencingContainerModelDao dao;

  @Before
  public void setup() throws IOException {
    MockitoAnnotations.initMocks(this);
    dao.setSessionFactory(sessionFactory);
  }

  @Test
  public void testFindModelByAlias() throws Exception {
    InstrumentModel platform = (InstrumentModel) sessionFactory.getCurrentSession().get(InstrumentModel.class, 16L);
    String search = "HiSeq PE Flow Cell v4";
    int lanes = 8;
    SequencingContainerModel model = dao.find(platform, search, lanes);
    assertNotNull(model);
    assertEquals(search, model.getAlias());
    assertEquals(lanes, model.getPartitionCount());
  }

  @Test
  public void testFindModelByBarcode() throws Exception {
    InstrumentModel platform = (InstrumentModel) sessionFactory.getCurrentSession().get(InstrumentModel.class, 16L);
    String search = "12345678";
    int lanes = 8;
    SequencingContainerModel model = dao.find(platform, search, lanes);
    assertNotNull(model);
    assertEquals(search, model.getIdentificationBarcode());
    assertEquals(lanes, model.getPartitionCount());
  }

  @Test
  public void testFindFallbackModel() throws Exception {
    InstrumentModel platform = (InstrumentModel) sessionFactory.getCurrentSession().get(InstrumentModel.class, 16L);
    String search = null;
    int lanes = 8;
    SequencingContainerModel model = dao.find(platform, search, lanes);
    assertNotNull(model);
    assertEquals(lanes, model.getPartitionCount());
    assertTrue(model.isFallback());
  }

  @Test
  public void testListModels() throws Exception {
    List<SequencingContainerModel> models = dao.list();
    assertEquals(3, models.size());
  }
}
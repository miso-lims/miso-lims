package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public class HibernateSequencingContainerModelDaoIT extends AbstractDAOTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @PersistenceContext
  private EntityManager entityManager;

  @InjectMocks
  private HibernateSequencingContainerModelDao dao;

  @Before
  public void setup() throws IOException {
    MockitoAnnotations.initMocks(this);
    dao.setEntityManager(entityManager);
  }

  @Test
  public void testFindModelByAlias() throws Exception {
    InstrumentModel platform = (InstrumentModel) entityManager.unwrap(Session.class).get(InstrumentModel.class, 16L);
    String search = "HiSeq PE Flow Cell v4";
    int lanes = 8;
    SequencingContainerModel model = dao.find(platform, search, lanes);
    assertNotNull(model);
    assertEquals(search, model.getAlias());
    assertEquals(lanes, model.getPartitionCount());
  }

  @Test
  public void testFindModelByBarcode() throws Exception {
    InstrumentModel platform = (InstrumentModel) entityManager.unwrap(Session.class).get(InstrumentModel.class, 16L);
    String search = "12345678";
    int lanes = 8;
    SequencingContainerModel model = dao.find(platform, search, lanes);
    assertNotNull(model);
    assertEquals(search, model.getIdentificationBarcode());
    assertEquals(lanes, model.getPartitionCount());
  }

  @Test
  public void testFindFallbackModel() throws Exception {
    InstrumentModel platform = (InstrumentModel) entityManager.unwrap(Session.class).get(InstrumentModel.class, 16L);
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

  @Test
  public void testGet() throws Exception {
    long id = 3L;
    SequencingContainerModel model = dao.get(id);
    assertNotNull(model);
    assertEquals(id, model.getId());
  }

  @Test
  public void testCreate() throws Exception {
    SequencingContainerModel model = new SequencingContainerModel();
    model.setAlias("New Model");
    model.setIdentificationBarcode("barbarbarbar");
    model.setPartitionCount(3);
    model.setPlatformType(PlatformType.ILLUMINA);
    long savedId = dao.create(model);

    clearSession();

    SequencingContainerModel saved =
        (SequencingContainerModel) currentSession().get(SequencingContainerModel.class, savedId);
    assertNotNull(saved);
    assertEquals(model.getAlias(), saved.getAlias());
    assertEquals(model.getIdentificationBarcode(), saved.getIdentificationBarcode());
    assertEquals(model.getPartitionCount(), saved.getPartitionCount());
    assertEquals(model.getPlatformType(), saved.getPlatformType());
  }

  @Test
  public void testUpdate() throws Exception {
    long id = 1L;
    String alias = "Changed Alias";
    SequencingContainerModel model =
        (SequencingContainerModel) currentSession().get(SequencingContainerModel.class, id);
    assertNotEquals(alias, model.getAlias());
    model.setAlias(alias);
    dao.update(model);

    clearSession();

    SequencingContainerModel saved =
        (SequencingContainerModel) currentSession().get(SequencingContainerModel.class, id);
    assertEquals(alias, saved.getAlias());
  }

  @Test
  public void testGetByPlatformAndAlias() throws Exception {
    PlatformType platform = PlatformType.ILLUMINA;
    String alias = "Generic 2-Lane Illumina Flow Cell";
    SequencingContainerModel model = dao.getByPlatformAndAlias(platform, alias);
    assertNotNull(model);
    assertEquals(platform, model.getPlatformType());
    assertEquals(alias, model.getAlias());
  }

  @Test
  public void testGetByPlatformAndBarcode() throws Exception {
    PlatformType platform = PlatformType.ILLUMINA;
    String barcode = "12345678";
    SequencingContainerModel model = dao.getByPlatformAndBarcode(platform, barcode);
    assertNotNull(model);
    assertEquals(platform, model.getPlatformType());
    assertEquals(barcode, model.getIdentificationBarcode());
  }

  @Test
  public void testGetUsage() throws Exception {
    SequencingContainerModel model =
        (SequencingContainerModel) currentSession().get(SequencingContainerModel.class, 1L);
    assertNotNull(model);
    assertEquals(4, dao.getUsage(model));
  }

  @Test
  public void testGetUsagePlatform() throws Exception {
    SequencingContainerModel containerModel =
        (SequencingContainerModel) currentSession().get(SequencingContainerModel.class, 1L);
    assertNotNull(containerModel);
    InstrumentModel instrumentModel = (InstrumentModel) currentSession().get(InstrumentModel.class, 16L);
    assertNotNull(instrumentModel);
    assertEquals(4, dao.getUsage(containerModel, instrumentModel));
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(dao::listByIdList, Arrays.asList(2L, 1L));
  }

  @Test
  public void testListByIdListNone() throws Exception {
    testListByIdListNone(dao::listByIdList);
  }

}

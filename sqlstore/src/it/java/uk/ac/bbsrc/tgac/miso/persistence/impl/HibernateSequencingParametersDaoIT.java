package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

public class HibernateSequencingParametersDaoIT extends AbstractDAOTest {

  private HibernateSequencingParametersDao sut;

  @Before
  public void setup() {
    sut = new HibernateSequencingParametersDao();
    sut.setSessionFactory(getSessionFactory());
  }

  @Test
  public void testGet() throws Exception {
    long id = 1L;
    SequencingParameters params = sut.get(id);
    assertNotNull(params);
    assertEquals(id, params.getId());
  }

  @Test
  public void testGetByNameAndInstrumentModel() throws Exception {
    String name = "HiSeq Params 1";
    InstrumentModel model = (InstrumentModel) currentSession().get(InstrumentModel.class, 16L);
    SequencingParameters params = sut.getByNameAndInstrumentModel(name, model);
    assertNotNull(params);
    assertEquals(name, params.getName());
    assertEquals(model.getAlias(), params.getInstrumentModel().getAlias());
  }

  @Test
  public void testList() throws Exception {
    List<SequencingParameters> list = sut.list();
    assertNotNull(list);
    assertEquals(3, list.size());
  }

  @Test
  public void testListByInstrumentModel() throws Exception {
    InstrumentModel model = (InstrumentModel) currentSession().get(InstrumentModel.class, 16L);
    List<SequencingParameters> list = sut.listByInstrumentModel(model);
    assertNotNull(list);
    assertEquals(2, list.size());
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
    SequencingParameters params = new SequencingParameters();
    params.setName("New Params");
    params.setReadLength(50);
    params.setReadLength2(50);
    InstrumentModel model = (InstrumentModel) currentSession().get(InstrumentModel.class, 16L);
    params.setInstrumentModel(model);
    User user = (User) currentSession().get(UserImpl.class, 1L);
    params.setChangeDetails(user);
    long savedId = sut.create(params);

    clearSession();

    SequencingParameters saved = (SequencingParameters) currentSession().get(SequencingParameters.class, savedId);
    assertNotNull(saved);
    assertEquals(params.getName(), saved.getName());
  }

  @Test
  public void testUpdate() throws Exception {
    long id = 1L;
    String name = "changed name";
    SequencingParameters params = (SequencingParameters) currentSession().get(SequencingParameters.class, id);
    assertNotNull(params);
    assertNotEquals(name, params.getName());
    params.setName(name);
    sut.update(params);

    clearSession();

    SequencingParameters saved = (SequencingParameters) currentSession().get(SequencingParameters.class, id);
    assertNotNull(saved);
    assertEquals(name, saved.getName());
  }

  @Test
  public void testGetUsageByRuns() throws Exception {
    SequencingParameters params = (SequencingParameters) currentSession().get(SequencingParameters.class, 1L);
    assertEquals(1L, sut.getUsageByRuns(params));
  }

  @Test
  public void testGetUsageByPoolOrders() throws Exception {
    SequencingParameters params = (SequencingParameters) currentSession().get(SequencingParameters.class, 2L);
    assertEquals(1L, sut.getUsageByPoolOrders(params));
  }

  @Test
  public void testGetUsageBySequencingOrders() throws Exception {
    SequencingParameters params = (SequencingParameters) currentSession().get(SequencingParameters.class, 1L);
    assertEquals(2L, sut.getUsageBySequencingOrders(params));
  }

}
